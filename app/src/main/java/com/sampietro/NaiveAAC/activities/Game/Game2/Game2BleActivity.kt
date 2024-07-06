package com.sampietro.NaiveAAC.activities.Game.Game2

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithListener
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClassWithRecognizerCallback
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothDevices
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothLeService
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAActivity
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAArrayList
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAFragment
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAOnFragmentEventListener
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADARecyclerViewAdapterInterface
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAViewPagerActivity
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAViewPagerFragment
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAViewPagerMediaContainerOnClickListener
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAViewPagerOnFragmentEventListener
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAViewPagerOnFragmentSoundMediaPlayerListener
import com.sampietro.NaiveAAC.activities.Game.GameADA.SettingsStoriesImprovementActivity
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.AndroidNotificationPermission
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper
import com.sampietro.NaiveAAC.activities.Settings.SettingsStoriesWordActivity
import com.sampietro.NaiveAAC.activities.Settings.StoriesFragment
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacFragment
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacRecyclerViewAdapterInterface
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.StoriesComparator
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import io.realm.Realm
import java.util.*
import kotlin.collections.ArrayList

/**
 * <h1>SettingsStoriesRegistrationActivity</h1>
 *
 * **SettingsStoriesRegistrationActivity** displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClassWithRecognizerCallback
 * @see Game2ActivityAbstractClass
 */
class Game2BleActivity : SettingsStoriesRegistrationActivityAbstractClass(),
    FragmentAbstractClassWithListener.onBaseFragmentEventListenerSettings,
    Game2BleDialogFragment.onFragmentEventListenerGame2BleDialogFragment,
    GameADARecyclerViewAdapterInterface,
    GameADAOnFragmentEventListener,
    GameADAViewPagerOnFragmentEventListener,
    GameADAViewPagerOnFragmentSoundMediaPlayerListener,
    GameADAViewPagerMediaContainerOnClickListener,
    Game2BleImagesSearchAdapter.ImagesSearchAdapterInterface,
    ImageSearchArasaacRecyclerViewAdapterInterface
{
    // Code to manage Service lifecycle.
    var bluetoothService : BluetoothLeService? = null
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        /**
         * step 3
         * A client binds to a service by calling bindService().
         * When it does, it must provide an implementation of ServiceConnection, which monitors
         * the connection with the service.
         * The return value of bindService() indicates whether the requested service exists and
         * whether the client is permitted access to it.
         *
         * @param componentName
         * @param service
         */
        override fun onServiceConnected(
            componentName: ComponentName,
            service: IBinder
        ) {
            bluetoothService = (service as BluetoothLeService.LocalBinder).getService()
            bluetoothService?.let { bluetooth ->
                // call functions on service to check connection and connect to devices
                /*
                BluetootLeService -> step 5 -> BluetootLeService
                The activity calls this function within its ServiceConnection implementation.
                Handling a false return value from the initialize() function depends on your application.
                You could show an error message to the user indicating that the current device
                does not support the Bluetooth operation or disable any features that require Bluetooth to work.
                In the following example, finish() is called on the activity to send the user back to the previous screen.
                 */
                if (!bluetooth.initialize(context)) {
                    Log.e(TAG, "Unable to initialize Bluetooth")
                    finish()
                }
                /*
                BluetootLeService -> step 7 -> BluetootLeService
                */
                bluetooth.setupServer()
                bluetooth.startAdvertising()
                /*
                step 10 -> BluetootLeService
                 */
                // perform device scanning
                bluetooth.scan()
                //
                bluetooth.activityInActiveState("Game2BleActivity")
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService = null
        }
    }
    /**
     * used for bluetooth
     */
    lateinit var btAdapter: BluetoothAdapter
    var mConnected : Boolean = false
    var deviceEnabledUserName : String? = "non trovato"
    var deviceEnabledName : String? = "non trovato"
    var messageFromGattServer = "nessun messaggio"
    var dialog: Game2BleDialogFragment? = null
    //
    var preference_PrintPermissions: String? = null
    lateinit var galleryList: ArrayList<GameADAArrayList>
    //
    private var voiceToBeRecordedInStories: VoiceToBeRecordedInStories? = null
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel
    //
    var phraseToDisplay = 0
    var wordToDisplay = 0
    var wordToDisplayInTheStory = 0
    //
    var updateMode:String? = null
    var theNewWordsMustBeInsertedBeforeThese: String? = ""
    /**
     *
     */
    /**
     * configurations of game start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement.prepareSpeechRecognizer
     *
     * @see ActionbarFragment
     *
     * @see android.app.Activity.onCreate
     * @see fragmentTransactionStart
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_2_ble)
        //
        AndroidPermission.checkPermission(this)
        //
        SpeechRecognizerManagement.prepareSpeechRecognizer(this)
        //
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment)).commit()
        }
        //
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
        */
        // In the Activity#onCreate make the only setItem
        voiceToBeRecordedInStories = VoiceToBeRecordedInStories()
        viewModel = ViewModelProvider(this).get(
            VoiceToBeRecordedInStoriesViewModel::class.java
        )
        viewModel.setItem(voiceToBeRecordedInStories!!)
        voiceToBeRecordedInStories!!.story = "Image exchange via Bluetooth"
        voiceToBeRecordedInStories!!.phraseNumberInt = 1
        clearFieldsOfViewmodelDataClass()
//        val intent = intent
//        if (intent.hasExtra(getString(R.string.story))) {
//        keywordStoryToAdd = "Image exchange via Bluetooth"
//        }
//        phraseNumberToAdd = "1"
        //
        AndroidNotificationPermission.checkPermission(this)
        //
        realm = Realm.getDefaultInstance()
        //
        context = this
        //
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        sharedLastSession = sharedPref.getInt(getString(R.string.preference_LastSession), 1)
        //
        val hasLastPhraseNumber =
            sharedPref.contains(getString(R.string.preference_last_phrase_number))
        sharedLastPhraseNumber = if (!hasLastPhraseNumber) {
            // no sentences have been recorded yet
            0
        } else {
            // it is not the first recorded sentence
            sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
        }
        // if is print permitted then preference_PrintPermissions = Y
        preference_PrintPermissions = sharedPref.getString(
            context.getString(R.string.preference_print_permissions),
            getString(R.string.default_string)
        )
        /*
        the device must have Bluetooth enabled and support low energy functionality
         */
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble is not supported", Toast.LENGTH_SHORT).show()
            finish()
        }
        // Initializes a Bluetooth adapter.
        val mBluetoothManager = getSystemService(BluetoothManager::class.java)
        btAdapter = mBluetoothManager.adapter
        /*
        we need to check that advertising is hardware supported before accessing the BluetoothLeAdvertiser
        */
//        if (!btAdapter.isMultipleAdvertisementSupported()) {
//            finish()
//            return
//        }
        //
        fragmentTransactionStart("")
    }
    /**
     * Hide the Navigation Bar,
     * if necessary :
     * check Bluetooth Ble Permissions,
     * binds to service BluetoothLeService by calling bindService(),
     * setup Gatt Server,
     * start Advertising,
     * start Bleuetooth scan
     *
     * @see android.app.Activity.onResume
     * @see BluetoothLeService
     */
    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        //
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        registerReceiver(bondStateReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
        //
        // check Bluetooth Ble Permissions
        //
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            // version code S = version 31 = Android 12
            && (
                    (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE
                    ) != PackageManager.PERMISSION_GRANTED)
                            ||
                            (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED)
                            ||
                            (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN
                            ) != PackageManager.PERMISSION_GRANTED)
                    ))
        {
            // se siamo qui è perchè non si è mostrata alcuna spiegazione all'utente, richiesta di permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN ),
                1
            )
        }
        else
        {
            if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                // version code S = version 31 = Android 12
                && (
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH
                        ) != PackageManager.PERMISSION_GRANTED)
                                ||
                                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED)
                        ))
            {
                // se siamo qui è perchè non si è mostrata alcuna spiegazione all'utente, richiesta di permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION ),
                    2
                )
            }
            else
            {
                if (!btAdapter.isEnabled) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    enableBtResultLauncher.launch(enableBtIntent)
                } else {
                    /*
                    step 3 -> BluetoothLeService
                    A client binds to a service by calling bindService().
                    When it does, it must provide an implementation of ServiceConnection, which monitors
                    the connection with the service.
                    The return value of bindService() indicates whether the requested service exists and
                    whether the client is permitted access to it.
                    */
                    if (bluetoothService == null) {
                        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
                        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                    }
                    else
                    {
                        /*
                            BluetootLeService -> step 5 -> BluetootLeService
                            The activity calls this function within its ServiceConnection implementation.
                            Handling a false return value from the initialize() function depends on your application.
                            You could show an error message to the user indicating that the current device
                            does not support the Bluetooth operation or disable any features that require Bluetooth to work.
                            In the following example, finish() is called on the activity to send the user back to the previous screen.
                        */
                        if (!bluetoothService!!.initialize(this)) {
                            Log.e(TAG, "Unable to initialize Bluetooth")
                            finish()
                        }
                        /*
                        BluetootLeService -> step 7 -> BluetootLeService
                        */
                        bluetoothService!!.setupServer()
                        bluetoothService!!.startAdvertising()
                        /*
                        step 10 -> BluetootLeService
                        */
                        // perform device scanning
                        bluetoothService!!.scan()
                        //
                        bluetoothService!!.activityInActiveState("Game2BleActivity")
                    }
                }
            }
        }

    }
    //
    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            addAction(BluetoothLeService.MESSAGE_FROM_GATT_SERVER)
        }
    }
    //
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            //
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                //
                if (!btAdapter.isEnabled) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    enableBtResultLauncher.launch(enableBtIntent)
                } else {
                    /*
                    step 3 -> BluetoothLeService
                    A client binds to a service by calling bindService().
                    When it does, it must provide an implementation of ServiceConnection, which monitors
                    the connection with the service.
                    The return value of bindService() indicates whether the requested service exists and
                    whether the client is permitted access to it.
                    */
                    val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
                    bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                }
                //
            }
            else
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN ),
                    1
                )
            }
        }
        if (requestCode == 2) {
            //
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                //
                if (!btAdapter.isEnabled) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    enableBtResultLauncher.launch(enableBtIntent)
                } else {
                    /*
                    step 3 -> BluetoothLeService
                    A client binds to a service by calling bindService().
                    When it does, it must provide an implementation of ServiceConnection, which monitors
                    the connection with the service.
                    The return value of bindService() indicates whether the requested service exists and
                    whether the client is permitted access to it.
                    */
                    val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
                    bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                }
                //
            }
            else
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION ),
                    2
                )
            }
        }

    }

    @SuppressLint("MissingPermission")
    var enableBtResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_CANCELED) {
            finish()
        }
        else
            if (result.resultCode == RESULT_OK) {
                /*
                step 3 -> BluetoothLeService
                A client binds to a service by calling bindService().
                When it does, it must provide an implementation of ServiceConnection, which monitors
                the connection with the service.
                The return value of bindService() indicates whether the requested service exists and
                whether the client is permitted access to it.
                */
                val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
                bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
    }
    /*
    BluetootLeService -> 18 -> BluetootLeService
    Once the service broadcasts the connection updates, the activity needs to implement a BroadcastReceiver.
    Register this receiver when setting up the activity, and unregister it when the activity is leaving the screen.
    By listening for the events from the service, the activity is able to update the user interface
    based on the current connection state with the BLE device.
    In Transfer BLE data, the BroadcastReceiver is also used to communicate the service discovery
    as well as the characteristic data from the device.
     */
    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    mConnected = true
                    // if MultipleAdvertisement is not supported the device name is not available
                    if (bluetoothService!!.getDeviceEnabledName() != null)
                    { deviceEnabledName = bluetoothService!!.getDeviceEnabledName() }
                    else
                    { deviceEnabledName = "mancante" }
                    //
                    enableTransmitToConnectedBluetooth()
                    val deviceenabledusername = findViewById<View>(R.id.deviceenabledusername) as TextView
                    deviceenabledusername.setText(deviceEnabledUserName)
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    mConnected = false
                    disableTransmitToDisconnectedBluetooth()
                    val deviceenabledusername = findViewById<View>(R.id.deviceenabledusername) as TextView
                    deviceenabledusername.setText(deviceEnabledUserName)
                }
                BluetoothLeService.MESSAGE_FROM_GATT_SERVER -> {
                    messageFromGattServer = bluetoothService!!.getMessageFromGattServer()
                    if (messageFromGattServer == "I'M DISCONNECTING")
                    {
                    }
                    else
                    {
                        // use comma as separator
                        val cvsSplitBy = getString(R.string.character_comma)
                        val oneWord: Array<String?> =
                            messageFromGattServer.split(cvsSplitBy.toRegex()).toTypedArray()
//                        messageLeftColumnContent = oneWord[0]
//                        messageMiddleColumnContent = oneWord[4]
//                        messageRightColumnContent = oneWord[8]
                        //
                        dialogFragmentShow()
                    }
                }
            }
        }
    }
    /*
    step 20
    This function is called when Bonding succeeded.
    */
    private val bondStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                    when (bondState) {
                        BluetoothDevice.BOND_BONDING -> {
                            // Bonding process has already started let it complete
//                            Log.i(BluetoothLeService.TAG, "waiting for bonding to complete")
                        }
                        BluetoothDevice.BOND_BONDED -> {
                            // Bonding succeeded
                            /*
                            If the discovery services was successful and bonding succeeded,
                            we can now negotiate a larger MTU than the default (23 bytes).
                            */
//                            bluetoothService!!.bondingSucceeded()
                        }
                        BluetoothDevice.BOND_NONE -> {}
                    }
                }
            }
        }
    }
    /**
     *
     *
     * @see androidx.fragment.app.Fragment.onPause
     */
    override fun onPause() {
        super.onPause()
        if (bluetoothService != null)
        {
            bluetoothService!!.sendMessage("I'M DISCONNECTING")
            bluetoothService!!.stopAdvertising()
            bluetoothService!!.stopScanner()
            //
            bluetoothService!!.activityInPausedState("Game2BleActivity")
        }
        unregisterReceiver(gattUpdateReceiver)
        unregisterReceiver(bondStateReceiver)
    }
    /**
     * destroy SpeechRecognizer, TTS shutdown and more
     *
     * @see androidx.fragment.app.Fragment.onDestroy
     */
    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        bluetoothService!!.stopServer()
        bluetoothService!!.disconnect()
        // TTS
        if (tTS1 != null) {
            tTS1!!.stop()
            tTS1!!.shutdown()
        }
    }
    /**
     * send message.
     *
     * @see BluetoothLeService.sendMessage
     */
    @SuppressLint("MissingPermission")
    fun sendMessage() {
        var messageToSend = ""
        val count = galleryList.size
        if (count != 0) {
            var irrh = 0
            while (irrh < count) {
                val firstMessageToSend = messageToSend
                messageToSend =
                    firstMessageToSend + galleryList[irrh].image_title!! + "," + galleryList[irrh].urlType + "," + galleryList[irrh].url + ","
                irrh++
            }
        }
        // Delete last character in String
        bluetoothService!!.sendMessage(messageToSend.substring(0, messageToSend.length - 1))
    }
    /**
     * enable transmit to connected Bluetooth .
     *
     * @see readingOfTheText
     * @see sendMessage
     * @see prepareTheFragmentTransaction
     * @see fragmentTransactionStart
     */
    @SuppressLint("MissingPermission")
    fun enableTransmitToConnectedBluetooth() {
        deviceEnabledUserName = "non trovato"
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
//        prepareTheFragmentTransaction()
//        fragmentTransactionStart()
    }
    /**
     * disable transmit to disconnected Bluetooth .
     *
     * @see displaySecondLevelMenu
     */
    @SuppressLint("MissingPermission")
    fun disableTransmitToDisconnectedBluetooth() {
        deviceEnabledName = "non trovato"
        deviceEnabledUserName = "non trovato"
        //
//        displaySecondLevelMenu()
    }
    /**
     * scan request Bluetooth Devices from Game1BleSecondLevelFragment .
     *
     * @see Game1BleSecondLevelFragment
     * @see BluetoothLeService.scan
     */
    fun scanRequestBluetoothDevices() {
        bluetoothService!!.scan()
    }
    /**
     * Show Game1BleDialogFragment .
     *
     * @see Game1BleDialogFragment
     */
    fun dialogFragmentShow() {
        dialog = Game2BleDialogFragment.newInstance(deviceEnabledUserName!!, messageFromGattServer )
        dialog!!.show(supportFragmentManager, "GAME_DIALOG")
    }
    /**
     * Called when the user taps the Game1BleDialogFragment buttons.
     *
     * @param v view of tapped button
     * @see Game1BleDialogFragment
     */
    override fun receiveResultOnClickFromGame2DialogFragment(v: View?) {
        when (v!!.id) {
            R.id.dialog_sendtobluetoothimage -> {
                // TTS
                tTS1 = TextToSpeech(context) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tTS1!!.speak(
                            deviceEnabledUserName,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            getString(R.string.prova_tts)
                        )
                    } else {
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.dialog_listenagainbutton -> {
                var messageToSpeak = ""
                // use comma as separator
                val cvsSplitBy = getString(R.string.character_comma)
                val oneWord: Array<String?> =
                    messageFromGattServer.split(cvsSplitBy.toRegex()).toTypedArray()
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
                // TTS
                tTS1 = TextToSpeech(context) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tTS1!!.speak(
                            messageToSpeak,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            getString(R.string.prova_tts)
                        )
                    } else {
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.dialog_continuegamebutton -> {
                dialog!!.dismiss()
            }
        }
    }
    /**
     * Callback from Game1BleDialogFragment
     *
     * @see Game1BleDialogFragment
     */
    override fun receiveResultFromGame2DialogFragment() {
        var messageToSpeak = ""
        // use comma as separator
        val cvsSplitBy = getString(R.string.character_comma)
        val oneWord: Array<String?> =
            messageFromGattServer.split(cvsSplitBy.toRegex()).toTypedArray()
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
        // TTS
        tTS1 = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                tTS1!!.speak(
                    messageToSpeak,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    getString(R.string.prova_tts)
                )
            } else {
                Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Called when the user taps the continue game button.
     *
     * display second level menu
     *
     * @param v view of tapped button
     * @see displaySecondLevelMenu
     */
    fun continueGameButton(v: View?) {
//        displaySecondLevelMenu()
    }
    /**
     * Called when the user taps the start speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement.startSpeech
     */
    override fun startSpeechSettingsStoriesQuickRegistration(v: View?) {
        SpeechRecognizerManagement.startSpeech()
        //
        val frag = GameFragmentHear()
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game2_fragment)) as SettingsStoriesRegistrationFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game_fragment_hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.settings_container, frag, getString(R.string.game_fragment_hear))
            // ok, we got the fragment instance, but should we manipulate its view?
        } else {
            ft.add(R.id.settings_container, frag, getString(R.string.game_fragment_hear))
        }
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see SettingsStoriesRegistrationFragment
     */
    override fun fragmentTransactionStart(eText: String?) {
        val frag = SettingsStoriesRegistrationFragment(R.layout.activity_game_2_ble_registration)
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
        bundle.putString(getString(R.string.etext), eText)
        bundle.putString(getString(R.string.keywordstorytoadd), voiceToBeRecordedInStories!!.story)
        bundle.putString(getString(R.string.phrasenumbertoadd),
            voiceToBeRecordedInStories!!.phraseNumberInt.toString()
        )
//        bundle.putString(getString(R.string.wordtoadd), wordToAdd)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game2_fragment)) as SettingsStoriesRegistrationFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game_fragment_hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.settings_container, frag, getString(R.string.game2_fragment))
        } else {
            ft.add(R.id.settings_container, frag, getString(R.string.game2_fragment))
        }
        ft.addToBackStack(null)
        ft.commitAllowingStateLoss()
    }
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see GameADAFragment
     */
    fun gameADAFragmentTransactionStart() {
        //
        val frag = Game2BleGameADAFragment(R.layout.activity_game_2_ble_improvement)
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
        bundle.putInt(getString(R.string.word_to_display_index), 0)
        bundle.putBoolean(getString(R.string.tts_enabled), true)
        bundle.putString(getString(R.string.game_use_video_and_sound), "Y")
        //
        bundle.putString(getString(R.string.keywordstorytoadd), voiceToBeRecordedInStories!!.story)
        bundle.putString(getString(R.string.phrasenumbertoadd),
            voiceToBeRecordedInStories!!.phraseNumberInt.toString()
        )
        //
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as Game2BleGameADAFragment?
        val settingsstoriesregistrationfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game2_fragment)) as SettingsStoriesRegistrationFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgame1hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || settingsstoriesregistrationfragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.settings_container, frag, getString(R.string.gamefragmentgameada))
        } else {
            ft.add(R.id.settings_container, frag, getString(R.string.gamefragmentgameada))
        }
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see GameADAFragment
     */
    fun gameADAViewPagerFragmentTransactionStart(position: Int) {
        val frag = GameADAViewPagerFragment(R.layout.activity_settings_stories_improvement_viewpager_content)
        val bundle = Bundle()
        bundle.putString(context.getString(R.string.story_to_display), voiceToBeRecordedInStories!!.story)
        bundle.putInt(context.getString(R.string.word_to_display_index), position)
        bundle.putString(context.getString(R.string.game_use_video_and_sound), "Y")
        //
//        val frag = GameADAFragment(R.layout.activity_game_2_ble_improvement)
//        val bundle = Bundle()
//        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
//        bundle.putInt(getString(R.string.word_to_display_index), 0)
//        bundle.putBoolean(getString(R.string.tts_enabled), true)
//        bundle.putString(getString(R.string.game_use_video_and_sound), "Y")
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag("GameADAViewPagerFragment") as GameADAViewPagerFragment?
        val gameADAfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as Game2BleGameADAFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgame1hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || gameADAfragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.settings_container, frag, "GameADAViewPagerFragment")
        } else {
            ft.add(R.id.settings_container, frag, "GameADAViewPagerFragment")
        }
        ft.addToBackStack(null)
        ft.commit()
    }
    override fun receiveWordToDisplayIndexGameFragment(v: View?, wordToDisplayIndex: Int) {
    }
    override fun receiveOnClickGameImage(v: View?) {
    }
    override fun receiveResultGameFragment(v: View?, soundMediaPlayer: MediaPlayer?) {
    }
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see GameADAFragment
     */
    fun storiesFragmentTransactionStart() {
        if (updateMode == getString(R.string.modify))
        {
            val daModificare = realm.where(Stories::class.java)
                .equalTo(
                    getString(R.string.story),
                    voiceToBeRecordedInStories!!.story
                )
                .equalTo(getString(R.string.phrasenumberint), voiceToBeRecordedInStories!!.phraseNumberInt)
                .equalTo(getString(R.string.wordnumberint), voiceToBeRecordedInStories!!.wordNumberInt)
                .findAll()
            val daModificareSize = daModificare.size
            if (daModificareSize > 0) {
                voiceToBeRecordedInStories!!.word = daModificare[0]!!.word
                voiceToBeRecordedInStories!!.uriType = daModificare[0]!!.uriType
                voiceToBeRecordedInStories!!.uri = daModificare[0]!!.uri
                voiceToBeRecordedInStories!!.answerActionType = daModificare[0]!!.answerActionType
                voiceToBeRecordedInStories!!.answerAction = daModificare[0]!!.answerAction
                voiceToBeRecordedInStories!!.video = daModificare[0]!!.video
                voiceToBeRecordedInStories!!.sound = daModificare[0]!!.sound
                voiceToBeRecordedInStories!!.soundReplacesTTS = daModificare[0]!!.soundReplacesTTS
                voiceToBeRecordedInStories!!.fromAssets = daModificare[0]!!.fromAssets
                theNewWordsMustBeInsertedBeforeThese = voiceToBeRecordedInStories!!.word
                // delete
                realm.beginTransaction()
                daModificare.deleteAllFromRealm()
                realm.commitTransaction()
                //
                StoriesHelper.renumberAPhraseOfAStory(
                    realm,
                    voiceToBeRecordedInStories!!.story,
                    voiceToBeRecordedInStories!!.phraseNumberInt
                )
                renumberAStory(realm, voiceToBeRecordedInStories!!.story)
            }
        }
        else
        {
            val daInserirePrimaDi = realm.where(Stories::class.java)
                .equalTo(
                    getString(R.string.story),
                    voiceToBeRecordedInStories!!.story
                )
                .equalTo(getString(R.string.phrasenumberint), voiceToBeRecordedInStories!!.phraseNumberInt)
                .equalTo(getString(R.string.wordnumberint), voiceToBeRecordedInStories!!.wordNumberInt)
                .findAll()
            val daInserirePrimaDiSize = daInserirePrimaDi.size
            if (daInserirePrimaDiSize > 0) {
                theNewWordsMustBeInsertedBeforeThese = daInserirePrimaDi[0]!!.word
            }
        }
        //
        val frag = StoriesFragment(R.layout.activity_game_2_ble_word)
        val bundle = Bundle()
        bundle.putString(context.getString(R.string.game_use_video_and_sound), "Y")
//        bundle.putString(context.getString(R.string.story_to_display), voiceToBeRecordedInStories!!.story)
//        bundle.putInt(context.getString(R.string.word_to_display_index), position)
//        bundle.putString(context.getString(R.string.game_use_video_and_sound), "Y")
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag("GameADAViewPagerFragment") as GameADAViewPagerFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgame1hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.settings_container, frag, "StoriesFragment")
        } else {
            ft.add(R.id.settings_container, frag, "StoriesFragment")
        }
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user taps the preview button.
     *
     * @param v view of tapped button
     * @see ToBeRecordedInHistory
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see historyAdd
     *
     * @see fragmentTransactionStart
     */
    override fun saveStories(v: View?) {
        //
//        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
//        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val textWord3 = findViewById<View>(R.id.sentencetoadd) as EditText
        //
        val keywordstorytoadd = textWord3.text.toString()
        //
        val toBeRecordedInHistory = gettoBeRecordedInHistory(realm, keywordstorytoadd)
        // REALM SESSION REGISTRATION
        val voicesToBeRecordedInHistory: MutableList<VoiceToBeRecordedInHistory?> =
            toBeRecordedInHistory.getListVoicesToBeRecordedInHistory()
        //
        val debugUrlNumber = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory()
        //
        historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory)
        val resultsStories = realm.where(Stories::class.java)
            .equalTo(getString(R.string.story), voiceToBeRecordedInStories!!.story)
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize != 0) {
            realm.beginTransaction()
            resultsStories.deleteAllFromRealm()
            realm.commitTransaction()
        }
        // Note that the realm object was generated with the createObject method
        // and not with the new operator.
        // The modification operations will be performed within a Transaction.
        // registro nuova riga per nuova storia
        val createLists1 = prepareData1()
        // Note that the realm object was generated with the createObject method
        // and not with the new operator.
        // The modification operations will be performed within a Transaction.
        realm.beginTransaction()
        val stories = realm.createObject(Stories::class.java)
        // set the fields here
        stories.story = voiceToBeRecordedInStories!!.story
        stories.phraseNumberInt = 1
        stories.wordNumberInt = 0
        stories.word = textWord3.text.toString().lowercase(Locale.getDefault())
        realm.commitTransaction()
        // scorre array list per registrare le singole parole
        var wordNumber = 0
        for (currentGame2ArrayList in createLists1) {
            // Do something with the value
            realm.beginTransaction()
            val storiesFromGame2ArrayList = realm.createObject(Stories::class.java)
            // set the fields here
            storiesFromGame2ArrayList.story =
                voiceToBeRecordedInStories!!.story
            storiesFromGame2ArrayList.phraseNumberInt = 1
            wordNumber++
            storiesFromGame2ArrayList.wordNumberInt = wordNumber
            storiesFromGame2ArrayList.word = currentGame2ArrayList.image_title
            storiesFromGame2ArrayList.uriType = currentGame2ArrayList.urlType
            storiesFromGame2ArrayList.uri = currentGame2ArrayList.url
            realm.commitTransaction()
        }
        //
        renumberAStory(realm, voiceToBeRecordedInStories!!.story)
        /*
        navigate to settings stories improvement activity
        */
//        val intent = Intent(context, SettingsStoriesImprovementActivity::class.java)
//        intent.putExtra(getString(R.string.story), textWord1.text.toString().lowercase(Locale.getDefault()))
//        intent.putExtra(getString(R.string.phrase_number), phraseNumberToPutInTheBundle)
//        startActivity(intent)
        gameADAFragmentTransactionStart()
    }
    /**
     * Called when the user taps the print button.
     *
     *
     * @param v view of tapped button
     * @see .printPhraseCacheImages
     *
     * @see .printPhraseCreateMergedImages
     */
    fun printSentenceOfAStory(v: View?) {
        GraphicsAndPrintingHelper.printPhrase(context, realm, galleryList)
    }
    /**
     * Called when the user taps send sentence button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesImprovementActivity
     */
    fun sendSentenceButton(view: View?) {
        // send message
        if (deviceEnabledName != "non trovato")
        { sendMessage() }
        else
        {
        scanRequestBluetoothDevices()
        }
        //
        fragmentTransactionStart("")
    }
    /**
     * Called when the user taps remove sentence button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesImprovementActivity
     */
    fun deleteTheSentenceButton(view: View?) {
        realm = Realm.getDefaultInstance()
        val daCancellare =
            realm.where(Stories::class.java)
                .equalTo(getString(R.string.story), voiceToBeRecordedInStories!!.story)
                .findAll()
        // delete
        realm.beginTransaction()
        daCancellare.deleteAllFromRealm()
        realm.commitTransaction()
        //
        fragmentTransactionStart("")
    }
    /**
     * Called when the user taps a picture of the story.
     * print or switch to viewpager
     * @param view view of tapped picture
     * @param i int index of tapped picture
     * @param galleryList ArrayList<GameADAArrayList> list for choice of words
     * @see GameADAArrayList
     *
     * @see .getTargetBitmapFromUrlUsingPicasso
     *
     * @see .getTargetBitmapFromFileUsingPicasso
     *
     * @see GameADAViewPagerActivity
     */
    override fun onItemClick(view: View?, i: Int, galleryList: ArrayList<GameADAArrayList>) {
        if (preference_PrintPermissions == "Y") {
            GraphicsAndPrintingHelper.printImage(
                context,
                galleryList[i].urlType,
                galleryList[i].url,
                200,
                200
            )
        } else {
            //
            wordToDisplayInTheStory = i
            gameADAViewPagerFragmentTransactionStart(i)
//            val intent: Intent?
//            intent = Intent(
//                this,
//                SettingsStoriesImprovementViewPagerActivity::class.java
//            )
//            intent.putExtra(getString(R.string.story_to_display), sharedStory)
//            intent.putExtra(getString(R.string.phrase_to_display), phraseToDisplayIndex)
//            intent.putExtra(getString(R.string.word_to_display), i + 1)
//            intent.putExtra(getString(R.string.game_use_video_and_sound), gameUseVideoAndSound)
//            startActivity(intent)
            //
        }
    }
    /**
     * on callback from GameFragment to this Activity
     *
     * @param v view root fragment view
     * @param t TextToSpeech
     * @param gL ArrayList<GameADAArrayList>
    */
    override fun receiveResultGameFragment(
        v: View?,
        t: TextToSpeech?,
        gL: ArrayList<GameADAArrayList>
    ) {
        tTS1 = t
        galleryList = gL
    }
    /**
     * Called when the user taps the image.
     * return to GameADAActivity
     * @param v view of tapped button
     * @see GameADAActivity
     */
    fun onClickGameImage(v: View?) {
        //
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), voiceToBeRecordedInStories!!.story)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            voiceToBeRecordedInStories!!.phraseNumberInt = resultsStories[0]!!.phraseNumberInt
            voiceToBeRecordedInStories!!.wordNumberInt = resultsStories[0]!!.wordNumberInt
            voiceToBeRecordedInStories!!.word = resultsStories[0]!!.word
            voiceToBeRecordedInStories!!.uriType = resultsStories[0]!!.uriType
            voiceToBeRecordedInStories!!.uri = resultsStories[0]!!.uri
        }
//        val intent: Intent
//        intent = Intent(
//            this,
//            SettingsStoriesWordActivity::class.java
//        )
//        intent.putExtra(getString(R.string.story_to_display), sharedStory)
//        intent.putExtra(getString(R.string.phrase_to_display), phraseToDisplay)
//        intent.putExtra(getString(R.string.word_to_display), wordToDisplay)
//        intent.putExtra(getString(R.string.update_mode), getString(R.string.modify))
//        startActivity(intent)
        updateMode = getString(R.string.modify)
        storiesFragmentTransactionStart()
    }
    /**
     * Called when the user taps insert a word before this one button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesWordActivity
     */
    fun insertsAWordBeforeThisOneButton(view: View?) {
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), voiceToBeRecordedInStories!!.story)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory +1)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            voiceToBeRecordedInStories!!.phraseNumberInt = resultsStories[0]!!.phraseNumberInt
            voiceToBeRecordedInStories!!.wordNumberInt = resultsStories[0]!!.wordNumberInt
        }
//        val intent: Intent?
//        intent = Intent(
//            this,
//            SettingsStoriesWordActivity::class.java
//        )
//        intent.putExtra(getString(R.string.story_to_display), voiceToBeRecordedInStories!!.story)
//        intent.putExtra(getString(R.string.phrase_to_display), phraseToDisplay)
//        intent.putExtra(getString(R.string.word_to_display), wordToDisplay)
//        intent.putExtra(getString(R.string.update_mode), getString(R.string.insert))
//        startActivity(intent)
        updateMode =  getString(R.string.insert)
        storiesFragmentTransactionStart()
    }
    /**
     * Called when the user taps insert a word after this button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesRegistrationActivity
     */
    fun insertsAWordAfterThisButton(view: View?) {
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), voiceToBeRecordedInStories!!.story)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory +1)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            voiceToBeRecordedInStories!!.phraseNumberInt = resultsStories[0]!!.phraseNumberInt
            voiceToBeRecordedInStories!!.wordNumberInt = resultsStories[0]!!.wordNumberInt + 1
        }
//        val intent: Intent?
//        intent = Intent(
//            this,
//            SettingsStoriesWordActivity::class.java
//        )
//        intent.putExtra(getString(R.string.story_to_display), voiceToBeRecordedInStories!!.story)
//        intent.putExtra(getString(R.string.phrase_to_display), phraseToDisplay)
//        intent.putExtra(getString(R.string.word_to_display), wordToDisplay + 1)
//        intent.putExtra(getString(R.string.update_mode), getString(R.string.insert))
//        startActivity(intent)
        updateMode =  getString(R.string.insert)
        storiesFragmentTransactionStart()
    }
    /**
     * Called when the user taps remove word button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesImprovementActivity
     */
    fun deleteTheWordButton(view: View?) {
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), voiceToBeRecordedInStories!!.story)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory +1)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            phraseToDisplay = resultsStories[0]!!.phraseNumberInt
            wordToDisplay = resultsStories[0]!!.wordNumberInt
        }
        realm = Realm.getDefaultInstance()
        val daCancellare =
            realm.where(Stories::class.java)
                .equalTo(getString(R.string.story), voiceToBeRecordedInStories!!.story)
                .equalTo(getString(R.string.phrasenumberint), phraseToDisplay)
                .equalTo(getString(R.string.wordnumberint), wordToDisplay)
                .findAll()
        val daCancellareSize = daCancellare.size
        if (daCancellareSize > 0) {
            // find words to delete
            val wordsToDelete = daCancellare[0]!!.word
            //
            val value0 = 0
            val daModificare =
                realm.where(Stories::class.java)
                    .equalTo(getString(R.string.story), voiceToBeRecordedInStories!!.story)
                    .equalTo(getString(R.string.phrasenumberint), phraseToDisplay)
                    .equalTo(getString(R.string.wordnumberint), value0)
                    .findFirst()
            //
            if(daModificare != null) {
                val wordsToChange = daModificare.word
                val beforeTheWordsToBeDeleted = wordsToChange!!.substringBefore(wordsToDelete!!)
                val afterTheWordsToBeDeleted =  wordsToChange.substringAfter(wordsToDelete)
                realm.beginTransaction()
                daModificare.word = "$beforeTheWordsToBeDeleted $afterTheWordsToBeDeleted"
                realm.insertOrUpdate(daModificare)
                realm.commitTransaction()
            }
            // delete
            realm.beginTransaction()
            daCancellare.deleteAllFromRealm()
            realm.commitTransaction()
            //
            StoriesHelper.renumberAPhraseOfAStory(
                realm,
                voiceToBeRecordedInStories!!.story,
                phraseToDisplay
            )
            renumberAStory(realm, voiceToBeRecordedInStories!!.story)
        }
        //
//        val intent: Intent?
//        intent = Intent(
//            this,
//            SettingsStoriesImprovementActivity::class.java
//        )
//        intent.putExtra(getString(R.string.story), sharedStory)
//        intent.putExtra(getString(R.string.phrase_number), phraseToDisplay)
//        startActivity(intent)
        gameADAFragmentTransactionStart()
    }
    /**
     * Called when the user taps the image search button.
     *
     * @param v view of tapped button
     */
    fun imageSearch(v: View) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as TextView
        val textWord4 = findViewById<View>(R.id.wordtoadd) as EditText
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString()
        if (textWord2.text.toString() != "") voiceToBeRecordedInStories!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") voiceToBeRecordedInStories!!.wordNumberInt =
            textWord3.text.toString().toInt()
        voiceToBeRecordedInStories!!.word = textWord4.text.toString()
        //
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = Game2BleImagesFragment(R.layout.activity_settings_stories_images_videos)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    override fun reloadFragmentFromImageSearch(imageKey: String?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                voiceToBeRecordedInStories.uriType = "I"
                voiceToBeRecordedInStories.uri = imageKey
                //
                val frag = StoriesFragment(R.layout.activity_game_2_ble_word)
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }
    /**
     * receives calls from fragment listeners.
     *
     * @param v view of calling fragment
     */
    override fun receiveResultSettings(v: View?) {
        when (v!!.id) {
            R.id.btn_search_arasaac -> {
                arasaacSearch(v)
            }
        }
    }
    /**
     * Called when the user taps the image search Arasaac button from Stories Fragment.
     *
     * @param v view of tapped button
     */
    fun imageSearchArasaac(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as TextView
        val textWord4 = findViewById<View>(R.id.wordtoadd) as EditText
        //
        // view the image search Arasaac  initializing ImageSearchArasaacFragment (FragmentTransaction
        // switch between Fragments).
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString()
        if (textWord2.text.toString() != "") voiceToBeRecordedInStories!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") voiceToBeRecordedInStories!!.wordNumberInt =
            textWord3.text.toString().toInt()
        voiceToBeRecordedInStories!!.word = textWord4.text.toString()
        //
        val frag = ImageSearchArasaacFragment(R.layout.activity_settings_stories_arasaac)
        //
        val bundle = Bundle()
        bundle.putString("keywordToSearchArasaac", textWord4.text.toString())
        frag.arguments = bundle
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user taps the image search Arasaac button from StoriesImagesSearchArasaac Fragment.
     *
     * @param v view of tapped button
     */
    fun arasaacSearch(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordarasaactosearch) as EditText
        // view the image search Arasaac  initializing ImageSearchArasaacFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ImageSearchArasaacFragment(R.layout.activity_settings_stories_arasaac)
        //
        val bundle = Bundle()
        bundle.putString("keywordToSearchArasaac", textWord1.text.toString())
        frag.arguments = bundle
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user taps the image Arasaac .
     *
     * @param view view of the image Arasaac
     * @param word string whit the keyword of the image Arasaac
     * @param url string whit the url of the image Arasaac
     */
    override fun onItemClick(view: View?, word: String?, url: String?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                voiceToBeRecordedInStories.uriType = "A"
                voiceToBeRecordedInStories.uri = url
                //
                val frag = StoriesFragment(R.layout.activity_game_2_ble_word)
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }
    /**
     * Called when the user taps the add button from stories settings.
     *
     * after the checks it adds the piece of story on realm
     *
     * and the activity is notified to view the stories settings.
     *
     * @param v view of tapped button
     * @see StoriesFragment
     *
     * @see Stories
     */
    fun storyToAdd(v: View?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                voiceToBeRecordedInStories.story = getString(R.string.nome_storia)
                voiceToBeRecordedInStories.phraseNumberInt = 0
                //
                realm = Realm.getDefaultInstance()
                //
                val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
                val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
                val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as TextView
                val textWord4 = findViewById<View>(R.id.wordtoadd) as EditText
                if (textWord1.text.toString() != "" && textWord2.text.toString() != ""
                ) //                  && (voiceToBeRecordedInStories.getUriType() != null) && (voiceToBeRecordedInStories.getUri() != null))
                {
                    if (textWord3.text.toString() == "0"
                        || (textWord3.text.toString() != "0"
                                && voiceToBeRecordedInStories.uriType != null && voiceToBeRecordedInStories.uri != null)
                    ) {
                        // cancello frase vecchia
                        // se textWord3 non è vuoto
                        // copio la frase vecchia fino a textWord3
                        // inserisco la nuova parola
                        // copio il resto della frase vecchia e riordino
                        // se textWord3 è vuoto
                        // copio la frase vecchia
                        // inserisco la nuova parola alla fine e riordino
                        //
                        // clear the table
                        // cancello frase vecchia dopo averne salvato copia
                        val resultsStories = realm.where(Stories::class.java)
                            .equalTo(
                                getString(R.string.story),
                                textWord1.text.toString()
                            )
                            .equalTo(getString(R.string.phrasenumberint), textWord2.text.toString().toInt())
                            .findAll()
                        val storiesSize = resultsStories.size
                        //
                        val resultsStoriesList = realm.copyFromRealm(resultsStories)
                        //
                        Collections.sort(resultsStoriesList, StoriesComparator())
                        //
                        realm.beginTransaction()
                        resultsStories.deleteAllFromRealm()
                        realm.commitTransaction()
                        //
                        var currentWordNumber = 0
                        if (textWord3.text.toString() != "") {
                            // copio la frase vecchia fino a textWord3
                            var irrh = 0
                            while (irrh < storiesSize) {
                                val resultStories = resultsStoriesList[irrh]!!
                                currentWordNumber = resultStories.wordNumberInt
                                if (currentWordNumber < textWord3.text.toString().toInt()) {
                                    realm.beginTransaction()
                                    realm.copyToRealm(resultStories)
                                    realm.commitTransaction()
                                } else {
                                    break
                                }
                                irrh++
                            }
                            // inserisco la nuova parola
                            // registro nuova parola
                            // Note that the realm object was generated with the createObject method
                            // and not with the new operator.
                            // The modification operations will be performed within a Transaction.
                            //
                            realm.beginTransaction()
                            val stories = realm.createObject(Stories::class.java)
                            // set the fields here
                            stories.story = textWord1.text.toString()
                            stories.phraseNumberInt = textWord2.text.toString().toInt()
                            stories.wordNumberInt = textWord3.text.toString().toInt()
                            stories.word = textWord4.text.toString()
                            stories.uriType = voiceToBeRecordedInStories.uriType
                            stories.uri = voiceToBeRecordedInStories.uri
                            stories.answerActionType = voiceToBeRecordedInStories.answerActionType
                            stories.answerAction = voiceToBeRecordedInStories.answerAction
                            stories.video = voiceToBeRecordedInStories.video
                            stories.sound = voiceToBeRecordedInStories.sound
                            stories.soundReplacesTTS = voiceToBeRecordedInStories.soundReplacesTTS
                            stories.fromAssets = ""
                            realm.commitTransaction()
                            // copio la frase vecchia
                            while (irrh < storiesSize) {
                                val resultStories = resultsStoriesList[irrh]!!
                                realm.beginTransaction()
                                realm.copyToRealm(resultStories)
                                realm.commitTransaction()
                                irrh++
                            }
                        } else {
                            // copio la frase vecchia
                            var irrh = 0
                            while (irrh < storiesSize) {
                                val resultStories = resultsStoriesList[irrh]!!
                                currentWordNumber = resultStories.wordNumberInt
                                //
                                realm.beginTransaction()
                                realm.copyToRealm(resultStories)
                                realm.commitTransaction()
                                //
                                irrh++
                            }
                            // inserisco la nuova parola
                            // registro nuova parola
                            // Note that the realm object was generated with the createObject method
                            // and not with the new operator.
                            // The modification operations will be performed within a Transaction.
                            //
                            realm.beginTransaction()
                            val stories = realm.createObject(Stories::class.java)
                            // set the fields here
                            stories.story = textWord1.text.toString()
                            stories.phraseNumberInt = textWord2.text.toString().toInt()
                            stories.wordNumberInt = ++currentWordNumber
                            stories.word = textWord4.text.toString()
                            stories.uriType = voiceToBeRecordedInStories.uriType
                            stories.uri = voiceToBeRecordedInStories.uri
                            stories.answerActionType = voiceToBeRecordedInStories.answerActionType
                            stories.answerAction = voiceToBeRecordedInStories.answerAction
                            stories.video = voiceToBeRecordedInStories.video
                            stories.sound = voiceToBeRecordedInStories.sound
                            stories.soundReplacesTTS = voiceToBeRecordedInStories.soundReplacesTTS
                            stories.fromAssets = ""
                            realm.commitTransaction()
                        }
                        // riordino
                        StoriesHelper.renumberAPhraseOfAStory(
                            realm,
                            textWord1.text.toString(),
                            textWord2.text.toString().toInt()
                        )
                        renumberAStory(
                            realm,
                            textWord1.text.toString()
                        )
                        //
                        val value0 = 0
                        val daModificare =
                            realm.where(Stories::class.java)
                                .equalTo(getString(R.string.story), textWord1.text.toString())
                                .equalTo(getString(R.string.phrasenumberint), textWord2.text.toString().toInt())
                                .equalTo(getString(R.string.wordnumberint), value0)
                                .findFirst()
                        if(daModificare != null) {
                            val wordsToChange = daModificare.word
                            val wordsToBeInserted = textWord4.text.toString()
                            if (theNewWordsMustBeInsertedBeforeThese!! == "")
                            {
                                // the new words are at the end of the sentence
                                realm.beginTransaction()
//                                if (updateMode == getString(R.string.modify))
//                                {
//                                    daModificare.word = "$wordsToChange $wordsToBeInserted"
//                                }
//                                else
//                                {
                                daModificare.word = "$wordsToChange $wordsToBeInserted"
//                                }
                                realm.insertOrUpdate(daModificare)
                                realm.commitTransaction()
                            }
                            else
                            {
                                val beforeTheWordsToBeInserted = wordsToChange!!.substringBefore(
                                    theNewWordsMustBeInsertedBeforeThese!!
                                )

                                val afterTheWordsToBeInserted =  wordsToChange.substringAfter(
                                    theNewWordsMustBeInsertedBeforeThese!!
                                )
                                realm.beginTransaction()
                                if (updateMode == getString(R.string.modify))
                                {
                                    daModificare.word = "$beforeTheWordsToBeInserted $wordsToBeInserted $afterTheWordsToBeInserted"
                                }
                                else
                                {
                                    daModificare.word = "$beforeTheWordsToBeInserted $wordsToBeInserted $theNewWordsMustBeInsertedBeforeThese $afterTheWordsToBeInserted"
                                }
                                realm.insertOrUpdate(daModificare)
                                realm.commitTransaction()
                            }
                        }
                        // clear fields of viewmodel data class
                        voiceToBeRecordedInStories.story =
                            textWord1.text.toString()
                        voiceToBeRecordedInStories.phraseNumberInt =
                            textWord2.text.toString().toInt()
                        clearFieldsOfViewmodelDataClass()
                    }
                    //
//                    val gameUseVideoAndSound = "N"
//                    val intent: Intent?
//                    intent = Intent(
//                        this,
//                        SettingsStoriesImprovementActivity::class.java
//                    )
//                    intent.putExtra(getString(R.string.story), textWord1.text.toString().lowercase(Locale.getDefault()))
//                    intent.putExtra(getString(R.string.phrase_number), textWord2.text.toString().toInt())
//                    startActivity(intent)
                    StoriesHelper.renumberAPhraseOfAStory(
                        realm,
                        voiceToBeRecordedInStories.story,
                        1
                    )
                    renumberAStory(realm, voiceToBeRecordedInStories.story)
                    gameADAFragmentTransactionStart()
                }
            }
    }

    /**
     * clear fields of viewmodel data class
     *
     *
     * @see VoiceToBeRecordedInStories
     */
    fun clearFieldsOfViewmodelDataClass() {
        voiceToBeRecordedInStories!!.wordNumberInt = 0
        voiceToBeRecordedInStories!!.word = ""
        voiceToBeRecordedInStories!!.uriType = ""
        voiceToBeRecordedInStories!!.uri = ""
        voiceToBeRecordedInStories!!.answerActionType = ""
        voiceToBeRecordedInStories!!.answerAction = ""
        voiceToBeRecordedInStories!!.video = ""
        voiceToBeRecordedInStories!!.sound = ""
        voiceToBeRecordedInStories!!.soundReplacesTTS = ""
    }
    companion object {
        //
        private const val TAG = "Game2BleActivity"
    }

}