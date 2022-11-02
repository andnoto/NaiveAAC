package com.sampietro.NaiveAAC.activities.Game.Game1;

import static com.sampietro.NaiveAAC.activities.Game.Game1.GetResultsWordPairsList.getResultsWordPairsList;
import static com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyRegistration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.print.PrintHelper;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Balloon.BalloonGameplayActivity;
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2Fragment;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear;
import com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper;
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment;
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment;
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper;
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper;
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch;
import com.sampietro.NaiveAAC.activities.Phrases.Phrases;
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs;
import com.sampietro.NaiveAAC.activities.history.History;
import com.example.voicerecognitionlibrary.AndroidPermission;
import com.example.voicerecognitionlibrary.SpeechRecognizerManagement;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>Game1Activity</h1>
 * <p><b>Game1Activity</b> displays collections of word images that you can select to form simple sentences
 * </p>
 * the search for words takes place via a two-level menu
 * the first level contains the main research classes such as games, food, family, animals
 * the second level contains the subclasses of the first level for example
 * for the game class subclasses ball, tablet, running, etc.
 * the sentences are formed by coupling the words of the subclasses with the words contained in the wordpairs table
 * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
 * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
 *
 * @version     1.0, 06/13/22
 * @see GameActivityAbstractClass
 * @see Game1RecyclerViewAdapterInterface
 * @see PrizeFragment.onFragmentEventListenerPrize
 * @see YoutubePrizeFragment.onFragmentEventListenerYoutubePrize
 */
public class Game1Activity extends GameActivityAbstractClass implements
        Game1RecyclerViewAdapterInterface,
        PrizeFragment.onFragmentEventListenerPrize,
        YoutubePrizeFragment.onFragmentEventListenerYoutubePrize
{
    public Bundle onCreateSavedInstanceState = null;
    //
    public static final String EXTRA_MESSAGE_BALLOON ="GameJavaClass" ;
    // TTS
    public TextToSpeech tTS1;
    public String toSpeak;
    //
    public List<WordPairs> resultsWordPairsList;
    public WordPairs resultWordPairs;
    //
    public RealmResults<WordPairs> resultsWordPairsCenter;
    public List<WordPairs> resultsWordPairsCenterList;
    public int resultsWordPairsCenterSize;
    //
    public RealmResults<WordPairs> resultsWordPairsRight;
    public List<WordPairs> resultsWordPairsRightList;
    public int resultsWordPairsRightSize;
    //
    public RealmResults<WordPairs> resultsWordPairsLeft;
    public List<WordPairs> resultsWordPairsLeftList;
    public int resultsWordPairsLeftSize;
    // at the fragment I pass three strings with, for each column,
    // the possible content of the chosen words
    // and three numbers which, if applicable, indicate the choice menus for each column
    // identified by PhraseNumber on History
    public String leftColumnContent;
    public String leftColumnContentUrlType;
    public String leftColumnContentUrl;
    public String middleColumnContent;
    public String middleColumnContentUrlType;
    public String middleColumnContentUrl;
    public String rightColumnContent;
    public String rightColumnContentUrlType;
    public String rightColumnContentUrl;
    //
    public Integer leftColumnMenuPhraseNumber;
    public Integer middleColumnMenuPhraseNumber;
    public Integer rightColumnMenuPhraseNumber;
    //
    //
    public String rightColumnAwardType;
    public String rightColumnUriPremiumVideo;
    //
    public String middleColumnContentVerbInTheInfinitiveForm;
    //
    public int numberOfWordsChosen;
    //
    public String sharedLastPlayer;
    //
    public String wordToSearchSecondLevelMenu;
    //
    public String preference_PrintPermissions;
    public int preference_AllowedMarginOfError;
    /**
     * used for printing
     */
    public Bitmap bitmap1;
    /**
     * used for printing
     */
    public Target target1 = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bitmap1 = bitmap;
        }
        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
    //  ViewPager2
    // - 1) Create the views (activity_game_1_viewpager_content.xml)
    // - 2) Create the fragment (Game1ViewPagerFirstLevelFragment and Game1ViewPagerSecondLevelFragment)
    // - 3) Add a ViewPager2
    // - 4) Create a FragmentStateAdapter who will use the fragments in step 2
    // - 5) Create a layout that contains a ViewPager2 object: (activity_game_1_viewpager.xml)
    // - 6) This activity must do the following things:
    //   - a) Sets the content view to be the layout with the ViewPager2.
    //   - b) Hooks up the FragmentStateAdapter to the ViewPager2 objects.
    //
    private ViewPager2 mViewPager;
    private Game1ViewPagerAdapter mAdapter;
    private Object lifecycle;
    OnPageChangeCallback callbackViewPager2 = new OnPageChangeCallback() {
        /**
         * Define page change callback
         * </p>
         * update activity variables and display second level menu.</p>
         *
         * @param position int with position index of the new selected page
         * @see Game1Activity#displaySecondLevelMenu
         * @see WordPairs
         * @see OnPageChangeCallback#onPageSelected
         */
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            //  update activity variables
            RealmResults<WordPairs> resultsWordPairs =
                    realm.where(WordPairs.class)
                            .beginGroup()
                            .equalTo("isMenuItem", "TLM")
                            .endGroup()
                            .findAll();
            int resultsWordPairsSize = resultsWordPairs.size();
            if (resultsWordPairsSize != 0) {
                WordPairs result = resultsWordPairs.get(position);
                assert result != null;
                wordToSearchSecondLevelMenu = result.getWord1();
                //
                // aggiungere ripristino video prize ?
                if (onCreateSavedInstanceState != null) {
                    // The onSaveInstanceState method is called before an activity may be killed
                    // (for Screen Rotation Handling) so that
                    // when it comes back it can restore its state.
                    // if this activity was playing an award video, it restores the award video
                    // else it restores second level menu
                    wordToSearchSecondLevelMenu =
                            onCreateSavedInstanceState.getString(getString(R.string.word_to_search_second_level_menu), getString(R.string.non_trovato) );
                    //
                    rightColumnAwardType = onCreateSavedInstanceState.getString(getString(R.string.award_type), getString(R.string.non_trovato) );
                    rightColumnUriPremiumVideo =
                            onCreateSavedInstanceState.getString(getString(R.string.uri_premium_video), getString(R.string.non_trovato) );
                    if (!rightColumnAwardType.equals(getString(R.string.non_trovato))) {
                        switch(rightColumnAwardType){
                            case "V":
                                // upload a video as a reward
                                uploadAVideoAsAReward();
                                break;
                            case "Y":
                                // upload a Youtube video as a reward
                                uploadAYoutubeVideoAsAReward();
                                break;
                            default:
                                break;
                        }
                    }
                    else
                    {
                        displaySecondLevelMenu();
                    }
                    onCreateSavedInstanceState = null;
                }
                else
                { displaySecondLevelMenu(); }
            }
        }
    };
    /**
     * configurations of game1 start screen.
     * <p>
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement#prepareSpeechRecognizer
     * @see ActionbarFragment
     * @see #welcomeSpeech
     * @see Game1ViewPagerAdapter
     * @see android.app.Activity#onCreate(Bundle)
     * @see OnPageChangeCallback#onPageSelected
     * @see #uploadAVideoAsAReward
     * @see #uploadAYoutubeVideoAsAReward
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            // if this activity was playing an award video, it restores the award video
            // (in callbackViewPager2)
            onCreateSavedInstanceState = savedInstanceState;
        }
        // viewpager
        setContentView(R.layout.activity_game_1_viewpager);
        //
        AndroidPermission.checkPermission(this);
        //
        SpeechRecognizerManagement.prepareSpeechRecognizer(this);
        //
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment)).commit();
        }
        //
        realm= Realm.getDefaultInstance();
        //
        context = this;
        //
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        preference_PrintPermissions =
                sharedPref.getString (getString(R.string.preference_print_permissions), "DEFAULT");
        preference_AllowedMarginOfError =
                sharedPref.getInt (getString(R.string.preference_allowed_margin_of_error), 20);
        // TTS
        if (savedInstanceState == null)
            welcomeSpeech();
        // viewpager
        // Wire Adapter with ViewPager2
        mViewPager = (ViewPager2) findViewById(R.id.pager);
        Lifecycle lifecycle = getLifecycle();
        mAdapter = new Game1ViewPagerAdapter (getSupportFragmentManager(), lifecycle, context, realm);
        mViewPager.setAdapter(mAdapter);
        // Register page change callback
        mViewPager.registerOnPageChangeCallback(callbackViewPager2);
     }
    /**
     * destroy SpeechRecognizer, TTS shutdown and more
     *
     * @see androidx.fragment.app.Fragment#onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpeechRecognizerManagement.destroyRecognizer();
        // TTS
        if (tTS1 != null) {
            tTS1.stop();
            tTS1.shutdown();
        }
        // Unregister page change callback
        mViewPager.unregisterOnPageChangeCallback(callbackViewPager2);
    }
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     * <p>
     * if it is playing an award video, it stores the relative references
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see PrizeFragment
     * @see YoutubePrizeFragment
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //
        savedInstanceState.putString(getString(R.string.word_to_search_second_level_menu), wordToSearchSecondLevelMenu);
        //
        PrizeFragment prizefragmentgotinstance =
                (PrizeFragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.prize_fragment));
        YoutubePrizeFragment yprizefragmentgotinstance =
                (YoutubePrizeFragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.youtube_prize_fragment));
        if ((prizefragmentgotinstance != null) || (yprizefragmentgotinstance != null))
        {
            savedInstanceState.putString(getString(R.string.award_type), rightColumnAwardType);
            savedInstanceState.putString(getString(R.string.uri_premium_video), rightColumnUriPremiumVideo);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    /**
     * Called when the user taps the start speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement#startSpeech()
     */
    public void startSpeechGame1(View v)
    {
        SpeechRecognizerManagement.startSpeech();
        //
        GameFragmentHear frag= new GameFragmentHear();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        Game1SecondLevelFragment fragmentgotinstance =
                (Game1SecondLevelFragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.game1_second_level_fragment));
        GameFragmentHear hearfragmentgotinstance =
                (GameFragmentHear)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.game_fragment_hear));
        if ((fragmentgotinstance != null) || (hearfragmentgotinstance != null))
        {
            ft.replace(R.id.game_container_game1, frag, getString(R.string.game_fragment_hear));
        }
        else
        {
            ft.add(R.id.game_container_game1, frag, getString(R.string.game_fragment_hear));
        }
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }
    /**
     * Called when the user taps the listen again button.
     * </p>
     * re-reads the text of the sentence just composed
     * </p>
     * Refer to <a href="https://stackoverflow.com/questions/24745546/android-change-activity-after-few-seconds">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/3586222/chefes">Chefes</a>
     *
     * @param v view of tapped button
     * @see #readingOfTheText
     * @see #startSpeechGame1
     */
    public void listenAgainButton (View v)
    {
        readingOfTheText();
        // Time to launch the another activity
        int TIME_OUT = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startSpeechGame1(rootViewImageFragment);
            }
        }, TIME_OUT);
    }
    /**
     * Called when the user taps the continue game button.
     * </p>
     * display second level menu
     *
     * @param v view of tapped button
     * @see #displaySecondLevelMenu
     */
    public void continueGameButton (View v)
    {
        displaySecondLevelMenu();
    }
    /**
     * Called on result of speech.
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResult(String eText) {
        checkAnswer(eText);
}
    /**
     * Called on beginning of speech.
     * replace with hear fragment
     * </p>
     * Refer to <a href="https://stackoverflow.com/questions/43520688/findfragmentbyid-and-findfragmentbytag">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/4266957/ricardo">Ricardo</a>
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     * @see GameFragmentHear
     */
    @Override
    public void onBeginningOfSpeech(String eText) {
    }
    /**
     * Called on end of speech.
     * replace with second level fragment
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see #fragmentTransactionStart
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     */
    @Override
    public void onEndOfSpeech(String editText) {
        fragmentTransactionStart();
    }
    /**
     * Called on error from SpeechRecognizerManagement.
     *
     * @param errorCode int error code from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     * @see #fragmentTransactionStart
     */
    @Override
    public void onError(int errorCode) {
        fragmentTransactionStart();
    }
    /**
     * on callback from PrizeFragment to this Activity
     * </p>
     *
     * @param v view inflated inside PrizeFragment
     * @see PrizeFragment
     */
    @Override
    public void receiveResultImagesFromPrizeFragment(View v) {
    }
    /**
     * callback from PrizeFragment to this Activity on completatio video
     * </p>
     * display second level menu
     *
     * @param v view inflated inside PrizeFragment
     * @see #displaySecondLevelMenu
     * @see PrizeFragment
     */
    @Override
    public void receiveResultOnCompletatioVideoFromPrizeFragment(View v) {
                displaySecondLevelMenu();
    }
    /**
     * on callback from YoutubePrizeFragment to this Activity
     * </p>
     *
     * @param v view inflated inside YoutubePrizeFragment
     * @see YoutubePrizeFragment
     */
    @Override
    public void receiveResultImagesFromYoutubePrizeFragment(View v) {
    }
    /**
     * callback from YoutubePrizeFragment to this Activity on completatio video
     * </p>
     * display second level menu
     *
     * @param v view inflated inside YoutubePrizeFragment
     * @see #displaySecondLevelMenu
     * @see YoutubePrizeFragment
     */
    @Override
    public void receiveResultOnCompletatioVideoFromYoutubePrizeFragment(View v) {
        displaySecondLevelMenu();
    }
    /**
     * display second level menu.
     * <p>
     * the second level contains the subclasses of the first level for example
     * for the game class subclasses ball, tablet, running, etc.
     * <p>
     * 1) search for the second level menu words in the wordpairs table,
     * 2) convert RealmResults<Model> to ArrayList<Model>
     * 3) record History and initiate Fragment transaction
     *
     * @see HistoryRegistrationHelper#historyRegistration
     * @see #fragmentTransactionStart
     */
    public void displaySecondLevelMenu()
    {
        numberOfWordsChosen = 1;
        // search for the second level menu words in the wordpairs table
        resultsWordPairsCenter =
                realm.where(WordPairs.class)
                        .beginGroup()
                        .equalTo(getString(R.string.word1), wordToSearchSecondLevelMenu)
                        .equalTo(getString(R.string.is_menu_item), getString(R.string.slm))
                        .endGroup()
                        .findAll();
        resultsWordPairsCenterSize = resultsWordPairsCenter.size();
        if (resultsWordPairsCenterSize != 0) {
            // convert RealmResults<Model> to ArrayList<Model>
            resultsWordPairsCenterList = getResultsWordPairsList(realm , resultsWordPairsCenter);
            // record History and initiate Fragment transaction
            historyRegistration(context, realm,
                    resultsWordPairsCenterList, resultsWordPairsCenterSize, false);
            leftColumnMenuPhraseNumber = 0;
            sharedLastPhraseNumber =
                        sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
            middleColumnMenuPhraseNumber = sharedLastPhraseNumber;
            rightColumnMenuPhraseNumber = 0;
            leftColumnContent = getString(R.string.nessuno);
            middleColumnContent = getString(R.string.nessuno);
            rightColumnContent = getString(R.string.nessuno);
            if (resultsWordPairsCenterSize == 1) {
                // the first word of the sentence was chosen
                // (in the case of noun and verb only possible choice,
                // the second word of the sentence is also considered to be chosen -
                // in the case of verb and nouns the only possible choice is considered
                // also chosen the second or second and third word of the sentence)
                theFirstWordOfTheSentenceWasChosen(0);
                }
            fragmentTransactionStart();
        }
    }
    /**
     * initiate Fragment transaction.
     * <p>
     *
     * @see Game1SecondLevelFragment
     */
    public void fragmentTransactionStart()
    {
        Game1SecondLevelFragment frag= new Game1SecondLevelFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.left_column_content), leftColumnContent);
        bundle.putString(getString(R.string.left_column_content_url_type), leftColumnContentUrlType);
        bundle.putString(getString(R.string.left_column_content_url), leftColumnContentUrl);
        bundle.putString(getString(R.string.middle_column_content), middleColumnContent);
        bundle.putString(getString(R.string.middle_column_content_url_type), middleColumnContentUrlType);
        bundle.putString(getString(R.string.middle_column_content_url), middleColumnContentUrl);
        bundle.putString(getString(R.string.right_column_content), rightColumnContent);
        bundle.putString(getString(R.string.right_column_content_url_type), rightColumnContentUrlType);
        bundle.putString(getString(R.string.right_column_content_url), rightColumnContentUrl);
        bundle.putInt(getString(R.string.left_column_menu_phrase_number), leftColumnMenuPhraseNumber);
        bundle.putInt(getString(R.string.middle_column_menu_phrase_number), middleColumnMenuPhraseNumber);
        bundle.putInt(getString(R.string.right_column_menu_phrase_number), rightColumnMenuPhraseNumber);
        frag.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        Game1SecondLevelFragment fragmentgotinstance =
                (Game1SecondLevelFragment)
                       getSupportFragmentManager().findFragmentByTag(getString(R.string.game1_second_level_fragment));
        PrizeFragment prizefragmentgotinstance =
                (PrizeFragment)
                       getSupportFragmentManager().findFragmentByTag(getString(R.string.prize_fragment));
        if ((fragmentgotinstance != null) || (prizefragmentgotinstance != null))
        {
            ft.replace(R.id.game_container_game1, frag, getString(R.string.game1_second_level_fragment));
        }
        else
        {
            ft.add(R.id.game_container_game1, frag, getString(R.string.game1_second_level_fragment));
        }
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }
    /**
     * called when a word list item is clicked.
     * </p>
     * 1) prepare the ui through the history table
     * 2) launch the fragment for displaying the ui
     * 3) in case of completion of the choice of words,
     * carries out the grammatical arrangement and the reading of the text,
     * 4) chosen the 3rd and last word of the sentence
     * the following clicks on the words will start the tts</p>
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     * @see Game1RecyclerViewAdapterInterface
     * @see WordPairs
     * @see ImageSearchHelper#imageSearch
     * @see GetResultsWordPairsList#getResultsWordPairsList
     * @see #refineSearchWordPairs
     * @see HistoryRegistrationHelper#historyRegistration
     * @see History
     * @see GrammarHelper#searchType
     * @see #registerTheChoiceForTheViewOnTheLeft
     * @see #sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText
     * @see #registerTheChoiceForTheViewOnTheRight
     * @see #fragmentTransactionStart
     * @see #getTargetBitmapFromUrlUsingPicasso(String, Target)
     * @see #getTargetBitmapFromFileUsingPicasso(File, Target)
     */
    @Override
    public void onItemClick(View view, int i) {
        switch(numberOfWordsChosen){
            case 1:
                // the first word of the sentence was chosen
                // (in the case of noun and verb only possible choice,
                // the second word of the sentence is also considered to be chosen -
                // in the case of verb and nouns the only possible choice is considered
                // also chosen the second or second and third word of the sentence)
                theFirstWordOfTheSentenceWasChosen(i);
                // start of transaction Fragment
                fragmentTransactionStart();
                break;
            case 2:
                // the second word of the sentence was chosen
                // 1) if the chosen word (central) is a verb (type = 3 verbs)
                // 1a) if the chosen word is on the left, I register the choice and keep the options
                // list in the recycler view on the right
                // 1b) if the chosen word is in the center the choice is not allowed
                // (because it has already been chosen) i keep both option lists in the recycler views
                // both on the right and on the left
                // 1c) if the word chosen is on the right i register the choice and
                // keep the options list in the recycler view on the left
                // 2) if the chosen (middle) word is not a verb
                // 2a) if the chosen word is on the left, i register the choice,
                // moving the central column to the right and the left column (that of the choice)
                // to the center and proposing in the left column the choices compatible
                // with the choice (it should be a verb)
                // 2b) if the chosen word is in the center the choice is not allowed (because it has
                // already been chosen) i keep both option lists in the recycler views
                // both on the right and on the left
                // 2c) if the word chosen is on the right, i register the choice moving the central
                // column to the left and the right column (that of the choice) to the center and
                // proposing in the right column the choices compatible with the choice (it should be a verb)
                ResponseImageSearch image = null;
                numberOfWordsChosen++;
                String middleColumnContentType =
                        GrammarHelper.searchType(middleColumnContent, realm);
                // type = 3 verbs
                if (middleColumnContentType.equals("3")) {
                    switch(view.getId()){
                        case R.id.img1:
                            // 1a) if the chosen word is on the left, I register the choice and keep the options
                            // list in the recycler view on the right
                            // if there are no options on the right, i consider the sentence completed
                            resultWordPairs = resultsWordPairsLeftList.get(i);
                            leftColumnContent = resultWordPairs.getWord1();
                            leftColumnMenuPhraseNumber = 0;
                            // image search
                            image = null;
                            image = ImageSearchHelper.imageSearch(realm, leftColumnContent);
                            if (image!=null) {
                                leftColumnContentUrlType = image.getUriType();
                                leftColumnContentUrl = image.getUriToSearch();
                            }
                            if (rightColumnMenuPhraseNumber == 0) {
                                rightColumnContent = " ";
                                numberOfWordsChosen++;
                                // sentence completion check, grammar arrangement and text reading
                                sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                            }
                            // start of transaction Fragment
                            fragmentTransactionStart();
                            break;
                        case R.id.img2:
                            // 1b) if the chosen word is in the center the choice is not allowed
                            // (because it has already been chosen) i keep both option lists in the recycler views
                            // both on the right and on the left
                            // start of transaction Fragment
                            numberOfWordsChosen--;
                            fragmentTransactionStart();
                            break;
                        case R.id.img3:
                            // 1c) if the word chosen is on the right i register the choice and
                            // keep the options list in the recycler view on the left
                            resultWordPairs = resultsWordPairsRightList.get(i);
                            rightColumnContent = resultWordPairs.getWord2();
                            //
                            rightColumnAwardType = resultWordPairs.getAwardType();
                            rightColumnUriPremiumVideo = resultWordPairs.getUriPremiumVideo();
                            //
                            rightColumnMenuPhraseNumber = 0;
                            // image search
                            image = null;
                            image = ImageSearchHelper.imageSearch(realm, rightColumnContent);
                            if (image!=null) {
                                rightColumnContentUrlType = image.getUriType();
                                rightColumnContentUrl = image.getUriToSearch();
                            }
                            // start of transaction Fragment
                            fragmentTransactionStart();
                            break;
                    }
                }
                else {
                    switch(view.getId()){
                        case R.id.img1:
                            // 2a) if the chosen word is on the left, i register the choice,
                            // moving the central column to the right (retrieving the data of the
                            // eventual prize video) and the left column (that of the choice)
                            // to the center and proposing in the left column the choices compatible
                            // with the choice (it should be a verb)
                            rightColumnContent = middleColumnContent;
                            rightColumnContentUrlType = middleColumnContentUrlType;
                            rightColumnContentUrl = middleColumnContentUrl;
                            rightColumnMenuPhraseNumber = 0;
                            //
                            resultWordPairs = resultsWordPairsLeftList.get(i);
                            //
                            rightColumnAwardType = resultWordPairs.getAwardType();
                            rightColumnUriPremiumVideo = resultWordPairs.getUriPremiumVideo();
                            //
                            middleColumnContent = resultWordPairs.getWord1();
                            middleColumnMenuPhraseNumber = 0;
                            // image search
                            image = null;
                            image = ImageSearchHelper.imageSearch(realm, middleColumnContent);
                            if (image!=null) {
                                middleColumnContentUrlType = image.getUriType();
                                middleColumnContentUrl = image.getUriToSearch();
                            }
                            //
                            resultsWordPairsLeft =
                                    realm.where(WordPairs.class)
                                            .beginGroup()
                                            .equalTo(getString(R.string.word2), middleColumnContent)
                                            .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                                            .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                                            .endGroup()
                                            .findAll();
                            resultsWordPairsLeftSize = resultsWordPairsLeft.size();
                            if (resultsWordPairsLeftSize != 0) {
                                // convert RealmResults<Model> to ArrayList<Model>
                                resultsWordPairsList = getResultsWordPairsList(realm, resultsWordPairsLeft);
                                // does not consider wordpairs with pairs of nouns or pairs of verbs
                                // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                                // (leaves only noun-verb pairs or vice versa verb-noun
                                // or verb-verb if the first is an auxiliary verb or a servile verb)
                                resultsWordPairsLeftList = refineSearchWordPairs(resultsWordPairsList);
                                resultsWordPairsLeftSize = resultsWordPairsLeftList.size();
                                //
                                if (resultsWordPairsLeftSize > 1) {
                                    // History registration
                                    historyRegistration
                                            (context, realm,
                                            resultsWordPairsLeftList, resultsWordPairsLeftSize , true);
                                    sharedLastPhraseNumber =
                                            sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
                                    leftColumnMenuPhraseNumber = sharedLastPhraseNumber;
                                    leftColumnContent = getString(R.string.nessuno);
                                }
                                else {
                                    if (resultsWordPairsLeftSize == 1) {
                                        registerTheChoiceForTheViewOnTheLeft();
                                        // sentence completion check, grammar arrangement and text reading
                                        sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                                    }
                                }
                            }
                            else {
                                // resultsWordPairsLeftSize == 0 : the sentence is complete with just two words
                                leftColumnContent = " ";
                                leftColumnMenuPhraseNumber = 0;
                                numberOfWordsChosen++;
                                // sentence completion check, grammar arrangement and text reading
                                sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                            }
                            // start of transaction Fragment
                            fragmentTransactionStart();
                            break;
                        case R.id.img2:
                            // 2b) if the chosen word is in the center the choice is not allowed (because it has
                            // already been chosen) i keep both option lists in the recycler views
                            // both on the right and on the left
                            numberOfWordsChosen--;
                            fragmentTransactionStart();
                            break;
                        case R.id.img3:
                            // 2c) if the word chosen is on the right, i register the choice moving the central
                            // column to the left and the right column (that of the choice) to the center and
                            // proposing in the right column the choices compatible with the choice (it should be a verb)
                            leftColumnContent = middleColumnContent;
                            leftColumnContentUrlType = middleColumnContentUrlType;
                            leftColumnContentUrl = middleColumnContentUrl;
                            leftColumnMenuPhraseNumber = 0;
                            //
                            resultWordPairs = resultsWordPairsRightList.get(i);
                            middleColumnContent = resultWordPairs.getWord2();
                            middleColumnMenuPhraseNumber = 0;
                            // image search
                            image = null;
                            image = ImageSearchHelper.imageSearch(realm, middleColumnContent);
                            if (image!=null) {
                                middleColumnContentUrlType = image.getUriType();
                                middleColumnContentUrl = image.getUriToSearch();
                            }
                            //
                            resultsWordPairsRight =
                                    realm.where(WordPairs.class)
                                            .beginGroup()
                                            .equalTo(getString(R.string.word1), middleColumnContent)
                                            .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                                            .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                                            .endGroup()
                                            .findAll();
                            resultsWordPairsRightSize = resultsWordPairsRight.size();
                            if (resultsWordPairsRightSize != 0) {
                                // convert RealmResults<Model> to ArrayList<Model>
                                resultsWordPairsList = getResultsWordPairsList(realm, resultsWordPairsRight);
                                // does not consider wordpairs with pairs of nouns or pairs of verbs
                                // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                                // (leaves only noun-verb pairs or vice versa verb-noun
                                // or verb-verb if the first is an auxiliary verb or a servile verb)
                                resultsWordPairsRightList = refineSearchWordPairs(resultsWordPairsList);
                                resultsWordPairsRightSize = resultsWordPairsRightList.size();
                                //
                                if (resultsWordPairsRightSize > 1) {
                                    // History registration
                                    historyRegistration
                                            (context, realm,
                                            resultsWordPairsRightList, resultsWordPairsRightSize , false);
                                    sharedLastPhraseNumber =
                                            sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
                                    rightColumnMenuPhraseNumber = sharedLastPhraseNumber;
                                    rightColumnContent = getString(R.string.nessuno);
                                }
                                else {
                                    if (resultsWordPairsRightSize == 1) {
                                        registerTheChoiceForTheViewOnTheRight();
                                        // sentence completion check, grammar arrangement and text reading
                                        sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                                    }
                                }
                            }
                            else {
                                // resultsWordPairsRightSize == 0 : the sentence is complete with just two words
                                rightColumnContent = " ";
                                rightColumnMenuPhraseNumber = 0;
                                numberOfWordsChosen++;
                                // sentence completion check, grammar arrangement and text reading
                                sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                            }
                            // start of transaction Fragment
                            fragmentTransactionStart();
                            break;
                    }
                }
                break;
            case 3:
                // chosen the 3rd and last word of the sentence (to be checked if the sentence completes)
                // the following clicks on the words (case 4) will start the tts
                // 1a) if the word chosen is on the left, register the choice (if it has not already been made)
                // 1b) if the chosen word is in the center, the choice is not allowed
                // (because it has already been chosen)
                // 1c) if the word chosen is on the right I register the choice
                // (if it has not already been made)
                numberOfWordsChosen++;
                switch(view.getId()){
                    case R.id.img1:
                        // 1a) if the word chosen is on the left, register the choice
                        // (if it has not already been made)
                        if (leftColumnContent.equals(getString(R.string.nessuno))) {
                            resultWordPairs = resultsWordPairsLeftList.get(i);
                            leftColumnContent = resultWordPairs.getWord1();
                            leftColumnMenuPhraseNumber = 0;
                            // image search
                            image = null;
                            image = ImageSearchHelper.imageSearch(realm, leftColumnContent);
                            if (image!=null) {
                                leftColumnContentUrlType = image.getUriType();
                                leftColumnContentUrl = image.getUriToSearch();
                            }
                            // sentence completion check, grammar arrangement and text reading
                            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                        }
                        else  {
                            numberOfWordsChosen--;
                        }
                        // start of transaction Fragment
                        fragmentTransactionStart();
                        break;
                    case R.id.img2:
                        // 1b) if the chosen word is in the center, the choice is not allowed
                        // (because it has already been chosen)
                        // start of transaction Fragment
                        numberOfWordsChosen--;
                        fragmentTransactionStart();
                        break;
                    case R.id.img3:
                        // 1c) if the word chosen is on the right I register the choice
                        // (if it has not already been made)
                        if (rightColumnContent.equals(getString(R.string.nessuno))) {
                            resultWordPairs = resultsWordPairsRightList.get(i);
                            rightColumnContent = resultWordPairs.getWord2();
                            //
                            rightColumnAwardType = resultWordPairs.getAwardType();
                            rightColumnUriPremiumVideo = resultWordPairs.getUriPremiumVideo();
                            //
                            rightColumnMenuPhraseNumber = 0;
                            // image search
                            image = null;
                            image = ImageSearchHelper.imageSearch(realm, rightColumnContent);
                            if (image!=null) {
                                rightColumnContentUrlType = image.getUriType();
                                rightColumnContentUrl = image.getUriToSearch();
                            }
                            // sentence completion check, grammar arrangement and text reading
                            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                        }
                        else  {
                            numberOfWordsChosen--;
                         }
                        // start of transaction Fragment
                        fragmentTransactionStart();
                        break;
                }
                break;
            case 4:
                // the following clicks on the words will start the tts
                switch(view.getId()){
                    case R.id.img1:
                        // TTS
                        tTS1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if(status != TextToSpeech.ERROR) {
                                    tTS1.speak(leftColumnContent, TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                                }
                                else
                                {
                                    Toast.makeText(context, status,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //
                        if (preference_PrintPermissions.equals(getString(R.string.character_y))) {
                            if (leftColumnContentUrlType.equals(getString(R.string.character_a)))
                            {
                                getTargetBitmapFromUrlUsingPicasso(leftColumnContentUrl, target1);
                            }
                            else
                            {
                                File f = new File(leftColumnContentUrl);
                                getTargetBitmapFromFileUsingPicasso(f, target1);
                            }
                            PrintHelper photoPrinter = new PrintHelper(context);
                            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                            photoPrinter.printBitmap(getString(R.string.stampa_immagine1), bitmap1);
                        }
                        break;
                    case R.id.img2:
                        // TTS
                        tTS1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if(status != TextToSpeech.ERROR) {
                                    tTS1.speak(middleColumnContent, TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                                }
                                else
                                {
                                    Toast.makeText(context, status,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //
                        if (preference_PrintPermissions.equals(getString(R.string.character_y))) {
                            if (middleColumnContentUrlType.equals(getString(R.string.character_a)))
                            {
                                getTargetBitmapFromUrlUsingPicasso(middleColumnContentUrl, target1);
                            }
                            else
                            {
                                File f = new File(middleColumnContentUrl);
                                getTargetBitmapFromFileUsingPicasso(f, target1);
                            }
                            PrintHelper photoPrinter = new PrintHelper(context);
                            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                            photoPrinter.printBitmap(getString(R.string.stampa_immagine1), bitmap1);
                        }
                        break;
                    case R.id.img3:
                        // TTS
                        tTS1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if(status != TextToSpeech.ERROR) {
                                    tTS1.speak(rightColumnContent, TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                                }
                                else
                                {
                                    Toast.makeText(context, status,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //
                        if (preference_PrintPermissions.equals("Y")) {
                            if (rightColumnContentUrlType.equals("A"))
                            {
                                getTargetBitmapFromUrlUsingPicasso(rightColumnContentUrl, target1);
                            }
                            else
                            {
                                File f = new File(rightColumnContentUrl);
                                getTargetBitmapFromFileUsingPicasso(f, target1);
                            }
                            PrintHelper photoPrinter = new PrintHelper(context);
                            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                            photoPrinter.printBitmap(getString(R.string.stampa_immagine1), bitmap1);
                        }
                        break;
                }
                break;
            default:
            }
    }
    public void theFirstWordOfTheSentenceWasChosen(int i) {
        numberOfWordsChosen++;
        //
        leftColumnMenuPhraseNumber = 0;
        middleColumnMenuPhraseNumber = 0;
        rightColumnMenuPhraseNumber = 0;
        leftColumnContent = getString(R.string.nessuno);
        middleColumnContent = getString(R.string.nessuno);
        rightColumnContent = getString(R.string.nessuno);
        // retrieves the chosen word
        resultWordPairs = resultsWordPairsCenterList.get(i);
        middleColumnContent = resultWordPairs.getWord2();
        // search for the corresponding image
        ResponseImageSearch image = null;
        image = ImageSearchHelper.imageSearch(realm, middleColumnContent);
        if (image!=null) {
            middleColumnContentUrlType = image.getUriType();
            middleColumnContentUrl = image.getUriToSearch();
        }
        // search for related words on wordpairs
        // if the chosen word is the first of the pair,
        // the corresponding second words go to the right column
        // vice versa if the chosen word is the second of the pair,
        // the corresponding second words go to the left column
        resultsWordPairsLeft =
                realm.where(WordPairs.class)
                        .beginGroup()
                        .equalTo(getString(R.string.word2), middleColumnContent)
                        .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                        .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                        .endGroup()
                        .findAll();
        resultsWordPairsLeftSize = resultsWordPairsLeft.size();
        if (resultsWordPairsLeftSize != 0) {
            // convert RealmResults<Model> to ArrayList<Model>
            resultsWordPairsList = getResultsWordPairsList(realm, resultsWordPairsLeft);
            // does not consider wordpairs with pairs of nouns or pairs of verbs
            // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
            // (leaves only noun-verb pairs or vice versa verb-noun
            // or verb-verb if the first is an auxiliary verb or a servile verb)
            resultsWordPairsLeftList = refineSearchWordPairs(resultsWordPairsList);
            resultsWordPairsLeftSize = resultsWordPairsLeftList.size();
            //
            if (resultsWordPairsLeftSize > 1) {
                // History registration
                historyRegistration
                        (context, realm,
                                resultsWordPairsLeftList, resultsWordPairsLeftSize , true);
                sharedLastPhraseNumber =
                        sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
                leftColumnMenuPhraseNumber = sharedLastPhraseNumber;
            }
        }
        //
        resultsWordPairsRight =
                realm.where(WordPairs.class)
                        .beginGroup()
                        .equalTo(getString(R.string.word1), middleColumnContent)
                        .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                        .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                        .endGroup()
                        .findAll();
        resultsWordPairsRightSize = resultsWordPairsRight.size();
        if (resultsWordPairsRightSize != 0) {
            // convert RealmResults<Model> to ArrayList<Model>
            resultsWordPairsList = getResultsWordPairsList(realm, resultsWordPairsRight);
            // does not consider wordpairs with pairs of nouns or pairs of verbs
            // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
            // (leaves only noun-verb pairs or vice versa verb-noun
            // or verb-verb if the first is an auxiliary verb or a servile verb)
            resultsWordPairsRightList = refineSearchWordPairs(resultsWordPairsList);
            resultsWordPairsRightSize = resultsWordPairsRightList.size();
            //
            if (resultsWordPairsRightSize > 1) {
                // History registration
                historyRegistration(context, realm,
                        resultsWordPairsRightList, resultsWordPairsRightSize, false);
                sharedLastPhraseNumber =
                        sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
                rightColumnMenuPhraseNumber = sharedLastPhraseNumber;
            }
        }
        // if you have only one possible choice (in the recycler views on the right and left)
        // and this is a verb (if the first word chosen is not a verb, the proposed choices
        // can only be verbs)
        // 1a) if the verb only possible choice is on the left, register the choice
        // by moving the central column to the right and the left column in the center
        // and proposing in the left column the choices compatible with the verb
        // 1b) if the verb only possible choice is on the right, register the choice
        // moving the middle column to the left and the right column in the center
        // and proposing in the right column the choices compatible with the verb
        // 1c) if I have only one possible choice verb both on the left and on the right,
        // I propose the choice both in the column on the right and in the one on the left
        // 2) if the first word chosen (the middle one) is a verb, check if I have only one
        // possible choice in the recycler views on the right and left
        // in this case I register the choice
        String middleColumnContentType =
                GrammarHelper.searchType(middleColumnContent, realm);
        // type = 3 verbs
        if (!middleColumnContentType.equals("3")) {
            if (resultsWordPairsLeftSize == 1 && resultsWordPairsRightSize == 0) {
                // 1a) if the verb only possible choice is on the left, register the choice
                // by moving the central column to the right and the left column in the center
                // and proposing in the left column the choices compatible with the verb
                rightColumnContent = middleColumnContent;
                rightColumnContentUrlType = middleColumnContentUrlType;
                rightColumnContentUrl = middleColumnContentUrl;
                rightColumnMenuPhraseNumber = 0;
                //
                resultWordPairs = resultsWordPairsLeftList.get(0);
                middleColumnContent = resultWordPairs.getWord1();
                middleColumnMenuPhraseNumber = 0;
                //
                rightColumnAwardType = resultWordPairs.getAwardType();
                rightColumnUriPremiumVideo = resultWordPairs.getUriPremiumVideo();
                // image search
                image = null;
                image = ImageSearchHelper.imageSearch(realm, middleColumnContent);
                if (image!=null) {
                    middleColumnContentUrlType = image.getUriType();
                    middleColumnContentUrl = image.getUriToSearch();
                }
                //
                resultsWordPairsLeft =
                        realm.where(WordPairs.class)
                                .beginGroup()
                                .equalTo(getString(R.string.word2), middleColumnContent)
                                .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                                .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                                .endGroup()
                                .findAll();
                int resultsWordPairsLeftSize = resultsWordPairsLeft.size();
                if (resultsWordPairsLeftSize != 0) {
                    // convert RealmResults<Model> to ArrayList<Model>
                    resultsWordPairsList = getResultsWordPairsList(realm, resultsWordPairsLeft);
                    // does not consider wordpairs with pairs of nouns or pairs of verbs
                    // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                    // (leaves only noun-verb pairs or vice versa verb-noun
                    // or verb-verb if the first is an auxiliary verb or a servile verb)
                    resultsWordPairsLeftList = refineSearchWordPairs(resultsWordPairsList);
                    resultsWordPairsLeftSize = resultsWordPairsLeftList.size();
                    //
                    if (resultsWordPairsLeftSize > 1) {
                        // History registration
                        historyRegistration
                                (context, realm,
                                        resultsWordPairsLeftList, resultsWordPairsLeftSize , true);
                        sharedLastPhraseNumber =
                                sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
                        leftColumnMenuPhraseNumber = sharedLastPhraseNumber;
                        leftColumnContent = getString(R.string.nessuno);
                    }
                    else {
                        if (resultsWordPairsLeftSize == 1) {
                            registerTheChoiceForTheViewOnTheLeft();
                            numberOfWordsChosen++;
                            // sentence completion check, grammar arrangement and text reading
                            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                        }
                    }
                }
                else {
                    // resultsWordPairsLeftSize == 0 : the sentence is complete
                    leftColumnContent = " ";
                    leftColumnMenuPhraseNumber = 0;
                    numberOfWordsChosen++;
                    // sentence completion check, grammar arrangement and text reading
                    sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                }
                numberOfWordsChosen++;
            }
            if (resultsWordPairsLeftSize == 0 && resultsWordPairsRightSize == 1) {
                // 1b) if the verb only possible choice is on the right, register the choice
                // moving the middle column to the left and the right column in the center
                // and proposing in the right column the choices compatible with the verb
                leftColumnContent = middleColumnContent;
                leftColumnContentUrlType = middleColumnContentUrlType;
                leftColumnContentUrl = middleColumnContentUrl;
                leftColumnMenuPhraseNumber = 0;
                //
                resultWordPairs = resultsWordPairsRightList.get(0);
                middleColumnContent = resultWordPairs.getWord2();
                middleColumnMenuPhraseNumber = 0;
                // image search
                image = null;
                image = ImageSearchHelper.imageSearch(realm, middleColumnContent);
                if (image!=null) {
                    middleColumnContentUrlType = image.getUriType();
                    middleColumnContentUrl = image.getUriToSearch();
                }
                //
                resultsWordPairsRight =
                        realm.where(WordPairs.class)
                                .beginGroup()
                                .equalTo(getString(R.string.word1), middleColumnContent)
                                .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                                .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                                .endGroup()
                                .findAll();
                int resultsWordPairsRightSize = resultsWordPairsRight.size();
                if (resultsWordPairsRightSize != 0) {
                    // convert RealmResults<Model> to ArrayList<Model>
                    resultsWordPairsList = getResultsWordPairsList(realm, resultsWordPairsRight);
                    // does not consider wordpairs with pairs of nouns or pairs of verbs
                    // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                    // (leaves only noun-verb pairs or vice versa verb-noun
                    // or verb-verb if the first is an auxiliary verb or a servile verb)
                    resultsWordPairsRightList = refineSearchWordPairs(resultsWordPairsList);
                    resultsWordPairsRightSize = resultsWordPairsRightList.size();
                    //
                    if (resultsWordPairsRightSize > 1) {
                        // History registration
                        historyRegistration
                                (context, realm,
                                        resultsWordPairsRightList, resultsWordPairsRightSize , false);
                        sharedLastPhraseNumber =
                                sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
                        rightColumnMenuPhraseNumber = sharedLastPhraseNumber;
                        rightColumnContent = getString(R.string.nessuno);
                    }
                    else {
                        if (resultsWordPairsRightSize == 1) {
                            registerTheChoiceForTheViewOnTheRight();
                            numberOfWordsChosen++;
                            // sentence completion check, grammar arrangement and text reading
                            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                        }
                    }
                }
                else {
                    // resultsWordPairsRightSize == 0 : la frase è completa
                    rightColumnContent = " ";
                    rightColumnMenuPhraseNumber = 0;
                    numberOfWordsChosen++;
                    // sentence completion check, grammar arrangement and text reading
                    sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
                }
                numberOfWordsChosen++;
            }
            if (resultsWordPairsLeftSize == 1 && resultsWordPairsRightSize == 1) {
                // 1c) if I have only one possible choice verb both on the left and on the right,
                // I propose the choice both in the column on the right and in the one on the left
                // History registration
                historyRegistration
                        (context, realm,
                                resultsWordPairsLeftList, resultsWordPairsLeftSize , true);
                sharedLastPhraseNumber =
                        sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
                leftColumnMenuPhraseNumber = sharedLastPhraseNumber;
                historyRegistration(context, realm,
                        resultsWordPairsRightList, resultsWordPairsRightSize, false);
                sharedLastPhraseNumber =
                        sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
                rightColumnMenuPhraseNumber = sharedLastPhraseNumber;
            }
        }
        else {
            // 2) if the first word chosen (the middle one) is a verb, check if I have only one
            // possible choice in the recycler views on the right and left
            // in this case I register the choice
            if (resultsWordPairsLeftSize == 1) {
                registerTheChoiceForTheViewOnTheLeft();
            }
            if (resultsWordPairsRightSize == 1) {
                registerTheChoiceForTheViewOnTheRight();
            }
            if (resultsWordPairsLeftSize == 1 && resultsWordPairsRightSize == 1) {
                // sentence completion check, grammar arrangement and text reading
                sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
            }
        }
        // if you do not have possible choices neither on the recycler view on the right
        // nor on that one on the left, the sentence is completed with a single word
        if (resultsWordPairsLeftSize == 0 && resultsWordPairsRightSize == 0) {
            leftColumnContent = " ";
            leftColumnMenuPhraseNumber = 0;
            numberOfWordsChosen++;
            rightColumnContent = " ";
            rightColumnMenuPhraseNumber = 0;
            numberOfWordsChosen++;
            // sentence completion check, grammar arrangement and text reading
            sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText();
        }
    }
    /**
     * register the choice for the view on the left.
     * <p>
     *
     * @see ImageSearchHelper#imageSearch
     */
    public void registerTheChoiceForTheViewOnTheLeft() {
        //
        resultWordPairs = resultsWordPairsLeftList.get(0);
        leftColumnContent = resultWordPairs.getWord1();
        leftColumnMenuPhraseNumber = 0;
        // image search
        ResponseImageSearch image = null;
        image = ImageSearchHelper.imageSearch(realm, leftColumnContent);
        if (image!=null) {
            leftColumnContentUrlType = image.getUriType();
            leftColumnContentUrl = image.getUriToSearch();
        }
        numberOfWordsChosen++;
    }
    /**
     * register the choice for the view on the right.
     * <p>
     *
     * @see ImageSearchHelper#imageSearch
     */
    public void registerTheChoiceForTheViewOnTheRight() {
        //
        resultWordPairs = resultsWordPairsRightList.get(0);
        rightColumnContent = resultWordPairs.getWord2();
        //
        rightColumnAwardType = resultWordPairs.getAwardType();
        rightColumnUriPremiumVideo = resultWordPairs.getUriPremiumVideo();
        //
        rightColumnMenuPhraseNumber = 0;
        // image search
        ResponseImageSearch image = null;
        image = ImageSearchHelper.imageSearch(realm, rightColumnContent);
        if (image!=null) {
            rightColumnContentUrlType = image.getUriType();
            rightColumnContentUrl = image.getUriToSearch();
        }
        numberOfWordsChosen++;
    }
    /**
     * leaves only noun-verb pairs or vice versa verb-noun.
     * <p>
     *
     * @param resultsWordPairsList List<WordPairs> to clean up
     * @return List<WordPairs> cleaned up
     * @see WordPairs
     * @see GrammarHelper#searchType
     */
    public List<WordPairs> refineSearchWordPairs(List<WordPairs> resultsWordPairsList)
    {
        // does not consider wordpairs with pairs of nouns or pairs of verbs
        // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
        // (leaves only noun-verb pairs or vice versa verb-noun
        // or verb-verb if the first is an auxiliary verb or a servile verb)
        String auxiliaryVerb;
        String servileVerb;
        //
        int i=0;
        int resultsWordPairsSize = resultsWordPairsList.size();
        while(i < resultsWordPairsSize) {
            resultWordPairs = resultsWordPairsList.get(i);
            String word1Type =
                    GrammarHelper.searchType(resultWordPairs.getWord1(), realm);
            // type = 3 verbs
            String word2Type =
                    GrammarHelper.searchType(resultWordPairs.getWord2(), realm);
            //
            auxiliaryVerb = GrammarHelper.searchAuxiliaryVerbs(resultWordPairs.getWord1(), realm);
            servileVerb = GrammarHelper.searchServileVerbs(resultWordPairs.getWord1(), realm);
            if ((word1Type.equals("3") && word2Type.equals("3")
                && (!auxiliaryVerb.equals("Is an auxiliary verb")) && (!servileVerb.equals("Is a servile verb")))
                || (!word1Type.equals("3") && !word2Type.equals("3"))) {
                resultsWordPairsList.remove(i);
                resultsWordPairsSize--;
                } else {
                i++;
            }
        }
        return resultsWordPairsList;
    }
    /**
     * sentence completion check, grammar arrangement and text reading.
     * <p>
     *
     * @see #grammaticalArrangement
     * @see #readingOfTheText
     * @see #startSpeechGame1
     */
    public void sentenceCompletionCheckGrammaticalArrangementAndReadingOfTheText() {
        // sentence completion check
        if (!leftColumnContent.equals(getString(R.string.nessuno))
                && !middleColumnContent.equals(getString(R.string.nessuno))
                && !rightColumnContent.equals(getString(R.string.nessuno))) {
            // grammar arrangement and text reading
            grammaticalArrangement();
            readingOfTheText();
            //
            int TIME_OUT = 2000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSpeechGame1(rootViewImageFragment);
                }
            }, TIME_OUT);
        }
    }
    /**
     * grammar arrangement:
     * adds the corresponding article.
     * <p>
     *
     * @see WordPairs
     * @see GrammarHelper#searchPlural
     * @see GrammarHelper#searchGender
     * @see GrammarHelper#searchArticle
     * @see GrammarHelper#searchVerb
     * @see GrammarHelper#searchVerbsOfMovement
     */
    public void grammaticalArrangement() {
    // nominal group subject
    String pluralToSearchRealm = "";
    String genderToSearchRealm;
    String formToSearchRealm;
    String verbOfMovement;
    //
    String leftColumnContentIsAServileVerb = GrammarHelper.searchServileVerbs(leftColumnContent, realm);
    if (leftColumnContentIsAServileVerb.equals("Is a servile verb")) {
        formToSearchRealm = getString(R.string.s1);
        String conjugationOfTheVerb =
                GrammarHelper.searchVerb(leftColumnContent,formToSearchRealm, realm);
        leftColumnContent = conjugationOfTheVerb;
        }
    else {
        if (!leftColumnContent.equals(sharedLastPlayer) && !leftColumnContent.equals(getString(R.string.io))) {
            // adds the corresponding article
            // search if plural
            // if gender male / female
            pluralToSearchRealm = GrammarHelper.searchPlural(leftColumnContent, realm);
            genderToSearchRealm = GrammarHelper.searchGender(leftColumnContent, realm);
            String articleToSearch = GrammarHelper.searchArticle(leftColumnContent,
                    genderToSearchRealm, pluralToSearchRealm, "", realm);
            leftColumnContent = articleToSearch + leftColumnContent;
        }
    }
    // verb group verb
    if (!leftColumnContentIsAServileVerb.equals("Is a servile verb")) {
        if (!(leftColumnContent.equals(" ")
                && rightColumnContent.equals(" "))) {
            if (leftColumnContent.equals(getString(R.string.io))) {
                formToSearchRealm = getString(R.string.s1); }
            else {
            if (leftColumnContent.equals(getString(R.string.la_famiglia))
                    || leftColumnContent.equals(" ")) {
                formToSearchRealm = getString(R.string.p1); }
                else {
                if (!pluralToSearchRealm.equals(getString(R.string.character_y))) {
                    formToSearchRealm = getString(R.string.s3); }
                    else {
                    formToSearchRealm = getString(R.string.p3); }
                }
            }
        middleColumnContentVerbInTheInfinitiveForm = middleColumnContent;
        String conjugationOfTheVerb =
                GrammarHelper.searchVerb(middleColumnContent,formToSearchRealm, realm);
        middleColumnContent = conjugationOfTheVerb;
        }
    }
    else {
        middleColumnContentVerbInTheInfinitiveForm = middleColumnContent;
    }
    // verbal group direct object
    if (!rightColumnContent.equals(sharedLastPlayer)
            && !rightColumnContent.equals(getString(R.string.io))
            && !rightColumnContent.equals(" ")) {
        // ricerca complementi
        RealmResults<WordPairs> resultsWordPairs =
                realm.where(WordPairs.class)
                        .beginGroup()
                        .equalTo(getString(R.string.word1), middleColumnContentVerbInTheInfinitiveForm)
                        .equalTo(getString(R.string.word2), rightColumnContent)
                        .notEqualTo(getString(R.string.is_menu_item), getString(R.string.tlm))
                        .notEqualTo(getString(R.string.is_menu_item), getString(R.string.slm))
                        .endGroup()
                        .findAll();
        int resultsWordPairsSize = resultsWordPairs.size();
        if (resultsWordPairsSize != 0) {
            WordPairs resultWordPairs = resultsWordPairs.get(0);
            assert resultWordPairs != null;
            String rightColumnComplement = resultWordPairs.getComplement();
            if (!rightColumnComplement.equals(" ") && !rightColumnComplement.equals("")) {
                rightColumnContent = rightColumnComplement + " " + rightColumnContent;
                }
                else {
                //
                // adds the corresponding article
                // search if plural
                // if gender male / female
                verbOfMovement = GrammarHelper.searchVerbsOfMovement(middleColumnContentVerbInTheInfinitiveForm, realm);
                pluralToSearchRealm = GrammarHelper.searchPlural(rightColumnContent, realm);
                genderToSearchRealm = GrammarHelper.searchGender(rightColumnContent, realm);
                String articleToSearch = GrammarHelper.searchArticle(rightColumnContent,
                        genderToSearchRealm, pluralToSearchRealm, verbOfMovement, realm);
                rightColumnContent = articleToSearch + rightColumnContent;
                }
        }
    }
    }
    /**
     * reads the previously completed sentence
     * <p>
     */
    public void readingOfTheText() {
    // text reading
    toSpeak = leftColumnContent + " " + middleColumnContent
                    + " " + rightColumnContent;
    tTS1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        tTS1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), status,Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
    /**
     * welcome speech
     * <p>
     *
     * @see Phrases
     */
    public void welcomeSpeech()
    {
        // TTS
        Phrases phraseToSearch1 = realm.where(Phrases.class)
                .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_first_part))
                .findFirst();
        sharedLastPlayer =
                sharedPref.getString (getString(R.string.preference_LastPlayer), "DEFAULT");
        Phrases phraseToSearch2 = realm.where(Phrases.class)
                .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_second_part))
                .findFirst();
        if ((phraseToSearch1 != null) && (phraseToSearch2 != null))  {
            toSpeak = phraseToSearch1.getDescrizione() + " " + sharedLastPlayer
                    + " " + phraseToSearch2.getDescrizione();
            tTS1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            tTS1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), status,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
    /**
     * check the results of speech.
     * with an allowed margin of error, if there is a correspondence
     * upload a video as a reward or charge the activity balloon , a simple balloon game, as a reward
     * <p>
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see GrammarHelper#thereIsACorrespondenceWithAnAllowedMarginOfError
     * @see #uploadAVideoAsAReward
     * @see #uploadAYoutubeVideoAsAReward
     * @see BalloonGameplayActivity
     * @see #fragmentTransactionStart
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkAnswer (String eText)
    {
        // convert uppercase letter to lowercase
        eText = eText.toLowerCase();
        if (GrammarHelper.thereIsACorrespondenceWithAnAllowedMarginOfError
                (eText, leftColumnContent + " " + middleColumnContent
                        + " " + rightColumnContent,preference_AllowedMarginOfError))
        {
            switch(rightColumnAwardType){
                case "V":
                    // upload a video as a reward
                    uploadAVideoAsAReward();
                    break;
                case "Y":
                    // upload a Youtube video as a reward
                    uploadAYoutubeVideoAsAReward();
                    break;
                default:
                    // charge the activity balloon as a reward
                    // a simple balloon game
                    Intent intent = new Intent(getApplicationContext(), BalloonGameplayActivity.class);
                    // ad uso futuro
                    intent.putExtra(EXTRA_MESSAGE_BALLOON, getString(R.string.pensieri_e_parole));
                    startActivity(intent);
                    break;
            }
        }
        else
        {
            fragmentTransactionStart();
        }
    }
    /**
     * used for printing load an image in a target bitmap from a file
     *
     * @param file file of origin
     * @param target target bitmap
     * @see Picasso
     */
    public void getTargetBitmapFromFileUsingPicasso(File file, Target target){
        Picasso.get()
                .load(file)
                .resize(200, 200)
                .into(target);
    }
    /**
     * used for printing load an image in a target bitmap from a url
     *
     * @param url string with url of origin
     * @param target target bitmap
     * @see Picasso
     */
    public void getTargetBitmapFromUrlUsingPicasso(String url, Target target){
        Picasso.get()
                .load(url)
                .resize(200, 200)
                .into(target);
    }
    /**
     * upload a video as a reward
     * <p>
     *
     * @see PrizeFragment
     */
    public void uploadAVideoAsAReward () {
        // upload a video as a reward
        PrizeFragment frag= new PrizeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.award_type), rightColumnAwardType);
        bundle.putString(getString(R.string.uri_premium_video), rightColumnUriPremiumVideo);
        frag.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        Game1SecondLevelFragment fragmentgotinstance =
                (Game1SecondLevelFragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.game1_second_level_fragment));
        PrizeFragment prizefragmentgotinstance =
                (PrizeFragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.prize_fragment));
        if ((fragmentgotinstance != null) || (prizefragmentgotinstance != null))
        {
            ft.replace(R.id.game_container_game1, frag, getString(R.string.prize_fragment));
        }
        else
        {
            ft.add(R.id.game_container_game1, frag, getString(R.string.prize_fragment));
        }
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * upload a Youtube video as a reward
     * <p>
     *
     * @see YoutubePrizeFragment
     */
    public void uploadAYoutubeVideoAsAReward () {
        // upload a Youtube video as a reward
        YoutubePrizeFragment yfrag= new YoutubePrizeFragment();
        Bundle ybundle = new Bundle();
        ybundle.putString(getString(R.string.award_type), rightColumnAwardType);
        ybundle.putString(getString(R.string.uri_premium_video), rightColumnUriPremiumVideo);
        yfrag.setArguments(ybundle);
        FragmentTransaction yft=getSupportFragmentManager().beginTransaction();
        Game1SecondLevelFragment yfragmentgotinstance =
                (Game1SecondLevelFragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.game1_second_level_fragment));
        PrizeFragment yprizefragmentgotinstance =
                (PrizeFragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.prize_fragment));
        if ((yfragmentgotinstance != null) || (yprizefragmentgotinstance != null))
        {
            yft.replace(R.id.game_container_game1, yfrag, getString(R.string.youtube_prize_fragment));
        }
        else
        {
            yft.add(R.id.game_container_game1, yfrag, getString(R.string.youtube_prize_fragment));
        }
        yft.addToBackStack(null);
        yft.commit();
    }
}