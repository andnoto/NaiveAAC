package com.sampietro.NaiveAAC.activities.Settings

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>ChoiseOfGameToSetFragment</h1>
 *
 * **ChoiseOfGameToSetFragment** UI for choise of game to set
 *
 *
 * @version     5.0, 01/04/2024
 * @see SettingsActivity
 */
class ChoiseOfGameToSetFragment() : FragmentAbstractClassWithoutConstructor() {
    //
    var textGameToSet: String? = null
    private lateinit var spinnerAdapter: ArrayAdapter<String>
//    lateinit var ctext: Context

    /**
     * <h1>onFragmentEventListenerChoiseOfGameToSet</h1>
     *
     * **onFragmentEventListenerChoiseOfGameToSet**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     *
     * @see .onAttach
     */
    internal interface onFragmentEventListenerChoiseOfGameToSet {
        //  insert here any references to the Settings Activity
        fun receiveResultGameToSet(g: String?)
    }

    //
    private var listener: onFragmentEventListenerChoiseOfGameToSet? = null

    /**
     * listener setting for settings activity callbacks and context annotation
     *
     * @see Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        listener = activity as onFragmentEventListenerChoiseOfGameToSet
        //
        ctext = context
    }

    /**
     * prepares the ui
     *
     * @see Fragment.onCreateView
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
        rootView = inflater.inflate(R.layout.activity_settings_games_menu, container, false)
        // logic of fragment
        // SPINNER
        // an Adapter has been prepared that has been connected to spinner with the setAdapter method
        // the onItemSelected method manages the selection of an item of the Spinner
        val arraySpinner = arrayOf(
            getString(R.string.parole_in_liberta)
        )
        spinnerAdapter = ArrayAdapter(
            ctext,
            R.layout.activity_settings_choiseofgametoset_row, arraySpinner
        )
        val sp = rootView.findViewById<Spinner>(R.id.gametoset)
        sp.adapter = spinnerAdapter
        //
        sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View,
                arg2: Int, arg3: Long
            ) {
                val txt = arg1.findViewById<TextView>(R.id.rowtext)
                textGameToSet = txt.text.toString()
                listener!!.receiveResultGameToSet(textGameToSet)
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
        //
        return rootView
    }
}