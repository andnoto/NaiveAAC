package com.sampietro.NaiveAAC.activities.Game.Utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R

/**
 * <h1>GameFragmentHear</h1>
 *
 * **GameFragmentHear** displays the ui called on beginning of speech
 *
 *
 * @version     5.0, 01/04/2024
 */
class GameFragmentHear : Fragment() {
    lateinit var rootView: View
    /**
     * prepares the ui
     *
     *
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_hear, container, false)
        return rootView
    }
}