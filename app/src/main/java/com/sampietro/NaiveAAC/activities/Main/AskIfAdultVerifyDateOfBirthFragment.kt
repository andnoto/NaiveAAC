package com.sampietro.NaiveAAC.activities.Main

import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R

/**
 * <h1>VerifyDateOfBirthFragment</h1>
 *
 * **VerifyDateOfBirthFragment** UI to verify if the user is an adult
 *
 *
 * @version     4.0, 09/09/2023
 * @see AskIfAdultActivity
 */
class AskIfAdultVerifyDateOfBirthFragment : Fragment() {
    var rootView: View? = null
    var textView: TextView? = null
    var ctext: Context? = null

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
    ): View? {
        rootView = inflater.inflate(
            R.layout.activity_checkifadult_verifydateofbirth,
            container,
            false
        )
        // logic of fragment
        return rootView
    }
}