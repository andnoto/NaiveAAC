package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImage
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri
import io.realm.Realm
import java.util.*

/**
 * <h1>StoriesFragment</h1>
 *
 * **StoriesFragment** UI for stories settings
 *
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithoutConstructor
 *
 * @see SettingsActivity
 */
class StoriesFragment() : FragmentAbstractClassWithoutConstructor() {
    //
//    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel
//    private lateinit var viewModel: Game2ViewModel
    //
    lateinit var realm: Realm
    //
//    var deviceEnabledUserName: String? = ""

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
        rootView = inflater.inflate(R.layout.activity_settings_stories_word, container, false)
        // logic of fragment
        val keywordstorytoadd = rootView.findViewById<View>(R.id.keywordstorytoadd) as TextView
        val phrasenumbertoadd = rootView.findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val wordnumbertoadd = rootView.findViewById<View>(R.id.wordnumbertoadd) as TextView
        val wordtoadd = rootView.findViewById<View>(R.id.wordtoadd) as EditText
        val gameimage = rootView.findViewById<View>(R.id.gameimage) as ImageView
        //
        realm = Realm.getDefaultInstance()
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
//        viewModel = ViewModelProvider(requireActivity()).get(
//            Game2ViewModel::class.java
//        )
//        viewModel.getSelectedItem()
//            .observe(viewLifecycleOwner) { game2ViewModelItem: Game2ViewModelItem ->
                // Perform an action with the latest item data
//                keywordstorytoadd.setText(game2ViewModelItem.story)
//                phrasenumbertoadd.setText(String.format(Locale.getDefault(), "%d",game2ViewModelItem.phraseNumberInt))
//                wordnumbertoadd.setText(String.format(Locale.getDefault(), "%d",game2ViewModelItem.wordNumberInt))
//                wordtoadd.setText(game2ViewModelItem.word)
//                if (game2ViewModelItem.uriType != "")
//                {   var uriType = game2ViewModelItem.uriType
//                    var uri = game2ViewModelItem.uri
//                    if (uriType == "I") {
//            }
        val bundle = this.arguments
        if (bundle != null) {
            //
            val story = bundle.getString("STORY")
            keywordstorytoadd.setText(story)
            //
            val phraseNumberInt = bundle.getInt("PHRASE NUMBER")
            phrasenumbertoadd.setText(String.format(Locale.getDefault(), "%d",phraseNumberInt))
            //
            val wordNumberInt = bundle.getInt("WORD NUMBER")
            wordnumbertoadd.setText(String.format(Locale.getDefault(), "%d",wordNumberInt))
            //
            val wordToAdd = bundle.getString("WORD")
            wordtoadd.setText(wordToAdd)
            //
            var uriType = bundle.getString("URI TYPE")
            var uri = bundle.getString("URI")
            //
            if (uriType != "")
            {   if (uriType == "I") {
                    val imageUrl = searchUri(
                        ctext,
                        realm,
                        uri
                    )
                    uriType = "S"
                    uri = imageUrl
                }
                addImage(
                    uriType,
                    uri, gameimage,
                    100, 100
                )
            }
        }
        //
        return rootView
    }
}