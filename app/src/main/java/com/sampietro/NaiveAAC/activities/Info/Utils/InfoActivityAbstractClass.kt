package com.sampietro.NaiveAAC.activities.Info.Utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

/**
 * <h1>InfoActivityAbstractClass</h1>
 *
 * **InfoActivityAbstractClass**
 * abstract class containing common methods that is extended by
 *
 * 1) GameActivityChoiseOfGameMediaPlayer
 * 2) GameActivityGame1ViewPager
 * 3) GameActivityGame2
 * implements voice recognizer common methods and others
 * Refer to [howtomakeavideogameusingvoicecommands.blogspot.com](https://howtomakeavideogameusingvoicecommands.blogspot.com/2021/08/presentation.html)
 * and [github.com](https://github.com/andnoto/MyApplicationLibrary)
 *
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1Activity
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game2.Game2Activity
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Utils.OnFragmentEventListenerGame
 */
abstract class InfoActivityAbstractClass : AppCompatActivity() {
    lateinit var fragmentManager: FragmentManager

//
    lateinit var context: Context
    lateinit var sharedPref: SharedPreferences
}