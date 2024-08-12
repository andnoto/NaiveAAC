package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Settings.SettingsStoriesWordActivity
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAPhraseOfAStory
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission.checkPermission
import io.realm.Realm
import io.realm.RealmResults

/**
 * <h1>SettingsStoriesImprovementViewPagerActivity</h1>
 *
 * **SettingsStoriesImprovementViewPagerActivity** displays images (uploaded by the user or Arasaac pictograms) of the
 * * phrases of a story
 *
 * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
 * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
 *
 * @version     5.0, 01/04/2024
 * @see GameADAViewPagerActivityAbstractClass
 *
 * @see SettingsStoriesImprovementViewPagerAdapter
 */
class SettingsStoriesImprovementViewPagerActivity : GameADAViewPagerActivityAbstractClass(),
    GameADAViewPagerOnFragmentEventListener, GameADAViewPagerOnFragmentSoundMediaPlayerListener,
    GameADAViewPagerMediaContainerOnClickListener {
    val gameUseVideoAndSound = "N"
    //  ViewPager2
    // - 1) Create the views (activity_game_ada_viewpager_content.xml)
    // - 2) Create the fragment (GameADAViewPagerFragment)
    // - 3) Add a ViewPager2
    // - 4) Create a FragmentStateAdapter who will use the fragments in step 2
    // - 5) Create a layout that contains a ViewPager2 object: (activity_game_ada_viewpager.xml)
    // - 6) This activity must do the following things:
    //   - a) Sets the content view to be the layout with the ViewPager2.
    //   - b) Hooks up the FragmentStateAdapter to the ViewPager2 objects.
    //
    private var mAdapter: SettingsStoriesImprovementViewPagerAdapter? = null
    /**
     * configurations of gameadaviewpager start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SettingsStoriesImprovementViewPagerAdapter
     * @see android.app.Activity.onCreate
     * @see OnPageChangeCallback.onPageSelected
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // viewpager
        setContentView(R.layout.activity_game_ada_viewpager)
        //
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment)).commit()
        }
        //
        realm = Realm.getDefaultInstance()
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            // it display the last word
            // else display the word passed by GameADAActivity
            sharedStory = savedInstanceState.getString(getString(R.string.story_to_display))
            phraseToDisplay = savedInstanceState.getInt(getString(R.string.phrase_to_display))
            wordToDisplay = savedInstanceState.getInt(getString(R.string.word_to_display))
            //
            val resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(getString(R.string.phrasenumberint), phraseToDisplay)
                .equalTo(getString(R.string.wordnumberint), wordToDisplay)
                .endGroup()
                .findAll()
            val storiesSize = resultsStories.size
            if (storiesSize > 0) {
                assert(resultsStories[0] != null)
                wordToDisplayInTheStory = resultsStories[0]!!.wordNumberIntInTheStory
            }
        }
        //
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras != null) {
                sharedStory = extras.getString(getString(R.string.story_to_display))
                phraseToDisplay = extras.getInt(getString(R.string.phrase_to_display))
                wordToDisplay = extras.getInt(getString(R.string.word_to_display))
                //
                val resultsStories = realm.where(Stories::class.java)
                    .beginGroup()
                    .equalTo(getString(R.string.story), sharedStory)
                    .equalTo(getString(R.string.phrasenumberint), phraseToDisplay)
                    .equalTo(getString(R.string.wordnumberint), wordToDisplay)
                    .endGroup()
                    .findAll()
                val storiesSize = resultsStories.size
                if (storiesSize > 0) {
                    assert(resultsStories[0] != null)
                    wordToDisplayInTheStory = resultsStories[0]!!.wordNumberIntInTheStory
                }
                //
            }
        }
        //
        checkPermission(this)
        //
        val resultsStories: RealmResults<Stories> = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .greaterThan(getString(R.string.wordnumberint), 0)
            .lessThan(getString(R.string.wordnumberint), 999)
            .endGroup()
            .findAll()
        //
        context = this
        // viewpager
        // Wire Adapter with ViewPager2
        mViewPager = findViewById<View>(R.id.pager) as ViewPager2
        mAdapter = SettingsStoriesImprovementViewPagerAdapter(
            supportFragmentManager, lifecycle, context, realm,
            sharedStory!!, wordToDisplayInTheStory - 1, gameUseVideoAndSound, resultsStories
        )
        mViewPager!!.adapter = mAdapter
        // Register page change callback
        mViewPager!!.registerOnPageChangeCallback(callbackViewPager2)
        // Set the currently selected page
        mViewPager!!.setCurrentItem(wordToDisplayInTheStory - 1, false)
    }
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //
        savedInstanceState.putString(getString(R.string.story_to_display), sharedStory)
        savedInstanceState.putInt(getString(R.string.phrase_to_display), phraseToDisplay)
        savedInstanceState.putInt(getString(R.string.word_to_display), wordToDisplay)
        savedInstanceState.putString(getString(R.string.game_use_video_and_sound), gameUseVideoAndSound)
        //
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }
    /**
     * Called when the user taps the go back button.
     * return to SettingsStoriesImprovementActivity
     * @param v view of tapped button
     * @see SettingsStoriesImprovementActivity
     */
    fun onClickGoBackFromImprovementViewPager(v: View?) {
        //
        val intent: Intent?
        intent = Intent(
            this,
            SettingsStoriesImprovementActivity::class.java
        )
        intent.putExtra(getString(R.string.story), sharedStory)
        intent.putExtra(getString(R.string.phrase_number), phraseToDisplay)
        startActivity(intent)
    }
    /**
     * Called when the user taps the image.
     * return to SettingsStoriesWordActivity
     * @param v view of tapped button
     * @see GameADAActivity
     */
    override fun onClickGameImage(v: View?) {
        //
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            phraseToDisplay = resultsStories[0]!!.phraseNumberInt
            wordToDisplay = resultsStories[0]!!.wordNumberInt
        }
        val intent: Intent
        intent = Intent(
            this,
            SettingsStoriesWordActivity::class.java
        )
        intent.putExtra(getString(R.string.story_to_display), sharedStory)
        intent.putExtra(getString(R.string.phrase_to_display), phraseToDisplay)
        intent.putExtra(getString(R.string.word_to_display), wordToDisplay)
        intent.putExtra(getString(R.string.update_mode), getString(R.string.modify))
        startActivity(intent)
    }
    /**
     * Called when the user taps insert a word before this one button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesWordActivity
     */
    fun insertsAWordBeforeThisOneButton(view: View?) {
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            phraseToDisplay = resultsStories[0]!!.phraseNumberInt
            wordToDisplay = resultsStories[0]!!.wordNumberInt
        }
        val intent: Intent?
        intent = Intent(
            this,
            SettingsStoriesWordActivity::class.java
        )
        intent.putExtra(getString(R.string.story_to_display), sharedStory)
        intent.putExtra(getString(R.string.phrase_to_display), phraseToDisplay)
        intent.putExtra(getString(R.string.word_to_display), wordToDisplay)
        intent.putExtra(getString(R.string.update_mode), getString(R.string.insert))
        startActivity(intent)
    }
    /**
     * Called when the user taps insert a word after this button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesWordActivity
     */
    fun insertsAWordAfterThisButton(view: View?) {
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            phraseToDisplay = resultsStories[0]!!.phraseNumberInt
            wordToDisplay = resultsStories[0]!!.wordNumberInt
        }
        val intent: Intent?
        intent = Intent(
            this,
            SettingsStoriesWordActivity::class.java
        )
        intent.putExtra(getString(R.string.story_to_display), sharedStory)
        intent.putExtra(getString(R.string.phrase_to_display), phraseToDisplay)
        intent.putExtra(getString(R.string.word_to_display), wordToDisplay + 1)
        intent.putExtra(getString(R.string.update_mode), getString(R.string.insert))
        startActivity(intent)
    }
    /**
     * Called when the user taps remove word button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesImprovementActivity
     */
    fun deleteTheWordButton(view: View?) {
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            phraseToDisplay = resultsStories[0]!!.phraseNumberInt
            wordToDisplay = resultsStories[0]!!.wordNumberInt
        }
        realm = Realm.getDefaultInstance()
        val daCancellare =
            realm.where(Stories::class.java)
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(getString(R.string.phrasenumberint), phraseToDisplay)
                .equalTo(getString(R.string.wordnumberint), wordToDisplay)
                .findAll()
        val daCancellareSize = daCancellare.size
        if (daCancellareSize > 0) {
            // find words to delete
            val wordsToDelete = daCancellare[0]!!.word
            //
            val value0 = 0
            val daModificare =
                realm.where(Stories::class.java)
                    .equalTo(getString(R.string.story), sharedStory)
                    .equalTo(getString(R.string.phrasenumberint), phraseToDisplay)
                    .equalTo(getString(R.string.wordnumberint), value0)
                    .findFirst()
            //
            if(daModificare != null) {
                val wordsToChange = daModificare.word
                val beforeTheWordsToBeDeleted = wordsToChange!!.substringBefore(wordsToDelete!!)
                val afterTheWordsToBeDeleted =  wordsToChange.substringAfter(wordsToDelete)
                realm.beginTransaction()
                daModificare.word = "$beforeTheWordsToBeDeleted $afterTheWordsToBeDeleted"
                realm.insertOrUpdate(daModificare)
                realm.commitTransaction()
            }
            // delete
            realm.beginTransaction()
            daCancellare.deleteAllFromRealm()
            realm.commitTransaction()
            //
            renumberAPhraseOfAStory(
                realm,
                sharedStory,
                phraseToDisplay
            )
            renumberAStory(realm, sharedStory)
        }
        //
        val intent: Intent?
        intent = Intent(
            this,
            SettingsStoriesImprovementActivity::class.java
        )
        intent.putExtra(getString(R.string.story), sharedStory)
        intent.putExtra(getString(R.string.phrase_number), phraseToDisplay)
        startActivity(intent)
    }
}