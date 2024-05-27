package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass
import com.sampietro.NaiveAAC.activities.WordPairs.VoiceToBeRecordedInWordPairs
import com.sampietro.NaiveAAC.activities.WordPairs.VoiceToBeRecordedInWordPairsViewModel

/**
 * <h1>WordPairsFragment</h1>
 *
 * **WordPairsFragment** UI for word pairs settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClass
 *
 * @see SettingsActivity
 */
class WordPairsFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
    /*
    used for viewmodel
    */
    private lateinit var viewModel: VoiceToBeRecordedInWordPairsViewModel
    /*

     */
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // logic of fragment
        val firstword = view.findViewById<View>(R.id.firstword) as EditText
        val secondword = view.findViewById<View>(R.id.secondword) as EditText
        val complement = view.findViewById<View>(R.id.complement) as EditText
        val awardtype = view.findViewById<View>(R.id.awardtype) as EditText
        val uripremiumvideo = view.findViewById<View>(R.id.uripremiumvideo) as TextView
        val linkyoutube = view.findViewById<View>(R.id.linkyoutube) as EditText
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
         constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        viewModel = ViewModelProvider(requireActivity()).get(
            VoiceToBeRecordedInWordPairsViewModel::class.java
        )
        viewModel.getSelectedItem()
            .observe(viewLifecycleOwner) { voiceToBeRecordedInWordPairs: VoiceToBeRecordedInWordPairs ->
                // Perform an action with the latest item data
                firstword.setText(voiceToBeRecordedInWordPairs.word1)
                secondword.setText(voiceToBeRecordedInWordPairs.word2)
                complement.setText(voiceToBeRecordedInWordPairs.complement)
                awardtype.setText(voiceToBeRecordedInWordPairs.awardType)
                if (voiceToBeRecordedInWordPairs.awardType == "V")
                    { uripremiumvideo.setText(voiceToBeRecordedInWordPairs.uriPremiumVideo) }
                    else
                    if (voiceToBeRecordedInWordPairs.awardType == "Y")
                        { linkyoutube.setText(voiceToBeRecordedInWordPairs.uriPremiumVideo) }
            }
    }
}