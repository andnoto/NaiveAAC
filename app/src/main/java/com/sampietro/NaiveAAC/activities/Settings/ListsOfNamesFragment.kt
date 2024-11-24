package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import io.realm.Realm

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
    lateinit var realm: Realm
    /**
     * prepares the ui also using a listview
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see ListsOfNames
     *
     * @see ListsOfNamesAdapter
     */
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
        val gameimage = rootView.findViewById<View>(R.id.gameimage) as ImageView
        //
        realm = Realm.getDefaultInstance()
        //
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
            //
            var uriType = bundle.getString("URI TYPE")
            var uri = bundle.getString("URI")
            //
            if (uriType != "")
            {   if (uriType == "I") {
                val imageUrl = ImageSearchHelper.searchUri(
                    ctext,
                    realm,
                    uri
                )
                uriType = "S"
                uri = imageUrl
            }
                GraphicsAndPrintingHelper.addImage(
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