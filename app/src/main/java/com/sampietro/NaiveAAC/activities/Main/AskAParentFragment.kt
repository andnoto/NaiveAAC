package com.sampietro.NaiveAAC.activities.Main

import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper.displayFileInAssets

/**
 * <h1>AskAParentFragment</h1>
 *
 * **AskAParentFragment** UI to ask a parent
 *
 *
 * @version     4.0, 09/09/2023
 * @see AskIfAdultActivity
 */
class AskAParentFragment : Fragment() {
    lateinit var rootView: View
    var textView: TextView? = null
    lateinit var ctext: Context

    /**
     * context annotation
     *
     * @see Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //
        ctext = context
    }

    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
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
        //
        val myImage = rootView.findViewById<View>(R.id.askaparentiv) as ImageView
        displayFileInAssets(ctext,"images/genitori.png",myImage)
        return rootView
    }
}