package com.sampietro.NaiveAAC.activities.Settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper
import com.sampietro.NaiveAAC.activities.Graphics.Images
import com.sampietro.NaiveAAC.activities.Graphics.ImagesAdapter

/**
 * <h1>ImagesFragment</h1>
 *
 * **ImagesFragment** UI for images settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see SettingsActivity
 */
class ImagesFragment() : FragmentAbstractClassWithoutConstructor() {
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see Images
     *
     * @see ImagesAdapter
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
        rootView = inflater.inflate(R.layout.activity_settings_images, container, false)
        // logic of fragment
        val bundle = this.arguments
        val stringUri: String?
        //
        val descrizione: String?
        //
        if (bundle != null) {
            //
            descrizione = bundle.getString("descrizione")
            val immD = rootView.findViewById<View>(R.id.imageDescription) as EditText
            immD.setText(descrizione)
            //
            stringUri = bundle.getString("URI")
            if (stringUri != "none") {
                val uri = Uri.parse(stringUri)
                //
                val myImage = rootView.findViewById<View>(R.id.imageviewTest) as ImageView
                GraphicsAndPrintingHelper.showImage(ctext, uri, myImage)
            }
        }
        //
        return rootView
    }
}