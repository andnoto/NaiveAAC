package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Stories.StoriesImportExportAdapter
import android.widget.EditText
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.sampietro.NaiveAAC.R
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Stories.Stories
import io.realm.Realm
import io.realm.Sort
import java.util.*

/**
 * <h1>StoriesImportExportFragment</h1>
 *
 * **StoriesImportExportFragment** UI for stories import/export
 *
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     4.0, 09/09/2023
 * @see SettingsFragmentAbstractClass
 *
 * @see SettingsStoriesImportExportActivity
 */
class StoriesImportExportFragment : SettingsFragmentAbstractClass() {
    private lateinit var realm: Realm

    //
    private var listView: ListView? = null
    private var adapter: StoriesImportExportAdapter? = null

    //
    var storyToSearch: EditText? = null
//    var phraseNumberToSearch: EditText? = null

    //
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel

    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see Stories
     *
     * @see StoriesImportExportAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView =
            inflater.inflate(R.layout.activity_settings_stories_import_export, container, false)
        // logic of fragment
        // ListView
        // 1) we get a reference to the data structure through the RealmResults class which constitutes
        // the query result set and is an iterable collection accessible with Java constructs:
        // for loop, basic access to position and Iterator.
        // The approach for realm queries is object-oriented.
        // The where method will retrieve the objects from the specified class and the result will
        // be treated with filtrate by other specific methods.
        // At the end of the selection configuration, the findAll method will be invoked to retrieve
        // all the corresponding results.
        // 2) we instantiate an Adapter by assigning it, the collection and the layout related to
        // each single row
        // 3) we retrieve the ListView prepared in the layout and assign it the reference to the adapter
        // which will be your View "supplier".
        //
        storyToSearch = rootView.findViewById<View>(R.id.storytosearch) as EditText
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        viewModel = ViewModelProvider(requireActivity()).get(
            VoiceToBeRecordedInStoriesViewModel::class.java
        )
        viewModel.getSelectedItem()
            .observe(viewLifecycleOwner) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                realm = Realm.getDefaultInstance()
                var results: RealmResults<Stories>
                storyToSearch!!.setText(voiceToBeRecordedInStories.story)
                //
                results = if (voiceToBeRecordedInStories.story == getString(R.string.nome_storia)) {
                    realm.where(Stories::class.java).findAll()
                } else {
                    realm.where(Stories::class.java)
                        .equalTo(
                            "story",
                            voiceToBeRecordedInStories.story!!.lowercase(Locale.getDefault())
                        )
                        .findAll()
                }
                //
                val mStrings1 = arrayOf("story", "phraseNumberInt", "wordNumberInt")
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                //
                listView =
                    rootView.findViewById<View>(R.id.import_export_stories_listview) as ListView
                //
                adapter = StoriesImportExportAdapter(ctext, results, listView)
                //
                listView!!.adapter = adapter
            }
        //
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}