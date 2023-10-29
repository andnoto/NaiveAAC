package com.sampietro.NaiveAAC.activities.Info.Utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.view.View
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
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1Activity
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game2.Game2Activity
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Utils.OnFragmentEventListenerGame
 */
abstract class InfoActivityAbstractClass : AppCompatActivity(), OnFragmentEventListenerInfo {
    @JvmField
    var fragmentManager: FragmentManager? = null

    //
    var rootViewImageFragment: View? = null

    //
//    @JvmField
    lateinit var context: Context
    lateinit var sharedPref: SharedPreferences
    //
    /**
     * on callback from InfoFragment to this Activity
     *
     * @param v view root fragment view
     */
    override fun receiveResultInfoFragment(v: View?) {
        rootViewImageFragment = v
    } //
}