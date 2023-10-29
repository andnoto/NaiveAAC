package com.sampietro.NaiveAAC.activities.Game.Utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R

/**
 * <h1>GameFragmentHear</h1>
 *
 * **GameFragmentHear** displays the ui called on beginning of speech
 *
 *
 * @version     4.0, 09/09/2023
 * @see GameFragmentAbstractClass
 */
class GameFragmentHear : GameFragmentAbstractClass() {
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