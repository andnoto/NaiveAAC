package com.sampietro.NaiveAAC.activities.Settings

import android.widget.TextView
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import java.util.*

/**
 * <h1>VerifyFragment</h1>
 *
 * **VerifyFragment** UI for check the right to access the settings
 *
 * sets up an addition or subtraction with random numbers and calculates the result
 *
 * @version     4.0, 09/09/2023
 * @see SettingsActivity
 */
class VerifyFragment : Fragment() {
    lateinit var rootView: View
    lateinit var textView: TextView
    var resultToVerify = 0

    /**
     * <h1>onFragmentEventListenerVerify</h1>
     *
     * **onFragmentEventListenerVerify**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     *
     * @see .onAttach
     */
    internal interface onFragmentEventListenerVerify {
        //  insert here any references to the Settings Activity
        fun receiveResultToVerify(v: View?, r: Int)
    }

    //
    private var listener: onFragmentEventListenerVerify? = null

    /**
     * listener setting for settings activity callbacks and context annotation
     *
     * @see Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        listener = activity as onFragmentEventListenerVerify
    }

    /**
     * prepares the ui and makes the callback to the activity.
     *
     * sets up an addition or subtraction with random numbers and calculates the result
     *
     * @see Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_verify, container, false)
        // logic of fragment
        // if you need to generate numbers from min to max (including both),
        // you write random.nextInt(max - min + 1) + min
        val rn = Random()
        val rn1 = rn.nextInt(10) + 1
        val rn2 = rn.nextInt(rn1) + 1
        val rn3 = rn.nextInt(2) + 1
        //
        resultToVerify = if (rn3 == 1) {
            rn1 + rn2
        } else {
            rn1 - rn2
        }
        //
        textView = rootView.findViewById(R.id.verifyCalculation)
        if (rn3 == 1) {
            textView.setText("$rn1 + $rn2")
        } else {
            textView.setText("$rn1 - $rn2")
        }
        //
        listener!!.receiveResultToVerify(rootView, resultToVerify)
        //
        return rootView
    }
}