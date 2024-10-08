package com.sampietro.NaiveAAC.activities.Main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.displayFileInAssets

/**
 * <h1>AskAParentFragment</h1>
 *
 * **AskAParentFragment** UI to ask a parent
 *
 *
 * @version     5.0, 01/04/2024
 * @see AskIfAdultActivity
 */
class AskAParentFragment() : FragmentAbstractClassWithoutConstructor() {
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
        rootView = inflater.inflate(
            R.layout.activity_askaparent,
            container,
            false
        )
        // logic of fragment
        val myImage = rootView.findViewById<View>(R.id.askaparentiv) as ImageView
        displayFileInAssets(ctext,"images/genitori.png",myImage)
        return rootView
    }
}