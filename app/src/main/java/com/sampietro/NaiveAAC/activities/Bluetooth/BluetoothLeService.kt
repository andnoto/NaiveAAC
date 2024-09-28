package com.sampietro.NaiveAAC.activities.Bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
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
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1BleActivity
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2BleActivity
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.realm.Realm
import java.io.File
import java.io.UnsupportedEncodingException
import java.util.Arrays
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
 * Refer to [medium.com](https://medium.com/@martijn.van.welie/making-android-ble-work-part-1-a736dcd53b02
 * and https://medium.com/@martijn.van.welie/making-android-ble-work-part-4-72a0b85cb442 )
 * By [Martijn van Welie](https://medium.com/@martijn.van.welie)
 * Refer to [bignerdranch.com](https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-1/
 * https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-2/
 * and https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-3/)
 * By [Andrew Lunsford](https://www.linkedin.com/in/andrew-lunsford-403b4b6/)
 * Refer to [learn.adafruit.com](https://learn.adafruit.com/introduction-to-bluetooth-low-energy/gap
 * and https://learn.adafruit.com/introduction-to-bluetooth-low-energy/gatt )
 * By [Kevin Townsend](https://learn.adafruit.com/u/ktownsend)
 * Refer to [https://punchthrough.com/](https://punchthrough.com/android-ble-guide/?utm_source=BlogEmail&utm_medium=Email&utm_campaign=BlogRoundUp&mc_cid=9113555d36&mc_eid=767bbcdc1b)
 * By [Chee Yi Ong](https://punchthrough.com/author/cong/)
 *
 * @version     5.0, 01/04/2024
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1BleActivity
 * @see com.sampietro.NaiveAAC.activities.Game.Game2.Game2BleActivity
 */

class BluetoothLeService : Service() {
    //
    private val SERVICE_UUID = UUID.fromString("37abdac5-afa6-457b-b53b-1d6bd2b37342")
    private val CHARACTERISTIC_UUID = UUID.fromString("c1623d15-7bd2-4e3d-a6ea-4076b53e1c5a")
    val CCC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_MESSAGE_FROM_GATT_SERVER_UUID = UUID.fromString("83a7f6ec-479f-4c00-8e0c-7afb9447ba0c")
    val CLIENT_CONFIGURATION_DESCRIPTOR_ID = "2902"
    //
    private lateinit var NOTIFICATION_CHANNEL_ID: String

    private val binder = LocalBinder()
    /**
     * step 2
     * The BluetoothLeService needs a Binder implementation that provides access to the service for the activity.
     *
     * @param intent
     * @return binder
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
    private val messagesStack = mutableListOf<String>()
    private var messageToNotify = "nessun messaggio"
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
            // create the Client Configuration Descriptor to handle the Server pushing Characteristic
            // updates to the Client, and add to new Characteristic
            val clientConfigurationDescriptor = BluetoothGattDescriptor(
                CCC_DESCRIPTOR_UUID,
                BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE).apply {
                value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            }
            val notifyCharacteristic = BluetoothGattCharacteristic(
                CHARACTERISTIC_MESSAGE_FROM_GATT_SERVER_UUID,
                0,
                0)
            notifyCharacteristic.addDescriptor(clientConfigurationDescriptor)
            service.addCharacteristic(notifyCharacteristic)
            //
            mGattServer!!.addService(service)
            //
        }
    }
    private var messageFromGattServer = "nessun messaggio"
    val clientConfigurations = LinkedHashMap<String, ByteArray>()
    private val gattServerCallback = object : BluetoothGattServerCallback()
    {
        @SuppressLint("MissingPermission")
        /**
         * step 8 (2 Gatt Server) :
         * 1) what to do when a write request is received
         * Refer to [bignerdranch.com](https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-1/
         * and https://bignerdranch.com/blog/bluetooth-low-energy-on-android-part-2/ )
         * By [Andrew Lunsford](https://www.linkedin.com/in/andrew-lunsford-403b4b6/)
         * 2) add the Clients that are listening to the Configuration Descriptor to a clientConfigurations map.
         * 3) Once the broadcast function is in place, it is used within the BluetoothGattServerCallback
         * to send information about the connection state with the GATT client.
         * As soon as a peripheral connects to a GATT server, GATT server will stop advertising
         * and as soon the existing connection is broken, GATT server will start advertising
         */
        // what to do when a write request is received
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
            if (characteristic.uuid == CHARACTERISTIC_UUID
                // if the user of the device has not been found, the request is not considered
                && deviceEnabledUserName != "non trovato") {
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
                    // if the activity is paused I add the message to the message stack
                    if (messageFromGattServer != "I'M DISCONNECTING")
                    {
                        messagesStack.add(messageFromGattServer) // Push element onto the stack
                        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                            || (
                                    (ActivityCompat.checkSelfPermission(ctext, Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED))){
                            // version code TIRAMISU = version 33 = Android 13
                            // You can use the API that requires the permission.
                            messageToNotify = messageFromGattServer
                            deviceEnabledUserNameImageSearch()
                        }
                    }
                }
            }
        }
        // add the Clients that are listening to the Configuration Descriptor to a clientConfigurations map
        @SuppressLint("MissingPermission")
        override fun onDescriptorWriteRequest(device: BluetoothDevice,
                                              requestId: Int,
                                              descriptor: BluetoothGattDescriptor,
                                              preparedWrite: Boolean,
                                              responseNeeded: Boolean,
                                              offset: Int,
                                              value: ByteArray) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value)
            if (CCC_DESCRIPTOR_UUID == descriptor.uuid) {
                clientConfigurations[device.address] = value
                mGattServer!!.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
            }
        }
        //  Once the broadcast function is in place, it is used within the BluetoothGattServerCallback
        // to send information about the connection state with the GATT client.
        // As soon as a peripheral connects to a GATT server, GATT server will stop advertising
        // and as soon the existing connection is broken, GATT server will start advertising
        override fun onConnectionStateChange(deviceConnected: BluetoothDevice , status:Int , newState: Int ) {
        super.onConnectionStateChange(deviceConnected, status, newState)
            deviceEnabledUserName = searchForDeviceUser(deviceConnected)
            // if the user of the device has not been found, the connection will not be made
            if (deviceEnabledUserName != "non trovato")
            {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    stopAdvertising()
                    devicesArrayList.add(deviceConnected)
                    device=deviceConnected
                    broadcastUpdate(ACTION_GATT_SERVER_CONNECTED)
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    startAdvertising()
                    devicesArrayList.remove(deviceConnected)
                    broadcastUpdate(ACTION_GATT_SERVER_DISCONNECTED)
                }
            }
        }
    }
    fun deviceEnabledUserNameImageSearch()  {
        if (deviceEnabledUserName != "non trovato")
        {
            val realm = Realm.getDefaultInstance()
            val image: ResponseImageSearch?
            image = imageSearch(ctext, realm, deviceEnabledUserName)
            addImage(image!!.uriType, image.uriToSearch)
        }
    }
    // TTS
    var tTS1: TextToSpeech? = null
//    var toSpeak: String? = null
    @SuppressLint("MissingPermission")
    fun notifyMessage()  {
        if (messageToNotify != "I'M DISCONNECTING")
        {
            var messageToSpeak = ""
            // use comma as separator
            val cvsSplitBy = getString(R.string.character_comma)
            val oneWord: Array<String?> =
                messageToNotify.split(cvsSplitBy.toRegex()).toTypedArray()
            val oneWordSize = oneWord.size
            //
            var irrh = 0
            while (irrh < oneWordSize) {
                if (oneWord[irrh] == " ")
                    break
                val firstMessageToSpeak = messageToSpeak
                if (oneWord[irrh] == getString(R.string.io))
                { oneWord[irrh] = " " }
                if (oneWord[irrh] != getString(R.string.nessuno) && oneWord[irrh] != " ")
                    messageToSpeak =
                        firstMessageToSpeak + " " + oneWord[irrh]!!
                irrh = irrh+3
            }
            // Create an explicit intent for an Activity in your app.
            var fullScreenPendingIntent: PendingIntent? = null
            if (clientActivity == "Game1BleActivity")
            /* for the problem : Android FLAG_ACTIVITY_NEW_TASK does not resume activity
                but always try to recreate SomeActivity, calling onCreate instead
                Refer to [stackoverflow](https://stackoverflow.com/questions/23446120/onnewintent-is-not-called)
                answer of [Marawan Mamdouh](https://stackoverflow.com/users/12173531/marawan-mamdouh)
             */
                {
                val fullScreenIntent = Intent(this, Game1BleActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val flags = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
                        else -> FLAG_UPDATE_CURRENT
                    }
//                fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, flags)
                }
                else
                {
                val fullScreenIntent = Intent(this, Game2BleActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val flags = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
                        else -> FLAG_UPDATE_CURRENT
                    }
//                fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, flags)
                }
            //
            val builder = Notification.Builder(ctext,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.heart)
                .setContentTitle(deviceEnabledUserName)
                .setContentText(messageToSpeak)
                // Set the intent that fires when the user taps the notification.
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setLargeIcon(bitmap1)
                .setStyle(Notification.BigPictureStyle()
                    .bigPicture(bitmap1))
            with(NotificationManagerCompat.from(ctext)) {
                // notificationId is a unique int for each notification that you must define.
                notify(1, builder.build())
            }
            tTS1 = TextToSpeech(ctext) { status ->
                if (status != TextToSpeech.ERROR) {
                    tTS1!!.speak(
                        messageToSpeak,
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
            /**
             * get message from Gatt Server
             *
             * @return string message from Gatt Server
             */
    fun getMessageFromGatt () : String {
        return messageFromDeviceConnected
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
    val devicesArrayList: MutableList<BluetoothDevice> = mutableListOf()
//    var devicesArrayList: ArrayList<BluetoothDevice> = ArrayList()
//    private lateinit var deviceAddressFound: String
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
            if (callbackType != ScanSettings.CALLBACK_TYPE_MATCH_LOST)
                {
                    val device: BluetoothDevice = result.getDevice()
                    // ...do whatever you want with this found device
                    if (!devicesArrayList.contains(device)) {
                        devicesArrayList.add(device)
                        val deviceAddressFound = device.address!!
                        connect(deviceAddressFound)
                    }
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
                    deviceEnabledUserName = searchForDeviceUser(device!!)
                    // connect to the GATT server on the device
                    /*
                    step 17
                    Once the BluetoothGattCallback is declared, the service can use the BluetoothDevice
                    object from the connect() function to connect to the GATT service on the device.
                    The connectGatt() function is used.
                    This requires a Context object, an autoConnect boolean flag, and the BluetoothGattCallback.
                    In this example, the app is directly connecting to the BLE device, so false is passed for autoConnect.
                     */
                    // if the user of the device has not been found, the connection will not be made
                    if (deviceEnabledUserName != "non trovato")
                    {
                        bluetoothGatt = device!!.connectGatt(this, false, bluetoothGattCallback, TRANSPORT_LE)
                        return true
                    }
                    else
                    {
                        return false
                    }
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
//    private var connectionState = STATE_DISCONNECTED
    private var device: BluetoothDevice? = null
//    private var service: BluetoothGattService? = null
    private var characteristic: BluetoothGattCharacteristic? = null
    private var mConnected: Boolean = false
//    private var mInitialized: Boolean = false
    //
    var messageFromDeviceConnected = "nessun messaggio"
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
                /*
                If you call createBond yourself, it still holds that you should not do
                anything else at the same time and you still need to register
                your broadcast receiver to monitor the process.
                Note that if you call createBond on a device that is already paired,
                you will get an error so check before calling it.
                 */
//                device.createBond()
                gatt!!.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
//                connectionState = STATE_DISCONNECTED
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
                mConnected = false
                gatt!!.close()
            }

        }

        /**
         * step 19bis
         * This function is called when the device reports on its available services.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/65441218/calling-connectgatt-and-createbond-together)
         * answer of [Emil](https://stackoverflow.com/users/556495/emil)
         */
        @SuppressLint("MissingPermission")
//        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
                /*
                If you call createBond yourself, it still holds that you should not do
                anything else at the same time and you still need to register
                your broadcast receiver to monitor the process.
                Note that if you call createBond on a device that is already paired,
                you will get an error so check before calling it.
                 */
//                device.createBond()
//            } else {
//                disconnect()
//                Log.w(TAG, "onServicesDiscovered received: $status")
//            }
//        }
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                /*
                If the discovery services was successful, we can now negotiate a larger MTU than the
                default (23 bytes).
                */
                gatt!!.requestMtu(512)
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
                    device = gatt.device
                    // search for image of the device user to send any notifications
                    deviceEnabledUserName = searchForDeviceUser(device!!)
                    // if the user of the device has not been found, the connection will not be made
                    if (deviceEnabledUserName == "non trovato")
                    {
                        gatt.disconnect()
                        return
                    }
                }
                if (gatt != null) {
                    val service = gatt.getService(SERVICE_UUID)
                    if (service != null) {
                        characteristic = service.getCharacteristic(CHARACTERISTIC_UUID)
                        /*
                        Now that we have found our Characteristic, we need to set the write type and
                        enable notifications.
                        mInitialized is used to signify that our Characteristic is fully ready for use.
                        Without reaching this point, the Characteristic would not have the correct write type
                        or notify us when there is a change.
                        Make sure to set this to false when disconnecting from the GATT server.
                         */
                        characteristic!!.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                        // enable notifications once we’ve discovered services
                        gatt.services.find { it.uuid == SERVICE_UUID }
                            ?.characteristics?.find { it.uuid == CHARACTERISTIC_MESSAGE_FROM_GATT_SERVER_UUID }
                            ?.let {
                                if (gatt.setCharacteristicNotification(it, true)) {
                                    enableCharacteristicConfigurationDescriptor(gatt, it)
                                }
                            }
//                        mInitialized = gatt.setCharacteristicNotification(characteristic, true)
//                        enableNotifications(characteristic)
                    }
                }
                //
//                connectionState = STATE_CONNECTED
//                broadcastUpdate(ACTION_GATT_CONNECTED)
//                mConnected = true
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

        @Deprecated("Deprecated for Android 13+")
        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic) {
//                Log.i("BluetoothGattCallback", "Characteristic $uuid changed | value: ${value.toHexString()}")
//                if (characteristic.uuid == CHARACTERISTIC_UUID
                if (characteristic.uuid == CHARACTERISTIC_MESSAGE_FROM_GATT_SERVER_UUID
                    // if the user of the device has not been found, the request is not considered
                    && deviceEnabledUserName != "non trovato") {
//                    mGattServer!!.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                    // inserire qui visualizzazione messaggio
                    broadcastUpdate(MESSAGE_FROM_GATT)
                    messageFromDeviceConnected =  value.toString(Charsets.UTF_8)
                    //
                    if (activityIsPaused)
                    {
                        // if the activity is paused I add the message to the message stack
                        if (messageFromDeviceConnected != "I'M DISCONNECTING")
                        {
                            messagesStack.add(messageFromDeviceConnected) // Push element onto the stack
                            if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                                || (
                                        (ActivityCompat.checkSelfPermission(ctext, Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED))){
                                // version code TIRAMISU = version 33 = Android 13
                                // You can use the API that requires the permission.
                                messageToNotify = messageFromDeviceConnected
                                deviceEnabledUserNameImageSearch()
                            }
                        }
                    }
                }
            }
        }
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
//            val newValueHex = value.toHexString()
            with(characteristic) {
//                Log.i("BluetoothGattCallback", "Characteristic $uuid changed | value: $newValueHex")
//                if (characteristic.uuid == CHARACTERISTIC_UUID
                if (characteristic.uuid == CHARACTERISTIC_MESSAGE_FROM_GATT_SERVER_UUID
                    // if the user of the device has not been found, the request is not considered
                    && deviceEnabledUserName != "non trovato") {
//                    mGattServer!!.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                    // inserire qui visualizzazione messaggio
                    broadcastUpdate(MESSAGE_FROM_GATT)
                    messageFromDeviceConnected =  value.toString(Charsets.UTF_8)
                    //
                    if (activityIsPaused)
                    {
                        // if the activity is paused I add the message to the message stack
                        if (messageFromDeviceConnected != "I'M DISCONNECTING")
                        {
                            messagesStack.add(messageFromDeviceConnected) // Push element onto the stack
                            if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                                || (
                                        (ActivityCompat.checkSelfPermission(ctext, Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED))){
                                // version code TIRAMISU = version 33 = Android 13
                                // You can use the API that requires the permission.
                                messageToNotify = messageFromDeviceConnected
                                deviceEnabledUserNameImageSearch()
                            }
                        }
                    }
                }
            }
        }
        //        override fun onCharacteristicChanged(
//            gatt: BluetoothGatt,
//            characteristic: BluetoothGattCharacteristic,
//            value: ByteArray
//        ) {
//            super.onCharacteristicChanged(gatt, characteristic, value)
//        }
        /*
        step 21 bis
        If the discovery services was successful , the MTU has been agreed,
        and write descriptor was successful
        connection setup is done, and the BLE device is ready to be interacted with.
        */
        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //
//                connectionState = STATE_CONNECTED
                broadcastUpdate(ACTION_GATT_CONNECTED)
                mConnected = true
//                mInitialized = true
                //
            }
            else {
                disconnect()
                Log.w(TAG, "onDescriptorWrite received: $status")
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun enableCharacteristicConfigurationDescriptor(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        characteristic.descriptors.find { it.uuid.toString().substring(4, 8) == CLIENT_CONFIGURATION_DESCRIPTOR_ID }
            ?.apply {
                value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(this)
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
        intent.setPackage(ctext.packageName)
        sendBroadcast(intent)
    }
    /**
     * @return string device enabled name
     */
    @SuppressLint("MissingPermission")
    fun getDeviceEnabledName(): String? {
        return device?.name
    }
    /**
     * @return string device enabled name
     */
    @SuppressLint("MissingPermission")
    fun getDeviceEnabledName(bluetoothDeviceToCheck: BluetoothDevice): String? {
        return bluetoothDeviceToCheck.name
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
    // Disconnects all established connections from Gatt Server
//    @SuppressLint("MissingPermission")
//    fun disconnectFromGattServer() {
//        devicesArrayList.forEach { device ->
//            mGattServer!!.cancelConnection(device)
//        }
//    }
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
        if (mGattServer != null) {
            mGattServer!!.close()
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
    fun sendMessageFromServer(message: String) {
        // Before doing anything, make sure we are connected and our Characteristic is initialized.
//        if (!mConnected || !mInitialized) {
//            return
//        }
        // n order to send the data we must first convert our String to byte[].
        var messageBytes = ByteArray(0)
        try {
            messageBytes = message.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Failed to convert message string to byte array")
        }
        // Now set the value on the Characteristic and our message will be sent!
//        val characteristic = mGattServer!!.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_UUID)
//        characteristic.value = messageBytes
        notifyCharacteristic(messageBytes, CHARACTERISTIC_MESSAGE_FROM_GATT_SERVER_UUID)
    }
    // send message to Gatt client
    @SuppressLint("MissingPermission")
    private fun notifyCharacteristic(value: ByteArray, uuid: UUID) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            mGattServer?.getService(SERVICE_UUID)
                ?.getCharacteristic(uuid)?.let {
                    it.value = value
                    val confirm = it.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE == BluetoothGattCharacteristic.PROPERTY_INDICATE
                    devicesArrayList.forEach { device ->
                        if (clientEnabledNotifications(device, it)) {
                            mGattServer!!.notifyCharacteristicChanged(device, it, confirm)
                        }
                    }
                }
        }
    }
    // check that each connected device has enabled notifications before sending them
    private fun clientEnabledNotifications(device: BluetoothDevice, characteristic: BluetoothGattCharacteristic): Boolean {
        val descriptorList = characteristic.descriptors
//        val descriptor = descriptorList.find { isClientConfigurationDescriptor(descriptorList) }
        val descriptor = descriptorList.find { isClientConfigurationDescriptor(it) }
            ?: // There is no client configuration descriptor, treat as true
            return true
        val deviceAddress = device.address
        val clientConfiguration = clientConfigurations[deviceAddress]
            ?: // Descriptor has not been set
            return false
        return Arrays.equals(clientConfiguration, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
    }
    private fun isClientConfigurationDescriptor(descriptor: BluetoothGattDescriptor?) =
        descriptor?.let {
            it.uuid.toString().substring(4, 8) == CLIENT_CONFIGURATION_DESCRIPTOR_ID
        } ?: false
    /**
     * set the value on the Characteristic and our message will be sent
     *
     * @param message string
     */
    @SuppressLint("MissingPermission")
    fun sendMessageFromClient(message: String) {
        // Before doing anything, make sure we are connected and our Characteristic is initialized.
//        if (!mConnected || !mInitialized) {
        if (!mConnected) {
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
            bluetoothGatt!!.writeCharacteristic(characteristic!!, messageBytes, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT )
            } else {
            characteristic!!.value = messageBytes
            bluetoothGatt!!.writeCharacteristic(characteristic)
        }
    }

    var activityIsPaused:Boolean = false
    var clientActivity: String? = null
    fun activityInPausedState(parameterWithClientActivity: String) {
        clientActivity = parameterWithClientActivity
        activityIsPaused = true
    }

    fun activityInActiveState(parameterWithClientActivity: String) {
        clientActivity = parameterWithClientActivity
        activityIsPaused = false
        // sending last message (if any), clearing message stack
        if (messagesStack.size != 0)
        {
            messageFromGattServer =  messagesStack[messagesStack.size-1]
            broadcastUpdate(MESSAGE_FROM_GATT_SERVER)
            //
            messagesStack.clear()
        }
    }
    /**
     * used for notification
     */
    var deviceEnabledUserName = "non trovato"
    fun searchForDeviceUser(bluetoothDeviceToCheck: BluetoothDevice): String {
        var deviceEnabledName : String? = "non trovato"
        if (getDeviceEnabledName(bluetoothDeviceToCheck) != null)
        { deviceEnabledName = getDeviceEnabledName(bluetoothDeviceToCheck) }
        else
        { deviceEnabledName = "manca device name" }
        //
        val realm = Realm.getDefaultInstance()
        val results = realm.where(
            BluetoothDevices::class.java
        ).equalTo("deviceName", deviceEnabledName).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
//                deviceEnabledUserName = result.deviceUserName!!
                return result.deviceUserName!!
            }
            else
            {
                return "non trovato"
            }
        }
        else
        {
            return "non trovato"
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
            notifyMessage()
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }
    /**
     * used for notification
     * load an image in bitmap1 from a url or from a file
     * for the problem Picasso get a java.lang.IllegalStateException: Method call should happen from the main thread
     * REFER to [stackoverflow](https://stackoverflow.com/questions/47432094/android-picasso-method-call-should-happen-from-the-main-thread)
     * answer of [Sup.Ia](https://stackoverflow.com/users/8458132/sup-ia)
     *
     * @param urlType if string equal to "A" the image is loaded from a url otherwise it is loaded from a file
     * @param url string with url or file path of origin
     * @see GraphicsAndPrintingHelper.getTargetBitmapFromUrlUsingPicasso
     */
    fun addImage(urlType: String, url: String?) {
        Handler(Looper.getMainLooper()).post {
            if (urlType == getString(R.string.character_a)) {
                GraphicsAndPrintingHelper.getTargetBitmapFromUrlUsingPicasso(url, target1,200,200)
            } else {
                val f = File(url!!)
                GraphicsAndPrintingHelper.getTargetBitmapFromFileUsingPicasso(f, target1,200,200)
            }
        }
    }
    /*
    step 20
    This function is called when Bonding succeeded.
    */
    @SuppressLint("MissingPermission")
    fun bondingSucceeded() {
        if (bluetoothGatt != null)
        {
            // Ignore updates for other devices
            if (device!!.getAddress().equals(bluetoothGatt!!.getDevice().getAddress()))
                bluetoothGatt!!.discoverServices()
        }
    }
    /*
    **
    *
    * @param profile int GATT or GATT_SERVER
    * @return List<BluetoothDevice>
    */
//    @SuppressLint("MissingPermission")
//    fun devicesConnected(profile: Int): List<BluetoothDevice> {
//        val bleManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
//        val bluetoothConnectedDevicesList = bleManager.getConnectedDevices(profile)
//        return bluetoothConnectedDevicesList
//    }
    /**
     * clear the service variables
     *
     */
    fun clearServiceVariables() {
        messagesStack.clear()
        messageToNotify = "nessun messaggio"
        messageFromGattServer = "nessun messaggio"
        mBluetoothLeAdvertiser = null
        //
        mScanning = false
        devicesArrayList.clear()
        device = null
        characteristic = null
        mConnected = false
        messageFromDeviceConnected = "nessun messaggio"
        //
        activityIsPaused = false
        clientActivity = null
        //
        deviceEnabledUserName = "non trovato"
        bitmap1 = null
    }
    companion object {
        // Stops scanning after 60 seconds.
        private const val SCAN_PERIOD: Long = 60000

        const val ACTION_GATT_SERVER_CONNECTED =
            "com.example.androiddocumentationbluetoothble.ACTION_GATT_SERVER_CONNECTED"
        const val ACTION_GATT_SERVER_DISCONNECTED =
            "com.example.androiddocumentationbluetoothble.ACTION_GATT_SERVER_DISCONNECTED"
        const val ACTION_GATT_CONNECTED =
            "com.example.androiddocumentationbluetoothble.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.androiddocumentationbluetoothble.ACTION_GATT_DISCONNECTED"
        const val MESSAGE_FROM_GATT_SERVER = "com.example.androiddocumentationbluetoothble.MESSAGE_FROM_GATT_SERVER"
        const val MESSAGE_FROM_GATT = "com.example.androiddocumentationbluetoothble.MESSAGE_FROM_GATT"

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