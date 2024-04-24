package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1Activity
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2Activity
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAActivity
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import com.google.android.material.snackbar.Snackbar
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1BleActivity
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper
import io.realm.Realm
import io.realm.RealmResults


/**
 * <h1>ChoiseOfGameActivity</h1>
 *
 * **ChoiseOfGameActivity** allows game choice
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClass
 *
 * @see ChoiseOfGameRecyclerViewAdapterInterface
 */
class ChoiseOfGameActivity : GameActivityAbstractClass(), ChoiseOfGameRecyclerViewAdapterInterface {
    // USED FOR FULL SCREEN
    lateinit var mywindow: Window
    //
    var resultsGameParameters: RealmResults<GameParameters>? = null

    //
//    var rootViewGameFragment: View? = null
    /**
     * configurations of game choice start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see android.app.Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_container)
        //
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        /*
        USED FOR FULL SCREEN
        */
        mywindow = getWindow()
        GraphicsAndPrintingHelper.setToFullScreen(mywindow)
        /*

        */
        val realm: Realm = Realm.getDefaultInstance()
        //
        val resultsGameParametersBS: RealmResults<GameParameters>? = realm.where(GameParameters::class.java)
            .beginGroup()
            .equalTo(getString(R.string.gameactive), "A")
            .endGroup()
            .findAll()
        if (resultsGameParametersBS != null) {
            resultsGameParameters = resultsGameParametersBS.sort(getString(R.string.gamename))
        }
    }

    /**
     * configurations of game choice start screen. Hide the Navigation Bar
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/43520688/findfragmentbyid-and-findfragmentbytag)
     * answer of [Ricardo](https://stackoverflow.com/users/4266957/ricardo)
     *
     * @see ChoiseOfGameFragment
     *
     * @see android.app.Activity.onResume
     */
    public override fun onResume() {
        /*
        USED FOR FULL SCREEN
        */
        super.onResume()
        mywindow = getWindow()
        GraphicsAndPrintingHelper.setToFullScreen(mywindow)
        /*

        */
        val frag = ChoiseOfGameFragment(R.layout.activity_game_choise_of_game_mediaplayer)
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.choise_of_game_fragment)) as ChoiseOfGameFragment?
        if (fragmentgotinstance != null && fragmentgotinstance.isAdded()) {
            ft.replace(R.id.game_container, frag, getString(R.string.choise_of_game_fragment))
            // ok, we got the fragment instance, but should we manipulate its view?
        } else {
            ft.add(R.id.game_container, frag, getString(R.string.choise_of_game_fragment))
        }
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the settings button.
     * replace with hear fragment
     *
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    fun returnSettings(v: View?) {
        /*
                navigate to settings screen (ChoiseOfGameActivity)
    */
        val intent = Intent(this, VerifyActivity::class.java)
        startActivity(intent)
    }
    /**
     * called when a game list item is clicked.
     *
     * launch the corresponding game or the corresponding info.
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     * @see ChoiseOfGameRecyclerViewAdapterInterface
     *
     * @see GameParameters
     *
     * @see ChoiseOfGameRecyclerView
     *
     * @see Game1Activity
     *
     * @see Game2Activity
     */
    override fun onItemClick(view: View?, i: Int) {
        val result: GameParameters?
        result = resultsGameParameters!![i]
        when (view!!.id) {
            R.id.media_container -> {
                // game choice
                val recyclerView =
                    findViewById<View>(R.id.recycler_view) as ChoiseOfGameRecyclerView
                recyclerView.releasePlayer()
                assert(result != null)
                val gameJavaClass = result!!.gameJavaClass
                val gameParameter = result.gameParameter
                val gameUseVideoAndSound = result.gameUseVideoAndSound
                when (gameJavaClass) {
                    getString(R.string.navigatore) -> {
                        intent = Intent(
                            this,
                            Game1Activity::class.java
                        )
                        intent.putExtra(EXTRA_MESSAGE_GAME_PARAMETER, gameParameter)
                        intent.putExtra(
                            EXTRA_MESSAGE_GAME_USE_VIDEO_AND_SOUND,
                            gameUseVideoAndSound
                        )
                        startActivity(intent)
                    }
                    getString(R.string.comunicatore) -> {
                        intent = Intent(
                            this,
                            Game2Activity::class.java
                        )
                        intent.putExtra(EXTRA_MESSAGE_GAME_PARAMETER, gameParameter)
                        intent.putExtra(
                            EXTRA_MESSAGE_GAME_USE_VIDEO_AND_SOUND,
                            gameUseVideoAndSound
                        )
                        startActivity(intent)
                    }
                    getString(R.string.a_da) -> {
                        intent = Intent(
                            this,
                            GameADAActivity::class.java
                        )
                        intent.putExtra(EXTRA_MESSAGE_GAME_PARAMETER, gameParameter)
                        intent.putExtra(
                            EXTRA_MESSAGE_GAME_USE_VIDEO_AND_SOUND,
                            gameUseVideoAndSound
                        )
                        startActivity(intent)
                    }
                    "PECS" -> {
                        intent = Intent(
                            this,
                            Game1BleActivity::class.java
                        )
                        intent.putExtra(EXTRA_MESSAGE_GAME_PARAMETER, gameParameter)
                        intent.putExtra(
                            EXTRA_MESSAGE_GAME_USE_VIDEO_AND_SOUND,
                            gameUseVideoAndSound
                        )
                        startActivity(intent)
                    }
                    else -> {}
                }
            }
            R.id.infomediaplayer -> {
                // game info request
                assert(result != null)
                val gameInfo = result!!.gameInfo
                //
                val ctw = ContextThemeWrapper(this, R.style.CustomSnackbarTheme)
                val snackbar = Snackbar.make(
                    ctw,
                    findViewById(R.id.imagegallerychoiseofgameLLEP),
                    gameInfo!!,
                    10000
                )
                snackbar.setTextMaxLines(5)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()
            }
        }
    } //

    companion object {
        const val EXTRA_MESSAGE_GAME_PARAMETER = "GameParameter"
        const val EXTRA_MESSAGE_GAME_USE_VIDEO_AND_SOUND = "GameUseVideoAndSound"
    }
}