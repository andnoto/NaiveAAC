package com.sampietro.NaiveAAC.activities.BaseAndAbstractClass

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * <h1>FragmentAbstractClassWithListener</h1>
 *
 * **FragmentAbstractClassWithListener**
 * class containing common methods that is extended by Settings Fragment
 *
 * @version     5.0, 01/04/2024
 */
abstract class FragmentAbstractClassWithListener(@LayoutRes contentLayoutId : Int = 0) : Fragment(contentLayoutId) {
//
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
    interface onBaseFragmentEventListenerSettings {
        //  insert here any references to the Settings Activity
        fun receiveResultSettings(v: View?)
    }

    //
    lateinit var  listener: onBaseFragmentEventListenerSettings

    /**
     * listener setting for settings activity callbacks and context annotation
     *
     * @see androidx.fragment.app.Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        //
        listener = activity as onBaseFragmentEventListenerSettings
        //
        ctext = context
    }
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // logic of fragment
        listener.receiveResultSettings(view)
    }
}