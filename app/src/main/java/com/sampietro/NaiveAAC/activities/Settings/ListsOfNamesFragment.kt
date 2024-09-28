package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter

/**
 * <h1>ListsOfNamesFragment</h1>
 *
 * **ListsOfNamesFragment** UI for list of names settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see SettingsActivity
 */
class ListsOfNamesFragment() : FragmentAbstractClassWithoutConstructor() {
    /*
    used for viewmodel
    */
//    private lateinit var viewModel: VoiceToBeRecordedInListsOfNamesViewModel
    /*

     */
    /**
     * prepares the ui also using a listview
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see ListsOfNames
     *
     * @see ListsOfNamesAdapter
     */
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_lists_of_names, container, false)
        // logic of fragment
        val keywordtoadd = rootView.findViewById<View>(R.id.keywordtoadd) as EditText
        val nametoadd = rootView.findViewById<View>(R.id.nametoadd) as EditText
        val elementactivetoadd = rootView.findViewById<View>(R.id.elementactive) as EditText
        val ismenuitemtoadd = rootView.findViewById<View>(R.id.ismenuitem) as EditText
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
         constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
//        viewModel = ViewModelProvider(requireActivity()).get(
//            VoiceToBeRecordedInListsOfNamesViewModel::class.java
//        )
//        viewModel.getSelectedItem()
//            .observe(viewLifecycleOwner) { voiceToBeRecordedInListsOfNames: VoiceToBeRecordedInListsOfNames ->
                // Perform an action with the latest item data
//                keywordtoadd.setText(voiceToBeRecordedInListsOfNames.keyword)
//                nametoadd.setText(voiceToBeRecordedInListsOfNames.word)
//                elementactivetoadd.setText(voiceToBeRecordedInListsOfNames.elementActive)
//                ismenuitemtoadd.setText(voiceToBeRecordedInListsOfNames.isMenuItem)
//            }
        val bundle = this.arguments
        if (bundle != null) {
            //
            val keyword = bundle.getString("KEYWORD")
            keywordtoadd.setText(keyword)
            //
            val word = bundle.getString("WORD")
            nametoadd.setText(word)
            //
            val elementActive = bundle.getString("ELEMENT ACTIVE")
            elementactivetoadd.setText(elementActive)
            //
            val isMenuItem = bundle.getString("IS MENU ITEM")
            ismenuitemtoadd.setText(isMenuItem)
        }
        //
        return rootView
    }
}