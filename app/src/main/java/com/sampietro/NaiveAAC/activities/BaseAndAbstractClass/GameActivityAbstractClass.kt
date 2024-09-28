package com.sampietro.NaiveAAC.activities.BaseAndAbstractClass

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import io.realm.Realm
import java.util.*
import kotlin.properties.Delegates

/**
 * <h1>GameActivityAbstractClass</h1>
 *
 * **GameActivityAbstractClass**
 * abstract class containing common methods that is extended by
 *
 * 1) ChoiseOfGameActivity
 * 2) Game1Activity
 * 3) Game2Activity
 * 4) GameADAActivity
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
 */
abstract class GameActivityAbstractClass : AppCompatActivity()
    {
    //
    @JvmField
    var hearingImageButton: ImageButton? = null

    //
    lateinit var realm: Realm

    //
    @JvmField
    var rootViewPrizeFragment: View? = null

    //
    @JvmField
    var toBeRecordedInHistory: ToBeRecordedInHistory<VoiceToBeRecordedInHistory>? = null
//
    lateinit var context: Context
//
    lateinit var sharedPref: SharedPreferences
//
    var sharedLastSession by Delegates.notNull<Int>()
//
    var sharedLastPhraseNumber by Delegates.notNull<Int>()
    @JvmField
    var currentTime: Date? = null
    @JvmField
    var voiceToBeRecordedInHistory: VoiceToBeRecordedInHistory? = null
}