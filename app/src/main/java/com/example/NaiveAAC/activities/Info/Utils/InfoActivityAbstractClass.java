package com.example.NaiveAAC.activities.Info.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.example.NaiveAAC.activities.Game.Game1.Game1Activity;
import com.example.NaiveAAC.activities.Game.Utils.OnFragmentEventListenerGame;
import com.example.voicerecognitionlibrary.RecognizerCallback;

/**
 * <h1>InfoActivityAbstractClass</h1>
 * <p><b>InfoActivityAbstractClass</b>
 * abstract class containing common methods that is extended by
 * </p>
 * 1) GameActivityChoiseOfGameMediaPlayer</p>
 * 2) GameActivityGame1ViewPager</p>
 * 3) GameActivityGame2</p>
 * implements voice recognizer common methods and others</p>
 * Refer to <a href="https://howtomakeavideogameusingvoicecommands.blogspot.com/2021/08/presentation.html">howtomakeavideogameusingvoicecommands.blogspot.com</a>
 * and <a href="https://github.com/andnoto/MyApplicationLibrary">github.com</a>
 *
 *
 * @version     1.2, 04/28/22
 * @see ChoiseOfGameActivity
 * @see Game1Activity
 * @see com.example.NaiveAAC.activities.Game.Game2.Game2Activity
 * @see RecognizerCallback
 * @see OnFragmentEventListenerGame
 */
public abstract class InfoActivityAbstractClass extends AppCompatActivity implements
        OnFragmentEventListenerInfo {
    public FragmentManager fragmentManager;
    //
    public View rootViewImageFragment;
    //
    public Context context;
    public SharedPreferences sharedPref;
    //
    /**
     * on callback from InfoFragment to this Activity
     *
     * @param v view root fragment view
     */
    @Override
    public void receiveResultInfoFragment(View v) {
        rootViewImageFragment = v;
    }
//
}