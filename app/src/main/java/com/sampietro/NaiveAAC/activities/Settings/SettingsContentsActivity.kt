package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter.ListsOfNamesAdapterInterface
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass.onFragmentEventListenerSettings
import android.widget.TextView
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import android.widget.EditText
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentManager
import com.sampietro.NaiveAAC.activities.Game.Game2.SettingsStoriesQuickRegistrationActivity
import io.realm.Realm

/**
 * <h1>SettingsContentsActivity</h1>
 *
 * **SettingsContentsActivity** app settings.
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.WordPairs.WordPairsAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter
 */
class SettingsContentsActivity : AccountActivityAbstractClass(), ListsOfNamesAdapterInterface,
    onFragmentEventListenerSettings {
    var message = "messaggio non formato"
    var textView: TextView? = null

    //
    lateinit var fragmentManager: FragmentManager

    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see .setActivityResultLauncher
     *
     * @see ActionbarFragment
     *
     * @see ContentsFragment
     *
     * @see androidx.appcompat.app.AppCompatActivity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        //
        setActivityResultLauncher()
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(R.id.settings_container, ContentsFragment(), "ContentsFragment")
                .commit()
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
    }
    //
    /**
     * receives calls from fragment listeners.
     *
     * @param v view of calling fragment
     * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
     */
    override fun receiveResultSettings(v: View?) {
        rootViewFragment = v
    }
    //
    /**
     * Called when the user taps the lists of names button from the contents settings menu.
     *
     * the activity is notified to view the lists of names settings.
     *
     *
     * @param view view of tapped button
     * @see ContentsFragment
     *
     * @see ListsOfNamesFragment
     */
    fun submitListsOfNames(view: View?) {
        // view the lists of names settings fragment initializing ListsOfNamesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ListsOfNamesFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the add button from lists of names settings.
     *
     * after the checks it adds the name on realm
     *
     * and the activity is notified to view the updated lists of names settings.
     *
     * @param v view of tapped button
     * @see ListsOfNamesFragment
     *
     * @see ListsOfNames
     */
    fun nameAddToList(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val textWord1 = findViewById<View>(R.id.keywordtoadd) as EditText
        val textWord2 = findViewById<View>(R.id.nametoadd) as EditText
        val textWord3 = findViewById<View>(R.id.uritypenametoadd) as EditText
        val textWord4 = findViewById<View>(R.id.urinametoadd) as EditText
        // Note that the realm object was generated with the createObject method
        // and not with the new operator.
        // The modification operations will be performed within a Transaction.
        realm.beginTransaction()
        val listsOfNames = realm.createObject(ListsOfNames::class.java)
        // set the fields here
        listsOfNames.keyword = textWord1.text.toString()
        listsOfNames.word = textWord2.text.toString()
        listsOfNames.uriType = textWord3.text.toString()
        listsOfNames.uri = textWord4.text.toString()
        listsOfNames.fromAssets = ""
        realm.commitTransaction()
        // view the lists of names settings initializing ListsOfNamesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ListsOfNamesFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * on callback from ListOfNamesAdapter to this Activity
     *
     * after deleting a name the activity is notified to view the updated lists of names settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter
     *
     * @see ListsOfNamesFragment
     */
    override fun reloadListOfNamesFragment() {
        // view the lists of names settings initializing ListsOfNamesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ListsOfNamesFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the word pairs button from the contents settings menu.
     *
     *
     * @param view view of tapped button
     * @see ContentsFragment
     *
     * @see SettingsWordPairsActivity
     */
    fun submitWordPairs(view: View?) {
        /*
                navigate to settings word pairs screen
        */
        val intent = Intent(context, SettingsWordPairsActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called when the user taps the stories add word button from the contents settings menu.
     *
     * the activity is notified to view the stories settings.
     *
     *
     * @param view view of tapped button
     * @see ContentsFragment
     *
     * @see SettingsStoriesActivity
     */
    fun submitStories(view: View?) {
        /*
                navigate to settings stories screen
        */
        val intent = Intent(context, SettingsStoriesActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called when the user taps the Stories - Quick Registration button from the contents settings menu.
     *
     *
     * @param view view of tapped button
     * @see ContentsFragment
     *
     * @see SettingsStoriesQuickRegistrationActivity
     */
    fun submitStoriesQuickRegistration(view: View?) {
        /*
                navigate to settings Stories Quick Registration screen
        */
        val intent = Intent(context, SettingsStoriesQuickRegistrationActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called when the user taps the Stories - import/export button from the contents settings menu.
     *
     *
     * @param view view of tapped button
     * @see ContentsFragment
     *
     * @see SettingsStoriesImportExportActivity
     */
    fun submitStoriesImportExport(view: View?) {
        /*
                navigate to settings Stories Import/Export screen
        */
        val intent = Intent(context, SettingsStoriesImportExportActivity::class.java)
        startActivity(intent)
    }
}