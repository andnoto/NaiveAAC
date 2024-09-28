package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>WordPairsFragment</h1>
 *
 * **WordPairsFragment** UI for word pairs settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithoutConstructor
 *
 * @see SettingsActivity
 */
class WordPairsFragment() : FragmentAbstractClassWithoutConstructor() {
    /*
    used for viewmodel
    */
//    private lateinit var viewModel: VoiceToBeRecordedInWordPairsViewModel
    /*

     */
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
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
//        viewModel = ViewModelProvider(requireActivity()).get(
//            VoiceToBeRecordedInWordPairsViewModel::class.java
//        )
//        viewModel.getSelectedItem()
//            .observe(viewLifecycleOwner) { voiceToBeRecordedInWordPairs: VoiceToBeRecordedInWordPairs ->
                // Perform an action with the latest item data
//                firstword.setText(voiceToBeRecordedInWordPairs.word1)
//                secondword.setText(voiceToBeRecordedInWordPairs.word2)
//                complement.setText(voiceToBeRecordedInWordPairs.complement)
//                awardtype.setText(voiceToBeRecordedInWordPairs.awardType)
//                if (voiceToBeRecordedInWordPairs.awardType == "V")
//                    { uripremiumvideo.setText(voiceToBeRecordedInWordPairs.uriPremiumVideo) }
//                    else
//                    if (voiceToBeRecordedInWordPairs.awardType == "Y")
//                        { linkyoutube.setText(voiceToBeRecordedInWordPairs.uriPremiumVideo) }
//            }
        val bundle = this.arguments
        if (bundle != null) {
            //
            val word1 = bundle.getString("WORD1")
            firstword.setText(word1)
            //
            val word2 = bundle.getString("WORD2")
            secondword.setText(word2)
            //
            val bundleComplement = bundle.getString("COMPLEMENT")
            complement.setText(bundleComplement)
            //
            val bundleAwardtype = bundle.getString("AWARD TYPE")
            awardtype.setText(bundleAwardtype)
            //
            val bundleUriPremiumVideo = bundle.getString("URI PREMIUM VIDEO")
            uripremiumvideo.setText(bundleUriPremiumVideo)
            //
            val bundleLinkYoutube = bundle.getString("LINK YOUTUBE")
            linkyoutube.setText(bundleLinkYoutube)
        }
        //
        return rootView
    }
}