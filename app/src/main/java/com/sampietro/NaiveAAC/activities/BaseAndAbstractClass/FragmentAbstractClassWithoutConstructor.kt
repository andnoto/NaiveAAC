package com.sampietro.NaiveAAC.activities.BaseAndAbstractClass

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * <h1>FragmentAbstractClass</h1>
 *
 * **FragmentAbstractClass**
 * class containing common methods that is extended by Settings Fragment
 *
 * @version     5.0, 01/04/2024
 */
abstract class FragmentAbstractClassWithoutConstructor() : Fragment() {
    lateinit var ctext: Context
    lateinit var rootView: View

    /**
     * context annotation
     *
     * @see androidx.fragment.app.Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //
        ctext = context
    }
}