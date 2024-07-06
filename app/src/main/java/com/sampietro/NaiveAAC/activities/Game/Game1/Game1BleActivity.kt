package com.sampietro.NaiveAAC.activities.Game.Game1

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothDevices
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothLeService
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2BleDialogFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.AndroidNotificationPermission
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyRegistration
import com.sampietro.NaiveAAC.activities.Grammar.ComposesASentenceResults
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.composesASentence
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.printImage
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.setToFullScreen
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import io.realm.Realm
import java.util.*

/**
 * <h1>Game1BleActivity</h1>
 *
 * **Game1BleActivity** displays collections of word images that you can select to form simple sentences
 *
 * the search for words takes place via a two-level menu
 * the first level contains the main research classes such as games, food, family, animals
 * the second level contains the subclasses of the first level for example
 * for the game class subclasses ball, tablet, running, etc.
 * the sentences are formed by coupling the words of the subclasses with the words contained in the wordpairs table
 * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
 * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
 * Step 1
 * the BLE app provides an activity (Game1BleActivity) to connect to Bluetooth device.
 * Based on user input, this activity communicates with a Service called BluetoothLeService,
 * which interacts with the BLE device via the BLE API.
 * The communication is performed using a bound service which allows the activity to connect to the
 * BluetoothLeService and call functions to connect to the devices.
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClass
 *
 * @see Game1RecyclerViewAdapterInterface
 * @see BluetoothLeService
 */
class Game1BleActivity : GameActivityAbstractClass(),
    Game1RecyclerViewAdapterInterface,
    Game1BleSecondLevelFragment.onFragmentEventListenerGame1BleSecondLevelFragment,
    Game2BleDialogFragment.onFragmentEventListenerGame2BleDialogFragment
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
                bluetooth.activityInActiveState("Game1BleActivity")
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
    /**
     *
     */
    var onCreateSavedInstanceState: Bundle? = null
    // USED FOR FULL SCREEN
    lateinit var mywindow: Window

    // TTS
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null
    //
    var listOfWordsLeft = arrayListOf<String>()
    var listOfWordsCenter = arrayListOf<String>()
    var listOfWordsRight = arrayListOf<String>()

    // at the fragment I pass three strings with, for each column,
    // the possible content of the chosen words
    // and three numbers which, if applicable, indicate the choice menus for each column
    // identified by PhraseNumber on History
    var leftColumnContent: String? = null
//    var leftColumnContentUrlType: String? = null
//    var leftColumnContentUrl: String? = null
    var middleColumnContent: String? = null
//    var middleColumnContentUrlType: String? = null
//    var middleColumnContentUrl: String? = null
    var rightColumnContent: String? = null
//    var rightColumnContentUrlType: String? = null
//    var rightColumnContentUrl: String? = null

    //
    var leftColumnMenuPhraseNumber: Int? = null
    var middleColumnMenuPhraseNumber: Int? = null
    var rightColumnMenuPhraseNumber: Int? = null
    //
    var leftColumnContentWord: String? = null
    var middleColumnContentWord: String? = null
    var rightColumnContentWord: String? = null
    //
    var dialog: Game2BleDialogFragment? = null
    var messageLeftColumnContent: String? = null
    var messageMiddleColumnContent: String? = null
    var messageRightColumnContent: String? = null
    //
    var numberOfWordsChosen = 0
    //
    var sharedLastPlayer: String? = null
    //
    var wordToSearchSecondLevelMenu: String? = null
    //
    var preference_PrintPermissions: String? = null
    var preference_AllowedMarginOfError = 0
    //  ViewPager2
    // - 1) Create the views (activity_game_1_viewpager_content.xml)
    // - 2) Create the fragment (Game1ViewPagerFirstLevelFragment and Game1ViewPagerSecondLevelFragment)
    // - 3) Add a ViewPager2
    // - 4) Create a FragmentStateAdapter who will use the fragments in step 2
    // - 5) Create a layout that contains a ViewPager2 object: (activity_game_1_viewpager.xml)
    // - 6) This activity must do the following things:
    //   - a) Sets the content view to be the layout with the ViewPager2.
    //   - b) Hooks up the FragmentStateAdapter to the ViewPager2 objects.
    //
    private lateinit var mViewPager: ViewPager2
    private lateinit var mAdapter: Game1BleViewPagerAdapter
    var callbackViewPager2: OnPageChangeCallback = object : OnPageChangeCallback() {
        /**
         * Define page change callback
         *
         * update activity variables and display second level menu.
         *
         * @param position int with position index of the new selected page
         * @see Game1BleActivity.displaySecondLevelMenu
         *
         * @see WordPairs
         *
         * @see OnPageChangeCallback.onPageSelected
         */
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            //  update activity variables
            val resultsListsOfNames = realm.where(ListsOfNames::class.java)
                .beginGroup()
                .equalTo("isMenuItem", "F")
                .equalTo("elementActive", "A")
                .endGroup()
                .findAll()
            val resultsListsOfNamesSize = resultsListsOfNames.size
            if (resultsListsOfNamesSize != 0) {
                val result = resultsListsOfNames[position]!!
                wordToSearchSecondLevelMenu = result.keyword
                // aggiungere ripristino video prize ?
                if (onCreateSavedInstanceState != null) {
                    // The onSaveInstanceState method is called before an activity may be killed
                    // (for Screen Rotation Handling) so that
                    // when it comes back it can restore its state.
                    // if this activity was playing an award video, it restores the award video
                    // else it restores second level menu
                    wordToSearchSecondLevelMenu = onCreateSavedInstanceState!!.getString(
                        getString(R.string.word_to_search_second_level_menu),
                        getString(R.string.non_trovato)
                    )
                    //
                    displaySecondLevelMenu()
                    onCreateSavedInstanceState = null
                } else {
                    displaySecondLevelMenu()
                }
            }
        }
    }
    /**
     * configurations of game1 start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     *
     * @see Game1BleViewPagerAdapter
     *
     * @see android.app.Activity.onCreate
     * @see OnPageChangeCallback.onPageSelected
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            // if this activity was playing an award video, it restores the award video
            // (in callbackViewPager2)
            onCreateSavedInstanceState = savedInstanceState
        }
        // viewpager
        setContentView(R.layout.activity_game_1_viewpager)
        //
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        /*
        USED FOR FULL SCREEN
        */
        mywindow = getWindow()
        setToFullScreen(mywindow)
        /*

        */
        AndroidNotificationPermission.checkPermission(this)
        //
        realm = Realm.getDefaultInstance()
        //
        context = this
        //
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        //
        sharedLastPlayer =
            sharedPref.getString(getString(R.string.preference_LastPlayer), "DEFAULT")
        //
        preference_PrintPermissions =
            sharedPref.getString(getString(R.string.preference_print_permissions), "DEFAULT")
        preference_AllowedMarginOfError =
            sharedPref.getInt(getString(R.string.preference_allowed_margin_of_error), 20)
        // viewpager
        // Wire Adapter with ViewPager2
        mViewPager = findViewById<View>(R.id.pager) as ViewPager2
        mAdapter = Game1BleViewPagerAdapter(this, this, realm)
        mViewPager.adapter = mAdapter
        // Register page change callback
        mViewPager.registerOnPageChangeCallback(callbackViewPager2)
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
        mywindow = getWindow()
        setToFullScreen(mywindow)
        //
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        registerReceiver(bondStateReceiver, IntentFilter(ACTION_BOND_STATE_CHANGED))
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
                    bluetoothService!!.activityInActiveState("Game1BleActivity")
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
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    mConnected = false
                    disableTransmitToDisconnectedBluetooth()
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
            bluetoothService!!.activityInPausedState("Game1BleActivity")
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
        // Unregister page change callback
        mViewPager.unregisterOnPageChangeCallback(callbackViewPager2)
    }

    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     * if it is playing an award video, it stores the relative references
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //
        savedInstanceState.putString(
            getString(R.string.word_to_search_second_level_menu),
            wordToSearchSecondLevelMenu
        )
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }
    /**
     * Called when the user taps the home button.
     *
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    fun returnHome(v: View?) {
    /*
                navigate to home screen (ChoiseOfGameActivity)
    */
        val intent = Intent(this, ChoiseOfGameActivity::class.java)
        startActivity(intent)
        finish()
    }
    /**
     * Called when the user taps the settings button.
     * replace with hear fragment
     *
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    fun returnSettings(v: View?) {
    /*
                navigate to settings screen (ChoiseOfGameActivity)
    */
        val intent = Intent(this, VerifyActivity::class.java)
        startActivity(intent)
        finish()
    }
    /**
     * Called when the user taps the listen again button.
     *
     * re-reads the text of the sentence just composed
     *
     * REFER to [stackoverflow](https://stackoverflow.com/questions/24745546/android-change-activity-after-few-seconds)
     * answer of [Chefes](https://stackoverflow.com/users/3586222/chefes)
     *
     * @param v view of tapped button
     * @see readingOfTheText
     */
    fun listenAgainButton(v: View?) {
        readingOfTheText()
    }

    /**
     * send message.
     *
     * @see BluetoothLeService.sendMessage
     */
    @SuppressLint("MissingPermission")
    fun sendMessage() {
        var messageToSend: String?
        var leftColumnMessageToSend: String = ""
        var middleColumnMessageToSend: String = ""
        var rightColumnMessageToSend: String = ""
        if (leftColumnContent != getString(R.string.nessuno))
    //        { leftColumnMessageToSend = leftColumnContent + ","  + "," + ","  + "," }
    //        else
            {
                val image: ResponseImageSearch?
                image = imageSearch(this, realm, leftColumnContentWord)
                leftColumnMessageToSend = leftColumnContent + "," + image!!.uriType + "," + image.uriToSearch + ","
            }
        if (middleColumnContent != getString(R.string.nessuno))
 //           { middleColumnMessageToSend = middleColumnContent + ","  + "," + ","  + "," }
 //           else
            {
                val image: ResponseImageSearch?
                image = imageSearch(this, realm, middleColumnContentWord)
                middleColumnMessageToSend = middleColumnContent + "," + image!!.uriType + "," + image.uriToSearch + ","
            }
        if (rightColumnContent != getString(R.string.nessuno))
//            { rightColumnMessageToSend = rightColumnContent + ","  + "," + ","  + "," }
//            else
            {
                val image: ResponseImageSearch?
                image = imageSearch(this, realm, rightColumnContentWord)
                rightColumnMessageToSend = rightColumnContent + "," + image!!.uriType + "," + image.uriToSearch + ","
            }
        messageToSend = leftColumnMessageToSend + middleColumnMessageToSend + rightColumnMessageToSend
//        bluetoothService!!.sendMessage(messageToSend)
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
        if (numberOfWordsChosen == 3)
        {
            // text reading
            readingOfTheText()
            // send message
            if (deviceEnabledName != "non trovato")
            { sendMessage() }
        }
        prepareTheFragmentTransaction()
        fragmentTransactionStart()
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
        displaySecondLevelMenu()
    }
    /**
     * scan request Bluetooth Devices from Game1BleSecondLevelFragment .
     *
     * @see Game1BleSecondLevelFragment
     * @see BluetoothLeService.scan
     */
    override fun scanRequestBluetoothDevicesFromGame1BleSecondLevelFragment() {
        bluetoothService!!.scan()
    }
    /**
     * Show Game2BleDialogFragment .
     *
     * @see Game2BleDialogFragment
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
     * display second level menu
     *
     * @param v view of tapped button
     * @see displaySecondLevelMenu
     */
    fun continueGameButton(v: View?) {
        displaySecondLevelMenu()
    }

    /**
     * display second level menu.
     *
     *
     * the second level contains the subclasses of the first level for example
     * for the game class subclasses ball, tablet, running, etc.
     *
     *
     * 1) search for the second level menu words in the wordpairs table,
     * 2) convert RealmResults<Model> to ArrayList<Model>
     * 3) record History and initiate Fragment transaction
     *
     * @see ComposesASentenceResults
     * @see prepareTheFragmentTransaction
     * @see fragmentTransactionStart
     */
    fun displaySecondLevelMenu() {
        numberOfWordsChosen = 0
        leftColumnContent = getString(R.string.nessuno)
        middleColumnContent = getString(R.string.nessuno)
        rightColumnContent = getString(R.string.nessuno)
        listOfWordsLeft.clear()
        listOfWordsCenter.clear()
        listOfWordsRight.clear()
        // search for the second level menu words in the listsofnames table
        val resultsListsOfNames = realm.where(ListsOfNames::class.java)
            .beginGroup()
            .equalTo("keyword", wordToSearchSecondLevelMenu)
            .equalTo("isMenuItem", "S")
            .endGroup()
            .findAll()
        val resultsListsOfNamesSize = resultsListsOfNames!!.size
        var resultsListsOfNamesIndex = 0
        while (resultsListsOfNamesSize > resultsListsOfNamesIndex) {
            listOfWordsCenter.add(resultsListsOfNames[resultsListsOfNamesIndex]!!.word!!)
            resultsListsOfNamesIndex++
        }
        val composesASentenceResults: ComposesASentenceResults = composesASentence (
            context,
            realm,
            numberOfWordsChosen,
            "none",
            leftColumnContent!!,
            middleColumnContent!!,
            rightColumnContent!!,
            listOfWordsLeft,
            listOfWordsCenter,
            listOfWordsRight,
            sharedLastPlayer!!
        )
        //
        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
        leftColumnContent = composesASentenceResults.leftColumnContent
        middleColumnContent = composesASentenceResults.middleColumnContent
        rightColumnContent = composesASentenceResults.rightColumnContent
        listOfWordsLeft.clear()
        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
        listOfWordsCenter.clear()
        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
        listOfWordsRight.clear()
        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
        //
        if (numberOfWordsChosen == 3)
            {
            // sentence completion check, grammar arrangement and text reading
            sentenceReadingOfTheText()
            // send message
            if (deviceEnabledName != "non trovato")
                { sendMessage() }
            }
        prepareTheFragmentTransaction()
        fragmentTransactionStart()
    }
    /**
     * prepare fragment transaction:
     * record History
     *
     * @see GameHelper.historyRegistration
     */
    fun prepareTheFragmentTransaction() {
        //
        if (listOfWordsLeft.size != 0) {
            // History registration
            historyRegistration(
                context, realm,
                leftColumnContent!!,
                listOfWordsLeft, listOfWordsLeft.size
            )
            sharedLastPhraseNumber =
                sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
            leftColumnMenuPhraseNumber = sharedLastPhraseNumber
        } else { leftColumnMenuPhraseNumber = 0 }
        if (listOfWordsCenter.size != 0) {
            // record History and initiate Fragment transaction
            historyRegistration(
                context, realm,
                middleColumnContent!!,
                listOfWordsCenter, listOfWordsCenter.size
            )
            sharedLastPhraseNumber =
                sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
            middleColumnMenuPhraseNumber = sharedLastPhraseNumber
        } else { middleColumnMenuPhraseNumber = 0 }
        if (listOfWordsRight.size != 0) {
            // History registration
            historyRegistration(
                context, realm,
                rightColumnContent!!,
                listOfWordsRight, listOfWordsRight.size
            )
            sharedLastPhraseNumber =
                sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
            rightColumnMenuPhraseNumber = sharedLastPhraseNumber
        } else { rightColumnMenuPhraseNumber = 0 }
    }
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see Game1BleSecondLevelFragment
     */
    fun fragmentTransactionStart() {
        val frag = Game1BleSecondLevelFragment(R.layout.activity_game_1_ble)
        val bundle = Bundle()
        bundle.putString(getString(R.string.left_column_content), leftColumnContent)
//        bundle.putString(getString(R.string.left_column_content_url_type), leftColumnContentUrlType)
//        bundle.putString(getString(R.string.left_column_content_url), leftColumnContentUrl)
        bundle.putString(getString(R.string.middle_column_content), middleColumnContent)
//        bundle.putString(
//            getString(R.string.middle_column_content_url_type),
//            middleColumnContentUrlType
//        )
//        bundle.putString(getString(R.string.middle_column_content_url), middleColumnContentUrl)
        bundle.putString(getString(R.string.right_column_content), rightColumnContent)
//        bundle.putString(
//            getString(R.string.right_column_content_url_type),
//            rightColumnContentUrlType
//        )
//        bundle.putString(getString(R.string.right_column_content_url), rightColumnContentUrl)
        bundle.putInt(
            getString(R.string.left_column_menu_phrase_number),
            leftColumnMenuPhraseNumber!!
        )
        bundle.putInt(
            getString(R.string.middle_column_menu_phrase_number),
            middleColumnMenuPhraseNumber!!
        )
        bundle.putInt(
            getString(R.string.right_column_menu_phrase_number),
            rightColumnMenuPhraseNumber!!
        )
        //
        if (numberOfWordsChosen == 3)
            { bundle.putBoolean("THE SENTENCE IS COMPLETED", true) }
            else
            { bundle.putBoolean("THE SENTENCE IS COMPLETED", false) }
        //
        bundle.putString("DEVICE ENABLED NAME", deviceEnabledName)
        bundle.putString("DEVICE ENABLED USER NAME", deviceEnabledUserName)
        bundle.putString("MESSAGE FROM GATT SERVER", messageFromGattServer)
        //
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag("Game1BleSecondLevelFragment") as Game1BleSecondLevelFragment?
        if (fragmentgotinstance != null) {
            ft.replace(
                R.id.game_container_game1,
                frag,
                "Game1BleSecondLevelFragment"
            )
        } else {
            ft.add(R.id.game_container_game1, frag, "Game1BleSecondLevelFragment")
        }
        ft.addToBackStack(null)
        ft.commitAllowingStateLoss()
    }

    /**
     * called when a word list item is clicked.
     *
     * 1) prepare the ui through the history table
     * 2) launch the fragment for displaying the ui
     * 3) in case of completion of the choice of words,
     * carries out the grammatical arrangement and the reading of the text,
     * 4) chosen the 3rd and last word of the sentence
     * the following clicks on the words will start the tts
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     *
     * @see GrammarHelper.ComposesASentenceResults
     * @see sentenceReadingOfTheText
     * @see sendMessage
     * @see prepareTheFragmentTransaction
     * @see fragmentTransactionStart
     */
    override fun onItemClick(view: View?, i: Int) {
        when (numberOfWordsChosen) {
            0 -> {
                val chosenWordCenter = listOfWordsCenter[i]
                listOfWordsCenter.clear()
                listOfWordsCenter.add(chosenWordCenter)
                val composesASentenceResults: ComposesASentenceResults = composesASentence (
                    context,
                    realm,
                    numberOfWordsChosen,
                    "center",
                    leftColumnContent!!,
                    middleColumnContent!!,
                    rightColumnContent!!,
                    listOfWordsLeft,
                    listOfWordsCenter,
                    listOfWordsRight,
                    sharedLastPlayer!!
                )
                //
                numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                leftColumnContent = composesASentenceResults.leftColumnContent
                middleColumnContent = composesASentenceResults.middleColumnContent
                rightColumnContent = composesASentenceResults.rightColumnContent
                listOfWordsLeft.clear()
                listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                listOfWordsCenter.clear()
                listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                listOfWordsRight.clear()
                listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                // the first word of the sentence was chosen
                // (in the case of noun and verb only possible choice,
                // the second word of the sentence is also considered to be chosen -
                // in the case of verb and nouns the only possible choice is considered
                // also chosen the second or second and third word of the sentence)
                if (numberOfWordsChosen == 3)
                {
                    // sentence completion check, grammar arrangement and text reading
                    sentenceReadingOfTheText()
                    // send message
                    if (deviceEnabledName != "non trovato")
                    { sendMessage() }
                }
                    // start of transaction Fragment
                    prepareTheFragmentTransaction()
                    fragmentTransactionStart()
                    return
            }
            1 -> {
                // the second word of the sentence was chosen
                // 1) if the chosen word (central) is a verb (type = 3 verbs)
                // 1a) if the chosen word is on the left, I register the choice and keep the options
                // list in the recycler view on the right
                // 1b) if the chosen word is in the center the choice is not allowed
                // (because it has already been chosen) i keep both option lists in the recycler views
                // both on the right and on the left
                // 1c) if the word chosen is on the right i register the choice and
                // keep the options list in the recycler view on the left
                // 2) if the chosen (middle) word is not a verb
                // 2a) if the chosen word is on the left, i register the choice,
                // moving the central column to the right and the left column (that of the choice)
                // to the center and proposing in the left column the choices compatible
                // with the choice (it should be a verb)
                // 2b) if the chosen word is in the center the choice is not allowed (because it has
                // already been chosen) i keep both option lists in the recycler views
                // both on the right and on the left
                // 2c) if the word chosen is on the right, i register the choice moving the central
                // column to the left and the right column (that of the choice) to the center and
                // proposing in the right column the choices compatible with the choice (it should be a verb)
                when (view!!.id) {
                    R.id.img1 -> {
                        val chosenWordLeft = listOfWordsLeft[i]
                        listOfWordsLeft.clear()
                        listOfWordsLeft.add(chosenWordLeft)
                        val composesASentenceResults: ComposesASentenceResults = composesASentence (
                            context,
                            realm,
                            numberOfWordsChosen,
                            "left",
                            leftColumnContent!!,
                            middleColumnContent!!,
                            rightColumnContent!!,
                            listOfWordsLeft,
                            listOfWordsCenter,
                            listOfWordsRight,
                            sharedLastPlayer!!
                        )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                    }
                    R.id.img2 -> {
                        val chosenWordCenter = listOfWordsCenter[i]
                        listOfWordsCenter.clear()
                        listOfWordsCenter.add(chosenWordCenter)
                        val composesASentenceResults: ComposesASentenceResults = composesASentence (
                            context,
                            realm,
                            numberOfWordsChosen,
                            "center",
                            leftColumnContent!!,
                            middleColumnContent!!,
                            rightColumnContent!!,
                            listOfWordsLeft,
                            listOfWordsCenter,
                            listOfWordsRight,
                            sharedLastPlayer!!
                        )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                    }
                    R.id.img3 -> {
                        val chosenWordRight = listOfWordsRight[i]
                        listOfWordsRight.clear()
                        listOfWordsRight.add(chosenWordRight)
                        val composesASentenceResults: ComposesASentenceResults = composesASentence (
                            context,
                            realm,
                            numberOfWordsChosen,
                            "right",
                            leftColumnContent!!,
                            middleColumnContent!!,
                            rightColumnContent!!,
                            listOfWordsLeft,
                            listOfWordsCenter,
                            listOfWordsRight,
                            sharedLastPlayer!!
                        )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                        listOfWordsRight = composesASentenceResults.listOfWordsRight
                    }
                }
                //
                if (numberOfWordsChosen == 3)
                {
                    // sentence completion check, grammar arrangement and text reading
                    sentenceReadingOfTheText()
                    // send message
                    if (deviceEnabledName != "non trovato")
                    { sendMessage() }
                }
                prepareTheFragmentTransaction()
                fragmentTransactionStart()
            }
            2 -> {
                // chosen the 3rd and last word of the sentence (to be checked if the sentence completes)
                // the following clicks on the words (case 3) will start the tts
                // 1a) if the word chosen is on the left, register the choice (if it has not already been made)
                // 1b) if the chosen word is in the center, the choice is not allowed
                // (because it has already been chosen)
                // 1c) if the word chosen is on the right I register the choice
                // (if it has not already been made)
                when (view!!.id) {
                    R.id.img1 -> {
                        val chosenWordLeft = listOfWordsLeft[i]
                        listOfWordsLeft.clear()
                        listOfWordsLeft.add(chosenWordLeft)
                        val composesASentenceResults: ComposesASentenceResults = composesASentence (
                            context,
                            realm,
                            numberOfWordsChosen,
                            "left",
                            leftColumnContent!!,
                            middleColumnContent!!,
                            rightColumnContent!!,
                            listOfWordsLeft,
                            listOfWordsCenter,
                            listOfWordsRight,
                            sharedLastPlayer!!
                        )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                    }
                    R.id.img2 -> {
                    }
                    R.id.img3 -> {
                        val chosenWordRight = listOfWordsRight[i]
                        listOfWordsRight.clear()
                        listOfWordsRight.add(chosenWordRight)
                        val composesASentenceResults: ComposesASentenceResults = composesASentence (
                            context,
                            realm,
                            numberOfWordsChosen,
                            "right",
                            leftColumnContent!!,
                            middleColumnContent!!,
                            rightColumnContent!!,
                            listOfWordsLeft,
                            listOfWordsCenter,
                            listOfWordsRight,
                            sharedLastPlayer!!
                        )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                    }
                }
                //
                if (numberOfWordsChosen == 3)
                {
                    // sentence completion check, grammar arrangement and text reading
                    sentenceReadingOfTheText()
                    // send message
                    if (deviceEnabledName != "non trovato")
                    { sendMessage() }
                }
                prepareTheFragmentTransaction()
                fragmentTransactionStart()
            }
            3 -> when (view!!.id) {
                R.id.img1 -> {
                    // TTS
                    tTS1 = TextToSpeech(context) { status ->
                        if (status != TextToSpeech.ERROR) {
                            tTS1!!.speak(
                                leftColumnContent,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                getString(R.string.prova_tts)
                            )
                        } else {
                            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                        }
                    }
                    //
                    if (preference_PrintPermissions == getString(R.string.character_y)) {
                        // image search
                        var image: ResponseImageSearch? = null
                        image = imageSearch(context, realm, listOfWordsLeft[0])
                        printImage(
                            context,
                            image!!.uriType,
                            image.uriToSearch,
                            200,
                            200
                        )
                    }
                }
                R.id.img2 -> {
                    // TTS
                    tTS1 = TextToSpeech(context) { status ->
                        if (status != TextToSpeech.ERROR) {
                            tTS1!!.speak(
                                middleColumnContent,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                getString(R.string.prova_tts)
                            )
                        } else {
                            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                        }
                    }
                    //
                    if (preference_PrintPermissions == getString(R.string.character_y)) {
                        // image search
                        var image: ResponseImageSearch? = null
                        image = imageSearch(context, realm, listOfWordsCenter[0])
                        printImage(
                            context,
                            image!!.uriType,
                            image.uriToSearch,
                            200,
                            200
                        )
                    }
                }
                R.id.img3 -> {
                    // TTS
                    tTS1 = TextToSpeech(context) { status ->
                        if (status != TextToSpeech.ERROR) {
                            tTS1!!.speak(
                                rightColumnContent,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                getString(R.string.prova_tts)
                            )
                        } else {
                            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                        }
                    }
                    //
                    if (preference_PrintPermissions == "Y") {
                        // image search
                        var image: ResponseImageSearch? = null
                        image = imageSearch(context, realm, listOfWordsRight[0])
                        printImage(
                            context,
                            image!!.uriType,
                            image.uriToSearch,
                            200,
                            200
                        )
                    }
                }
            }
            else -> {}
        }
    }

     /**
     * sentence completion check, grammar arrangement and text reading.
     *
     *
     *
     * @see .grammaticalArrangement
     *
     * @see .readingOfTheText
     *
     * @see .startSpeechGame1
     */
    fun sentenceReadingOfTheText() {
         if (listOfWordsLeft.isEmpty()) { leftColumnContentWord = " " }
                                    else { leftColumnContentWord = listOfWordsLeft[0] }
         if (listOfWordsCenter.isEmpty()) { middleColumnContentWord = " " }
                                    else { middleColumnContentWord = listOfWordsCenter[0] }
         if (listOfWordsRight.isEmpty()) { rightColumnContentWord = " " }
                                    else { rightColumnContentWord = listOfWordsRight[0] }
            //
            readingOfTheText()
    }

    /**
     * reads the previously completed sentence
     *
     *
     */
    fun readingOfTheText() {
        // text reading
        var leftColumnToSpeak: String
        var middleColumnToSpeak: String
        var rightColumnToSpeak: String
        if (leftColumnContent == getString(R.string.nessuno))
        { leftColumnToSpeak = " " } else { leftColumnToSpeak = leftColumnContent!! }
        if (rightColumnContent == getString(R.string.nessuno))
        { rightColumnToSpeak = " " } else { rightColumnToSpeak = rightColumnContent!! }
        toSpeak = (leftColumnToSpeak + " " + middleColumnContent
                + " " + rightColumnToSpeak)
        tTS1 = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tTS1!!.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, getString(R.string.prova_tts))
            } else {
                Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        //
        private const val TAG = "Game1BleActivity"
    }
}