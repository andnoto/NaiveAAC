package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.WordPairs.VoiceToBeRecordedInWordPairs
import com.sampietro.NaiveAAC.activities.WordPairs.VoiceToBeRecordedInWordPairsViewModel

/**
 * <h1>WordPairsFragment</h1>
 *
 * **WordPairsFragment** UI for word pairs settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see SettingsFragmentAbstractClass
 *
 * @see SettingsActivity
 */
class WordPairsFragment : SettingsFragmentAbstractClass() {
    /*
    used for viewmodel
    */
    private lateinit var viewModel: VoiceToBeRecordedInWordPairsViewModel
    /*

     */
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see com.sampietro.simsimtest.activities.WordPairs.WordPairs
     *
     * @see com.sampietro.simsimtest.activities.WordPairs.WordPairsAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_wordpairs, container, false)
        // logic of fragment
        val firstword = rootView.findViewById<View>(R.id.firstword) as EditText
        val secondword = rootView.findViewById<View>(R.id.secondword) as EditText
        val complement = rootView.findViewById<View>(R.id.complement) as EditText
        val awardtype = rootView.findViewById<View>(R.id.awardtype) as EditText
        val uripremiumvideo = rootView.findViewById<View>(R.id.uripremiumvideo) as TextView
        val linkyoutube = rootView.findViewById<View>(R.id.linkyoutube) as EditText
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
        //
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}