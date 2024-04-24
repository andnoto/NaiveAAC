package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter.ListsOfNamesAdapterInterface
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import android.widget.EditText
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.ActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Game.Game2.SettingsStoriesQuickRegistrationActivity
import com.sampietro.NaiveAAC.activities.Grammar.VoiceToBeRecordedInListsOfNames
import com.sampietro.NaiveAAC.activities.Grammar.VoiceToBeRecordedInListsOfNamesViewModel
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

/**
 * <h1>SettingsContentsActivity</h1>
 *
 * **SettingsContentsActivity** app settings.
 *
 * @version     5.0, 01/04/2024
 * @see SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.WordPairs.WordPairsAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter
 */
class SettingsContentsActivity : ActivityAbstractClass(), ListsOfNamesAdapterInterface
    {
    /*
    used for viewmodel
     */
    private var voiceToBeRecordedInListsOfNames: VoiceToBeRecordedInListsOfNames? = null
    private lateinit var viewModel: VoiceToBeRecordedInListsOfNamesViewModel
    /*

     */
    lateinit var fragmentManager: FragmentManager
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see setActivityResultLauncher
     *
     * @see ActionbarFragment
     *
     * @see androidx.appcompat.app.AppCompatActivity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
        */
        // In the Activity#onCreate make the only setItem
        voiceToBeRecordedInListsOfNames = VoiceToBeRecordedInListsOfNames()
        viewModel = ViewModelProvider(this).get(
            VoiceToBeRecordedInListsOfNamesViewModel::class.java
        )
        viewModel.setItem(voiceToBeRecordedInListsOfNames!!)
        clearFieldsOfViewmodelDataClass()
        //
//        setActivityResultLauncher()
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(R.id.settings_container, Fragment(R.layout.activity_settings_contents_menu), "ContentsFragment")
                .commit()
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
    }
    /**
     * Called when the user taps the lists of names button from the contents settings menu.
     *
     * the activity is notified to view the lists of names settings.
     *
     *
     * @param view view of tapped button
     * @see ListsOfNamesFragment
     */
    fun submitListsOfNames(view: View?) {
        // view the lists of names settings fragment initializing ListsOfNamesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ListsOfNamesFragment(R.layout.activity_settings_lists_of_names)
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
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInListsOfNames: VoiceToBeRecordedInListsOfNames ->
                // Perform an action with the latest item data
                val textWord1 = findViewById<View>(R.id.keywordtoadd) as EditText
                val textWord2 = findViewById<View>(R.id.nametoadd) as EditText
                val textWord3 = findViewById<View>(R.id.elementactive) as EditText
                val textWord4 = findViewById<View>(R.id.ismenuitem) as EditText
                // cancello il vecchio item
                val resultsListsOfNames = realm.where(ListsOfNames::class.java)
                    .equalTo(
                        "keyword",
                        textWord1.text.toString()
                    )
                    .equalTo("word", textWord2.text.toString())
                    .findAll()
                val resultsListsOfNamesSize = resultsListsOfNames.size
                //
                if (resultsListsOfNamesSize > 0) {
                    realm.beginTransaction()
                    resultsListsOfNames.deleteAllFromRealm()
                    realm.commitTransaction()
                }
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction()
                val listsOfNames = realm.createObject(ListsOfNames::class.java)
                // set the fields here
                listsOfNames.keyword = textWord1.text.toString()
                listsOfNames.word = textWord2.text.toString()
                listsOfNames.elementActive = textWord3.text.toString()
                listsOfNames.isMenuItem = textWord4.text.toString()
                listsOfNames.fromAssets = voiceToBeRecordedInListsOfNames.fromAssets
                realm.commitTransaction()
                //
                clearFieldsOfViewmodelDataClass()
                // view the lists of names settings initializing ListsOfNamesFragment (FragmentTransaction
                // switch between Fragments).
                val frag = ListsOfNamesFragment(R.layout.activity_settings_lists_of_names)
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }
    /**
     * clear fields of viewmodel data class
     *
     *
     * @see VoiceToBeRecordedInListsOfNames
     */
    fun clearFieldsOfViewmodelDataClass() {
        voiceToBeRecordedInListsOfNames!!.keyword = ""
        voiceToBeRecordedInListsOfNames!!.word = ""
        voiceToBeRecordedInListsOfNames!!.elementActive = ""
        voiceToBeRecordedInListsOfNames!!.isMenuItem = ""
        voiceToBeRecordedInListsOfNames!!.fromAssets = ""
    }
    override fun reloadListOfNamesFragmentForDeletion(position: Int) {
        var results: RealmResults<ListsOfNames>
        results = realm.where(ListsOfNames::class.java).findAll()
        //
        val mStrings1 = arrayOf(getString(R.string.keyword), "word")
        val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING)
        results = results.sort(mStrings1, mStrings2)
        realm.beginTransaction()
        val daCancellare = results[position]
        daCancellare!!.deleteFromRealm()
        realm.commitTransaction()
        //
        reloadListOfNamesFragment()
    }

    override fun reloadListOfNamesFragmentForInsertion(position: Int) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInListsOfNames: VoiceToBeRecordedInListsOfNames ->
                realm = Realm.getDefaultInstance()
                var results: RealmResults<ListsOfNames>
                results = realm.where(ListsOfNames::class.java).findAll()
                //
                val mStrings1 = arrayOf(getString(R.string.keyword), "word")
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                val daInserire = results[position]!!
                voiceToBeRecordedInListsOfNames.keyword = daInserire.keyword
                reloadListOfNamesFragment()
            }
    }

    override fun reloadListOfNamesFragmentForEditing(position: Int) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInListsOfNames: VoiceToBeRecordedInListsOfNames ->
                realm = Realm.getDefaultInstance()
                var results: RealmResults<ListsOfNames>
                results = realm.where(ListsOfNames::class.java).findAll()
                //
                val mStrings1 = arrayOf(getString(R.string.keyword), "word")
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                val daModificare = results[position]!!
                voiceToBeRecordedInListsOfNames.keyword = daModificare.keyword
                voiceToBeRecordedInListsOfNames.word = daModificare.word
                voiceToBeRecordedInListsOfNames.elementActive = daModificare.elementActive
                voiceToBeRecordedInListsOfNames.isMenuItem = daModificare.isMenuItem
                voiceToBeRecordedInListsOfNames.fromAssets = daModificare.fromAssets
                //
                reloadListOfNamesFragment()
            }
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
    fun reloadListOfNamesFragment() {
        // view the lists of names settings initializing ListsOfNamesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ListsOfNamesFragment(R.layout.activity_settings_lists_of_names)
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