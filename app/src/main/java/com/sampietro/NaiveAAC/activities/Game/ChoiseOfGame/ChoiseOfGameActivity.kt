package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1Activity
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2Activity
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAActivity
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.RealmResults


/**
 * <h1>ChoiseOfGameActivity</h1>
 *
 * **ChoiseOfGameActivity** allows game choice
 *
 *
 * @version     4.0, 09/09/2023
 * @see GameActivityAbstractClass
 *
 * @see ChoiseOfGameRecyclerViewAdapterInterface
 */
class ChoiseOfGameActivity : GameActivityAbstractClass(), ChoiseOfGameRecyclerViewAdapterInterface {
    //
    var resultsGameParameters: RealmResults<GameParameters>? = null

    //
    var rootViewGameFragment: View? = null

    //
    // private var mContentView: ViewGroup? = null

    /**
     * configurations of game choice start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * // * @see ActionbarFragment
     * @see android.app.Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_container)
        /*
        USED FOR FULL SCREEN
        */
        val mContentView:ViewGroup = findViewById(R.id.activity_game_container_id)
        setToFullScreen()
        val viewTreeObserver = mContentView.getViewTreeObserver()
        //
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                }
            })
        }
        //
        mContentView.setOnClickListener(View.OnClickListener { view: View? -> setToFullScreen() })
        /*

        */
        val realm: Realm = Realm.getDefaultInstance()
        //
        val resultsGameParametersBS: RealmResults<GameParameters>? = realm.where(GameParameters::class.java)
            .beginGroup()
            .equalTo("gameActive", "A")
            .endGroup()
            .findAll()
        if (resultsGameParametersBS != null) {
            resultsGameParameters = resultsGameParametersBS.sort("gameName")
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
        setToFullScreen()
        /*

        */
        val frag = ChoiseOfGameFragment()
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
     * This method is responsible to transfer MainActivity into fullscreen mode.
     * REFER to [stackoverflow](https://stackoverflow.com/questions/68926378/windowinsetscontrollerhidestatusbars-causes-content-to-jump)
     * answer of [flauschtrud](https://stackoverflow.com/users/5369519/flauschtrud)
     */
    private fun setToFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
//        findViewById<View>(R.id.activity_game_container_id).systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    //
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
    //
    /**
     * Called on result of speech.
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onResult(eText: String?) {}

    /**
     * on callback from GameFragment to this Activity
     *
     * @param v view root fragment view
     */
    override fun receiveResultGameFragment(v: View?) {
        rootViewGameFragment = v
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
                    rootViewGameFragment!!.findViewById<View>(R.id.recycler_view) as ChoiseOfGameRecyclerView
                recyclerView.releasePlayer()
//                var gameJavaClass: String? = null
//                var gameParameter: String? = null
//                var gameUseVideoAndSound: String? = null
//                var intent: Intent? = null
                assert(result != null)
                val gameJavaClass = result!!.gameJavaClass
                val gameParameter = result.gameParameter
                val gameUseVideoAndSound = result.gameUseVideoAndSound
                when (gameJavaClass) {
                    "NAVIGATORE" -> {
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
                    "COMUNICATORE" -> {
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
                    "A/DA" -> {
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
                    else -> {}
                }
            }
            R.id.infomediaplayer -> {
                // game info request
//                var gameInfo: String? = null
                assert(result != null)
                val gameInfo = result!!.gameInfo
                //                TextView textinfo = (TextView)rootViewGameFragment.findViewById(R.id.textinfoEP);
//                textinfo.setText("");
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