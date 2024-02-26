package com.sampietro.NaiveAAC.activities.Bluetooth

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.GATT_SERVER
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.ParcelUuid
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1BleActivity
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.realm.Realm
import java.io.File
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.util.UUID

/**
 * <h1>BluetoothLeService</h1>
 *
 * **BluetoothLeService**
 * step 1
 * The BLE app provides an activity (Game1BleActivity) to connect to Bluetooth device.
 * Based on user input, this activity communicates with a Service called BluetoothLeService,
 * which interacts with the BLE device via the BLE API.
 * The communication is performed using a bound service which allows the activity to connect to the
 * BluetoothLeService and call functions to connect to the devices.
 * The BluetoothLeService needs a Binder implementation that provides access to the service for the activity.
 * Refer to [developer.android.com](https://developer.android.com/develop/connectivity/bluetooth/ble/ble-overview)
 * Refer to [developer.android.com](https://developer.android.com/develop/connectivity/bluetooth/ble/find-ble-devices)
 * Refer to [developer.android.com](https://developer.android.com/develop/connectivity/bluetooth/ble/connect-gatt-server)
 * Refer to [developer.android.com](https://developer.android.com/develop/connectivity/bluetooth/ble/transfer-ble-data)
 * Refer to [medium.com](https://medium.com/@martijn.van.welie/making-android-ble-work-part-1-a736dcd53b02)
 * By [Martijn van Welie](https://medium.com/@martijn.van.welie)
 * Refer to [bignerdranch.com](https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-1/
 * and https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-2/)
 * By [Andrew Lunsford](https://www.linkedin.com/in/andrew-lunsford-403b4b6/)
 *
 * @version     4.0, 09/09/2023
 *
 * @see com.sampietro.simsimtest.activities.Game.Game1.Game1BleActivity
 */

class BluetoothLeService : Service() {
    //
    private val SERVICE_UUID = UUID.fromString("37abdac5-afa6-457b-b53b-1d6bd2b37342")
    private val CHARACTERISTIC_UUID = UUID.fromString("c1623d15-7bd2-4e3d-a6ea-4076b53e1c5a")
    //
    private lateinit var NOTIFICATION_CHANNEL_ID: String

    private val binder = LocalBinder()
    /**
     * step 2
     * The BluetoothLeService needs a Binder implementation that provides access to the service for the activity.
     *
     * @param intent
     * @return IBinder
     */
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    /*

     */
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var scanner: BluetoothLeScanner
    private lateinit var scanSettings: ScanSettings
    //
    private lateinit var realm: Realm
    private lateinit var ctext: Context
    /**
     * Game1BleActivity -> step 4 -> Game1BleActivity
     * Once the service is bound to, it needs to access the BluetoothAdapter.
     * It should check that the adapter is available on the device.
     * Read Set up Bluetooth for more information on the BluetoothAdapter.
     * The following example wraps this setup code in an initialize() function that returns a Boolean
     * value indicating success.
     *
     * @return Boolean
     */
    fun initialize(context:Context): Boolean {
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        /*
        step 6 (1 sc)
        Refer to [/medium.com](https://medium.com/@martijn.van.welie/making-android-ble-work-part-1-a736dcd53b02)
        By [Martijn van Welie](https://medium.com/@martijn.van.welie)
         */
        scanner = bluetoothAdapter!!.getBluetoothLeScanner()
        scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(0L)
            .build()
        //
        ctext = context
        //
        realm = Realm.getDefaultInstance()
        // Create the NotificationChannel.
        val name = ctext.getString(R.string.notification_channel_name)
        val descriptionText = ctext.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        NOTIFICATION_CHANNEL_ID = ctext.getString(R.string.notification_channel_name)
        val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
        //
        return true
    }
    /*

    */
    private var mGattServer: BluetoothGattServer? = null
    /**
     * step 7 (1 gatt server)
     * Refer to [bignerdranch.com](https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-1/
     * and https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-2/ )
     * By [Andrew Lunsford](https://www.linkedin.com/in/andrew-lunsford-403b4b6/)
     */
    @SuppressLint("MissingPermission")
    fun setupServer() {
        if (mGattServer == null) {
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            mGattServer = bluetoothManager.openGattServer(this, gattServerCallback)
            val service = BluetoothGattService(
                SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
            )
            // add a Characteristic that will allow Clients to send a message to the Server.
            val writeCharacteristic = BluetoothGattCharacteristic(
                CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE
            )
            service.addCharacteristic(writeCharacteristic)
            //
            mGattServer!!.addService(service)
            //
        }
    }
    private var messageFromGattServer = "nessun messaggio"
    private val gattServerCallback = object : BluetoothGattServerCallback()
    {
        @SuppressLint("MissingPermission")
        /**
         * step 8 (2 Gatt Server) : what to do when a write request is received
         * Refer to [bignerdranch.com](https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-1/
         * and https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-2/ )
         * By [Andrew Lunsford](https://www.linkedin.com/in/andrew-lunsford-403b4b6/)
         */
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
            if (characteristic.uuid == CHARACTERISTIC_UUID) {
                mGattServer!!.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                /*
                // reverse the value of the Characteristic to differentiate the response from the request
                val length: Int = value!!.size
                val reversed = ByteArray(length)
                for (i in 0 until length) {
                    reversed[i] = value.get(length - (i + 1))
                }
                // After setting the Characteristic’s new value, we tell mGattServer to notify all
                // connected devices for this characteristic
                characteristic.value = reversed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    mGattServer!!.notifyCharacteristicChanged(device!!, characteristic, false, reversed)
                }
                else {
                    mGattServer!!.notifyCharacteristicChanged(device, characteristic, false)
                }
                 */
                // inserire qui visualizzazione messaggio
                broadcastUpdate(MESSAGE_FROM_GATT_SERVER)
                messageFromGattServer =  value!!.toString(Charsets.UTF_8)
                //
                if (activityIsPaused)
                {
                    notifyMessageFromGattServer()
                }
            }
        }
    }
    // TTS
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null
    @SuppressLint("MissingPermission")
    fun notifyMessageFromGattServer ()  {
        if (messageFromGattServer != "I'M DISCONNECTING")
        {
            // use comma as separator
            val cvsSplitBy = getString(R.string.character_comma)
            val oneWord: Array<String?> =
                messageFromGattServer.split(cvsSplitBy.toRegex()).toTypedArray()
            // Create an explicit intent for an Activity in your app.
            val fullScreenIntent = Intent(this, Game1BleActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // You need this if starting
            //  the activity from a service
//            intent.setAction(Intent.ACTION_MAIN)
//            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val fullScreenPendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            //
            val builder = Notification.Builder(ctext,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.heart)
                .setContentTitle(deviceEnabledUserName)
                .setContentText(oneWord[0] + " " + oneWord[4] + " " + oneWord[8])
                // Set the intent that fires when the user taps the notification.
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setLargeIcon(bitmap1)
                .setStyle(Notification.BigPictureStyle()
                    .bigPicture(bitmap1))
            with(NotificationManagerCompat.from(ctext)) {
                // notificationId is a unique int for each notification that you must define.
                notify(1, builder.build())
            }
            //
            tTS1 = TextToSpeech(ctext) { status ->
                if (status != TextToSpeech.ERROR) {
                    tTS1!!.speak(
                        oneWord[0] + " " + oneWord[4] + " " + oneWord[8],
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        getString(R.string.prova_tts)
                    )
                } else {
                    Toast.makeText(ctext, status, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    /**
     * get message from Gatt Server
     *
     * @return string message from Gatt Server
     */
    fun getMessageFromGattServer () : String {
        return messageFromGattServer
    }
    @SuppressLint("MissingPermission")
    fun stopServer() {
        if (mGattServer != null) {
            mGattServer!!.close()
        }
    }
    private var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    /**
     * step 9 (3 Gatt Server)
     * Refer to [bignerdranch.com](https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-1/
     * and https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-2/ )
     * By [Andrew Lunsford](https://www.linkedin.com/in/andrew-lunsford-403b4b6/)
     */
    @SuppressLint("MissingPermission")
    fun startAdvertising() {
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = bluetoothAdapter!!.getBluetoothLeAdvertiser()
            if (mBluetoothLeAdvertiser == null) {
                return
            }
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build()
            val parcelUuid = ParcelUuid(SERVICE_UUID)
            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(parcelUuid)
                .build()
            mBluetoothLeAdvertiser!!.startAdvertising(settings, data, mAdvertiseCallback)
        }
    }
    //
    private val mAdvertiseCallback: AdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.d(TAG, "Peripheral advertising started.")
        }
        override fun onStartFailure(errorCode: Int) {
            Log.d(TAG, "Peripheral advertising failed: $errorCode")
        }
    }
    @SuppressLint("MissingPermission")
    fun stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser!!.stopAdvertising(mAdvertiseCallback)
        }
    }
//
    private var mScanning = false
    var devicesArrayList: ArrayList<BluetoothDevice> = ArrayList()
    private lateinit var deviceAddressFound: String
    /**
     * step 11 (3 sc) Bluetooth scan
     * Refer to [bignerdranch.com](https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-1/
     * and https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-2/ )
     * By [Andrew Lunsford](https://www.linkedin.com/in/andrew-lunsford-403b4b6/)
     */
    @SuppressLint("MissingPermission")
    fun scan() {
        if (!mScanning)
        {
            // Stops scanning after a pre-defined scan period.
            Handler(Looper.getMainLooper()).postDelayed({
                mScanning = false
                scanner.stopScan(scanCallback)
                // the scan  did not find any devices so we check if there are devices connected
                // to the gatt server to connect their gatt
                val bluetoothDevicesConnectedToTheGattServerList = devicesConnected(GATT_SERVER)
                if (!bluetoothDevicesConnectedToTheGattServerList.isEmpty())
                {
                    device = bluetoothDevicesConnectedToTheGattServerList.first()
                    // search for image of the device user to send any notifications
                    searchForImageOfTheDeviceUser()
                    //
                    bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback, TRANSPORT_LE)
                }
            }, SCAN_PERIOD)
            //
            var filters: MutableList<ScanFilter> = ArrayList()
            val filter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString(SERVICE_UUID.toString()))
                .build()
            filters.add(filter)
            //
            mScanning = true
            scanner.startScan(filters, scanSettings, scanCallback)
        }
    }
    @SuppressLint("MissingPermission")
    fun stopScanner() {
        if (mScanning) {
            scanner.stopScan(scanCallback)
            mScanning = false
        }
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        /**
         * step 12 (4 sc) Bluetooth scan callback
         * Refer to [bignerdranch.com](https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-1/
         * and https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-2/ )
         * By [Andrew Lunsford](https://www.linkedin.com/in/andrew-lunsford-403b4b6/)
         *
         * @param callbackType int
         * @param result ScanResult
         */
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device: BluetoothDevice = result.getDevice()
            // ...do whatever you want with this found device
            if (!devicesArrayList.contains(device)) {
                devicesArrayList.add(device)
                deviceAddressFound = device.address!!
                connect(deviceAddressFound)
            }
        }
        override fun onBatchScanResults(results: List<ScanResult?>?) {
            // Ignore for now
        }
        @SuppressLint("MissingPermission")
        override fun onScanFailed(errorCode: Int) {
            // Ignore for now
        }
    }

    private var bluetoothGatt: BluetoothGatt? = null
    /**
     * step 13
     * Once the BluetoothService is initialized, it can connect to the BLE device.
     * The activity needs to send the device address to the service so it can initiate the connection.
     * The service will first call getRemoteDevice() on the BluetoothAdapter to access the device.
     * If the adapter is unable to find a device with that address, getRemoteDevice()
     * throws an IllegalArgumentException.
     *
     * @param address string
     * @return boolean
     */
    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        /*
        step 14 (5 sc)
        */
        if (mScanning) {
            scanner.stopScan(scanCallback)
            mScanning = false
        }
        bluetoothAdapter?.let { adapter ->
            try {
                if (bluetoothGatt == null)
                {
                    device = adapter.getRemoteDevice(address)
                    // search for image of the device user to send any notifications
                    searchForImageOfTheDeviceUser()
                    // connect to the GATT server on the device
                    /*
                    step 17
                    Once the BluetoothGattCallback is declared, the service can use the BluetoothDevice
                    object from the connect() function to connect to the GATT service on the device.
                    The connectGatt() function is used.
                    This requires a Context object, an autoConnect boolean flag, and the BluetoothGattCallback.
                    In this example, the app is directly connecting to the BLE device, so false is passed for autoConnect.
                     */
                    bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback, TRANSPORT_LE)
                }
                return true
            } catch (exception: IllegalArgumentException) {
                Log.w(TAG, "Device not found with provided address.")
                return false
            }

        } ?: run {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return false
        }
    }
    //
    private var connectionState = STATE_DISCONNECTED
    private lateinit var device: BluetoothDevice
    private var service: BluetoothGattService? = null
    private lateinit var characteristic: BluetoothGattCharacteristic
    private var mConnected: Boolean = false
    private var mInitialized: Boolean = false
    /*
    step 15
    Once the activity tells the service which device to connect to and the service connects to the device,
    the service needs to connect to the GATT server on the BLE device.
    This connection requires a BluetoothGattCallback to receive notifications about the connection state,
    service discovery, characteristic reads, and characteristic notifications.
    This topic focuses on the connection state notifications.
    See Transfer BLE data for how to perform service discovery, characteristic reads, and request
    characteristic notifications.
    The onConnectionStateChange() function is triggered when the connection to the device’s
    GATT server changes.
    The callback is defined in the Service class so it can be used with the BluetoothDevice once
    the service connects to it.
    */
    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            /*
            step 17 -> Game1BleActivity
            Once the broadcast function is in place, it is used within the BluetoothGattCallback
            to send information about the connection state with the GATT server.
            Constants and the service’s current connection state are declared in the service
            representing the Intent actions.
             */
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
//                connectionState = STATE_CONNECTED
//                broadcastUpdate(ACTION_GATT_CONNECTED)
                /*
                Game1BleActivity -> step 19
                The first thing to do once you connect to the GATT Server on the BLE device is
                to perform service discovery.
                This provides information about the services available on the remote device as well
                as the service characteristics and their descriptors.
                Once the service successfully connects to the device
                (indicated by the appropriate call to the onConnectionStateChange() function of the
                BluetoothGattCallback), the discoverServices() function queries the information from the BLE device.
                The service needs to override the onServicesDiscovered() function in the BluetoothGattCallback.
                This function is called when the device reports on its available services.
                 */
                // Attempts to discover services after successful connection.
//                mConnected = true
                gatt!!.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
                mConnected = false
                gatt!!.close()
            }

        }

        /*
        step 20
        This function is called when the device reports on its available services.
        */
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                /*
                If the discovery services was successful, we can now negotiate a larger MTU than the
                default (23 bytes).
                */
                gatt!!.requestMtu(256)
            } else {
                disconnect()
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        /*
        step 21
        If the discovery services was successful and the MTU has been agreed,
        we can now look for our Characteristic.
        Since we know the full UUID of both the Service and the Characteristic,
        we can access them directly.
        */
        @SuppressLint("MissingPermission")
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (gatt != null) {
                    service = gatt.getService(SERVICE_UUID)
                    if (service != null) {
                        characteristic = service!!.getCharacteristic(CHARACTERISTIC_UUID)
                        /*
                        Now that we have found our Characteristic, we need to set the write type and
                        enable notifications.
                        mInitialized is used to signify that our Characteristic is fully ready for use.
                        Without reaching this point, the Characteristic would not have the correct write type
                        or notify us when there is a change.
                        Make sure to set this to false when disconnecting from the GATT server.
                         */
                        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                        mInitialized = gatt.setCharacteristicNotification(characteristic, true)
                    }
                }
                //
                connectionState = STATE_CONNECTED
                broadcastUpdate(ACTION_GATT_CONNECTED)
                mConnected = true
                //
            }
            else {
                disconnect()
                Log.w(TAG, "onMtuChanged received: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {  }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
        }
    }
    /**
     * step 16
     * When the server connects or disconnects from the GATT server, it needs to notify the activity of the new state.
     * There are several ways to accomplish this.
     * We use broadcasts to send the information from the service to activity.
     * The service declares a function to broadcast the new state.
     * This function takes in an action string which is passed to an Intent object before being broadcast to the system.
     *
     * @param action string
     */
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }
    /**
     *
     * @param profile int GATT or GATT_SERVER
     * @return List<BluetoothDevice>
     */
    @SuppressLint("MissingPermission")
    fun devicesConnected(profile: Int): List<BluetoothDevice> {
        val bleManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothConnectedDevicesList = bleManager.getConnectedDevices(profile)
        return bluetoothConnectedDevicesList
    }
    /**
     * @return string device enabled name
     */
    @SuppressLint("MissingPermission")
    fun getDeviceEnabledName () : String? {
        return device.name
    }
    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bluetoothGatt!!.disconnect()
        bluetoothGatt = null
    }
    /*
    step 23
    One important step when dealing with Bluetooth connections is to close the connection when you are finished with it.
    To do this, call the close() function on the BluetoothGatt object.
    In the following example, the service holds the reference to the BluetoothGatt.
    When the activity unbinds from the service, the connection is closed to avoid draining the device battery.
     */
    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }
    @SuppressLint("MissingPermission")
    private fun close() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
        // TTS
        if (tTS1 != null) {
            tTS1!!.stop()
            tTS1!!.shutdown()
        }
    }
    /*
    The service uses broadcasts to notify the activity.
    Once the services have been discovered, the service can call getServices() to get the reported data.
     */
    /*
    fun getSupportedGattServices(): List<BluetoothGattService?>? {
        return bluetoothGatt!!.services
    }
     */
    /**
     * set the value on the Characteristic and our message will be sent
     *
     * @param message string
     */
    @SuppressLint("MissingPermission")
    fun sendMessage(message: String) {
        // Before doing anything, make sure we are connected and our Characteristic is initialized.
        if (!mConnected || !mInitialized) {
            return
        }
        // n order to send the data we must first convert our String to byte[].
        var messageBytes = ByteArray(0)
        try {
            messageBytes = message.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Failed to convert message string to byte array")
        }
        // Now set the value on the Characteristic and our message will be sent!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bluetoothGatt!!.writeCharacteristic(characteristic, messageBytes, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT )
            } else {
            characteristic.value = messageBytes
            bluetoothGatt!!.writeCharacteristic(characteristic)
        }
    }

    var activityIsPaused:Boolean = false
    fun activityInPausedState() {
        activityIsPaused = true
    }

    fun activityInActiveState() {
        activityIsPaused = false
    }
    /**
     * used for notification
     */
    var deviceEnabledUserName = "non trovato"
    fun searchForImageOfTheDeviceUser() {
        var deviceEnabledName : String? = "non trovato"
        if (getDeviceEnabledName() != null)
        { deviceEnabledName = getDeviceEnabledName() }
        else
        { deviceEnabledName = "mancante" }
        //
        val results = realm.where(
            BluetoothDevices::class.java
        ).equalTo("deviceName", deviceEnabledName).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                deviceEnabledUserName = result.deviceUserName!!
            }
        }
        //
        if (deviceEnabledUserName != "non trovato")
        {
            val image: ResponseImageSearch?
            image = ImageSearchHelper.imageSearch(ctext, realm, deviceEnabledUserName)
            addImage(image!!.uriType, image.uriToSearch)
        }
        else {
            val uri = ctext.filesDir.absolutePath + "/images/puntointerrogativo.png"
            addImage("S", uri)
        }
    }
    /**
     * used for notification
     */
    var bitmap1: Bitmap? = null
    /**
     * used for notification
     */
    var target1: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            bitmap1 = bitmap
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }
    /**
     * used for notification
     * load an image in bitmap1 from a url or from a file
     *
     * @param urlType if string equal to "A" the image is loaded from a url otherwise it is loaded from a file
     * @param url string with url or file path of origin
     * @see GraphicsHelper.getTargetBitmapFromUrlUsingPicasso
     */
    fun addImage(urlType: String, url: String?) {
        if (urlType == getString(R.string.character_a)) {
            GraphicsHelper.getTargetBitmapFromUrlUsingPicasso(url, target1)
        } else {
            val f = File(url!!)
            GraphicsHelper.getTargetBitmapFromFileUsingPicasso(f, target1)
        }
    }

    companion object {
        // Stops scanning after 30 seconds.
        private const val SCAN_PERIOD: Long = 30000

        const val ACTION_GATT_CONNECTED =
            "com.example.androiddocumentationbluetoothble.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.androiddocumentationbluetoothble.ACTION_GATT_DISCONNECTED"
//        const val ACTION_GATT_SERVICES_DISCOVERED =
//            "com.example.androiddocumentationbluetoothble.ACTION_GATT_SERVICES_DISCOVERED"
        const val MESSAGE_FROM_GATT_SERVER = "com.example.androiddocumentationbluetoothble.MESSAGE_FROM_GATT_SERVER"


        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2
        //
        private const val TAG = "BluetoothLeService"
    }

    /**
    step 2
    The BluetoothLeService needs a Binder implementation that provides access to the service for the activity.
     */
    inner class LocalBinder : Binder() {
        fun getService() : BluetoothLeService {
            return this@BluetoothLeService
        }
    }
}