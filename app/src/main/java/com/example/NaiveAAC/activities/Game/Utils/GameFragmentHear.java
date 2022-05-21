package com.example.NaiveAAC.activities.Game.Utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.NaiveAAC.R;

/**
 * <h1>GameFragmentHear</h1>
 * <p><b>GameFragmentHear</b> displays the ui called on beginning of speech
 * </p>
 *
 * @version     1.3, 05/05/22
 * @see GameFragmentAbstractClass
 */
public class GameFragmentHear extends GameFragmentAbstractClass {
    /**
     * prepares the ui
     * <p>
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_hear, container, false);
        return rootView;
    }
}

