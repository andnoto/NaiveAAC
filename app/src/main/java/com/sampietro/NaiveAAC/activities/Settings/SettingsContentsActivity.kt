package com.sampietro.NaiveAAC.activities.Settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.ActivityAbstractClass
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithListenerWithoutConstructor
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromSharedToInternalStorageAndGetPath
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2ViewModelItem
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter.ListsOfNamesAdapterInterface
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacFragment
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacRecyclerViewAdapterInterface
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.net.URISyntaxException
import java.util.Objects

/**
 * <h1>SettingsContentsActivity</h1>
 *
 * **SettingsContentsActivity** app settings.
 *
 * @version     5.0, 01/04/2024
 * @see ListsOfNamesAdapterInterface
 */
class SettingsContentsActivity : ActivityAbstractClass(),
    FragmentAbstractClassWithListenerWithoutConstructor.onBaseFragmentEventListenerSettings,
    ImageSearchArasaacRecyclerViewAdapterInterface,
    ListsOfNamesAdapterInterface
    {
    lateinit var fragmentManager: FragmentManager
    //
    var keyword = ""
    var wordToAdd = ""
    var elementActive = ""
    var isMenuItem = ""
    var uriType = ""
    var uri:String? = ""
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     *
     * @see ActionbarFragment
     * @see androidx.appcompat.app.AppCompatActivity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
        //
        setActivityResultLauncher()
        //
        clearFields()
        //
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            filePath = savedInstanceState.getString("IMAGE VIDEO OR SOUND FILE PATH", getString(R.string.non_trovato))
            //
            keyword = savedInstanceState.getString("KEYWORD", "")
            wordToAdd = savedInstanceState.getString("WORD", "")
            elementActive = savedInstanceState.getString("ELEMENT ACTIVE", "")
            isMenuItem = savedInstanceState.getString("IS MENU ITEM", "")
            uriType = savedInstanceState.getString("URI TYPE", "")
            uri = savedInstanceState.getString("URI", "")
        }
        else
        {
//        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(R.id.settings_container, ContentsFragment(), "ContentsFragment")
                .commit()
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
        savedInstanceState.putString("IMAGE VIDEO OR SOUND FILE PATH", filePath)
        //
        savedInstanceState.putString("KEYWORD", keyword)
        savedInstanceState.putString("WORD", wordToAdd)
        savedInstanceState.putString("ELEMENT ACTIVE", elementActive)
        savedInstanceState.putString("IS MENU ITEM", isMenuItem)
        savedInstanceState.putString("URI TYPE", uriType)
        savedInstanceState.putString("URI", uri)
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
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
        val frag = ListsOfNamesFragment()
        //
        val bundle = Bundle()
        bundle.putString("KEYWORD", keyword)
        bundle.putString("WORD", wordToAdd)
        bundle.putString("ELEMENT ACTIVE", elementActive)
        bundle.putString("IS MENU ITEM", isMenuItem)
        bundle.putString("URI TYPE", uriType)
        bundle.putString("URI", uri)
        frag.arguments = bundle
        //
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
                    .equalTo("isMenuItem", textWord4.text.toString())
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
                listsOfNames.fromAssets = ""
                listsOfNames.uriType = uriType
                listsOfNames.uri = uri
                realm.commitTransaction()
                //
                clearFields()
                // view the lists of names settings initializing ListsOfNamesFragment (FragmentTransaction
                // switch between Fragments).
                submitListsOfNames(null)
//                val frag = ListsOfNamesFragment()
//                val ft = supportFragmentManager.beginTransaction()
//                ft.replace(R.id.settings_container, frag)
//                ft.addToBackStack(null)
//                ft.commit()
    }
    /**
    * Called when the user taps the show list button .
    *
    * the activity is notified to view the list of names list.
    *
    *
    * @param view view of tapped button
    * @see ListsOfNamesFragment
    *
    * @see ListsOfNamesListFragment
    */
    fun listsOfNamesList(view: View?) {
            // view the list of names list fragment initializing ListsOfNamesListFragment (FragmentTransaction
            // switch between Fragments).
            val frag = ListsOfNamesListFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.settings_container, frag)
            ft.addToBackStack(null)
            ft.commit()
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
        clearFields()
//        reloadListOfNamesFragment()
        listsOfNamesList(null)
    }

    override fun reloadListOfNamesFragmentForInsertion(position: Int) {
                realm = Realm.getDefaultInstance()
                var results: RealmResults<ListsOfNames>
                results = realm.where(ListsOfNames::class.java).findAll()
                //
                val mStrings1 = arrayOf(getString(R.string.keyword), "word")
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                val daInserire = results[position]!!
        //
        keyword = daInserire.keyword!!
        submitListsOfNames(null)
//        val frag = ListsOfNamesFragment()
//        val bundle = Bundle()
//        bundle.putString("KEYWORD", daInserire.keyword)
//        bundle.putString("WORD", "")
//        bundle.putString("ELEMENT ACTIVE", "")
//        bundle.putString("IS MENU ITEM", "")
//        frag.arguments = bundle
//        val ft = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.settings_container, frag)
//        ft.addToBackStack(null)
//        ft.commit()
    }

    override fun reloadListOfNamesFragmentForEditing(position: Int) {
                realm = Realm.getDefaultInstance()
                var results: RealmResults<ListsOfNames>
                results = realm.where(ListsOfNames::class.java).findAll()
                //
                val mStrings1 = arrayOf(getString(R.string.keyword), "word")
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                val daModificare = results[position]!!
                //
        keyword = daModificare.keyword!!
        wordToAdd = daModificare.word!!
        elementActive = daModificare.elementActive!!
        isMenuItem = daModificare.isMenuItem!!
        uriType = daModificare.uriType!!
        uri = daModificare.uri
        submitListsOfNames(null)
//        val frag = ListsOfNamesFragment()
//        val bundle = Bundle()
//        bundle.putString("KEYWORD", daModificare.keyword)
//        bundle.putString("WORD", daModificare.word)
//        bundle.putString("ELEMENT ACTIVE", daModificare.elementActive)
//        bundle.putString("IS MENU ITEM", daModificare.isMenuItem)
//        frag.arguments = bundle
//        val ft = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.settings_container, frag)
//        ft.addToBackStack(null)
//        ft.commit()
    }
    /**
     * on callback from ListOfNamesAdapter to this Activity
     *
     * after deleting a name the activity is notified to view the updated lists of names settings
     *
     *
     * @see ListsOfNamesFragment
     */
//    fun reloadListOfNamesFragment() {
//        submitListsOfNames(null)
        // view the lists of names settings initializing ListsOfNamesFragment (FragmentTransaction
        // switch between Fragments).
//        val frag = ListsOfNamesFragment()
//        val ft = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.settings_container, frag)
//        ft.addToBackStack(null)
//        ft.commit()
//    }

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
    //
    @JvmField
    var imageSearchListsOfNamesActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var filePath: String? = null
    /**
     * setting callbacks to search for images and videos via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     *
     *
     * REFER to [stackoverflow](https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)
     * answer of [Muntashir Akon](https://stackoverflow.com/users/4147849/muntashir-akon)
     *
     *
     * and to [stackoverflow](https://stackoverflow.com/questions/7620401/how-to-convert-image-file-data-in-a-byte-array-to-a-bitmap)
     * answer of [Uttam](https://stackoverflow.com/users/840861/uttam)
     *
     * @see copyFileFromSharedToInternalStorageAndGetPath
     */
    fun setActivityResultLauncher() {
        //
        imageSearchListsOfNamesActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        var uriImageSearch: Uri? = null
                        filePath = getString(R.string.non_trovato)
                        //
                        if (resultData != null) {
                            uriImageSearch = Objects.requireNonNull(resultData).data
                            //
                            try {
                                filePath = copyFileFromSharedToInternalStorageAndGetPath(context,
                                    uriImageSearch!!
                                )
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
                            //
                            val takeFlags =
                                resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                            context.contentResolver.takePersistableUriPermission(
                                uriImageSearch!!,
                                takeFlags
                            )
                            //
                            uriType = "S"
                            uri = filePath
                            //
                            submitListsOfNames(null)
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
            R.id.buttonimagesearchlistsofnames -> {
                /*  the instructions of the button */
                val textWord1 = findViewById<View>(R.id.keywordtoadd) as TextView
                val textWord2 = findViewById<View>(R.id.nametoadd) as TextView
                val textWord3 = findViewById<View>(R.id.elementactive) as TextView
                val textWord4 = findViewById<View>(R.id.ismenuitem) as EditText
                //
                keyword = textWord1.text.toString()
                wordToAdd = textWord2.text.toString()
                elementActive = textWord3.text.toString()
                isMenuItem = textWord4.text.toString()
                //
                imageSearchListsOfNamesActivityResultLauncher!!.launch(intent)
            }
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
     * Called when the user taps the image search Arasaac button from ListOfNames Fragment.
     *
     * @param v view of tapped button
     */
    fun imageSearchArasaac(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordtoadd) as TextView
        val textWord2 = findViewById<View>(R.id.nametoadd) as TextView
        val textWord3 = findViewById<View>(R.id.elementactive) as TextView
        val textWord4 = findViewById<View>(R.id.ismenuitem) as EditText
        //
        keyword = textWord1.text.toString()
        wordToAdd = textWord2.text.toString()
        elementActive = textWord3.text.toString()
        isMenuItem = textWord4.text.toString()
        //
        val frag = ImageSearchArasaacFragment()
        //
        val bundle = Bundle()
        bundle.putString("keywordToSearchArasaac", wordToAdd)
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
        uriType = "A"
        uri = url
        //
        submitListsOfNames(null)
    }
    /**
     * clear fields
     *
     */
    fun clearFields() {
        keyword = ""
        wordToAdd = ""
        elementActive = ""
        isMenuItem = ""
        uriType = ""
        uri = ""
    }
}