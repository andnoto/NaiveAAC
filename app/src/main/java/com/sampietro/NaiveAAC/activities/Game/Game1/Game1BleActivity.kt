package com.sampietro.NaiveAAC.activities.Game.Game1

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.print.PrintHelper
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothDevices
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothLeService
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Game.Utils.AndroidNotificationPermission
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper
import com.sampietro.NaiveAAC.activities.Game.Utils.OnFragmentEventListenerGame
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper.getTargetBitmapFromFileUsingPicasso
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper.getTargetBitmapFromUrlUsingPicasso
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import io.realm.Realm
import io.realm.RealmResults
import java.io.File
import java.util.*
import kotlin.properties.Delegates

/**
 * <h1>Game1Activity</h1>
 *
 * **Game1Activity** displays collections of word images that you can select to form simple sentences
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
 * @version     4.0, 09/09/2023
 * @see GameActivityAbstractClass
 *
 * @see Game1RecyclerViewAdapterInterface
 * @see BluetoothLeService
 */
class Game1BleActivity : AppCompatActivity(),
    OnFragmentEventListenerGame,
    Game1RecyclerViewAdapterInterface,
    Game1BleSecondLevelFragment.onFragmentEventListenerGame1BleSecondLevelFragment,
    Game1BleDialogFragment.onFragmentEventListenerGame1BleDialogFragment {
    //
    lateinit var realm: Realm
    lateinit var context: Context
    lateinit var sharedPref: SharedPreferences
    var sharedLastPhraseNumber by Delegates.notNull<Int>()
    //
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
                bluetooth.activityInActiveState()
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

    // TTS
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null
    //
    var resultsWordPairsList: MutableList<WordPairs>? = null
    var resultWordPairs: WordPairs? = null
    //
    var resultsWordPairsCenter: RealmResults<WordPairs>? = null
    var resultsWordPairsCenterList: MutableList<WordPairs>? = null
    var resultsWordPairsCenterSize = 0
    //
    var resultsWordPairsRight: RealmResults<WordPairs>? = null
    var resultsWordPairsRightList: MutableList<WordPairs>? = null
    var resultsWordPairsRightSize = 0
    //
    var resultsWordPairsLeft: RealmResults<WordPairs>? = null
    var resultsWordPairsLeftList: MutableList<WordPairs>? = null
    var resultsWordPairsLeftSize = 0
    // at the fragment I pass three strings with, for each column,
    // the possible content of the chosen words
    // and three numbers which, if applicable, indicate the choice menus for each column
    // identified by PhraseNumber on History
    var leftColumnContent: String? = null
    var leftColumnContentUrlType: String? = null
    var leftColumnContentUrl: String? = null
    var middleColumnContent: String? = null
    var middleColumnContentUrlType: String? = null
    var middleColumnContentUrl: String? = null
    var rightColumnContent: String? = null
    var rightColumnContentUrlType: String? = null
    var rightColumnContentUrl: String? = null
    //
    var leftColumnContentWord: String? = null
    var middleColumnContentWord: String? = null
    var rightColumnContentWord: String? = null
    //
    var leftColumnMenuPhraseNumber: Int? = null
    var middleColumnMenuPhraseNumber: Int? = null
    var rightColumnMenuPhraseNumber: Int? = null
    //
    var dialog: Game1BleDialogFragment? = null
    var messageLeftColumnContent: String? = null
    var messageMiddleColumnContent: String? = null
    var messageRightColumnContent: String? = null
    //
    var middleColumnContentVerbInTheInfinitiveForm: String? = null
    //
    var numberOfWordsChosen = 0
    //
    var sharedLastPlayer: String? = null
    //
    var wordToSearchSecondLevelMenu: String? = null
    //
    var preference_PrintPermissions: String? = null
    var preference_AllowedMarginOfError = 0
    /**
     * used for printing
     */
    var bitmap1: Bitmap? = null
    /**
     * used for printing
     */
    var target1: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
            bitmap1 = bitmap
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }
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
            val resultsWordPairs = realm.where(WordPairs::class.java)
                .beginGroup()
                .equalTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                .endGroup()
                .findAll()
            val resultsWordPairsSize = resultsWordPairs.size
            if (resultsWordPairsSize != 0) {
                val result = resultsWordPairs[position]!!
                wordToSearchSecondLevelMenu = result.word1
                //
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
        setToFullScreen()
        /*

        */
        AndroidNotificationPermission.checkPermission(this)
        //
//        prepareSpeechRecognizer(this)
        //
        realm = Realm.getDefaultInstance()
        //
        context = this
        //
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
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
        setToFullScreen()
        //
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
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
                    bluetoothService!!.activityInActiveState()
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
//            addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
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

//    override fun onResult(editText: String?) {}

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
//                        mConnected = false
//                        disableTransmitToDisconnectedBluetooth()
                    }
                    else
                    {
                        // use comma as separator
                        val cvsSplitBy = getString(R.string.character_comma)
                        val oneWord: Array<String?> =
                            messageFromGattServer.split(cvsSplitBy.toRegex()).toTypedArray()
                        messageLeftColumnContent = oneWord[0]
                        messageMiddleColumnContent = oneWord[4]
                        messageRightColumnContent = oneWord[8]
                        //
                        dialogFragmentShow()
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
//            bluetoothService!!.stopServer()
            bluetoothService!!.stopScanner()
//            bluetoothService!!.disconnect()
            //
            bluetoothService!!.activityInPausedState()
            }
        unregisterReceiver(gattUpdateReceiver)
    }
    /**
     * destroy SpeechRecognizer, TTS shutdown and more
     *
     * @see androidx.fragment.app.Fragment.onDestroy
     */
    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
//        destroyRecognizer()
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
     * This method is responsible to transfer MainActivity into fullscreen mode.
     */
    private fun setToFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
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
        var leftColumnMessageToSend: String?
        var middleColumnMessageToSend: String?
        var rightColumnMessageToSend: String?
        if (leftColumnContent == getString(R.string.nessuno))
            { leftColumnMessageToSend = leftColumnContent + ","  + "," + ","  + "," }
            else
            { if (leftColumnContentUrlType == "A")
                { leftColumnMessageToSend = leftColumnContent + ","  + leftColumnContentUrlType + "," + leftColumnContentUrl + "," + "," }
                else
                { leftColumnMessageToSend = leftColumnContent + ","  + leftColumnContentUrlType + ","  + "," + leftColumnContentWord + ","}
            }
        if (middleColumnContent == getString(R.string.nessuno))
            { middleColumnMessageToSend = middleColumnContent + ","  + "," + ","  + "," }
            else
            { if (middleColumnContentUrlType == "A")
                { middleColumnMessageToSend = middleColumnContent + ","  + middleColumnContentUrlType + "," + middleColumnContentUrl + "," + "," }
                else
                { middleColumnMessageToSend = middleColumnContent + ","  + middleColumnContentUrlType + ","  + "," + middleColumnContentWord + "," }
        }
        if (rightColumnContent == getString(R.string.nessuno))
            { rightColumnMessageToSend = rightColumnContent + ","  + "," + ","  + "," }
        else
            { if (rightColumnContentUrlType == "A")
                { rightColumnMessageToSend = rightColumnContent + ","  + rightColumnContentUrlType + "," + rightColumnContentUrl + "," + "," }
                else
                { rightColumnMessageToSend = rightColumnContent + ","  + rightColumnContentUrlType + ","  + "," + rightColumnContentWord + ","}
            }
        messageToSend = leftColumnMessageToSend + middleColumnMessageToSend + rightColumnMessageToSend
        bluetoothService!!.sendMessage(messageToSend)
    }
    //
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
//        displaySecondLevelMenu()
        if (leftColumnContent != getString(R.string.nessuno)
            && middleColumnContent != getString(R.string.nessuno)
            && rightColumnContent != getString(R.string.nessuno)
        ) {
            // text reading
            readingOfTheText()
            // send message
            if (deviceEnabledName != "non trovato")
            { sendMessage() }
        }
        fragmentTransactionStart()
    }

    @SuppressLint("MissingPermission")
    fun disableTransmitToDisconnectedBluetooth() {
        deviceEnabledName = "non trovato"
        deviceEnabledUserName = "non trovato"
        //
        displaySecondLevelMenu()
    }
    //
    override fun scanRequestBluetoothDevicesFromGame1BleSecondLevelFragment() {
        bluetoothService!!.scan()
    }
    //
    fun dialogFragmentShow() {
        dialog = Game1BleDialogFragment.newInstance(deviceEnabledUserName!!, messageFromGattServer )
        dialog!!.show(supportFragmentManager, "GAME_DIALOG")
    }
    override fun receiveResultOnClickFromGame1DialogFragment(v: View?) {
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
                // TTS
                tTS1 = TextToSpeech(context) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tTS1!!.speak(
                            messageLeftColumnContent + " " +
                                 messageMiddleColumnContent + " " +
                                 messageRightColumnContent,
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
            R.id.dialog_img1 -> {
                // TTS
                tTS1 = TextToSpeech(context) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tTS1!!.speak(
                            messageLeftColumnContent,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            getString(R.string.prova_tts)
                        )
                    } else {
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.dialog_img2 -> {
                // TTS
                tTS1 = TextToSpeech(context) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tTS1!!.speak(
                            messageMiddleColumnContent,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            getString(R.string.prova_tts)
                        )
                    } else {
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.dialog_img3 -> {
                // TTS
                tTS1 = TextToSpeech(context) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tTS1!!.speak(
                            messageRightColumnContent,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            getString(R.string.prova_tts)
                        )
                    } else {
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun receiveResultFromGame1DialogFragment() {
        // TTS
        tTS1 = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                tTS1!!.speak(
                messageLeftColumnContent + " " +
                     messageMiddleColumnContent + " " +
                     messageRightColumnContent,
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
     * @see .displaySecondLevelMenu
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
     * @see GameHelper.historyRegistration
     *
     * @see fragmentTransactionStart
     */
    fun displaySecondLevelMenu() {
        numberOfWordsChosen = 1
        // search for the second level menu words in the wordpairs table
        resultsWordPairsCenter = realm.where(WordPairs::class.java)
            .beginGroup()
            .equalTo(getString(R.string.word1), wordToSearchSecondLevelMenu)
            .equalTo(getString(R.string.is_menu_item), getString(R.string.slm))
            .endGroup()
            .findAll()
        resultsWordPairsCenterSize = this.resultsWordPairsCenter!!.size
        if (resultsWordPairsCenterSize != 0) {
            // convert RealmResults<Model> to ArrayList<Model>
            resultsWordPairsCenterList =
                GetResultsWordPairsList.getResultsWordPairsList(realm, resultsWordPairsCenter)
            // record History and initiate Fragment transaction
            GameHelper.historyRegistration(
                context, realm,
                resultsWordPairsCenterList!!, resultsWordPairsCenterSize, false
            )
            leftColumnMenuPhraseNumber = 0
            sharedLastPhraseNumber =
                sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
            middleColumnMenuPhraseNumber = sharedLastPhraseNumber
            rightColumnMenuPhraseNumber = 0
            leftColumnContent = getString(R.string.nessuno)
            middleColumnContent = getString(R.string.nessuno)
            rightColumnContent = getString(R.string.nessuno)
            if (resultsWordPairsCenterSize == 1) {
                // the first word of the sentence was chosen
                // (in the case of noun and verb only possible choice,
                // the second word of the sentence is also considered to be chosen -
                // in the case of verb and nouns the only possible choice is considered
                // also chosen the second or second and third word of the sentence)
                theFirstWordOfTheSentenceWasChosen(0)
            }
            fragmentTransactionStart()
        }
    }

    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see Game1SecondLevelFragment
     */
    fun fragmentTransactionStart() {
        val frag = Game1BleSecondLevelFragment()
        val bundle = Bundle()
        bundle.putString(getString(R.string.left_column_content), leftColumnContent)
        bundle.putString(getString(R.string.left_column_content_url_type), leftColumnContentUrlType)
        bundle.putString(getString(R.string.left_column_content_url), leftColumnContentUrl)
        bundle.putString(getString(R.string.middle_column_content), middleColumnContent)
        bundle.putString(
            getString(R.string.middle_column_content_url_type),
            middleColumnContentUrlType
        )
        bundle.putString(getString(R.string.middle_column_content_url), middleColumnContentUrl)
        bundle.putString(getString(R.string.right_column_content), rightColumnContent)
        bundle.putString(
            getString(R.string.right_column_content_url_type),
            rightColumnContentUrlType
        )
        bundle.putString(getString(R.string.right_column_content_url), rightColumnContentUrl)
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
     * @see Game1RecyclerViewAdapterInterface
     *
     * @see WordPairs
     *
     * @see ImageSearchHelper.imageSearch
     *
     * @see GetResultsWordPairsList.getResultsWordPairsList
     *
     * @see refineSearchWordPairs
     *
     * @see GameHelper.historyRegistration
     *
     * @see com.sampietro.simsimtest.activities.history.History
     *
     * @see GrammarHelper.searchType
     *
     * @see registerTheChoiceForTheViewOnTheLeft
     *
     * @see sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText
     *
     * @see registerTheChoiceForTheViewOnTheRight
     *
     * @see fragmentTransactionStart
     *
     * @see getTargetBitmapFromUrlUsingPicasso
     * @see getTargetBitmapFromFileUsingPicasso
     */
    override fun onItemClick(view: View?, i: Int) {
        when (numberOfWordsChosen) {
            1 -> {
                // the first word of the sentence was chosen
                // (in the case of noun and verb only possible choice,
                // the second word of the sentence is also considered to be chosen -
                // in the case of verb and nouns the only possible choice is considered
                // also chosen the second or second and third word of the sentence)
                theFirstWordOfTheSentenceWasChosen(i)
                // start of transaction Fragment
                fragmentTransactionStart()
            }
            2 -> {
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
                var image: ResponseImageSearch?
                numberOfWordsChosen++
                val middleColumnContentType = GrammarHelper.searchType(context, middleColumnContent, realm)
                // type = 3 verbs
                if (middleColumnContentType == "3") {
                    when (view!!.id) {
                        R.id.img1 -> {
                            // 1a) if the chosen word is on the left, I register the choice and keep the options
                            // list in the recycler view on the right
                            // if there are no options on the right, i consider the sentence completed
                            resultWordPairs = resultsWordPairsLeftList!![i]
                            leftColumnContent = resultWordPairs!!.word1
                            leftColumnMenuPhraseNumber = 0
                            // image search
                            image = ImageSearchHelper.imageSearch(context, realm, leftColumnContent)
                            if (image != null) {
                                leftColumnContentUrlType = image.uriType
                                leftColumnContentUrl = image.uriToSearch
                            }
                            if (rightColumnMenuPhraseNumber == 0) {
                                rightColumnContent = " "
                                numberOfWordsChosen++
                                // sentence completion check, grammar arrangement and text reading
                                sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                            }
                            // start of transaction Fragment
                            fragmentTransactionStart()
                        }
                        R.id.img2 -> {
                            // 1b) if the chosen word is in the center the choice is not allowed
                            // (because it has already been chosen) i keep both option lists in the recycler views
                            // both on the right and on the left
                            // start of transaction Fragment
                            numberOfWordsChosen--
                            fragmentTransactionStart()
                        }
                        R.id.img3 -> {
                            // 1c) if the word chosen is on the right i register the choice and
                            // keep the options list in the recycler view on the left
                            resultWordPairs = resultsWordPairsRightList!![i]
                            rightColumnContent = resultWordPairs!!.word2
                            //
                            rightColumnMenuPhraseNumber = 0
                            // image search
                            image = ImageSearchHelper.imageSearch(context, realm, rightColumnContent)
                            if (image != null) {
                                rightColumnContentUrlType = image.uriType
                                rightColumnContentUrl = image.uriToSearch
                            }
                            // start of transaction Fragment
                            fragmentTransactionStart()
                        }
                    }
                } else {
                    when (view!!.id) {
                        R.id.img1 -> {
                            // 2a) if the chosen word is on the left, i register the choice,
                            // moving the central column to the right (retrieving the data of the
                            // eventual prize video) and the left column (that of the choice)
                            // to the center and proposing in the left column the choices compatible
                            // with the choice (it should be a verb)
                            rightColumnContent = middleColumnContent
                            rightColumnContentUrlType = middleColumnContentUrlType
                            rightColumnContentUrl = middleColumnContentUrl
                            rightColumnMenuPhraseNumber = 0
                            //
                            resultWordPairs = resultsWordPairsLeftList!![i]
                            //
                            middleColumnContent = resultWordPairs!!.word1
                            middleColumnMenuPhraseNumber = 0
                            // image search
                            image = ImageSearchHelper.imageSearch(context, realm, middleColumnContent)
                            if (image != null) {
                                middleColumnContentUrlType = image.uriType
                                middleColumnContentUrl = image.uriToSearch
                            }
                            //
                            resultsWordPairsLeft = realm.where(WordPairs::class.java)
                                .beginGroup()
                                .equalTo(getString(R.string.word2), middleColumnContent)
                                .notEqualTo(
                                    getString(R.string.is_menu_item),
                                    getString(R.string.tlm)
                                )
                                .notEqualTo(
                                    getString(R.string.is_menu_item),
                                    getString(R.string.slm)
                                )
                                .endGroup()
                                .findAll()
                            resultsWordPairsLeftSize = resultsWordPairsLeft!!.size
                            if (resultsWordPairsLeftSize != 0) {
                                // convert RealmResults<Model> to ArrayList<Model>
                                resultsWordPairsList =
                                    GetResultsWordPairsList.getResultsWordPairsList(
                                        realm,
                                        resultsWordPairsLeft
                                    )
                                // does not consider wordpairs with pairs of nouns or pairs of verbs
                                // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                                // (leaves only noun-verb pairs or vice versa verb-noun
                                // or verb-verb if the first is an auxiliary verb or a servile verb)
                                resultsWordPairsLeftList =
                                    refineSearchWordPairs(resultsWordPairsList)
                                resultsWordPairsLeftSize = resultsWordPairsLeftList!!.size
                                //
                                if (resultsWordPairsLeftSize > 1) {
                                    // History registration
                                    GameHelper.historyRegistration(
                                        context, realm,
                                        resultsWordPairsLeftList!!, resultsWordPairsLeftSize, true
                                    )
                                    sharedLastPhraseNumber = sharedPref.getInt(
                                        getString(R.string.preference_last_phrase_number),
                                        1
                                    )
                                    leftColumnMenuPhraseNumber = sharedLastPhraseNumber
                                    leftColumnContent = getString(R.string.nessuno)
                                } else {
                                    if (resultsWordPairsLeftSize == 1) {
                                        registerTheChoiceForTheViewOnTheLeft()
                                        // sentence completion check, grammar arrangement and text reading
                                        sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                                    }
                                }
                            } else {
                                // resultsWordPairsLeftSize == 0 : the sentence is complete with just two words
                                leftColumnContent = " "
                                leftColumnMenuPhraseNumber = 0
                                numberOfWordsChosen++
                                // sentence completion check, grammar arrangement and text reading
                                sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                            }
                            // start of transaction Fragment
                            fragmentTransactionStart()
                        }
                        R.id.img2 -> {
                            // 2b) if the chosen word is in the center the choice is not allowed (because it has
                            // already been chosen) i keep both option lists in the recycler views
                            // both on the right and on the left
                            numberOfWordsChosen--
                            fragmentTransactionStart()
                        }
                        R.id.img3 -> {
                            // 2c) if the word chosen is on the right, i register the choice moving the central
                            // column to the left and the right column (that of the choice) to the center and
                            // proposing in the right column the choices compatible with the choice (it should be a verb)
                            leftColumnContent = middleColumnContent
                            leftColumnContentUrlType = middleColumnContentUrlType
                            leftColumnContentUrl = middleColumnContentUrl
                            leftColumnMenuPhraseNumber = 0
                            //
                            resultWordPairs = resultsWordPairsRightList!![i]
                            middleColumnContent = resultWordPairs!!.word2
                            middleColumnMenuPhraseNumber = 0
                            // image search
                            image = ImageSearchHelper.imageSearch(context, realm, middleColumnContent)
                            if (image != null) {
                                middleColumnContentUrlType = image.uriType
                                middleColumnContentUrl = image.uriToSearch
                            }
                            //
                            resultsWordPairsRight = realm.where(WordPairs::class.java)
                                .beginGroup()
                                .equalTo(getString(R.string.word1), middleColumnContent)
                                .notEqualTo(
                                    getString(R.string.is_menu_item),
                                    getString(R.string.tlm)
                                )
                                .notEqualTo(
                                    getString(R.string.is_menu_item),
                                    getString(R.string.slm)
                                )
                                .endGroup()
                                .findAll()
                            resultsWordPairsRightSize = this.resultsWordPairsRight!!.size
                            if (resultsWordPairsRightSize != 0) {
                                // convert RealmResults<Model> to ArrayList<Model>
                                resultsWordPairsList =
                                    GetResultsWordPairsList.getResultsWordPairsList(
                                        realm,
                                        resultsWordPairsRight
                                    )
                                // does not consider wordpairs with pairs of nouns or pairs of verbs
                                // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                                // (leaves only noun-verb pairs or vice versa verb-noun
                                // or verb-verb if the first is an auxiliary verb or a servile verb)
                                resultsWordPairsRightList =
                                    refineSearchWordPairs(resultsWordPairsList)
                                resultsWordPairsRightSize = resultsWordPairsRightList!!.size
                                //
                                if (resultsWordPairsRightSize > 1) {
                                    // History registration
                                    GameHelper.historyRegistration(
                                        context, realm,
                                        resultsWordPairsRightList!!, resultsWordPairsRightSize, false
                                    )
                                    sharedLastPhraseNumber = sharedPref.getInt(
                                        getString(R.string.preference_last_phrase_number),
                                        1
                                    )
                                    rightColumnMenuPhraseNumber = sharedLastPhraseNumber
                                    rightColumnContent = getString(R.string.nessuno)
                                } else {
                                    if (resultsWordPairsRightSize == 1) {
                                        registerTheChoiceForTheViewOnTheRight()
                                        // sentence completion check, grammar arrangement and text reading
                                        sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                                    }
                                }
                            } else {
                                // resultsWordPairsRightSize == 0 : the sentence is complete with just two words
                                rightColumnContent = " "
                                rightColumnMenuPhraseNumber = 0
                                numberOfWordsChosen++
                                // sentence completion check, grammar arrangement and text reading
                                sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                            }
                            // start of transaction Fragment
                            fragmentTransactionStart()
                        }
                    }
                }
            }
            3 -> {
                // chosen the 3rd and last word of the sentence (to be checked if the sentence completes)
                // the following clicks on the words (case 4) will start the tts
                // 1a) if the word chosen is on the left, register the choice (if it has not already been made)
                // 1b) if the chosen word is in the center, the choice is not allowed
                // (because it has already been chosen)
                // 1c) if the word chosen is on the right I register the choice
                // (if it has not already been made)
                numberOfWordsChosen++
                when (view!!.id) {
                    R.id.img1 -> {
                        // 1a) if the word chosen is on the left, register the choice
                        // (if it has not already been made)
                        if (leftColumnContent == getString(R.string.nessuno)) {
                            resultWordPairs = resultsWordPairsLeftList!![i]
                            leftColumnContent = resultWordPairs!!.word1
                            leftColumnMenuPhraseNumber = 0
                            // image search
                            var image: ResponseImageSearch?
                            image = ImageSearchHelper.imageSearch(context, realm, leftColumnContent)
                            if (image != null) {
                                leftColumnContentUrlType = image.uriType
                                leftColumnContentUrl = image.uriToSearch
                            }
                            // sentence completion check, grammar arrangement and text reading
                            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                        } else {
                            numberOfWordsChosen--
                        }
                        // start of transaction Fragment
                        fragmentTransactionStart()
                    }
                    R.id.img2 -> {
                        // 1b) if the chosen word is in the center, the choice is not allowed
                        // (because it has already been chosen)
                        // start of transaction Fragment
                        numberOfWordsChosen--
                        fragmentTransactionStart()
                    }
                    R.id.img3 -> {
                        // 1c) if the word chosen is on the right I register the choice
                        // (if it has not already been made)
                        if (rightColumnContent == getString(R.string.nessuno)) {
                            resultWordPairs = resultsWordPairsRightList!![i]
                            rightColumnContent = resultWordPairs!!.word2
                            //
                            rightColumnMenuPhraseNumber = 0
                            // image search
                            var image: ResponseImageSearch?
                            image = ImageSearchHelper.imageSearch(context, realm, rightColumnContent)
                            if (image != null) {
                                rightColumnContentUrlType = image.uriType
                                rightColumnContentUrl = image.uriToSearch
                            }
                            // sentence completion check, grammar arrangement and text reading
                            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                        } else {
                            numberOfWordsChosen--
                        }
                        // start of transaction Fragment
                        fragmentTransactionStart()
                    }
                }
            }
            4 -> when (view!!.id) {
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
                        if (leftColumnContentUrlType == getString(R.string.character_a)) {
                            getTargetBitmapFromUrlUsingPicasso(leftColumnContentUrl, target1)
                        } else {
                            val f = File(leftColumnContentUrl!!)
                            getTargetBitmapFromFileUsingPicasso(f, target1)
                        }
                        val photoPrinter = PrintHelper(context)
                        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
                        photoPrinter.printBitmap(getString(R.string.stampa_immagine1), bitmap1!!)
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
                        if (middleColumnContentUrlType == getString(R.string.character_a)) {
                            getTargetBitmapFromUrlUsingPicasso(middleColumnContentUrl, target1)
                        } else {
                            val f = File(middleColumnContentUrl!!)
                            getTargetBitmapFromFileUsingPicasso(f, target1)
                        }
                        val photoPrinter = PrintHelper(context)
                        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
                        photoPrinter.printBitmap(getString(R.string.stampa_immagine1), bitmap1!!)
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
                        if (rightColumnContentUrlType == "A") {
                            getTargetBitmapFromUrlUsingPicasso(rightColumnContentUrl, target1)
                        } else {
                            val f = File(rightColumnContentUrl!!)
                            getTargetBitmapFromFileUsingPicasso(f, target1)
                        }
                        val photoPrinter = PrintHelper(context)
                        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
                        photoPrinter.printBitmap(getString(R.string.stampa_immagine1), bitmap1!!)
                    }
                }
            }
            else -> {}
        }
    }

    fun theFirstWordOfTheSentenceWasChosen(i: Int) {
        numberOfWordsChosen++
        //
        leftColumnMenuPhraseNumber = 0
        middleColumnMenuPhraseNumber = 0
        rightColumnMenuPhraseNumber = 0
        leftColumnContent = getString(R.string.nessuno)
        middleColumnContent = getString(R.string.nessuno)
        rightColumnContent = getString(R.string.nessuno)
        // retrieves the chosen word
        resultWordPairs = resultsWordPairsCenterList!![i]
        middleColumnContent = resultWordPairs!!.word2
        // search for the corresponding image
        var image: ResponseImageSearch?
        image = ImageSearchHelper.imageSearch(context, realm, middleColumnContent)
        if (image != null) {
            middleColumnContentUrlType = image.uriType
            middleColumnContentUrl = image.uriToSearch
        }
        // search for related words on wordpairs
        // if the chosen word is the first of the pair,
        // the corresponding second words go to the right column
        // vice versa if the chosen word is the second of the pair,
        // the corresponding second words go to the left column
        resultsWordPairsLeft = realm.where(WordPairs::class.java)
            .beginGroup()
            .equalTo(getString(R.string.word2), middleColumnContent)
            .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
            .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
            .endGroup()
            .findAll()
        resultsWordPairsLeftSize = this.resultsWordPairsLeft!!.size
        if (resultsWordPairsLeftSize != 0) {
            // convert RealmResults<Model> to ArrayList<Model>
            resultsWordPairsList =
                GetResultsWordPairsList.getResultsWordPairsList(realm, resultsWordPairsLeft)
            // does not consider wordpairs with pairs of nouns or pairs of verbs
            // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
            // (leaves only noun-verb pairs or vice versa verb-noun
            // or verb-verb if the first is an auxiliary verb or a servile verb)
            resultsWordPairsLeftList = refineSearchWordPairs(resultsWordPairsList)
            resultsWordPairsLeftSize = resultsWordPairsLeftList!!.size
            //
            if (resultsWordPairsLeftSize > 1) {
                // History registration
                GameHelper.historyRegistration(
                    context, realm,
                    resultsWordPairsLeftList!!, resultsWordPairsLeftSize, true
                )
                sharedLastPhraseNumber =
                    sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
                leftColumnMenuPhraseNumber = sharedLastPhraseNumber
            }
        }
        //
        resultsWordPairsRight = realm.where(WordPairs::class.java)
            .beginGroup()
            .equalTo(getString(R.string.word1), middleColumnContent)
            .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
            .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
            .endGroup()
            .findAll()
        resultsWordPairsRightSize = this.resultsWordPairsRight!!.size
        if (resultsWordPairsRightSize != 0) {
            // convert RealmResults<Model> to ArrayList<Model>
            resultsWordPairsList =
                GetResultsWordPairsList.getResultsWordPairsList(realm, resultsWordPairsRight)
            // does not consider wordpairs with pairs of nouns or pairs of verbs
            // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
            // (leaves only noun-verb pairs or vice versa verb-noun
            // or verb-verb if the first is an auxiliary verb or a servile verb)
            resultsWordPairsRightList = refineSearchWordPairs(resultsWordPairsList)
            resultsWordPairsRightSize = resultsWordPairsRightList!!.size
            //
            if (resultsWordPairsRightSize > 1) {
                // History registration
                GameHelper.historyRegistration(
                    context, realm,
                    resultsWordPairsRightList!!, resultsWordPairsRightSize, false
                )
                sharedLastPhraseNumber =
                    sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
                rightColumnMenuPhraseNumber = sharedLastPhraseNumber
            }
        }
        // if you have only one possible choice (in the recycler views on the right and left)
        // and this is a verb (if the first word chosen is not a verb, the proposed choices
        // can only be verbs)
        // 1a) if the verb only possible choice is on the left, register the choice
        // by moving the central column to the right and the left column in the center
        // and proposing in the left column the choices compatible with the verb
        // 1b) if the verb only possible choice is on the right, register the choice
        // moving the middle column to the left and the right column in the center
        // and proposing in the right column the choices compatible with the verb
        // 1c) if I have only one possible choice verb both on the left and on the right,
        // I propose the choice both in the column on the right and in the one on the left
        // 2) if the first word chosen (the middle one) is a verb, check if I have only one
        // possible choice in the recycler views on the right and left
        // in this case I register the choice
        val middleColumnContentType = GrammarHelper.searchType(context, middleColumnContent, realm)
        // type = 3 verbs
        if (middleColumnContentType != "3") {
            if (resultsWordPairsLeftSize == 1 && resultsWordPairsRightSize == 0) {
                // 1a) if the verb only possible choice is on the left, register the choice
                // by moving the central column to the right and the left column in the center
                // and proposing in the left column the choices compatible with the verb
                rightColumnContent = middleColumnContent
                rightColumnContentUrlType = middleColumnContentUrlType
                rightColumnContentUrl = middleColumnContentUrl
                rightColumnMenuPhraseNumber = 0
                //
                resultWordPairs = resultsWordPairsLeftList!![0]
                middleColumnContent = resultWordPairs!!.word1
                middleColumnMenuPhraseNumber = 0
                // image search
                image = ImageSearchHelper.imageSearch(context, realm, middleColumnContent)
                if (image != null) {
                    middleColumnContentUrlType = image.uriType
                    middleColumnContentUrl = image.uriToSearch
                }
                //
                resultsWordPairsLeft = realm.where(WordPairs::class.java)
                    .beginGroup()
                    .equalTo(getString(R.string.word2), middleColumnContent)
                    .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                    .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                    .endGroup()
                    .findAll()
                var resultsWordPairsLeftSize = this.resultsWordPairsLeft!!.size
                if (resultsWordPairsLeftSize != 0) {
                    // convert RealmResults<Model> to ArrayList<Model>
                    resultsWordPairsList =
                        GetResultsWordPairsList.getResultsWordPairsList(realm, resultsWordPairsLeft)
                    // does not consider wordpairs with pairs of nouns or pairs of verbs
                    // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                    // (leaves only noun-verb pairs or vice versa verb-noun
                    // or verb-verb if the first is an auxiliary verb or a servile verb)
                    resultsWordPairsLeftList = refineSearchWordPairs(resultsWordPairsList)
                    resultsWordPairsLeftSize = resultsWordPairsLeftList!!.size
                    //
                    if (resultsWordPairsLeftSize > 1) {
                        // History registration
                        GameHelper.historyRegistration(
                            context, realm,
                            resultsWordPairsLeftList!!, resultsWordPairsLeftSize, true
                        )
                        sharedLastPhraseNumber = sharedPref.getInt(
                            getString(R.string.preference_last_phrase_number),
                            1
                        )
                        leftColumnMenuPhraseNumber = sharedLastPhraseNumber
                        leftColumnContent = getString(R.string.nessuno)
                    } else {
                        if (resultsWordPairsLeftSize == 1) {
                            registerTheChoiceForTheViewOnTheLeft()
                            numberOfWordsChosen++
                            // sentence completion check, grammar arrangement and text reading
                            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                        }
                    }
                } else {
                    // resultsWordPairsLeftSize == 0 : the sentence is complete
                    leftColumnContent = " "
                    leftColumnMenuPhraseNumber = 0
                    numberOfWordsChosen++
                    // sentence completion check, grammar arrangement and text reading
                    sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                }
                numberOfWordsChosen++
            }
            if (resultsWordPairsLeftSize == 0 && resultsWordPairsRightSize == 1) {
                // 1b) if the verb only possible choice is on the right, register the choice
                // moving the middle column to the left and the right column in the center
                // and proposing in the right column the choices compatible with the verb
                leftColumnContent = middleColumnContent
                leftColumnContentUrlType = middleColumnContentUrlType
                leftColumnContentUrl = middleColumnContentUrl
                leftColumnMenuPhraseNumber = 0
                //
                resultWordPairs = resultsWordPairsRightList!![0]
                middleColumnContent = resultWordPairs!!.word2
                middleColumnMenuPhraseNumber = 0
                // image search
                image = ImageSearchHelper.imageSearch(context, realm, middleColumnContent)
                if (image != null) {
                    middleColumnContentUrlType = image.uriType
                    middleColumnContentUrl = image.uriToSearch
                }
                //
                resultsWordPairsRight = realm.where(WordPairs::class.java)
                    .beginGroup()
                    .equalTo(getString(R.string.word1), middleColumnContent)
                    .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                    .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                    .endGroup()
                    .findAll()
                var resultsWordPairsRightSize = this.resultsWordPairsRight!!.size
                if (resultsWordPairsRightSize != 0) {
                    // convert RealmResults<Model> to ArrayList<Model>
                    resultsWordPairsList = GetResultsWordPairsList.getResultsWordPairsList(
                        realm,
                        resultsWordPairsRight
                    )
                    // does not consider wordpairs with pairs of nouns or pairs of verbs
                    // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                    // (leaves only noun-verb pairs or vice versa verb-noun
                    // or verb-verb if the first is an auxiliary verb or a servile verb)
                    resultsWordPairsRightList = refineSearchWordPairs(resultsWordPairsList)
                    resultsWordPairsRightSize = resultsWordPairsRightList!!.size
                    //
                    if (resultsWordPairsRightSize > 1) {
                        // History registration
                        GameHelper.historyRegistration(
                            context, realm,
                            resultsWordPairsRightList, resultsWordPairsRightSize, false
                        )
                        sharedLastPhraseNumber = sharedPref.getInt(
                            getString(R.string.preference_last_phrase_number),
                            1
                        )
                        rightColumnMenuPhraseNumber = sharedLastPhraseNumber
                        rightColumnContent = getString(R.string.nessuno)
                    } else {
                        if (resultsWordPairsRightSize == 1) {
                            registerTheChoiceForTheViewOnTheRight()
                            numberOfWordsChosen++
                            // sentence completion check, grammar arrangement and text reading
                            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                        }
                    }
                } else {
                    // resultsWordPairsRightSize == 0 : la frase è completa
                    rightColumnContent = " "
                    rightColumnMenuPhraseNumber = 0
                    numberOfWordsChosen++
                    // sentence completion check, grammar arrangement and text reading
                    sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
                }
                numberOfWordsChosen++
            }
            if (resultsWordPairsLeftSize == 1 && resultsWordPairsRightSize == 1) {
                // 1c) if I have only one possible choice verb both on the left and on the right,
                // I propose the choice both in the column on the right and in the one on the left
                // History registration
                GameHelper.historyRegistration(
                    context, realm,
                    resultsWordPairsLeftList, resultsWordPairsLeftSize, true
                )
                sharedLastPhraseNumber =
                    sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
                leftColumnMenuPhraseNumber = sharedLastPhraseNumber
                GameHelper.historyRegistration(
                    context, realm,
                    resultsWordPairsRightList, resultsWordPairsRightSize, false
                )
                sharedLastPhraseNumber =
                    sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
                rightColumnMenuPhraseNumber = sharedLastPhraseNumber
            }
        } else {
            // 2) if the first word chosen (the middle one) is a verb, check if I have only one
            // possible choice in the recycler views on the right and left
            // in this case I register the choice
            if (resultsWordPairsLeftSize == 1) {
                registerTheChoiceForTheViewOnTheLeft()
            }
            if (resultsWordPairsRightSize == 1) {
                registerTheChoiceForTheViewOnTheRight()
            }
            if (resultsWordPairsLeftSize == 1 && resultsWordPairsRightSize == 1) {
                // sentence completion check, grammar arrangement and text reading
                sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
            }
        }
        // if you do not have possible choices neither on the recycler view on the right
        // nor on that one on the left, the sentence is completed with a single word
        if (resultsWordPairsLeftSize == 0 && resultsWordPairsRightSize == 0) {
            leftColumnContent = " "
            leftColumnMenuPhraseNumber = 0
            numberOfWordsChosen++
            rightColumnContent = " "
            rightColumnMenuPhraseNumber = 0
            numberOfWordsChosen++
            // sentence completion check, grammar arrangement and text reading
            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText()
        }
    }

    /**
     * register the choice for the view on the left.
     *
     *
     *
     * @see ImageSearchHelper.imageSearch
     */
    fun registerTheChoiceForTheViewOnTheLeft() {
        //
        resultWordPairs = resultsWordPairsLeftList!![0]
        leftColumnContent = resultWordPairs!!.word1
        leftColumnMenuPhraseNumber = 0
        // image search
        var image: ResponseImageSearch?
        image = ImageSearchHelper.imageSearch(context, realm, leftColumnContent)
        if (image != null) {
            leftColumnContentUrlType = image.uriType
            leftColumnContentUrl = image.uriToSearch
        }
        numberOfWordsChosen++
    }

    /**
     * register the choice for the view on the right.
     *
     *
     *
     * @see ImageSearchHelper.imageSearch
     */
    fun registerTheChoiceForTheViewOnTheRight() {
        //
        resultWordPairs = resultsWordPairsRightList!![0]
        rightColumnContent = resultWordPairs!!.word2
        //
        rightColumnMenuPhraseNumber = 0
        // image search
        var image: ResponseImageSearch?
        image = ImageSearchHelper.imageSearch(context, realm, rightColumnContent)
        if (image != null) {
            rightColumnContentUrlType = image.uriType
            rightColumnContentUrl = image.uriToSearch
        }
        numberOfWordsChosen++
    }

    /**
     * leaves only noun-verb pairs or vice versa verb-noun.
     *
     *
     *
     * @param resultsWordPairsList List<WordPairs> to clean up
     * @return List<WordPairs> cleaned up
     * @see WordPairs
     *
     * @see GrammarHelper.searchType
     */
    fun refineSearchWordPairs(resultsWordPairsList: MutableList<WordPairs>?): MutableList<WordPairs> {
        // does not consider wordpairs with pairs of nouns or pairs of verbs
        // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
        // (leaves only noun-verb pairs or vice versa verb-noun
        // or verb-verb if the first is an auxiliary verb or a servile verb)
        var auxiliaryVerb: String
        var servileVerb: String
        //
        var i = 0
        var resultsWordPairsSize = resultsWordPairsList!!.size
        while (i < resultsWordPairsSize) {
            resultWordPairs = resultsWordPairsList[i]
            val word1Type = GrammarHelper.searchType(context, resultWordPairs!!.word1, realm)
            // type = 3 verbs
            val word2Type = GrammarHelper.searchType(context, resultWordPairs!!.word2, realm)
            //
            auxiliaryVerb = GrammarHelper.searchAuxiliaryVerbs(context, resultWordPairs!!.word1, realm)
            servileVerb = GrammarHelper.searchServileVerbs(context, resultWordPairs!!.word1, realm)
            if (word1Type == "3" && word2Type == "3"
                && auxiliaryVerb != getString(R.string.is_an_auxiliary_verb) && servileVerb != getString(R.string.is_a_servile_verb)
                || word1Type != "3" && word2Type != "3"
            ) {
                resultsWordPairsList.removeAt(i)
                resultsWordPairsSize--
            } else {
                i++
            }
        }
        return resultsWordPairsList
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
    fun sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText() {
        leftColumnContentWord = leftColumnContent
        middleColumnContentWord = middleColumnContent
        rightColumnContentWord = rightColumnContent
        // sentence completion check
        if (leftColumnContent != getString(R.string.nessuno)
            && middleColumnContent != getString(R.string.nessuno)
            && rightColumnContent != getString(R.string.nessuno)
        ) {
            // grammar arrangement and text reading
            grammaticalArrangement()
            readingOfTheText()
            // send message
            if (deviceEnabledName != "non trovato")
            { sendMessage() }
        }
    }

    /**
     * grammar arrangement:
     * adds the corresponding article.
     *
     *
     *
     * @see WordPairs
     *
     * @see GrammarHelper.searchPlural
     *
     * @see GrammarHelper.searchGender
     *
     * @see GrammarHelper.searchArticle
     *
     * @see GrammarHelper.searchVerb
     *
     * @see GrammarHelper.searchVerbsOfMovement
     */
    fun grammaticalArrangement() {
        // nominal group subject
        var pluralToSearchRealm = ""
        var genderToSearchRealm: String?
        var formToSearchRealm: String
        val verbOfMovement: String
        //
        val leftColumnContentIsAServileVerb =
            GrammarHelper.searchServileVerbs(context, leftColumnContent, realm)
        if (leftColumnContentIsAServileVerb == getString(R.string.is_a_servile_verb)) {
            formToSearchRealm = getString(R.string.s1)
            val conjugationOfTheVerb =
                GrammarHelper.searchVerb(context, leftColumnContent, formToSearchRealm, realm)
            leftColumnContent = conjugationOfTheVerb
        } else {
            if (leftColumnContent != sharedLastPlayer && leftColumnContent != getString(R.string.io)) {
                // adds the corresponding article
                // search if plural
                // if gender male / female
                pluralToSearchRealm = GrammarHelper.searchPlural(context, leftColumnContent, realm)
                genderToSearchRealm = GrammarHelper.searchGender(context, leftColumnContent!!, realm)
                val articleToSearch = GrammarHelper.searchArticle(
                    context,
                    leftColumnContent!!,
                    genderToSearchRealm, pluralToSearchRealm, "", realm
                )
                leftColumnContent = articleToSearch + leftColumnContent
            }
        }
        // verb group verb
        if (leftColumnContentIsAServileVerb != getString(R.string.is_a_servile_verb)) {
            if (!(leftColumnContent == " " && rightColumnContent == " ")) {
                formToSearchRealm = if (leftColumnContent == getString(R.string.io)) {
                    getString(R.string.s1)
                } else {
                    if (leftColumnContent == getString(R.string.la_famiglia) || leftColumnContent == " ") {
                        getString(R.string.p1)
                    } else {
                        if (pluralToSearchRealm != getString(R.string.character_y)) {
                            getString(R.string.s3)
                        } else {
                            getString(R.string.p3)
                        }
                    }
                }
                middleColumnContentVerbInTheInfinitiveForm = middleColumnContent
                val conjugationOfTheVerb =
                    GrammarHelper.searchVerb(context, middleColumnContent, formToSearchRealm, realm)
                middleColumnContent = conjugationOfTheVerb
            }
        } else {
            middleColumnContentVerbInTheInfinitiveForm = middleColumnContent
        }
        // verbal group direct object
        if (rightColumnContent != sharedLastPlayer
            && rightColumnContent != getString(R.string.io)
            && rightColumnContent != " "
        ) {
            // ricerca complementi
            val resultsWordPairs = realm.where(WordPairs::class.java)
                .beginGroup()
                .equalTo(getString(R.string.word1), middleColumnContentVerbInTheInfinitiveForm)
                .equalTo(getString(R.string.word2), rightColumnContent)
                .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                .endGroup()
                .findAll()
            val resultsWordPairsSize = resultsWordPairs.size
            if (resultsWordPairsSize != 0) {
                val resultWordPairs = resultsWordPairs[0]!!
                val rightColumnComplement = resultWordPairs.complement
                if (rightColumnComplement != " " && rightColumnComplement != "") {
                    rightColumnContent = "$rightColumnComplement $rightColumnContent"
                } else {
                    //
                    // adds the corresponding article
                    // search if plural
                    // if gender male / female
                    verbOfMovement = GrammarHelper.searchVerbsOfMovement(
                        context,
                        middleColumnContentVerbInTheInfinitiveForm,
                        realm
                    )
                    pluralToSearchRealm = GrammarHelper.searchPlural(context, rightColumnContent, realm)
                    genderToSearchRealm = GrammarHelper.searchGender(context, rightColumnContent!!, realm)
                    val articleToSearch = GrammarHelper.searchArticle(
                        context,
                        rightColumnContent!!,
                        genderToSearchRealm, pluralToSearchRealm, verbOfMovement, realm
                    )
                    rightColumnContent = articleToSearch + rightColumnContent
                }
            }
        }
    }

    /**
     * reads the previously completed sentence
     *
     *
     */
    fun readingOfTheText() {
        // text reading
        toSpeak = (leftColumnContent + " " + middleColumnContent
                + " " + rightColumnContent)
        tTS1 = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tTS1!!.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, getString(R.string.prova_tts))
            } else {
                Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * load an image in imageview from a url or from a file
     *
     * @param urlType if string equal to "A" the image is loaded from a url otherwise it is loaded from a file
     * @param url string with url or file path of origin
     * @param img target imageview
     * @param width int with the width of the target imageview
     * @param height int with the height of the target imageview
     * @see GraphicsHelper.addImageUsingPicasso
     */
    fun addImage(urlType: String, url: String?, img: ImageView?, width: Int, height: Int) {
        if (urlType == "A") {
            GraphicsHelper.addImageUsingPicasso(url, img, width, height)
        } else {
            val f = File(url!!)
            GraphicsHelper.addFileImageUsingPicasso(f, img, width, height)
        }
    }
    companion object {
        //
        private const val TAG = "Game1BleActivity"
    }

    /**
     * on callback from GameFragment to this Activity
     *
     * @param v view root fragment view
     */
    override fun receiveResultGameFragment(v: View?) {}
    //
}