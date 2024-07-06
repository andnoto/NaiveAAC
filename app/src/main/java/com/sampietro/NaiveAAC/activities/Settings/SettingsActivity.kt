package com.sampietro.NaiveAAC.activities.Settings

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModify
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModifyAdapter.PictogramsAllToModifyAdapterInterface
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.AccountActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothDevices
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothDevicesAdapter
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFile
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromInternalToSharedStorage
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromSharedToInternalStorage
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileZipFromInternalToSharedStorage
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFilesInFolderToRoot
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.extractFolder
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.getFilePath
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.zipFileAtPath
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter.GameParametersAdapterInterface
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptions
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptionsAdapter.GrammaticalExceptionsAdapterInterface
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.showImage
import com.sampietro.NaiveAAC.activities.Graphics.Images
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.Main.MainActivity
import com.sampietro.NaiveAAC.activities.Phrases.Phrases
import com.sampietro.NaiveAAC.activities.Settings.ChoiseOfGameToSetFragment.onFragmentEventListenerChoiseOfGameToSet
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberStories
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import io.realm.RealmResults
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.util.*

/**
 * <h1>SettingsActivity</h1>
 *
 * **SettingsActivity** app settings.
 *
 * @version     5.0, 01/04/2024
 * @see AccountActivityAbstractClass
 *
 * @see VerifyFragment
 *
 * @see ChoiseOfGameToSetFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptionsAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
 */
class SettingsActivity : AccountActivityAbstractClass(), onFragmentEventListenerChoiseOfGameToSet,
    BluetoothDevicesAdapter.BluetoothDevicesAdapterInterface, GameParametersAdapterInterface,
    GrammaticalExceptionsAdapterInterface, PictogramsAllToModifyAdapterInterface {
    var message = "messaggio non formato"
    var textView: TextView? = null

    //
    var textPassword: String? = null
    //
    var textGameToSet: String? = null

    //
    var fragmentManager: FragmentManager? = null

    //
    var radiobuttonGameParametersActiveClicked = false
    var radiobuttonGameParametersNotActiveClicked = false

    //
    var radiobuttonGameParametersUseVideoAndSoundClicked = false
    var radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = false

    //
    var radiobuttonDataImportReplaceClicked = false
    var radiobuttonDataImportAppendClicked = false

    //
    var checkboxImagesChecked = false
    var checkboxVideosChecked = false
    var checkboxSoundsChecked = false
    var checkboxPhrasesChecked = false
    var checkboxWordPairsChecked = false
    var checkboxListsOfNamesChecked = false
    var checkboxStoriesChecked = false
    var checkboxGrammaticalExceptionsChecked = false
    var checkboxGameParametersChecked = false
    //
    var myLayoutId = R.layout.activity_settings_advanced_settings
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see .setActivityResultLauncher
     *
     * @see ActionbarFragment
     *
     * @see VerifyFragment
     *
     * @see Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        //
        setActivityResultLauncher()
        //
        setCsvSearchActivityResultLauncher()
        //
        setExportCsvSearchActivityResultLauncher()
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(R.id.settings_container, Fragment(R.layout.activity_settings_menu), "MenuSettingsFragment")
                .commit()
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
    }
    /**
     * receive game to set.
     *
     * @param g string containing the game to be set
     * @see ChoiseOfGameToSetFragment
     */
    override fun receiveResultGameToSet(g: String?) {
        textGameToSet = g
    }

    /**
     * Called when the user taps the account button from the settings menu.
     * (activity_settings_menu)
     * the activity is notified to view the account settings.
     *
     *
     * @param view view of tapped button
     * @see AccountFragment
     */
    fun submitAccount(view: View?) {
        // view the fragment account initializing AccountFragment (FragmentTransaction
        // switch between Fragments).
        val frag = AccountFragment(R.layout.activity_settings_account)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user taps the enter password button.
     *
     * @param v view of tapped button
     */
    fun enterPassword(v: View?) {
        //
        val d = Dialog(this)
        // Setting dialogview
        val window = d.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        //
        d.setTitle("Inserisci la password per accedere alle impostazioni \\n(opzionale se omessa , per accedere alle impostazioni, \\nverrà richiesto il risultato di un calcolo aritmetico)")
        d.setCancelable(false)
        d.setContentView(R.layout.activity_account_write_dialog)
        //
        val submitPasswordButton =
            d.findViewById<ImageButton>(R.id.submitPasswordButton)
        val cancelPasswordButton =
            d.findViewById<ImageButton>(R.id.cancelPasswordButton)
        val editTextPasswordAccount = d.findViewById<View>(R.id.editTextPasswordAccount) as EditText
        //
        editTextPasswordAccount.requestFocus()
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(d.findViewById<View>(R.id.editTextPasswordAccount),
            InputMethodManager.SHOW_IMPLICIT
        )
        imm.hideSoftInputFromWindow(d.findViewById<View>(R.id.editTextPasswordAccount).getWindowToken(),
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
        //
        submitPasswordButton.setOnClickListener {
            //
            textPassword = editTextPasswordAccount.text.toString()
            imm.hideSoftInputFromWindow(editTextPasswordAccount.windowToken, 0)
            //
            d.cancel()
        }
        cancelPasswordButton.setOnClickListener {
            //
            imm.hideSoftInputFromWindow(editTextPasswordAccount.windowToken, 0)
            //
            d.cancel()
        }
        //
        d.show()
    }
    /**
     * Called when the user taps the save account button from account settings.
     *
     * register the user in the shared preferences and in realm
     *
     * the activity is notified to view the fragment settings.
     *
     * @param view view of tapped button
     * @see AccountFragment
     *
     * @see registerPersonName
     * @see Images
     */
    fun saveAccount(view: View?) {
        val editText = findViewById<View>(R.id.editTextTextAccount) as EditText
        val textPersonName = editText.text.toString()
        //
        if (textPersonName.length > 0 && filePath != null &&
            filePath != getString(R.string.non_trovato)
        ) {
            // register the user in the shared preferences
            registerPersonName(textPersonName)
            // delete any old user images from realm
            realm.beginTransaction()
            val results = realm.where(
                Images::class.java
            ).equalTo(getString(R.string.descrizione), textPersonName).findAll()
            results.deleteAllFromRealm()
            realm.commitTransaction()
            //
            realm.beginTransaction()
            val resultsIo = realm.where(
                Images::class.java
            ).equalTo(getString(R.string.descrizione), getString(R.string.io)).findAll()
            resultsIo.deleteAllFromRealm()
            realm.commitTransaction()
            // register the user and the linked image in realm
            realm.beginTransaction()
            val i = realm.createObject(
                Images::class.java
            )
            i.descrizione = textPersonName
            i.uri = filePath
            i.fromAssets = ""
            realm.commitTransaction()
            //
            realm.beginTransaction()
            val iIo = realm.createObject(
                Images::class.java
            )
            iIo.descrizione = getString(R.string.io)
            iIo.uri = filePath
            i.fromAssets = ""
            realm.commitTransaction()
            // register the linked lists of names
            realm.beginTransaction()
            val listsOfNames = realm.createObject(ListsOfNames::class.java)
            // set the fields here
            listsOfNames.keyword = getString(R.string.famiglia)
            listsOfNames.word = textPersonName
            listsOfNames.elementActive = "A"
            listsOfNames.isMenuItem = "N"
            listsOfNames.fromAssets = ""
            realm.commitTransaction()
            // register the password
            if (textPassword == null)   { textPassword = "nessuna password" }
            else
            { if (textPassword!!.length <= 0) textPassword = "nessuna password" }
            registerPassword(textPassword)
        }
        // view the fragment settings initializing MenuSettingsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = Fragment(R.layout.activity_settings_menu)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the media settings button from the settings menu.
     *
     * the activity is notified to view the media settings.
     *
     *
     * @param view view of tapped button
     * @see SettingsMediaActivity
     */
    fun submitChoiseOfMediaToSet(view: View?) {
        /*
                navigate to settings word pairs screen
        */
        val intent = Intent(context, SettingsMediaActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called when the user taps the game settings button from the settings menu.
     *
     * the activity is notified to view the choise of game to set.
     *
     *
     * @param view view of tapped button
     * @see ChoiseOfGameToSetFragment
     */
    fun submitChoiseOfGameToSet(view: View?) {
        // view the choise of game to set fragment initializing ChoiseOfGameToSetFragment
        // (FragmentTransaction switch between Fragments).
        val frag = ChoiseOfGameToSetFragment(R.layout.activity_settings_games_menu)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the Send button from the choise of game to set menu.
     *
     * the activity is notified to view the game to set chosen.
     *
     *
     * @param view view of tapped button
     * @see ChoiseOfGameToSetFragment
     *
     * @see PhrasesFragment
     */
    fun goToGameToSet(view: View?) {
        // view the fragment relating to the game to be set chosen
        // (FragmentTransaction switch between Fragments).
        val ft: FragmentTransaction
        when (textGameToSet) {
            getString(R.string.comunicatore) -> {
                val phrasesFragment = PhrasesFragment(R.layout.activity_settings_phrases)
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, phrasesFragment)
                ft.addToBackStack(null)
                ft.commit()
            }
            else -> {}
        }
    }

    /**
     * Called when the user taps the save button from the phrases settings (game2 settings).
     *
     * after the checks it adds the phrases on realm
     *
     * and the activity is notified to view the the fragment settings.
     *
     * @param v view of tapped button
     * @see ChoiseOfGameToSetFragment
     *
     * @see Phrases
     */
    fun savePhrases(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val textWPFP = findViewById<View>(R.id.edittextwelcomephrasefirstpart) as EditText
        // Note that the realm object was generated with the createObject method
        // and not with the new operator.
        // The modification operations will be performed within a Transaction.
        var phraseToSearch = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_first_part))
            .findFirst()
        realm.beginTransaction()
        if (phraseToSearch == null) {
            val phrase = realm.createObject(Phrases::class.java)
            // set the fields here
            phrase.tipo = getString(R.string.welcome_phrase_first_part)
            phrase.descrizione = textWPFP.text.toString()
            phrase.fromAssets = "Y"
        } else {
            phraseToSearch.descrizione = textWPFP.text.toString()
        }
        realm.commitTransaction()
        //
        val textWPSP = findViewById<View>(R.id.edittextwelcomephrasesecondpart) as EditText
        phraseToSearch = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_second_part))
            .findFirst()
        realm.beginTransaction()
        if (phraseToSearch == null) {
            val phrase = realm.createObject(Phrases::class.java)
            // set the fields here
            phrase.tipo = getString(R.string.welcome_phrase_second_part)
            phrase.descrizione = textWPSP.text.toString()
            phrase.fromAssets = "Y"
        } else {
            phraseToSearch.descrizione = textWPSP.text.toString()
        }
        realm.commitTransaction()
        //
        val textRP = findViewById<View>(R.id.edittextreminderphrase) as EditText
        phraseToSearch = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.reminder_phrase))
            .findFirst()
        realm.beginTransaction()
        if (phraseToSearch == null) {
            val phrase = realm.createObject(Phrases::class.java)
            // set the fields here
            phrase.tipo = getString(R.string.reminder_phrase)
            phrase.descrizione = textRP.text.toString()
            phrase.fromAssets = "Y"
        } else {
            phraseToSearch.descrizione = textRP.text.toString()
        }
        realm.commitTransaction()
        //
        val textRPP = findViewById<View>(R.id.edittextreminderphraseplural) as EditText
        //
        phraseToSearch = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.reminder_phrase_plural))
            .findFirst()
        realm.beginTransaction()
        if (phraseToSearch == null) {
            val phrase = realm.createObject(Phrases::class.java)
            // set the fields here
            phrase.tipo = getString(R.string.reminder_phrase_plural)
            phrase.descrizione = textRPP.text.toString()
            phrase.fromAssets = "Y"
        } else {
            phraseToSearch.descrizione = textRPP.text.toString()
        }
        realm.commitTransaction()
        // view the menu settings fragment initializing MenuSettingsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = Fragment(R.layout.activity_settings_menu)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the contents settings button from the settings menu.
     *
     * the activity is notified to view the contents settings.
     *
     *
     * @param view view of tapped button
     * @see SettingsContentsActivity
     */
    fun submitContents(view: View?) {
        val intent = Intent(context, SettingsContentsActivity::class.java)
        startActivity(intent)
    }
    //
    /**
     * Called when the user taps the advanced settings button from the settings menu.
     *
     * the activity is notified to view the advanced settings.
     * (activity_settings_advanced_settings)
     *
     * @param view view of tapped button
     */
    fun submitAdvancedSettings(view: View?) {
        // view the advanced settings initializing AdvancedSettingsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = Fragment(R.layout.activity_settings_advanced_settings)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the general settings button from the advanced settings menu.
     * (activity_settings_advanced_settings)
     *
     * the activity is notified to view the general settings.
     *
     *
     * @param view view of tapped button
     */
    fun generalSettings(view: View?) {
        // view the general settings initializing GeneralSettingsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = Fragment(R.layout.activity_settings_general_settings)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user click the enable/disable printing radiobutton from general settings.
     *
     * register in the shared preferences
     *
     * @param view view of clicked radiobutton
     */
    fun PrintingRadioButtonClicked(view: View) {
        //
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.enableprinting -> if (checked) {
                sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                editor.putString(
                    getString(R.string.preference_print_permissions),
                    getString(R.string.character_y)
                )
                editor.apply()
            }
            R.id.disableprinting -> if (checked) {
                sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                editor.putString(
                    getString(R.string.preference_print_permissions),
                    getString(R.string.character_n)
                )
                editor.apply()
            }
        }
    }

    /**
     * Called when the user click the title writing type radiobutton from general settings.
     *
     * register in the shared preferences
     *
     * @param view view of clicked radiobutton
     */
    fun titleWritingTypeRadioButtonClicked(view: View) {
        //
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.uppercase -> if (checked) {
                sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                editor.putString(getString(R.string.preference_title_writing_type), getString(R.string.uppercase))
                editor.apply()
            }
            R.id.lowercase -> if (checked) {
                sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                editor.putString(getString(R.string.preference_title_writing_type),
                    getString(R.string.lowercase))
                editor.apply()
            }
        }
    }

    /**
     * Called when the user click the list mode radiobutton from general settings.
     *
     * register order of presentation of lists of names in the shared preferences
     *
     * @param view view of clicked radiobutton
     */
    fun listModeRadioButtonClicked(view: View) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.disablelistmode -> if (checked) {
                sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                editor.putString(
                    getString(R.string.preference_list_mode),
                    getString(R.string.disabled)
                )
                editor.apply()
            }
            R.id.enablesortedlistmode -> if (checked) {
                sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                editor.putString(
                    getString(R.string.preference_list_mode),
                    getString(R.string.sorted)
                )
                editor.apply()
            }
            R.id.enablerandomlistmode -> if (checked) {
                sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                editor.putString(
                    getString(R.string.preference_list_mode),
                    getString(R.string.random)
                )
                editor.apply()
            }
        }
    }

    /**
     * Called when the user taps the save button from general settings.
     *
     * after the checks register allowed margin of error in the shared preferences
     *
     * and the activity is notified to view the the advanced settings fragment.
     * (activity_settings_advanced_settings)
     *
     * @param view view of tapped button
     */
    fun generalSettingsSave(view: View?) {
        val textAllowedMarginOfError = findViewById<View>(R.id.allowedmarginoferror) as EditText
        val valueAllowedMarginOfError = textAllowedMarginOfError.text.toString()
        //
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val preference_AllowedMarginOfError =
            sharedPref.getInt(getString(R.string.preference_allowed_margin_of_error), 20)
        val finalValueAllowedMarginOfError: Int
        finalValueAllowedMarginOfError = try {
            valueAllowedMarginOfError.toInt()
        } catch (e: NumberFormatException) {
            preference_AllowedMarginOfError
        }
        //
        if (finalValueAllowedMarginOfError >= 0 && finalValueAllowedMarginOfError <= 100) {
            val editor = sharedPref.edit()
            editor.putInt(
                getString(R.string.preference_allowed_margin_of_error),
                finalValueAllowedMarginOfError
            )
            editor.apply()
            // view the advanced settings initializing AdvancedSettingsFragment (FragmentTransaction
            // switch between Fragments).
            val frag = Fragment(R.layout.activity_settings_advanced_settings)
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.settings_container, frag)
            ft.addToBackStack(null)
            ft.commit()
        }
    }
    /**
     * Called when the user taps Bluetooth Devices button from the advanced settings menu.
     * (activity_settings_advanced_settings)
     *
     * the activity is notified to view the Bluetooth Devices settings.
     *
     *
     * @param view view of tapped button
     * @see BluetoothDevicesFragment
     */
    fun bluetoothDevicesSettings(view: View?) {
        // view the Bluetooth Devices settings initializing BluetoothDevicesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = BluetoothDevicesFragment(R.layout.activity_settings_bluetooth_devices)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the save button from the Bluetooth Devices settings.
     *
     * after the checks it adds the Bluetooth Devices on realm
     *
     * and the activity is notified to view the updated Bluetooth Devices settings.
     *
     * @param v view of tapped button
     * @see BluetoothDevicesFragment
     *
     * @see BluetoothDevices
     */
    fun bluetoothDevicesSave(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val bdUN = findViewById<View>(R.id.bluetoothdevicesdeviceusername) as EditText
        val bdDN = findViewById<View>(R.id.bluetoothdevicesdevicename) as EditText
        if (bdUN.length() > 0 && bdDN.length() > 0) {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val bd = realm.createObject(
                BluetoothDevices::class.java
            )
            bd.deviceUserName = bdUN.text.toString()
            bd.deviceName = bdDN.text.toString()
            bd.fromAssets = "N"
            realm.commitTransaction()
            //
            doYouWantToImportImages(bdUN.text.toString())
        }
    }
    /**
     * Called when the user taps the save button from the Bluetooth Devices settings.
     *
     * @param deviceUserName string
     */
    fun doYouWantToImportImages(deviceUserName: String) {
            val d = Dialog(this)
            // Setting dialogview
            val window = d.window
            val wlp = window!!.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            window.attributes = wlp
            //
            d.setCancelable(false)
            d.setContentView(R.layout.activity_settings_write_dialog_bluetooth)
            //
            val dialogTitle =
                d.findViewById<TextView>(R.id.bluetoothTV)
            dialogTitle.setText("Vuoi importare le immagini \ndal file NaiveAACimages.zip precedentemente esportato\ndallo smartphone di " + deviceUserName )
            val submitResponseOrRequestButton =
                d.findViewById<ImageButton>(R.id.importImagesButton)
            val cancelTheAnswerOrRequestButton =
                d.findViewById<ImageButton>(R.id.doNotImportImagesButton)
            //
            submitResponseOrRequestButton.requestFocus()
            //
            submitResponseOrRequestButton.setOnClickListener { //
                //
                d.cancel()
                //
                checkboxImagesChecked = true
                checkboxVideosChecked = false
                checkboxSoundsChecked = false
                checkboxPhrasesChecked = false
                checkboxWordPairsChecked = false
                checkboxListsOfNamesChecked = false
                checkboxStoriesChecked = false
                checkboxGrammaticalExceptionsChecked = false
                radiobuttonDataImportAppendClicked = true
                settingsDataImportSave(submitResponseOrRequestButton)
            }
            cancelTheAnswerOrRequestButton.setOnClickListener { //
                d.cancel()
                // view the Bluetooth Devices settings initializing BluetoothDevicesFragment
                // (FragmentTransaction switch between Fragments).
                val frag = BluetoothDevicesFragment(R.layout.activity_settings_bluetooth_devices)
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
            //
            d.show()
    }
    /**
     * on callback from BluetoothDevicesAdapter to this Activity
     *
     * after deleting a Bluetooth Devices the activity is notified to view the Bluetooth Devices settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothDevicesAdapter
     *
     * @see BluetoothDevicesFragment
     */
    override fun reloadBluetoothDevicesFragment() {
        // view the Bluetooth Devices settings initializing BluetoothDevicesFragment
        // (FragmentTransaction switch between Fragments).
        val frag = BluetoothDevicesFragment(R.layout.activity_settings_bluetooth_devices)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user taps the game parameters button from the advanced settings menu.
     * (activity_settings_advanced_settings)
     * the activity is notified to view the game parameters settings.
     *
     *
     * @param view view of tapped button
     * @see GameParametersSettingsFragment
     */
    fun gameParametersSettings(view: View?) {
        // view the game parameters settings initializing GameParametersSettingsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = GameParametersSettingsFragment(R.layout.activity_settings_game_parameters)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user click the radio button from the game parameters settings.
     *
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     * @see GameParametersSettingsFragment
     */
    fun onGameParametersRadioButtonClicked(view: View) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.radio_active -> if (checked) {
                //
                radiobuttonGameParametersActiveClicked = true
                radiobuttonGameParametersNotActiveClicked = false
            }
            R.id.radio_not_active -> if (checked) {
                //
                radiobuttonGameParametersActiveClicked = false
                radiobuttonGameParametersNotActiveClicked = true
            }
        }
    }

    /**
     * Called when the user click the radio button UseVideoAndSound from the game parameters settings.
     *
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     * @see GameParametersSettingsFragment
     */
    fun onGameParametersUseVideoAndSoundClicked(view: View) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.radio_yes -> if (checked) {
                //
                radiobuttonGameParametersUseVideoAndSoundClicked = true
                radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = false
            }
            R.id.radio_no -> if (checked) {
                //
                radiobuttonGameParametersUseVideoAndSoundClicked = false
                radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = true
            }
        }
    }

    /**
     * Called when the user taps the save button from the game parameters settings.
     *
     * after the checks it adds the game parameters on realm
     *
     * and the activity is notified to view the updated game parameters settings.
     *
     * @param v view of tapped button
     * @see GameParametersSettingsFragment
     *
     * @see GameParameters
     */
    fun gameParametersSave(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val gamD = findViewById<View>(R.id.gameDescription) as EditText
        val gamI = findViewById<View>(R.id.gameinfo) as EditText
        val gamJC = findViewById<View>(R.id.gamejavaclass) as EditText
        val gamP = findViewById<View>(R.id.gameparameter) as EditText
        if (gamD.length() > 0 && gamI.length() > 0 && gamJC.length() > 0 && gamP.length() > 0 && filePath != null &&
            filePath != getString(R.string.non_trovato)
        ) {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val gp = realm.createObject(GameParameters::class.java)
            gp.gameName = gamD.text.toString()
            if (radiobuttonGameParametersActiveClicked) gp.gameActive = "A" else gp.gameActive =
                "N" // default
            gp.gameInfo = gamI.text.toString()
            gp.gameJavaClass = gamJC.text.toString()
            gp.gameParameter = gamP.text.toString()
            if (radiobuttonGameParametersUseVideoAndSoundClicked) gp.gameUseVideoAndSound =
                "Y" else gp.gameUseVideoAndSound = "N" // default
            gp.gameIconType = gameIconType
            gp.gameIconPath = filePath
            gp.fromAssets = ""
            realm.commitTransaction()
            // view the game parameters settings initializing GameParametersSettingsFragment (FragmentTransaction
            // switch between Fragments).
            val frag = GameParametersSettingsFragment(R.layout.activity_settings_game_parameters)
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.settings_container, frag)
            ft.addToBackStack(null)
            ft.commit()
        }
    }

    /**
     * on callback from GameParametersAdapter to this Activity
     *
     * after deleting game parameters the activity is notified to view the updated game parameters settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
     *
     * @see GameParametersSettingsFragment
     */
    override fun reloadGameParametersFragmentDeleteGameParameters(position: Int) {
        // delete
        realm = Realm.getDefaultInstance()
        var results = realm.where(GameParameters::class.java).findAll()
        results = results.sort(getString(R.string.gamename))
        realm.beginTransaction()
        val daCancellare = results[position]!!
        daCancellare.deleteFromRealm()
        realm.commitTransaction()
        // view the game parameters settings initializing GameParametersSettingsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = GameParametersSettingsFragment(R.layout.activity_settings_game_parameters)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * on callback from GameParametersAdapter to this Activity
     *
     * for editing a Game Parameter the activity is notified to view the Game Parameters settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
     *
     * @see GameParametersSettingsFragment
     */
    override fun reloadGameParametersFragmentForEditing(position: Int) {
        // view the Game Parameters settings fragment initializing GameParametersSettingsFragment (FragmentTransaction
        // switch between Fragments).
        realm = Realm.getDefaultInstance()
        var results: RealmResults<GameParameters>?
        results = realm.where(GameParameters::class.java).findAll()
        results = results.sort(getString(R.string.gamename))
        //
        val frag = GameParametersSettingsFragment(R.layout.activity_settings_game_parameters)
        //
        val bundle = Bundle()
        val daModificare = results[position]!!
        //
        bundle.putString(getString(R.string.gamename), daModificare.gameName)
        bundle.putString(getString(R.string.gameactive), daModificare.gameActive)
        if (daModificare.gameActive == "A") {
            //
            radiobuttonGameParametersActiveClicked = true
            radiobuttonGameParametersNotActiveClicked = false
        } else {
            //
            radiobuttonGameParametersActiveClicked = false
            radiobuttonGameParametersNotActiveClicked = true
        }
        //
        gameIconType = daModificare.gameIconType
        filePath = daModificare.gameIconPath
        //
        bundle.putString(getString(R.string.gameicontype), daModificare.gameIconType)
        bundle.putString(getString(R.string.gameiconpath), daModificare.gameIconPath)
        bundle.putString(getString(R.string.gameinfo), daModificare.gameInfo)
        bundle.putString(getString(R.string.gamejavaclass), daModificare.gameJavaClass)
        bundle.putString(getString(R.string.gameparameter), daModificare.gameParameter)
        bundle.putString(getString(R.string.gameusevideoandsound), daModificare.gameUseVideoAndSound)
        if (daModificare.gameUseVideoAndSound == "Y") {
            //
            radiobuttonGameParametersUseVideoAndSoundClicked = true
            radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = false
        } else {
            //
            radiobuttonGameParametersUseVideoAndSoundClicked = false
            radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = true
        }
        frag.arguments = bundle
        // delete
        realm.beginTransaction()
        daModificare.deleteFromRealm()
        realm.commitTransaction()
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps import tables button from the advanced settings menu.
     * (activity_settings_advanced_settings)
     * the activity is notified to view the import tables settings.
     *
     *
     * @param view view of tapped button
     */
    fun importTables(view: View?) {
        checkboxImagesChecked = false
        checkboxVideosChecked = false
        checkboxSoundsChecked = false
        checkboxPhrasesChecked = false
        checkboxWordPairsChecked = false
        checkboxListsOfNamesChecked = false
        checkboxStoriesChecked = false
        checkboxGrammaticalExceptionsChecked = false
        // view the import tables settings initializing DataImportSettingsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = Fragment(R.layout.activity_settings_data_import)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user click the radio button from import tables settings.
     *
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     */
    fun onDataImportRadioButtonClicked(view: View) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.radio_replace -> if (checked) //
                radiobuttonDataImportReplaceClicked = true
            R.id.radio_append -> if (checked) //
                radiobuttonDataImportAppendClicked = true
        }
    }

    /**
     * Called when the user click the checkbox from import tables settings.
     *
     * register which checkbox was clicked
     *
     * @param view view of clicked checkbox
     */
    fun onCheckboxClicked(view: View) {
        // Is the view now checked?
        val checked = (view as CheckBox).isChecked
        when (view.getId()) {
            R.id.checkbox_images -> checkboxImagesChecked = if (checked) //
                true else  //
                false
            R.id.checkbox_videos -> checkboxVideosChecked = if (checked) //
                true else  //
                false
            R.id.checkbox_sounds -> checkboxSoundsChecked = if (checked) //
                true else  //
                false
            R.id.checkbox_phrases -> checkboxPhrasesChecked = if (checked) //
                true else  //
                false
            R.id.checkbox_wordpairs -> checkboxWordPairsChecked = if (checked) //
                true else  //
                false
            R.id.checkbox_listsofnames -> checkboxListsOfNamesChecked = if (checked) //
                true else  //
                false
            R.id.checkbox_stories -> checkboxStoriesChecked = if (checked) //
                true else  //
                false
            R.id.checkbox_grammaticalexceptions -> checkboxGrammaticalExceptionsChecked =
                if (checked) //
                    true else  //
                    false
            R.id.checkbox_gameparameters -> checkboxGameParametersChecked = if (checked) //
                true else  //
                false
        }
    }

    /**
     * Called when the user taps the import button from the import tables settings settings.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/65203681/how-to-create-multiple-files-at-once-using-android-storage-access-framework)
     * answer of [Ismail Osunlana](https://stackoverflow.com/users/11355432/ismail-osunlana)
     *
     * @param view view of tapped button
     * @see isStoragePermissionGranted
     *
     */
    fun settingsDataImportSave(view: View?) {
        //
        when (view!!.id) {
            R.id.importImagesButton -> {
                myLayoutId = R.layout.activity_settings_bluetooth_devices
            }
            R.id.importSaveButton -> {
                myLayoutId = R.layout.activity_settings_advanced_settings
            }
        }
        // Choose a directory using the system's file picker.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        csvSearchActivityResultLauncher!!.launch(intent)
    }
    //
    /**
     * setting callbacks to search for csv via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     *
     * import the selected tables to realm
     *
     * and the activity is notified to view the advanced settings menu.
     * (activity_settings_advanced_settings)
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)
     * answer of [Muntashir Akon](https://stackoverflow.com/users/4147849/muntashir-akon)
     *
     *
     * and to [stackoverflow](https://stackoverflow.com/questions/7620401/how-to-convert-image-file-data-in-a-byte-array-to-a-bitmap)
     * answer of [Uttam](https://stackoverflow.com/users/840861/uttam)
     *
     * @see copyFileFromSharedToInternalStorage
     *
     * @see Images
     *
     * @see Videos
     *
     * @see GameParameters
     *
     * @see GrammaticalExceptions
     *
     * @see ListsOfNames
     *
     * @see Phrases
     *
     * @see Stories
     *
     * @see WordPairs
     */
    fun setCsvSearchActivityResultLauncher() {
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        csvSearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        csvTreeUri = null
                        //                            filePath = getString(R.string.non_trovato);
                        if (resultData != null) {
                            csvTreeUri = Objects.requireNonNull(resultData).data
                            val inputFolder = DocumentFile.fromTreeUri(context, csvTreeUri!!)
                            //
                            if (isStoragePermissionGranted) {
                                var importMode = getString(R.string.append) // default import mode
                                if (radiobuttonDataImportReplaceClicked) importMode = getString(R.string.replace)
                                if (checkboxImagesChecked) {
                                    assert(inputFolder != null)
                                    //
                                    imagesImportFromZip(inputFolder!!)
                                    //
//                                    val rootPath = context.filesDir.absolutePath
//                                    val dirName = "Images"
//                                    val inputFile = File("$rootPath/$dirName/images.csv")
//                                    val outputFile = File("$rootPath/images.csv")
//                                    copyFile(inputFile, outputFile)
                                    //
//                                    val documentFileNewFile = inputFolder.findFile("images.csv")!!
//                                    val csvFileUri = documentFileNewFile.uri
//                                    try {
//                                        copyFileFromSharedToInternalStorage(
//                                            context,
//                                            csvFileUri,
//                                            "images.csv"
//                                        )
//                                    } catch (e: IOException) {
//                                        e.printStackTrace()
//                                    }
                                    Images.importFromCsvFromInternalStorage(
                                        context,
                                        realm,
                                        importMode
                                    )
                                }
                                if (checkboxVideosChecked) {
                                    assert(inputFolder != null)
                                    val documentFileNewFile = inputFolder!!.findFile("videos.csv")!!
                                    val csvFileUri = documentFileNewFile.uri
                                    try {
                                        copyFileFromSharedToInternalStorage(
                                            context,
                                            csvFileUri,
                                            "videos.csv"
                                        )
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    Videos.importFromCsvFromInternalStorage(
                                        context,
                                        realm,
                                        importMode
                                    )
                                }
                                if (checkboxSoundsChecked) {
                                    assert(inputFolder != null)
                                    val documentFileNewFile = inputFolder!!.findFile("sounds.csv")!!
                                    val csvFileUri = documentFileNewFile.uri
                                    try {
                                        copyFileFromSharedToInternalStorage(
                                            context,
                                            csvFileUri,
                                            "sounds.csv"
                                        )
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    Sounds.importFromCsvFromInternalStorage(
                                        context,
                                        realm,
                                        importMode
                                    )
                                }
                                if (checkboxPhrasesChecked) {
                                    assert(inputFolder != null)
                                    val documentFileNewFile =
                                        inputFolder!!.findFile("phrases.csv")!!
                                    val csvFileUri = documentFileNewFile.uri
                                    try {
                                        copyFileFromSharedToInternalStorage(
                                            context,
                                            csvFileUri,
                                            "phrases.csv"
                                        )
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    Phrases.importFromCsvFromInternalStorage(
                                        context,
                                        realm,
                                        importMode
                                    )
                                }
                                if (checkboxWordPairsChecked) {
                                    assert(inputFolder != null)
                                    val documentFileNewFile =
                                        inputFolder!!.findFile("wordpairs.csv")!!
                                    val csvFileUri = documentFileNewFile.uri
                                    try {
                                        copyFileFromSharedToInternalStorage(
                                            context,
                                            csvFileUri,
                                            "wordpairs.csv"
                                        )
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    WordPairs.importFromCsvFromInternalStorage(
                                        context,
                                        realm,
                                        importMode
                                    )
                                }
                                if (checkboxListsOfNamesChecked) {
                                    assert(inputFolder != null)
                                    val documentFileNewFile =
                                        inputFolder!!.findFile("listsofnames.csv")!!
                                    val csvFileUri = documentFileNewFile.uri
                                    try {
                                        copyFileFromSharedToInternalStorage(
                                            context,
                                            csvFileUri,
                                            "listsofnames.csv"
                                        )
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    ListsOfNames.importFromCsvFromInternalStorage(
                                        context,
                                        realm,
                                        importMode
                                    )
                                }
                                if (checkboxStoriesChecked) {
                                    assert(inputFolder != null)
                                    val documentFileNewFile =
                                        inputFolder!!.findFile("stories.csv")!!
                                    val csvFileUri = documentFileNewFile.uri
                                    try {
                                        copyFileFromSharedToInternalStorage(
                                            context,
                                            csvFileUri,
                                            "stories.csv"
                                        )
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    Stories.importFromCsvFromInternalStorage(
                                        context,
                                        realm,
                                        importMode
                                    )
                                    if (importMode == getString(R.string.replace)) {
                                        renumberStories(realm)
                                    }
                                }
                                if (checkboxGrammaticalExceptionsChecked) {
                                    assert(inputFolder != null)
                                    val documentFileNewFile =
                                        inputFolder!!.findFile("grammaticalexceptions.csv")!!
                                    val csvFileUri = documentFileNewFile.uri
                                    try {
                                        copyFileFromSharedToInternalStorage(
                                            context,
                                            csvFileUri,
                                            "grammaticalexceptions.csv"
                                        )
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    GrammaticalExceptions.importFromCsvFromInternalStorage(
                                        context,
                                        realm,
                                        importMode
                                    )
                                }
                                if (checkboxGameParametersChecked) {
                                    assert(inputFolder != null)
                                    val documentFileNewFile =
                                        inputFolder!!.findFile("gameparameters.csv")!!
                                    val csvFileUri = documentFileNewFile.uri
                                    try {
                                        copyFileFromSharedToInternalStorage(
                                            context,
                                            csvFileUri,
                                            "gameparameters.csv"
                                        )
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    GameParameters.importFromCsvFromInternalStorage(
                                        context,
                                        realm,
                                        importMode
                                    )
                                }
                                // removed the import of history because it gives problems:
                                // phraseNumber on shared preferences always starts from 1
                                // the shared preferences should be set to the latest phrasenumber
                                // of imported history
                            }
                            when (myLayoutId) {
                                R.layout.activity_settings_bluetooth_devices -> {
                                    val frag = BluetoothDevicesFragment(myLayoutId)
                                    val ft = supportFragmentManager.beginTransaction()
                                    ft.replace(R.id.settings_container, frag)
                                    ft.addToBackStack(null)
                                    ft.commit()
                                }
                                R.layout.activity_settings_advanced_settings -> {
                                    // view the advanced settings initializing AdvancedSettingsFragment (FragmentTransaction
                                    // switch between Fragments).
                                    val frag = Fragment(myLayoutId)
                                    val ft = supportFragmentManager.beginTransaction()
                                    ft.replace(R.id.settings_container, frag)
                                    ft.addToBackStack(null)
                                    ft.commit()
                                }
                            }
                        }
                    }
                }
            })

        //
    }
    //
    fun imagesImportFromZip(inputFolder: DocumentFile) {
        // creo le dir della storia
        val rootPath = context.filesDir.absolutePath
        val dirName = "NaiveAACimages"
        val d = File("$rootPath/$dirName")
        if (!d.exists()) {
            d.mkdirs()
        } else {
            d.delete()
            d.mkdirs()
        }
        assert(inputFolder != null)
        val documentFileNewFile =
            inputFolder.findFile("$dirName.zip")!!
        val zipFileUri = documentFileNewFile.uri
        try {
            copyFileFromSharedToInternalStorage(
                context,
                zipFileUri,
                "$dirName.zip"
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // unzippo la directory di input
        try {
            extractFolder(
                File(rootPath),
                File("$rootPath/$dirName.zip")
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // creo copia dei file immagini
        copyFilesInFolderToRoot(context, "$rootPath/$dirName")
    }

    /**
     * Called when the user taps export tables button from the advanced settings menu.
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/65203681/how-to-create-multiple-files-at-once-using-android-storage-access-framework)
     * answer of [Ismail Osunlana](https://stackoverflow.com/users/11355432/ismail-osunlana)
     *
     * @param view view of tapped button
     * @see .isStoragePermissionGranted
     */
    fun exportTables(view: View?) {
        // Choose a directory using the system's file picker.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        exportCsvSearchActivityResultLauncher!!.launch(intent)
    }

    /**
     * Called when the user taps export tables button from the advanced settings menu.
     *
     * at the end the activity is notified to view the settings menu.
     *
     * @see isStoragePermissionGranted
     *
     * @see Images
     *
     * @see Videos
     *
     * @see GameParameters
     *
     * @see GrammaticalExceptions
     *
     * @see ListsOfNames
     *
     * @see Phrases
     *
     * @see Stories
     *
     * @see WordPairs
     *
     * @see History
     *
     * @see PictogramsAllToModify
     */
    fun setExportCsvSearchActivityResultLauncher() {
        //
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        exportCsvSearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        csvTreeUri = null
                        //
                        if (resultData != null) {
                            csvTreeUri = Objects.requireNonNull(resultData).data
                            val outputFolder = DocumentFile.fromTreeUri(context, csvTreeUri!!)
                            //
                            if (isStoragePermissionGranted) {
                                Images.exporttoCsv(context, realm)
                                //
                                imagesExportToZip(outputFolder!!)
                                //
                                Videos.exporttoCsv(context, realm)
                                Sounds.exporttoCsv(context, realm)
                                Phrases.exporttoCsv(context, realm)
                                WordPairs.exporttoCsv(context, realm)
                                ListsOfNames.exporttoCsv(context, realm)
                                Stories.exporttoCsv(context, realm)
                                History.exporttoCsv(context, realm)
                                PictogramsAllToModify.exporttoCsv(context, realm)
                                GameParameters.exporttoCsv(context, realm)
                                GrammaticalExceptions.exporttoCsv(context, realm)
                                assert(outputFolder != null)
                                //
                                try {
                                    copyFileFromInternalToSharedStorage(
                                        context,
                                        outputFolder!!,
                                        "images.csv"
                                    )
                                    copyFileFromInternalToSharedStorage(context,outputFolder, "videos.csv")
                                    copyFileFromInternalToSharedStorage(context,outputFolder, "sounds.csv")
                                    copyFileFromInternalToSharedStorage(context,outputFolder, "phrases.csv")
                                    copyFileFromInternalToSharedStorage(
                                        context,
                                        outputFolder,
                                        "wordpairs.csv"
                                    )
                                    copyFileFromInternalToSharedStorage(
                                        context,
                                        outputFolder,
                                        "listsofnames.csv"
                                    )
                                    copyFileFromInternalToSharedStorage(context,outputFolder, "stories.csv")
                                    copyFileFromInternalToSharedStorage(context,outputFolder, "history.csv")
                                    copyFileFromInternalToSharedStorage(
                                        context,
                                        outputFolder,
                                        "pictogramsalltomodify.csv"
                                    )
                                    copyFileFromInternalToSharedStorage(
                                        context,
                                        outputFolder,
                                        "gameparameters.csv"
                                    )
                                    copyFileFromInternalToSharedStorage(
                                        context,
                                        outputFolder,
                                        "grammaticalexceptions.csv"
                                    )
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                //
                            }
                            // view the fragment settings initializing MenuSettingsFragment (FragmentTransaction
                            // switch between Fragments).
                            val frag = Fragment(R.layout.activity_settings_menu)
                            val ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.settings_container, frag)
                            ft.addToBackStack(null)
                            ft.commit()
                        }
                    }
                }
            })
    }
    //
    fun imagesExportToZip(outputFolder: DocumentFile) {
        // immagini
        // creo le dir delle immagini
        val rootPath = context.filesDir.absolutePath
        val dirName = "NaiveAACimages"
        val d = File("$rootPath/$dirName")
        if (!d.exists()) {
            d.mkdirs()
        } else {
            d.delete()
            d.mkdirs()
        }
        // creo copia del file images.csv
        var finput: File
        var foutput: File
        finput = File("$rootPath/images.csv")
        foutput = File(
                "$rootPath/NaiveAACimages/" + "images.csv"
            )
        try {
                copyFile(finput, foutput)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        // creo un set (in modo da non ammettere duplicati) con i media file utilizzati
        val imagesSet = HashSet<String?>()
        val resultsImages = realm.where(
            Images::class.java
        ).findAll()
        //
        val count = resultsImages.size
        if (count != 0) {
            var irrh = 0
            while (irrh < count) {
                if (resultsImages[irrh]!!.descrizione != getString(R.string.io))
                { imagesSet.add(
                    resultsImages[irrh]!!.uri
                    )
                }
                irrh++
            }
        }
        // trasformo il set ottenuto in una list contenente i path dei media file utilizzati
        val imagesList: MutableList<String?> = ArrayList()
        imagesList.addAll(imagesSet)
        // creo copia dei file immagini
//        var finput: File
//        var foutput: File
        val imagesSize = imagesList.size
        var irrh = 0
        while (irrh < imagesSize) {
            finput = File(imagesList[irrh]!!)
            foutput = File(
                "$rootPath/NaiveAACimages/" + imagesList[irrh]!!
                    .substring(
                        imagesList[irrh]!!.lastIndexOf(
                            "/"
                        ) + 1
                    )
            )
            try {
                copyFile(finput, foutput)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            irrh++
        }
        // zippo la directory di output
        zipFileAtPath(
            "$rootPath/$dirName",
            "$rootPath/$dirName.zip"
        )
//        assert(outputFolder != null)
        // copio il file zip sulla shared storage
        try {
            copyFileZipFromInternalToSharedStorage(
                context,
                outputFolder,
                "$dirName.zip"
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    //permission is automatically granted on sdk<23 upon installation
    /**
     * check permissions.
     *
     * @return boolean whit true if permission is granted
     */
    val isStoragePermissionGranted: Boolean
        get() =
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
                true
            } else {

                // Log.v(TAGPERMISSION,getString(R.string.permission_is_revoked));
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }

    /**
     * Called when the user taps grammatical exceptions button from the advanced settings menu.
     * (activity_settings_advanced_settings)
     * the activity is notified to view the grammatical exceptions settings.
     *
     *
     * @param view view of tapped button
     * @see GrammaticalExceptionsFragment
     */
    fun grammaticalExceptions(view: View?) {
        // view the grammatical exceptions settings initializing GrammaticalExceptionsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = GrammaticalExceptionsFragment(R.layout.activity_settings_grammatical_exceptions)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the save button from the grammatical exceptions settings.
     *
     * after the checks it adds the grammatical exceptions on realm
     *
     * and the activity is notified to view the updated grammatical exceptions settings.
     *
     * @param v view of tapped button
     * @see GrammaticalExceptionsFragment
     *
     * @see GrammaticalExceptions
     */
    fun GrammaticalExceptionsSave(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val geK = findViewById<View>(R.id.grammaticalexceptionskeyword) as EditText
        val geET = findViewById<View>(R.id.exceptiontype) as EditText
        val geE1 = findViewById<View>(R.id.exception1) as EditText
        val geE2 = findViewById<View>(R.id.exception2) as EditText
        val geE3 = findViewById<View>(R.id.exception3) as EditText
        if (geK.length() > 0 && geET.length() > 0 && geE1.length() > 0 && geE2.length() > 0 && geE3.length() > 0) {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val ge = realm.createObject(
                GrammaticalExceptions::class.java
            )
            ge.keyword = geK.text.toString()
            ge.exceptionType = geET.text.toString()
            ge.exception1 = geE1.text.toString()
            ge.exception2 = geE2.text.toString()
            ge.exception3 = geE3.text.toString()
            ge.fromAssets = "Y"
            realm.commitTransaction()
            // view the grammatical exceptions settings initializing GrammaticalExceptionsFragment
            // (FragmentTransaction switch between Fragments).
            val frag = GrammaticalExceptionsFragment(R.layout.activity_settings_grammatical_exceptions)
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.settings_container, frag)
            ft.addToBackStack(null)
            ft.commit()
        }
    }

    /**
     * on callback from GrammaticalExceptionsAdapter to this Activity
     *
     * after deleting a grammatical exceptions the activity is notified to view the grammatical exceptions settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptionsAdapter
     *
     * @see GrammaticalExceptionsFragment
     */
    override fun reloadGrammaticalExceptionsFragment() {
        // view the grammatical exceptions settings initializing GrammaticalExceptionsFragment
        // (FragmentTransaction switch between Fragments).
        val frag = GrammaticalExceptionsFragment(R.layout.activity_settings_grammatical_exceptions)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the pictograms to modify button from the advanced settings menu.
     * (activity_settings_advanced_settings)
     * the activity is notified to view the pictograms to modify settings.
     *
     *
     * @param view view of tapped button
     * @see PictogramsToModifyFragment
     */
    fun pictogramsToModify(view: View?) {
        // view the pictograms to modify settings initializing PictogramsToModifyFragment
        // (FragmentTransaction switch between Fragments).
        val ft: FragmentTransaction
        val pictogramsToModifyFragment = PictogramsToModifyFragment(R.layout.activity_settings_pictograms_to_modify)
        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, pictogramsToModifyFragment)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the save button from the pictograms to modify settings.
     *
     * after the checks it adds the pictograms to modify on realm
     *
     * and the activity is notified to view the updated pictograms to modify settings.
     *
     * @param v view of tapped button
     * @see PictogramsToModifyFragment
     *
     * @see PictogramsAllToModify
     */
    fun pictogramsToModifySave(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val textPictogramId = findViewById<View>(R.id.pictogramid) as EditText
        val textModificationType = findViewById<View>(R.id.modificationtype) as EditText
        val textKeyword = findViewById<View>(R.id.keyword) as EditText
        val textPlural = findViewById<View>(R.id.plural) as EditText
        // Note that the realm object was generated with the createObject method
        // and not with the new operator.
        // The modification operations will be performed within a Transaction.
        realm.beginTransaction()
        val pictogramsAllToModify = realm.createObject(
            PictogramsAllToModify::class.java
        )
        // set the fields here
        pictogramsAllToModify.set_id(textPictogramId.text.toString())
        pictogramsAllToModify.modificationType = textModificationType.text.toString()
        pictogramsAllToModify.keyword = textKeyword.text.toString()
        pictogramsAllToModify.plural = textPlural.text.toString()
        realm.commitTransaction()
        // view the pictograms to modify settings initializing PictogramsToModifyFragment
        // (FragmentTransaction switch between Fragments).
        val frag = PictogramsToModifyFragment(R.layout.activity_settings_pictograms_to_modify)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * on callback from PictogramsToModifyAdapter to this Activity
     *
     * after deleting a pictogram to modify the activity is notified to view the pictograms to modify settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModifyAdapter
     *
     * @see PictogramsToModifyFragment
     */
    override fun reloadPictogramsToModifyFragment() {
        // view the pictograms to modify settings initializing PictogramsToModifyFragment
        // (FragmentTransaction switch between Fragments).
        val ft: FragmentTransaction
        val pictogramsToModifyFragment = PictogramsToModifyFragment(R.layout.activity_settings_pictograms_to_modify)
        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, pictogramsToModifyFragment)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the withdraw consent to the processing of personal data button from the advanced settings menu.
     * (activity_settings_advanced_settings)
     * Refer to [developer.android.com](https://developer.android.com/guide/components/activities/tasks-and-back-stack)
     *
     * @param view view of tapped button
     * @see MainActivity
     *
     */
    fun withdrawConsent(view: View?) {
        // registers withdraw consent to the processing of personal data
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putString(
            getString(R.string.preference_ConsentToTheProcessingOfPersonalData),
            getString(R.string.character_n)
        )
        editor.apply()
        // does not allow access to the app
        // finishAndRemoveTask();
        //
        val i = Intent(this, MainActivity::class.java)
        // FLAG_ACTIVITY_CLEAR_TOP
        // If the activity being started is already running in the current task,
        // then—instead of launching a new instance of that activity—the system destroys all the other activities on top of it.
        // The intent is delivered to the resumed instance of the activity, now on top, through onNewIntent().
        // ( MainActivity.class with launchMode attribute ="singleTask" )
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
    }
    //
    /**
     * Called when the user taps the disable Firebase button from the advanced settings menu.
     * (activity_settings_advanced_settings)
     *
     * @param view view of tapped button
     */
    fun disableFirebase(view: View?) {
        // disable Firebase Analytics and Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
    }
    //
    /**
     * Called when the user taps the test crash button from the advanced settings menu.
     * (activity_settings_advanced_settings)
     *
     *
     * @param view view of tapped button
     */
    fun testCrash(view: View?) {
        // Crash Test with Firebase Crashlytics
        throw RuntimeException("Test Crash") // Force a crash
    }
    // ActivityResultLauncher
    var imageSearchGameParametersActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    var imageSearchAccountActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var csvSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var exportCsvSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var csvTreeUri: Uri? = null
    //
    @JvmField
    var uri: Uri? = null
    @JvmField
    var gameIconType: String? = null
    @JvmField
    var filePath: String? = null
    //    @JvmField
    lateinit var byteArray: ByteArray
    //
    /**
     * setting callbacks to search for images and videos via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)
     * answer of [Muntashir Akon](https://stackoverflow.com/users/4147849/muntashir-akon)
     * and
     * Refer to [stackoverflow](https://stackoverflow.com/questions/56651444/deprecated-getbitmap-with-api-29-any-alternative-codes)
     * answer of [Ally](https://stackoverflow.com/users/6258197/ally)
     *
     * @see .getFilePath
     *
     * @see .showImage
     */
    fun setActivityResultLauncher() {
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        //
        imageSearchGameParametersActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        uri = null
                        filePath = getString(R.string.non_trovato)
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            try {
                                filePath = getFilePath(context, uri)
                                gameIconType = "S"
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
                            val takeFlags =
                                resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                            context.contentResolver.takePersistableUriPermission(
                                uri!!,
                                takeFlags
                            )
                            //
                            var bitmap: Bitmap? = null
                            //
                            try {
                                val source = ImageDecoder.createSource(
                                    context.contentResolver,
                                    uri!!
                                )
                                bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                                    decoder.setTargetSampleSize(1) // shrinking by
                                    decoder.isMutableRequired = true // this resolve the hardware type of bitmap problem
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            //
                            val stream = ByteArrayOutputStream()
                            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            byteArray = stream.toByteArray()
                            //
                            val myImage: ImageView
                            myImage = findViewById<View>(R.id.imageviewgameicon) as ImageView
                            showImage(context, uri, myImage)
                        }
                    }
                }
            })
        //
        imageSearchAccountActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                //                @RequiresApi(Build.VERSION_CODES.P)
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        uri = null
                        filePath = getString(R.string.non_trovato)
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            try {
                                filePath = getFilePath(context, uri)
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
                            if (filePath != getString(R.string.non_trovato))
                            {
                                if (filePath == "da download") {
                                    val ctw = ContextThemeWrapper(context, R.style.CustomSnackbarTheme)
                                    val snackbar = Snackbar.make(
                                        ctw,
                                        findViewById(R.id.imageviewaccounticon),
                                        "al momento l'app non è in grado di accedere ad immagini nella cartella download",
                                        10000
                                    )
                                    snackbar.setTextMaxLines(5)
                                    snackbar.setTextColor(Color.BLACK)
                                    snackbar.show()
                                }
                                else {
                                    val takeFlags =
                                        resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    context.contentResolver.takePersistableUriPermission(
                                        uri!!,
                                        takeFlags
                                    )
                                    //
                                    var bitmap: Bitmap? = null
                                    //
                                    try {
                                        val source = ImageDecoder.createSource(
                                            context.contentResolver,
                                            uri!!
                                        )
                                        bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                                            decoder.setTargetSampleSize(1) // shrinking by
                                            decoder.isMutableRequired = true // this resolve the hardware type of bitmap problem
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    //
                                    //
                                    val stream = ByteArrayOutputStream()
                                    bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                    byteArray = stream.toByteArray()
                                    //
                                    val myImage: ImageView
                                    myImage = findViewById<View>(R.id.imageviewaccounticon) as ImageView
                                    showImage(context, uri, myImage)
                                }
                            }
                        }
                    }
                }
            })
    }
    /**
     * Called when the user taps the image search button.
     *
     * @param v view of tapped button
     */
    fun imageSearch(v: View) {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.type = "image/*"
        when (v.id) {
            R.id.buttonimagesearchgameparameters ->                 /*  the instructions of the button */imageSearchGameParametersActivityResultLauncher!!.launch(
                intent
            )
            R.id.buttonimagesearchaccount ->                 /*  the instructions of the button */imageSearchAccountActivityResultLauncher!!.launch(
                intent
            )
        }
    }
    companion object {
    }
}