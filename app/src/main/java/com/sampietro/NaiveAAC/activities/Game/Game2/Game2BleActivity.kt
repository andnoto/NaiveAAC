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
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithListenerWithoutConstructor
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClassWithRecognizerCallback
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothDevices
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothLeService
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAActivity
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAArrayList
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAOnFragmentEventListener
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADARecyclerViewAdapterInterface
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAViewPagerFragment
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAViewPagerMediaContainerOnClickListener
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAViewPagerOnFragmentEventListener
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAViewPagerOnFragmentSoundMediaPlayerListener
import com.sampietro.NaiveAAC.activities.Game.GameADA.SettingsStoriesImprovementViewPagerFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.printImage
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.printPhrase
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacFragment
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacRecyclerViewAdapterInterface
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.StoriesComparator
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAPhraseOfAStory
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import io.realm.Realm
import java.util.*
import kotlin.collections.ArrayList

/**
 * <h1>Game2BleActivity</h1>
 *
 * **SettingsStoriesRegistrationActivity** displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 * * Step 1
 * the BLE app provides an activity (Game2BleActivity) to connect to Bluetooth device.
 * Based on user input, this activity communicates with a Service called BluetoothLeService,
 * which interacts with the BLE device via the BLE API.
 * The communication is performed using a bound service which allows the activity to connect to the
 * BluetoothLeService and call functions to connect to the devices.
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClassWithRecognizerCallback
 * @see Game2ActivityAbstractClass
 * @see SettingsStoriesRegistrationActivityAbstractClass
 * @see SettingsStoriesRegistrationFragment
 * @see Game2BleGameADAFragment
 * @see GameADAViewPagerFragment
 * @see Game2BleStoriesFragment
 * @see BluetoothLeService
 */
class Game2BleActivity : SettingsStoriesRegistrationActivityAbstractClass(),
    FragmentAbstractClassWithListenerWithoutConstructor.onBaseFragmentEventListenerSettings,
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
                if (preference_BluetoothMode != "Client")
                {
                    // mode = GATT server
                    /*
                    BluetootLeService -> step 7 -> BluetootLeService
                    */
                    bluetooth.setupServer()
                    bluetooth.startAdvertising()
                }
                else
                {
                    // mode = GATT client
                    /*
                    step 10 -> BluetootLeService
                     */
                    // perform device scanning
                    bluetooth.scan()
                }
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
//    var deviceEnabledUserName : String? = "non trovato"
    var deviceEnabledName : String? = "non trovato"
    var messageFromGattServer = "nessun messaggio"
    var dialog: Game2BleDialogFragment? = null
    //
    var preference_PrintPermissions: String? = null
    //
    var preference_BluetoothMode: String? = null
    //
//    private var bluetoothStatus: BluetoothStatus? = null
//    private lateinit var viewModel: Game2ViewModel
    //
    lateinit var galleryList: ArrayList<GameADAArrayList>
    //
    private var game2ViewModelItem: Game2ViewModelItem? = null
//    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel
    //
//    var phraseToDisplay = 0
//    var wordToDisplay = 0
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
//        AndroidPermission.checkPermission(this)
        //
        SpeechRecognizerManagement.prepareSpeechRecognizer(this)
        //
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .add(ActionbarFragment(), getString(R.string.actionbar_fragment)).commit()
//        }
        //
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
        */
        // In the Activity#onCreate make the only setItem
//        bluetoothStatus = BluetoothStatus()
//        voiceToBeRecordedInStories = VoiceToBeRecordedInStories()
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
        //
        preference_BluetoothMode =
            sharedPref.getString(getString(R.string.preference_bluetoothmode), "DEFAULT")
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
        game2ViewModelItem = Game2ViewModelItem()
//        viewModel = ViewModelProvider(this).get(
//            Game2ViewModel::class.java
//        )
//        viewModel.setItem(game2ViewModelItem!!)
        game2ViewModelItem!!.story = "Image exchange via Bluetooth"
        game2ViewModelItem!!.phraseNumberInt = 1
        clearFieldsOfViewmodelDataClass()
        //
        //
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            game2ViewModelItem!!.story = savedInstanceState.getString("STORY", "")
            game2ViewModelItem!!.phraseNumberInt = savedInstanceState.getInt("PHRASE NUMBER", 0)
            game2ViewModelItem!!.wordNumberInt = savedInstanceState.getInt("WORD NUMBER", 0)
            game2ViewModelItem!!.word = savedInstanceState.getString("WORD", "")
            game2ViewModelItem!!.uriType = savedInstanceState.getString("URI TYPE", "")
            game2ViewModelItem!!.uri = savedInstanceState.getString("URI", "")
            game2ViewModelItem!!.answerActionType = savedInstanceState.getString("ANSWER ACTION TYPE", "")
            game2ViewModelItem!!.answerAction = savedInstanceState.getString("ANSWER ACTION", "")
            game2ViewModelItem!!.video = savedInstanceState.getString("VIDEO", "")
            game2ViewModelItem!!.sound = savedInstanceState.getString("SOUND", "")
            game2ViewModelItem!!.soundReplacesTTS = savedInstanceState.getString("SOUND REPLACE TTS", "")
            //
            wordToDisplayInTheStory = savedInstanceState.getInt("WORD TO DISPLAY IN THE STORY", 0)
            updateMode = savedInstanceState.getString("UPDATE MODE", "")
            theNewWordsMustBeInsertedBeforeThese = savedInstanceState.getString("THE NEW WORDS MUST BE INSERTED BEFORE THESE", "")
        }
        else
        {
        supportFragmentManager.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment)).commit()
        fragmentTransactionStart("")
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter(), RECEIVER_NOT_EXPORTED)
            registerReceiver(bondStateReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED), RECEIVER_NOT_EXPORTED)
        }
        else
        {
            registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
            registerReceiver(bondStateReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
        }
        //
        // check Bluetooth Ble Permissions
        //
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            // version code TIRAMISU = version 33 = Android 13
            && (
                    (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED)
                            ||
                    (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE
                    ) != PackageManager.PERMISSION_GRANTED)
                            ||
                            (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED)
                            ||
                            (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN
                            ) != PackageManager.PERMISSION_GRANTED)
                            ||
                            (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED)
                    ))
        {
            // se siamo qui è perchè non si è mostrata alcuna spiegazione all'utente, richiesta di permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                1
            )
        }
        else
        {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                // version code S = version 31 = Android 12
                && (
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED)
                                ||
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
                    arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN ),
                    2
                )
            }
            else
            {
                if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                    // version code S = version 31 = Android 12
                    && (
                            (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED)
                                    ||
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
                        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION ),
                        3
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
                            if (preference_BluetoothMode != "Client")
                            {
                                /*
                                BluetootLeService -> step 7 -> BluetootLeService
                                */
                                bluetoothService!!.setupServer()
                                bluetoothService!!.startAdvertising()
                            }
                            else
                            {
                                /*
                                step 10 -> BluetootLeService
                                */
                                // perform device scanning
                                bluetoothService!!.scan()
                            }
                            bluetoothService!!.activityInActiveState("Game2BleActivity")
                        }
                    }
                }
            }
        }

    }
    //
    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_SERVER_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_SERVER_DISCONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            addAction(BluetoothLeService.MESSAGE_FROM_GATT_SERVER)
            addAction(BluetoothLeService.MESSAGE_FROM_GATT)
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
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
                && grantResults[4] == PackageManager.PERMISSION_GRANTED
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
                        if (preference_BluetoothMode != "Client")
                        {
                            /*
                            BluetootLeService -> step 7 -> BluetootLeService
                            */
                            bluetoothService!!.setupServer()
                            bluetoothService!!.startAdvertising()
                        }
                        else
                        {
                            /*
                            step 10 -> BluetootLeService
                            */
                            // perform device scanning
                            bluetoothService!!.scan()
                        }
                        bluetoothService!!.activityInActiveState("Game2BleActivity")
                    }
                }
            }
            else
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
        if (requestCode == 2) {
            //
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
            ) {
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
                        if (preference_BluetoothMode != "Client")
                        {
                            /*
                            BluetootLeService -> step 7 -> BluetootLeService
                            */
                            bluetoothService!!.setupServer()
                            bluetoothService!!.startAdvertising()
                        }
                        else
                        {
                            /*
                            step 10 -> BluetootLeService
                            */
                            // perform device scanning
                            bluetoothService!!.scan()
                        }
                        bluetoothService!!.activityInActiveState("Game2BleActivity")
                    }
                }
            }
            else
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                    2
                )
            }
        }
        if (requestCode == 3) {
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
                        if (preference_BluetoothMode != "Client")
                        {
                            /*
                            BluetootLeService -> step 7 -> BluetootLeService
                            */
                            bluetoothService!!.setupServer()
                            bluetoothService!!.startAdvertising()
                        }
                        else
                        {
                            /*
                            step 10 -> BluetootLeService
                            */
                            // perform device scanning
                            bluetoothService!!.scan()
                        }
                        bluetoothService!!.activityInActiveState("Game2BleActivity")
                    }
                }
                //
            }
            else
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH,
                        Manifest.permission.ACCESS_FINE_LOCATION ),
                    3
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    var enableBtResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        else
        {
            finish()
        }
    }
    /*
    BluetootLeService -> step 18 -> BluetootLeService
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
                BluetoothLeService.ACTION_GATT_SERVER_CONNECTED -> {
                    mConnected = true
                    // if MultipleAdvertisement is not supported the device name is not available
                    if (bluetoothService!!.getDeviceEnabledName() != null)
                    { deviceEnabledName = bluetoothService!!.getDeviceEnabledName() }
                    else
                    { deviceEnabledName = "manca device name" }
                    //
                    enableTransmitToConnectedBluetooth()
                    val deviceenabledusername = findViewById<View>(R.id.deviceenabledusername) as TextView
                    deviceenabledusername.setText(game2ViewModelItem!!.deviceEnabledUserName)
                }
                BluetoothLeService.ACTION_GATT_SERVER_DISCONNECTED -> {
                    mConnected = false
                    disableTransmitToDisconnectedBluetooth()
                    val deviceenabledusername = findViewById<View>(R.id.deviceenabledusername) as TextView
                    deviceenabledusername.setText(game2ViewModelItem!!.deviceEnabledUserName)
                }
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    mConnected = true
                    // if MultipleAdvertisement is not supported the device name is not available
                    if (bluetoothService!!.getDeviceEnabledName() != null)
                    { deviceEnabledName = bluetoothService!!.getDeviceEnabledName() }
                    else
                    { deviceEnabledName = "manca device name" }
                    //
                    enableTransmitToConnectedBluetooth()
                    val deviceenabledusername = findViewById<View>(R.id.deviceenabledusername) as TextView
                    deviceenabledusername.setText(game2ViewModelItem!!.deviceEnabledUserName)
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    mConnected = false
                    disableTransmitToDisconnectedBluetooth()
                    val deviceenabledusername = findViewById<View>(R.id.deviceenabledusername) as TextView
                    deviceenabledusername.setText(game2ViewModelItem!!.deviceEnabledUserName)
                }
                BluetoothLeService.MESSAGE_FROM_GATT_SERVER -> {
                    messageFromGattServer = bluetoothService!!.getMessageFromGattServer()
                    if (messageFromGattServer == "I'M DISCONNECTING")
                    {
                    }
                    else
                    {
                        dialogFragmentShow()
                    }
                }
                BluetoothLeService.MESSAGE_FROM_GATT -> {
                    messageFromGattServer = bluetoothService!!.getMessageFromGatt()
                    if (messageFromGattServer == "I'M DISCONNECTING")
                    {
                        mConnected = false
                        disableTransmitToDisconnectedBluetooth()
                    }
                    else
                    {
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
        if (preference_BluetoothMode != "Client")
        {
            if (bluetoothService != null)
            {
//                bluetoothService!!.sendMessageFromServer("I'M DISCONNECTING")
                bluetoothService!!.stopAdvertising()
            }
        }
        else
        {
            if (bluetoothService != null)
            {
//                bluetoothService!!.sendMessageFromClient("I'M DISCONNECTING")
                bluetoothService!!.stopScanner()
            }
        }
        if (bluetoothService != null)
        {
            bluetoothService!!.activityInPausedState("Game2BleActivity")
        }
        //
        unregisterReceiver(gattUpdateReceiver)
        unregisterReceiver(bondStateReceiver)
        //
        val Game2BleDialogFragmentgotinstance =
            supportFragmentManager.findFragmentByTag("GAME_DIALOG") as Game2BleDialogFragment?
        if (Game2BleDialogFragmentgotinstance != null) {
            dialog!!.dismiss()
        }
    }
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     * it stores the image video or sound file path
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString("STORY", game2ViewModelItem!!.story)
        savedInstanceState.putInt("PHRASE NUMBER", game2ViewModelItem!!.phraseNumberInt)
        savedInstanceState.putInt("WORD NUMBER", game2ViewModelItem!!.wordNumberInt)
        savedInstanceState.putString("WORD", game2ViewModelItem!!.word)
        savedInstanceState.putString("URI TYPE", game2ViewModelItem!!.uriType)
        savedInstanceState.putString("URI", game2ViewModelItem!!.uri)
        savedInstanceState.putString("ANSWER ACTION TYPE", game2ViewModelItem!!.answerActionType)
        savedInstanceState.putString("ANSWER ACTION", game2ViewModelItem!!.answerAction)
        savedInstanceState.putString("VIDEO", game2ViewModelItem!!.video)
        savedInstanceState.putString("SOUND", game2ViewModelItem!!.sound)
        savedInstanceState.putString("SOUND REPLACE TTS", game2ViewModelItem!!.soundReplacesTTS)
        //
        savedInstanceState.putInt("WORD TO DISPLAY IN THE STORY", wordToDisplayInTheStory)
        savedInstanceState.putString("UPDATE MODE", updateMode)
        savedInstanceState.putString("THE NEW WORDS MUST BE INSERTED BEFORE THESE", theNewWordsMustBeInsertedBeforeThese)
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }
    /**
     * destroy SpeechRecognizer, TTS shutdown and more
     *
     * @see androidx.fragment.app.Fragment.onDestroy
     */
    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        if (preference_BluetoothMode != "Client")
        {
//            bluetoothService!!.disconnectFromGattServer()
            bluetoothService!!.sendMessageFromServer("I'M DISCONNECTING")
//            bluetoothService!!.stopServer()
        }
        else
        {
            bluetoothService!!.disconnect()
        }
        //
        SpeechRecognizerManagement.destroyRecognizer()
        // TTS
        if (tTS1 != null) {
            tTS1!!.stop()
            tTS1!!.shutdown()
        }
        super.onDestroy()
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
        if (preference_BluetoothMode != "Client")
        {
            // Delete last character in String
            bluetoothService!!.sendMessageFromServer(messageToSend.substring(0, messageToSend.length - 1))
        }
        else
        {
            // Delete last character in String
            bluetoothService!!.sendMessageFromClient(messageToSend.substring(0, messageToSend.length - 1))
        }
    }
    /**
     * enable transmit to connected Bluetooth .
     *
     */
    @SuppressLint("MissingPermission")
    fun enableTransmitToConnectedBluetooth() {
//        bluetoothStatus!!.deviceEnabledUserName = "non trovato"
        game2ViewModelItem!!.deviceEnabledUserName = "non trovato"
        val results = realm.where(
            BluetoothDevices::class.java
        ).equalTo("deviceName", deviceEnabledName).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
//                bluetoothStatus!!.deviceEnabledUserName = result.deviceUserName!!
                game2ViewModelItem!!.deviceEnabledUserName = result.deviceUserName!!
            }
        }
    }
    /**
     * disable transmit to disconnected Bluetooth .
     *
     */
    @SuppressLint("MissingPermission")
    fun disableTransmitToDisconnectedBluetooth() {
        deviceEnabledName = "non trovato"
//        bluetoothStatus!!.deviceEnabledUserName = "non trovato"
        game2ViewModelItem!!.deviceEnabledUserName = "non trovato"
        //
        bluetoothService!!.clearServiceVariables()
        if (preference_BluetoothMode != "Client")
        {
            /*
            BluetootLeService -> step 7 -> BluetootLeService
            */
            bluetoothService!!.startAdvertising()
        }
        else
        {
            bluetoothService!!.disconnect()
            /*
            step 10 -> BluetootLeService
            */
            // perform device scanning
            bluetoothService!!.scan()
        }
        bluetoothService!!.activityInActiveState("Game2BleActivity")
    }
    /**
     * scan request Bluetooth Devices .
     *
     * @see BluetoothLeService.scan
     */
    fun scanRequestBluetoothDevices() {
        bluetoothService!!.scan()
    }
    /**
     * Show Game2BleDialogFragment .
     *
     * @see Game2BleDialogFragment
     */
    fun dialogFragmentShow() {
//        dialog = Game2BleDialogFragment.newInstance(deviceEnabledUserName!!, messageFromGattServer )
        dialog = Game2BleDialogFragment.newInstance(game2ViewModelItem!!.deviceEnabledUserName!!, messageFromGattServer )
        dialog!!.show(supportFragmentManager, "GAME_DIALOG")
    }
    /**
     * Called when the user taps the Game2BleDialogFragment buttons.
     *
     * @param v view of tapped button
     * @see Game2BleDialogFragment
     */
    override fun receiveResultOnClickFromGame2DialogFragment(v: View?) {
        when (v!!.id) {
            R.id.dialog_sendtobluetoothimage -> {
                // TTS
                tTS1 = TextToSpeech(context) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tTS1!!.speak(
//                            bluetoothStatus!!.deviceEnabledUserName,
                            game2ViewModelItem!!.deviceEnabledUserName,
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
     * Callback from Game2BleDialogFragment
     *
     * @see Game2BleDialogFragment
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
     *
     * @param v view of tapped button
     */
    fun continueGameButton(v: View?) {
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
            supportFragmentManager.findFragmentByTag("Game2BleStoriesRegistrationFragment") as Game2BleStoriesRegistrationFragment?
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
        val frag = Game2BleStoriesRegistrationFragment()
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
        bundle.putString(getString(R.string.etext), eText)
        //
        bundle.putString("STORY", game2ViewModelItem!!.story)
        bundle.putInt("PHRASE NUMBER", game2ViewModelItem!!.phraseNumberInt)
        //
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag("Game2BleStoriesRegistrationFragment") as Game2BleStoriesRegistrationFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game_fragment_hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.settings_container, frag, "Game2BleStoriesRegistrationFragment")
        } else {
            ft.add(R.id.settings_container, frag, "Game2BleStoriesRegistrationFragment")
        }
        ft.addToBackStack(null)
        ft.commitAllowingStateLoss()
    }
    /**
     * initiate Fragment transaction.
     *
     * @see Game2BleGameADAFragment
     */
    fun gameADAFragmentTransactionStart() {
        //
        val frag = Game2BleGameADAFragment()
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
        bundle.putInt(getString(R.string.word_to_display_index), 0)
        bundle.putBoolean(getString(R.string.tts_enabled), true)
        bundle.putString(getString(R.string.game_use_video_and_sound), "Y")
        //
        bundle.putString(getString(R.string.keywordstorytoadd), game2ViewModelItem!!.story)
        bundle.putString(getString(R.string.phrasenumbertoadd),
            game2ViewModelItem!!.phraseNumberInt.toString()
        )
        //
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as Game2BleGameADAFragment?
        val settingsstoriesregistrationfragmentgotinstance =
            supportFragmentManager.findFragmentByTag("Game2BleStoriesRegistrationFragment") as Game2BleStoriesRegistrationFragment?
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
     * @see SettingsStoriesImprovementViewPagerFragment
     */
    fun gameADAViewPagerFragmentTransactionStart(position: Int) {
//        val frag = GameADAViewPagerFragment(R.layout.activity_settings_stories_improvement_viewpager_content)
        val frag = SettingsStoriesImprovementViewPagerFragment()
        val bundle = Bundle()
        bundle.putString(context.getString(R.string.story_to_display), game2ViewModelItem!!.story)
        bundle.putInt(context.getString(R.string.word_to_display_index), position)
        bundle.putString(context.getString(R.string.game_use_video_and_sound), "Y")
        //
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag("SettingsStoriesImprovementViewPagerFragment") as SettingsStoriesImprovementViewPagerFragment?
        val gameADAfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as Game2BleGameADAFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgame1hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || gameADAfragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.settings_container, frag, "SettingsStoriesImprovementViewPagerFragment")
        } else {
            ft.add(R.id.settings_container, frag, "SettingsStoriesImprovementViewPagerFragment")
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
     * @see Game2BleStoriesFragment
     */
    fun storiesFragmentTransactionStart() {
        if (updateMode == getString(R.string.modify))
        {
            val daModificare = realm.where(Stories::class.java)
                .equalTo(
                    getString(R.string.story),
                    game2ViewModelItem!!.story
                )
                .equalTo(getString(R.string.phrasenumberint), game2ViewModelItem!!.phraseNumberInt)
                .equalTo(getString(R.string.wordnumberint), game2ViewModelItem!!.wordNumberInt)
                .findAll()
            val daModificareSize = daModificare.size
            if (daModificareSize > 0) {
                game2ViewModelItem!!.word = daModificare[0]!!.word
                game2ViewModelItem!!.uriType = daModificare[0]!!.uriType
                game2ViewModelItem!!.uri = daModificare[0]!!.uri
                game2ViewModelItem!!.answerActionType = daModificare[0]!!.answerActionType
                game2ViewModelItem!!.answerAction = daModificare[0]!!.answerAction
                game2ViewModelItem!!.video = daModificare[0]!!.video
                game2ViewModelItem!!.sound = daModificare[0]!!.sound
                game2ViewModelItem!!.soundReplacesTTS = daModificare[0]!!.soundReplacesTTS
                game2ViewModelItem!!.fromAssets = daModificare[0]!!.fromAssets
                theNewWordsMustBeInsertedBeforeThese = game2ViewModelItem!!.word
            }
        }
        else
        {
            val daInserirePrimaDi = realm.where(Stories::class.java)
                .equalTo(
                    getString(R.string.story),
                    game2ViewModelItem!!.story
                )
                .equalTo(getString(R.string.phrasenumberint), game2ViewModelItem!!.phraseNumberInt)
                .equalTo(getString(R.string.wordnumberint), game2ViewModelItem!!.wordNumberInt)
                .findAll()
            val daInserirePrimaDiSize = daInserirePrimaDi.size
            if (daInserirePrimaDiSize > 0) {
                theNewWordsMustBeInsertedBeforeThese = daInserirePrimaDi[0]!!.word
            }
        }
        //
        val frag = Game2BleStoriesFragment()
        val bundle = Bundle()
//        bundle.putString(context.getString(R.string.game_use_video_and_sound), "Y")
        bundle.putString("STORY", game2ViewModelItem!!.story)
        bundle.putInt("PHRASE NUMBER", game2ViewModelItem!!.phraseNumberInt)
        bundle.putInt("WORD NUMBER", game2ViewModelItem!!.wordNumberInt)
        bundle.putString("WORD", game2ViewModelItem!!.word)
        bundle.putString("URI TYPE", game2ViewModelItem!!.uriType)
        bundle.putString("URI", game2ViewModelItem!!.uri)
        //
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag("SettingsStoriesImprovementViewPagerFragment") as SettingsStoriesImprovementViewPagerFragment?
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
            .equalTo(getString(R.string.story), game2ViewModelItem!!.story)
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
        stories.story = game2ViewModelItem!!.story
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
                game2ViewModelItem!!.story
            storiesFromGame2ArrayList.phraseNumberInt = 1
            wordNumber++
            storiesFromGame2ArrayList.wordNumberInt = wordNumber
            storiesFromGame2ArrayList.word = currentGame2ArrayList.image_title
            storiesFromGame2ArrayList.uriType = currentGame2ArrayList.urlType
            storiesFromGame2ArrayList.uri = currentGame2ArrayList.url
            realm.commitTransaction()
        }
        //
        renumberAStory(realm, game2ViewModelItem!!.story)
        //
        gameADAFragmentTransactionStart()
    }
    /**
     * Called when the user taps the print button.
     *
     *
     * @param v view of tapped button
     * @see printPhrase
     */
    fun printSentenceOfAStory(v: View?) {
        printPhrase(context, realm, galleryList)
    }
    /**
     * Called when the user taps send sentence button.
     *
     * @param view view of tapped picture
     *
     * @see sendMessage
     * @see fragmentTransactionStart
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
     * @see fragmentTransactionStart
     */
    fun deleteTheSentenceButton(view: View?) {
        realm = Realm.getDefaultInstance()
        val daCancellare =
            realm.where(Stories::class.java)
                .equalTo(getString(R.string.story), game2ViewModelItem!!.story)
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
     * @see printImage
     * @see gameADAViewPagerFragmentTransactionStart
     */
    override fun onItemClick(view: View?, i: Int, galleryList: ArrayList<GameADAArrayList>) {
        if (preference_PrintPermissions == "Y") {
            printImage(
                context,
                galleryList[i].urlType,
                galleryList[i].url,
                200,
                200
            )
        } else {
            //
            wordToDisplayInTheStory = i
            gameADAViewPagerFragmentTransactionStart(wordToDisplayInTheStory)
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
     * Called when the user taps the go back button.
     * return to Game2BleGameADAFragment
     * @param v view of tapped button
     * @see GameADAActivity
     */
    fun onClickGoBackFromImprovementViewPager(v: View?) {
        gameADAFragmentTransactionStart()
    }
    /**
     * Called when the user taps the image.
     * @param v view of tapped button
     * @see storiesFragmentTransactionStart
     */
    fun onClickGameImage(v: View?) {
        //
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), game2ViewModelItem!!.story)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory + 1)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            game2ViewModelItem!!.phraseNumberInt = resultsStories[0]!!.phraseNumberInt
            game2ViewModelItem!!.wordNumberInt = resultsStories[0]!!.wordNumberInt
            game2ViewModelItem!!.word = resultsStories[0]!!.word
            game2ViewModelItem!!.uriType = resultsStories[0]!!.uriType
            game2ViewModelItem!!.uri = resultsStories[0]!!.uri
        }
        //
        updateMode = getString(R.string.modify)
        storiesFragmentTransactionStart()
    }
    /**
     * Called when the user taps insert a word before this one button.
     *
     * @param view view of tapped picture
     *
     * @see storiesFragmentTransactionStart
     */
    fun insertsAWordBeforeThisOneButton(view: View?) {
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), game2ViewModelItem!!.story)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory +1)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            game2ViewModelItem!!.phraseNumberInt = resultsStories[0]!!.phraseNumberInt
            game2ViewModelItem!!.wordNumberInt = resultsStories[0]!!.wordNumberInt
        }
        //
        updateMode =  getString(R.string.insert)
        storiesFragmentTransactionStart()
    }
    /**
     * Called when the user taps insert a word after this button.
     *
     * @param view view of tapped picture
     *
     * @see storiesFragmentTransactionStart
     */
    fun insertsAWordAfterThisButton(view: View?) {
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), game2ViewModelItem!!.story)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory +1)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            game2ViewModelItem!!.phraseNumberInt = resultsStories[0]!!.phraseNumberInt
            game2ViewModelItem!!.wordNumberInt = resultsStories[0]!!.wordNumberInt + 1
        }
        //
        updateMode =  getString(R.string.insert)
        storiesFragmentTransactionStart()
    }
    /**
     * Called when the user taps remove word button.
     *
     * @param view view of tapped picture
     *
     * @see gameADAFragmentTransactionStart
     */
    fun deleteTheWordButton(view: View?) {
        var phraseToDisplay = 0
        var wordToDisplay = 0
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), game2ViewModelItem!!.story)
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
                .equalTo(getString(R.string.story), game2ViewModelItem!!.story)
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
                    .equalTo(getString(R.string.story), game2ViewModelItem!!.story)
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
            renumberAPhraseOfAStory(
                realm,
                game2ViewModelItem!!.story,
                phraseToDisplay
            )
            renumberAStory(realm, game2ViewModelItem!!.story)
        }
        //
        gameADAFragmentTransactionStart()
    }
    /**
     * Called when the user taps the go back button.
     * return to SettingsStoriesImprovementViewPagerFragment
     * @param v view of tapped button
     * @see GameADAActivity
     */
    fun onClickGoBackFromWord(v: View?) {
        gameADAViewPagerFragmentTransactionStart(wordToDisplayInTheStory)
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
        game2ViewModelItem!!.story =
            textWord1.text.toString()
        if (textWord2.text.toString() != "") game2ViewModelItem!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") game2ViewModelItem!!.wordNumberInt =
            textWord3.text.toString().toInt()
        game2ViewModelItem!!.word = textWord4.text.toString()
        //
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = Game2BleImagesFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    override fun reloadFragmentFromImageSearch(imageKey: String?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
//        viewModel.getSelectedItem()
//            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                game2ViewModelItem!!.uriType = "I"
                game2ViewModelItem!!.uri = imageKey
                //
                val frag = Game2BleStoriesFragment()
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
//            }
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
        game2ViewModelItem!!.story =
            textWord1.text.toString()
        if (textWord2.text.toString() != "") game2ViewModelItem!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") game2ViewModelItem!!.wordNumberInt =
            textWord3.text.toString().toInt()
        game2ViewModelItem!!.word = textWord4.text.toString()
        //
        val frag = ImageSearchArasaacFragment()
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
        val frag = ImageSearchArasaacFragment()
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
//        viewModel.getSelectedItem()
//            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                game2ViewModelItem!!.uriType = "A"
                game2ViewModelItem!!.uri = url
                //
                val frag = Game2BleStoriesFragment()
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
//            }
    }
    /**
     * Called when the user taps the add button .
     *
     * after the checks it adds the piece of story on realm
     *
     * @param v view of tapped button
     * @see gameADAFragmentTransactionStart
     *
     * @see Stories
     */
    fun storyToAdd(v: View?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
//        viewModel.getSelectedItem()
//            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                game2ViewModelItem!!.story = getString(R.string.nome_storia)
                game2ViewModelItem!!.phraseNumberInt = 0
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
                                && game2ViewModelItem!!.uriType != null && game2ViewModelItem!!.uri != null)
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
                            if (updateMode == getString(R.string.modify))
                                {
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
                            stories.uriType = game2ViewModelItem!!.uriType
                            stories.uri = game2ViewModelItem!!.uri
                            stories.answerActionType = game2ViewModelItem!!.answerActionType
                            stories.answerAction = game2ViewModelItem!!.answerAction
                            stories.video = game2ViewModelItem!!.video
                            stories.sound = game2ViewModelItem!!.sound
                            stories.soundReplacesTTS = game2ViewModelItem!!.soundReplacesTTS
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
                            stories.uriType = game2ViewModelItem!!.uriType
                            stories.uri = game2ViewModelItem!!.uri
                            stories.answerActionType = game2ViewModelItem!!.answerActionType
                            stories.answerAction = game2ViewModelItem!!.answerAction
                            stories.video = game2ViewModelItem!!.video
                            stories.sound = game2ViewModelItem!!.sound
                            stories.soundReplacesTTS = game2ViewModelItem!!.soundReplacesTTS
                            stories.fromAssets = ""
                            realm.commitTransaction()
                        }
                        // riordino
                        renumberAPhraseOfAStory(
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
                                daModificare.word = "$wordsToChange $wordsToBeInserted"
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
                        game2ViewModelItem!!.story =
                            textWord1.text.toString()
                        game2ViewModelItem!!.phraseNumberInt =
                            textWord2.text.toString().toInt()
                        clearFieldsOfViewmodelDataClass()
                    }
                    //
                    renumberAPhraseOfAStory(
                        realm,
                        game2ViewModelItem!!.story,
                        1
                    )
                    renumberAStory(realm, game2ViewModelItem!!.story)
                    gameADAFragmentTransactionStart()
                }
//            }
    }

    /**
     * clear fields of viewmodel data class
     *
     *
     * @see VoiceToBeRecordedInStories
     */
    fun clearFieldsOfViewmodelDataClass() {
        game2ViewModelItem!!.wordNumberInt = 0
        game2ViewModelItem!!.word = ""
        game2ViewModelItem!!.uriType = ""
        game2ViewModelItem!!.uri = ""
        game2ViewModelItem!!.answerActionType = ""
        game2ViewModelItem!!.answerAction = ""
        game2ViewModelItem!!.video = ""
        game2ViewModelItem!!.sound = ""
        game2ViewModelItem!!.soundReplacesTTS = ""
        //
        game2ViewModelItem!!.deviceEnabledUserName = "non trovato"
    }
    companion object {
        //
        private const val TAG = "Game2BleActivity"
    }

}