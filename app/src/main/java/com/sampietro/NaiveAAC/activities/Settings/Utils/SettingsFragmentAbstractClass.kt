package com.sampietro.NaiveAAC.activities.Settings.Utils

import android.widget.TextView
import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment

/**
 * <h1>SettingsFragmentAbstractClass</h1>
 *
 * **SettingsFragmentAbstractClass**
 * abstract class containing common methods that is extended by Settings Fragment
 *
 * @version     4.0, 09/09/2023
 */
abstract class SettingsFragmentAbstractClass : Fragment() {
//    @JvmField
    lateinit var rootView: View
    var textView: TextView? = null
//    @JvmField
    lateinit var ctext: Context

    /**
     * <h1>onFragmentEventListenerSettings</h1>
     *
     * **onFragmentEventListenerSettings**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     *
     * @see androidx.fragment.app.Fragment.onAttach
     */
    interface onFragmentEventListenerSettings {
        //  insert here any references to the Settings Activity
        fun receiveResultSettings(v: View?)
    }

    //
//    @JvmField
    lateinit var  listener: onFragmentEventListenerSettings

    /**
     * listener setting for settings activity callbacks and context annotation
     *
     * @see androidx.fragment.app.Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        //
        listener = activity as onFragmentEventListenerSettings
        //
        ctext = context
    }
}