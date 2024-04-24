package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Grammar.VoiceToBeRecordedInListsOfNames
import com.sampietro.NaiveAAC.activities.Grammar.VoiceToBeRecordedInListsOfNamesViewModel
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

/**
 * <h1>ListsOfNamesFragment</h1>
 *
 * **ListsOfNamesFragment** UI for list of names settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see SettingsFragmentAbstractClass
 *
 * @see SettingsActivity
 */
class ListsOfNamesFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
    /*
    used for viewmodel
    */
    private lateinit var viewModel: VoiceToBeRecordedInListsOfNamesViewModel
    /*

     */
    //
    private lateinit var realm: Realm

    //
    private lateinit var listView: ListView
    private var adapter: ListsOfNamesAdapter? = null

    /**
     * prepares the ui also using a listview
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see ListsOfNames
     *
     * @see ListsOfNamesAdapter
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // logic of fragment
        val keywordtoadd = view.findViewById<View>(R.id.keywordtoadd) as EditText
        val nametoadd = view.findViewById<View>(R.id.nametoadd) as EditText
        val elementactivetoadd = view.findViewById<View>(R.id.elementactive) as EditText
        val ismenuitemtoadd = view.findViewById<View>(R.id.ismenuitem) as EditText
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
         constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        viewModel = ViewModelProvider(requireActivity()).get(
            VoiceToBeRecordedInListsOfNamesViewModel::class.java
        )
        viewModel.getSelectedItem()
            .observe(viewLifecycleOwner) { voiceToBeRecordedInListsOfNames: VoiceToBeRecordedInListsOfNames ->
                // Perform an action with the latest item data
                keywordtoadd.setText(voiceToBeRecordedInListsOfNames.keyword)
                nametoadd.setText(voiceToBeRecordedInListsOfNames.word)
                elementactivetoadd.setText(voiceToBeRecordedInListsOfNames.elementActive)
                ismenuitemtoadd.setText(voiceToBeRecordedInListsOfNames.isMenuItem)
            }
        //
        realm = Realm.getDefaultInstance()
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
        var results: RealmResults<ListsOfNames>
        results = realm.where(ListsOfNames::class.java).findAll()
        //
        val mStrings1 = arrayOf(getString(R.string.keyword), "word")
        val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING)
        results = results.sort(mStrings1, mStrings2)
        //
        listView = view.findViewById<View>(R.id.listview) as ListView
        //
        adapter = ListsOfNamesAdapter(ctext, results, listView)
        //
        listView.adapter = adapter
    }
}