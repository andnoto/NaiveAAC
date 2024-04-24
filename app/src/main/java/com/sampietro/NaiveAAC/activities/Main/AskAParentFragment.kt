package com.sampietro.NaiveAAC.activities.Main

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass
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
class AskAParentFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
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
        val myImage = view.findViewById<View>(R.id.askaparentiv) as ImageView
        displayFileInAssets(ctext,"images/genitori.png",myImage)
    }
}