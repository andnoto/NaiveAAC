package com.sampietro.NaiveAAC.activities.Info.Utils

import android.widget.TextView
import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment

/**
 * <h1>InfoFragmentAbstractClass</h1>
 *
 * **InfoFragmentAbstractClass**
 * abstract class containing common methods that is extended by Info Fragment
 *
 * @version     4.0, 09/09/2023
 */
abstract class InfoFragmentAbstractClass : Fragment() {
//    @JvmField
    lateinit var rootView: View
    var textView: TextView? = null
    @JvmField
    var ctext: Context? = null

    //
    var listener: OnFragmentEventListenerInfo? = null

    /**
     * listener setting for settings activity callbacks and context annotation
     *
     * @see Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        //
        listener = activity as OnFragmentEventListenerInfo
        //
        ctext = context
    }
}