package com.sampietro.NaiveAAC.activities.Game.GameADA;

import static com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd;
import static com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchNegationAdverb;
import static com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchVerb;
import static com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;
import androidx.print.PrintHelper;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2ArrayList;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear;
import com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper;
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment;
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper;
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper;
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch;
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity;
import com.sampietro.NaiveAAC.activities.Stories.Stories;
import com.sampietro.NaiveAAC.activities.history.History;
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory;
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl;
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory;
import com.example.voicerecognitionlibrary.AndroidPermission;
import com.example.voicerecognitionlibrary.SpeechRecognizerManagement;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>GameADAActivity</h1>
 * <p><b>GameADAActivity</b> displays images (uploaded by the user or Arasaac pictograms) of the
 * phrases of a story
 * </p>
 *
 * @version     1.4, 19/05/22
 * @see GameActivityAbstractClass
 */
public class GameADAActivity extends GameActivityAbstractClass implements
        GameADARecyclerViewAdapterInterface,
        PrizeFragment.onFragmentEventListenerPrize, GameADAOnFragmentEventListener {
    public String sharedStory = null;
    public RealmResults<Stories> resultsStories;
    public int sharedPhraseSize;
    //
    public int phraseToDisplayIndex;
    public Stories phraseToDisplay;
    //
    public String preference_PrintPermissions;
    //
    public int wordToDisplayIndex = 0;
    public boolean ttsEnabled = true;
    //
    /**
     * used for printing
     */
    ArrayList<Game2ArrayList> galleryList;
    //
    public Bitmap bitmap1;
    public Bitmap bitmap2;
    public Bitmap mergedImages;
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
    /**
     * used for printing
     */
    public Target target2 = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bitmap2 = bitmap;
        }
        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
    /**
     * used for TTS
     */
    public TextToSpeech tTS1 = null;
    //
    private ViewGroup mContentView;
    /**
     * configurations of gameADA start screen.
     * <p>
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement#prepareSpeechRecognizer
//     * @see ActionbarFragment
     * @see android.app.Activity#onCreate(Bundle)
     * @see #fragmentTransactionStart
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_game);
        //
        mContentView = findViewById(R.id.activity_game_id);
        setToFullScreen();
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        //
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
        //
        mContentView.setOnClickListener(view -> setToFullScreen());
        //
        AndroidPermission.checkPermission(this);
        //
        SpeechRecognizerManagement.prepareSpeechRecognizer(this);
        //
        realm= Realm.getDefaultInstance();
        // SEARCH HISTORY TO BE DISPLAYED
        Intent intent = getIntent();
        if (intent.hasExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_PARAMETER)) {
            sharedStory =
                    intent.getStringExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_PARAMETER);
            phraseToDisplayIndex = 1;
        }
        // if intent is from GameADAViewPagerActivity
        if (intent.hasExtra("STORY TO DISPLAY")) {
            sharedStory =
                    intent.getStringExtra("STORY TO DISPLAY");
            phraseToDisplayIndex= intent.getIntExtra("PHRASE TO DISPLAY INDEX", 1);
            wordToDisplayIndex= intent.getIntExtra("WORD TO DISPLAY INDEX", 0);
            ttsEnabled = false;
        }
        //
        context = this;
        //
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //
        if (sharedStory == null)
            {
            sharedStory =
                    sharedPref.getString ("preference_Story", "default");
            }
        // resumes the story from the last sentence displayed
        if (intent.hasExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_PARAMETER)) {
            boolean hasPhraseToDisplayIndex = sharedPref.contains(sharedStory + "preference_PhraseToDisplayIndex");
            if (hasPhraseToDisplayIndex) {
                phraseToDisplayIndex =
                        sharedPref.getInt (sharedStory + "preference_PhraseToDisplayIndex", 1);
            }
        }
        //
        boolean hasLastPhraseNumber = sharedPref.contains("preference_LastPhraseNumber");
        if (hasLastPhraseNumber) {
            sharedLastPhraseNumber =
                    sharedPref.getInt ("preference_LastPhraseNumber", 1);
        }
        // if is print permitted then preference_PrintPermissions = Y
        preference_PrintPermissions =
                sharedPref.getString (context.getString(R.string.preference_print_permissions), "DEFAULT");
        //
        // SEARCH FIRST PHRASE TO DISPLAY
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            // it display the last phrase
            // else display the first phrase
             phraseToDisplayIndex = savedInstanceState.getInt("PHRASE TO DISPLAY INDEX" );
             sharedLastPhraseNumber =
                    savedInstanceState.getInt("LAST PHRASE NUMBER" );
             fragmentTransactionStart();
        }
        else
        {
            resultsStories =
                    realm.where(Stories.class)
                            .beginGroup()
                            .equalTo("story", sharedStory)
                            .equalTo("phraseNumberInt", phraseToDisplayIndex)
                            .endGroup()
                            .findAll();
            sharedPhraseSize = resultsStories.size();
            //
            resultsStories = resultsStories.sort("wordNumberInt");
            //
            if (sharedPhraseSize != 0) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedStory + "preference_PhraseToDisplayIndex", phraseToDisplayIndex);
                editor.apply();
                //
                toBeRecordedInHistory=gettoBeRecordedInHistory();
                // REALM SESSION REGISTRATION
                List<VoiceToBeRecordedInHistory> voicesToBeRecordedInHistory =
                        toBeRecordedInHistory.getVoicesToBeRecordedInHistory();
                //
                int debugUrlNumber = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory();
                //
                historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory);
                //
                fragmentTransactionStart();
            }
        }
    }
//
    /**
     * Hide the Navigation Bar
     *
     * @see android.app.Activity#onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }
    //
    /**
     * destroy SpeechRecognizer
     *
     * @see android.app.Activity#onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpeechRecognizerManagement.destroyRecognizer();
    }
    //
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     * <p>
     * it stores the last phrase number
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //
        savedInstanceState.putInt("PHRASE TO DISPLAY INDEX", phraseToDisplayIndex);
        savedInstanceState.putInt("WORD TO DISPLAY INDEX", wordToDisplayIndex);
        savedInstanceState.putInt("LAST PHRASE NUMBER", sharedLastPhraseNumber);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    /**
     * This method is responsible to transfer MainActivity into fullscreen mode.
     */
    private void setToFullScreen() {
        findViewById(R.id.activity_game_id).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    //
    /**
     * Called when the user taps the home button.
     * </p>
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    public void returnHome(View v)
    {
    /*
                navigate to home screen (ChoiseOfGameActivity)
    */
        Intent intent = new Intent(this, ChoiseOfGameActivity.class);
        startActivity(intent);
    }
    //
    /**
     * Called when the user taps the print button.
     * </p>
     *
     * @param v view of tapped button
     * @see #printPhraseCacheImages
     * @see #printPhraseCreateMergedImages
     */
    public void printPhrase(View v)
    {
        printPhraseCacheImages();
        int TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                printPhraseCreateMergedImages();
                //
                PrintHelper photoPrinter = new PrintHelper(context);
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), mergedImages);
            }
        }, TIME_OUT);
        //
    }
    //
    /**
     * Cache images for printing
     * </p>
     *
     * @see #getTargetBitmapFromUrlUsingPicasso
     * @see #getTargetBitmapFromFileUsingPicasso
     * @see GrammarHelper#searchNegationAdverb
     */
    public void printPhraseCacheImages()
    {
        int count = galleryList.size();
        if (count != 0) {
            mergedImages = null;
            int irrh=0;
            while(irrh < count) {
                if (galleryList.get(irrh).getUrlType().equals("A"))
                {
                    getTargetBitmapFromUrlUsingPicasso(galleryList.get(irrh).getUrl(), target1);
                }
                else
                {
                    File f = new File(galleryList.get(irrh).getUrl());
                    getTargetBitmapFromFileUsingPicasso(f, target1);
                }
                // search for negation adverbs
                String negationAdverbImageToSearchFor = searchNegationAdverb(galleryList.get(irrh).getImage_title().toLowerCase(), realm);
                if (!(negationAdverbImageToSearchFor.equals("non trovato"))) {
                    // INTERNAL MEMORY IMAGE SEARCH
                    String uriToSearch = searchUri(realm, negationAdverbImageToSearchFor);
                    File f = new File(uriToSearch);
                    getTargetBitmapFromFileUsingPicasso(f, target2);
                }
                irrh++;
            }
        }
        //
    }
    /**
     * it merge images for printing
     * </p>
     *
     * @see #getTargetBitmapFromUrlUsingPicasso
     * @see #getTargetBitmapFromFileUsingPicasso
     * @see GrammarHelper#searchNegationAdverb(String, Realm)
     * @see ImageSearchHelper#searchUri(Realm, String) searchUri
     * @see #createSingleImageSuperimposedFromMultipleImages
     * @see #createSingleImageFromImageAndTitlePlacingThemVertically
     * @see #createSingleImageFromMultipleImages
     */
    public void printPhraseCreateMergedImages()
    {
        int count = galleryList.size();
        if (count != 0) {
            mergedImages = null;
            //
            int imagesContainedInARow = 0;
            int nrows = 1;
            int ncolumns = 1;
            //
            int irrh=0;
            while(irrh < count) {
                if (galleryList.get(irrh).getUrlType().equals("A"))
                {
                    getTargetBitmapFromUrlUsingPicasso(galleryList.get(irrh).getUrl(), target1);
                }
                else
                {
                    File f = new File(galleryList.get(irrh).getUrl());
                    getTargetBitmapFromFileUsingPicasso(f, target1);
                }
                // search for negation adverbs
                String negationAdverbImageToSearchFor = searchNegationAdverb(galleryList.get(irrh).getImage_title().toLowerCase(), realm);
                if (!(negationAdverbImageToSearchFor.equals("non trovato"))) {
                    // INTERNAL MEMORY IMAGE SEARCH
                    String uriToSearch = searchUri(realm, negationAdverbImageToSearchFor);
                    File f = new File(uriToSearch);
                    getTargetBitmapFromFileUsingPicasso(f, target2);
                    // addImage("S", uriToSearch, viewHolder.img2);
                    bitmap1 = createSingleImageSuperimposedFromMultipleImages(bitmap1, bitmap2);
                }
                // adding title
                bitmap1 = createSingleImageFromImageAndTitlePlacingThemVertically(bitmap1, galleryList.get(irrh).getImage_title());
                //
                if (irrh == 0) {
                    if (count > 24)
                        {
                        imagesContainedInARow = 7;
                        mergedImages = Bitmap.createBitmap(bitmap1.getWidth() * 7, bitmap1.getHeight() * 5, bitmap1.getConfig());
                        }
                    else
                    if (count > 15)
                        {
                        imagesContainedInARow = 6;
                        mergedImages = Bitmap.createBitmap(bitmap1.getWidth() * 6, bitmap1.getHeight() * 4, bitmap1.getConfig());
                        }
                    else
                    if (count > 12)
                        {
                        imagesContainedInARow = 5;
                        mergedImages = Bitmap.createBitmap(bitmap1.getWidth() * 5, bitmap1.getHeight() * 3, bitmap1.getConfig());
                        }
                    else
                        {
                        imagesContainedInARow = 4;
                        mergedImages = Bitmap.createBitmap(bitmap1.getWidth() * 4, bitmap1.getHeight() * 3, bitmap1.getConfig());
                        }
                }
                else
                {
                    ncolumns++;
                    if (ncolumns > imagesContainedInARow) {
                        nrows++;
                        ncolumns = 1;
                    }
                }
                Bitmap firstImage = mergedImages;
                mergedImages = createSingleImageFromMultipleImages(firstImage, bitmap1, nrows, ncolumns);
                irrh++;
            }
        }
        //
    }
    //
    /**
     * used for printing create single bitmap image from multiple bitmap images placing them horizontally
     * Refer to <a href="https://stackoverflow.com/questions/34719987/merging-two-images-into-one-image#:~:text=Here%20is%20the%20code%3A%20Used%20to%20combine%20Images&text=getHeight()%2C%20firstImage.,(firstImage%2C%20SecondImage)%3B%20im.">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/2026359/caulitomaz">caulitomaz</a>
     *
     * @param firstImage bitmap of first image
     * @param secondImage bitmap of second image
     * @return bitmap single image created from multiple images
     */
    private Bitmap createSingleImageFromMultipleImagesPlacingThemHorizontally(Bitmap firstImage, Bitmap secondImage) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth() + secondImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, firstImage.getWidth(), 0f, null);
        return result;
    }
    //
    /**
     * used for printing create single bitmap image from multiple bitmap images
     *
     * @param firstImage bitmap of first image
     * @param secondImage bitmap of second image
     * @param nrows int number of rows on the page
     * @param ncolumns int number of columns on the page
     * @return bitmap single image created from multiple images
     */
    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage, int nrows, int ncolumns ) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth() , firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, secondImage.getWidth() * (ncolumns - 1), secondImage.getHeight() * (nrows - 1), null);
        return result;
    }
    //
    /**
     * used for printing create single bitmap image from image and title
     * Refer to <a href="https://stackoverflow.com/questions/2655402/android-canvas-drawtext">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/119396/gaz">gaz</a>
     *
     * @param firstImage bitmap of image
     * @param title string image title
     * @return bitmap single image created from image and title
     */
    private Bitmap createSingleImageFromImageAndTitlePlacingThemVertically(Bitmap firstImage, String title) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight()  + 30, firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        //
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(20);
        canvas.drawText(title, 0f, firstImage.getHeight() + 10, paint);
        //
        return result;
    }
    //
    /**
     * used for printing create single bitmap image superimposed from multiple bitmap images
     *
     * @param firstImage bitmap of first image
     * @param secondImage bitmap of second image
     * @return bitmap single image created from multiple images
     */
    private Bitmap createSingleImageSuperimposedFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth() , firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 0f, 0f, null);
        return result;
    }
    /**
     * Called when the user taps the settings button.
     * replace with hear fragment
     * </p>
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    public void returnSettings(View v)
    {
    /*
                navigate to settings screen (ChoiseOfGameActivity)
    */
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivity(intent);
    }
    /**
     * Called when the user taps the start speech button.
     * replace with hear fragment
     * </p>
     * Refer to <a href="https://stackoverflow.com/questions/43520688/findfragmentbyid-and-findfragmentbytag">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/4266957/ricardo">Ricardo</a>
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement#startSpeech()
     */
    public void startSpeechGameADA(View v)
    {
        SpeechRecognizerManagement.startSpeech();
        //
        GameFragmentHear frag= new GameFragmentHear();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        GameADAFragment fragmentgotinstance =
                (GameADAFragment)
                        getSupportFragmentManager().findFragmentByTag("GameFragmentGameADA");
        GameFragmentHear hearfragmentgotinstance =
                (GameFragmentHear)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.game_fragment_hear));
        if ((fragmentgotinstance != null) || (hearfragmentgotinstance != null))
        {
            ft.replace(R.id.game_container, frag, getString(R.string.game_fragment_hear));
            // ok, we got the fragment instance, but should we manipulate its view?
        }
        else
        {
            ft.add(R.id.game_container, frag, getString(R.string.game_fragment_hear));
        }
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the first page button.
     * go to the first sentence
     * @param v view of tapped button
     * @see #continueGameAda
     */
    public void firstPageGameAdaButton(View v) {
        if (!(tTS1 == null)) {
            if (!tTS1.isSpeaking()) {
                phraseToDisplayIndex = 1;
                wordToDisplayIndex= 0;
                continueGameAda();
            }
            else
            { Toast.makeText(context, "TTS sta parlando : riprovare più tardi", Toast.LENGTH_SHORT).show(); }
        }
        else
        {
            phraseToDisplayIndex = 1;
            wordToDisplayIndex= 0;
            continueGameAda();
        }
    }
    /**
     * Called when the user taps the last page button.
     * go to the last sentence
     * @param v view of tapped button
     * @see #continueGameAda
     */
    public void lastPageGameAdaButton(View v) {
        // VIEW THE LAST PHRASE
        resultsStories =
                realm.where(Stories.class)
                        .beginGroup()
                        .equalTo("story", sharedStory)
                        .endGroup()
                        .findAll();
        int resultsStoriesSize = resultsStories.size();
        //
        resultsStories = resultsStories.sort("phraseNumberInt");
        //
        Stories resultStories = resultsStories.get(resultsStoriesSize-1);
        assert resultStories != null;
        if (!(tTS1 == null)) {
            if (!tTS1.isSpeaking()) {
                phraseToDisplayIndex = resultStories.getPhraseNumber();
                wordToDisplayIndex= 0;
                continueGameAda();
            }
            else
            { Toast.makeText(context, "TTS sta parlando : riprovare più tardi", Toast.LENGTH_SHORT).show(); }
        }
        else
        {
            phraseToDisplayIndex = resultStories.getPhraseNumber();
            wordToDisplayIndex= 0;
            continueGameAda();
        }
    }
    /**
     * Called when the user taps the return button.
     * go to the previous sentence
     * @param v view of tapped button
     * @see #continueGameAda
     */
    public void returnGameAdaButton(View v) {
        if (!(tTS1 == null)) {
            if (!tTS1.isSpeaking()) {
                if (phraseToDisplayIndex > 1)
                {
                    phraseToDisplayIndex--;
                    wordToDisplayIndex= 0;
                }
                continueGameAda();
            }
            else
            { Toast.makeText(context, "TTS sta parlando : riprovare più tardi", Toast.LENGTH_SHORT).show(); }
        }
        else
        {
            if (phraseToDisplayIndex > 1)
            {
                phraseToDisplayIndex--;
                wordToDisplayIndex= 0;
            }
            continueGameAda();
        }
    }
    //
    /**
     * Called when the user taps the continue button.
     * go to the next sentence
     * @param v view of tapped button
     * @see #continueGameAda
     */
    public void continueGameAdaButton(View v) {
        if (!(tTS1 == null)) {
            if (!tTS1.isSpeaking()) {
                phraseToDisplayIndex++;
                wordToDisplayIndex= 0;
                continueGameAda();
            }
            else
            { Toast.makeText(context, "TTS sta parlando : riprovare più tardi", Toast.LENGTH_SHORT).show(); }
        }
        else
        {
            phraseToDisplayIndex++;
            wordToDisplayIndex= 0;
            continueGameAda();
        }
    }
    //
    /**
     * View the next phrase.
     *
     * @see ToBeRecordedInHistory
     * @see VoiceToBeRecordedInHistory
     * @see HistoryRegistrationHelper#historyAdd
     * @see #gettoBeRecordedInHistory
     * @see #fragmentTransactionStart
     */
    public void continueGameAda() {
        // VIEW THE NEXT PHRASE
        resultsStories =
                realm.where(Stories.class)
                        .beginGroup()
                        .equalTo("story", sharedStory)
                        .equalTo("phraseNumberInt",
                                phraseToDisplayIndex)
                        .endGroup()
                        .findAll();
        sharedPhraseSize = resultsStories.size();
        //
        resultsStories = resultsStories.sort("wordNumberInt");
        // if there are no subsequent sentences, I start again from the first sentence
        if (sharedPhraseSize == 0) {
            phraseToDisplayIndex = 1;
            resultsStories =
                    realm.where(Stories.class)
                            .beginGroup()
                            .equalTo("story", sharedStory)
                            .equalTo("phraseNumberInt",
                                    phraseToDisplayIndex)
                            .endGroup()
                            .findAll();
            sharedPhraseSize = resultsStories.size();
            //
            resultsStories = resultsStories.sort("wordNumberInt");
        }
        //
        if (sharedPhraseSize != 0) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(sharedStory + "preference_PhraseToDisplayIndex", phraseToDisplayIndex);
            editor.apply();
            //
            toBeRecordedInHistory=gettoBeRecordedInHistory();
            // REALM SESSION REGISTRATION
            List<VoiceToBeRecordedInHistory> voicesToBeRecordedInHistory =
                    toBeRecordedInHistory.getVoicesToBeRecordedInHistory();
            //
            int debugUrlNumber = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory();
            //
            historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory);
            //
            fragmentTransactionStart();
        }
    }
    //
    /**
     * initiate Fragment transaction.
     * <p>
     *
     * @see GameADAFragment
     */
    public void fragmentTransactionStart()
    {
        GameADAFragment frag= new GameADAFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("LAST PHRASE NUMBER", sharedLastPhraseNumber);
        bundle.putInt("WORD TO DISPLAY INDEX", wordToDisplayIndex);
        bundle.putBoolean("TTS ENABLED", ttsEnabled);
        ttsEnabled = true;
        frag.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        GameADAFragment fragmentgotinstance =
                (GameADAFragment)
                        getSupportFragmentManager().findFragmentByTag("GameFragmentGameADA");
        PrizeFragment prizefragmentgotinstance =
                (PrizeFragment)
                        getSupportFragmentManager().findFragmentByTag("PrizeFragment");
        GameFragmentHear hearfragmentgotinstance =
                (GameFragmentHear)
                        getSupportFragmentManager().findFragmentByTag("GameFragmentGame1Hear");
        if ((fragmentgotinstance != null) || (prizefragmentgotinstance != null) || (hearfragmentgotinstance != null))
        {
            ft.replace(R.id.game_container, frag, "GameFragmentGameADA");
        }
        else
        {
            ft.add(R.id.game_container, frag, "GameFragmentGameADA");
        }
        ft.addToBackStack(null);
        ft.commit();
    }
    //
    @Override
    public void onItemClick(View view, int i, ArrayList<Game2ArrayList> galleryList) {
        if (preference_PrintPermissions.equals("Y")) {
            if (galleryList.get(i).getUrlType().equals("A"))
            {
                getTargetBitmapFromUrlUsingPicasso(galleryList.get(i).getUrl(), target1);
            }
            else
            {
                File f = new File(galleryList.get(i).getUrl());
                getTargetBitmapFromFileUsingPicasso(f, target1);
            }
            //
            PrintHelper photoPrinter = new PrintHelper(context);
            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), bitmap1);
        }
        else
        {
            Intent intent = null;
            intent = new Intent(this,
                    GameADAViewPagerActivity.class);
            intent.putExtra("STORY TO DISPLAY", sharedStory);
            intent.putExtra("PHRASE TO DISPLAY INDEX", phraseToDisplayIndex);
            intent.putExtra("WORD TO DISPLAY INDEX", i);
            startActivity(intent);
            //
        }

    }
    //
    /**
     * Called on result of speech.
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     * @see #checkAnswer
     */
    @Override
    public void onResult(String eText) {
        checkAnswer(eText);
    }
    //
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
    //
    /**
     * Called on beginning of speech.
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     */
    @Override
    public void onBeginningOfSpeech(String eText) {
    }
    //
    /**
     * Called on end of speech.
     * overrides the method on GameActivityAbstractClass
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see GameActivityAbstractClass
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     */
    @Override
    public void onEndOfSpeech(String editText) {
    }
    //
    /**
     * on callback from PrizeFragment to this Activity
     * </p>
     *
     * @param v view inflated inside PrizeFragment
     * @see PrizeFragment
     */
    @Override
    public void receiveResultImagesFromPrizeFragment(View v) {
        rootViewPrizeFragment = v;
    }
    /**
     * callback from PrizeFragment to this Activity on completation video
     * </p>
     * go to the next sentence
     *
     * @param v view inflated inside PrizeFragment
     * @see #continueGameAdaButton
     * @see PrizeFragment
     */
    @Override
    public void receiveResultOnCompletatioVideoFromPrizeFragment(View v) {
        continueGameAdaButton(v);
    }
    //
    /**
     * Called on result of speech.
     * check the speech results
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     * @see GrammarHelper#splitString
     * @see GrammarHelper#searchVerb
     * @see #extemporaneousInteractionWithVoice
     * @see #continueGameAda
     */
    public void checkAnswer (String eText)
    {
        // convert uppercase letter to lowercase
        eText = eText.toLowerCase();
        // break down EditText
        String[] arrWords = GrammarHelper.splitString(eText);
        int arrWordsLength=arrWords.length;
        //
        context = this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean hasLastPhraseNumber = sharedPref.contains("preference_LastPhraseNumber");
        if (hasLastPhraseNumber) {
            sharedLastPhraseNumber =
                    sharedPref.getInt ("preference_LastPhraseNumber", 1);
        }
        // if wordNumber = 999 the word contain the answer related to the phrase
        String correspondingWord = "non trovata";
        RealmResults<History> results =
                realm.where(History.class)
                        .beginGroup()
                        .equalTo("phraseNumber", String.valueOf(sharedLastPhraseNumber))
                        .and()
                        .equalTo("wordNumber", String.valueOf(999))
                        .endGroup()
                        .findAll();
        // hystory also contains the article that I have to exclude from the comparison
        int count = results.size();
        if (count != 0) {
            History result = results.get(0);
            if (result != null) {
                String resultHistoryCorrespondingWord = result.getWord();
                String[] arrResultHistoryCorrespondingWords =
                        GrammarHelper.splitString(resultHistoryCorrespondingWord);
                int arrCorrespondingWordsLength=arrResultHistoryCorrespondingWords.length;
                if (arrCorrespondingWordsLength == 2)
                {
                    correspondingWord = arrResultHistoryCorrespondingWords[1];
                }
                else
                {
                    correspondingWord = arrResultHistoryCorrespondingWords[0];
                }
            }
        }
        // I look for the presence of the corresponding word in eText
        boolean theWordMatches = false;
        //
        int i=0;
        while(i < arrWordsLength) {
            if (arrWords[i].equals(correspondingWord)) {
                theWordMatches = true;
                break;
            }
            // check if the word in eText is a conjugation of the corresponding word
            String verbToSearch = searchVerb(arrWords[i], realm);
            if (verbToSearch.equals(correspondingWord)) {
                theWordMatches = true;
                break;
            }
            //
            i++;
        }
        // if the corresponding word is present in eText, I give the prize,
        // otherwise I answer "no, try again"
        if (!theWordMatches)
        {
            // treatment of impromptu responses
            //(go to the next sentence, go back to the previous sentence, go to topic)
            extemporaneousInteractionWithVoice(eText);
            continueGameAda();
        }
        else
        {
            // upload the award video
            PrizeFragment frag= new PrizeFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            GameADAFragment fragmentgotinstance =
                    (GameADAFragment)
                            getSupportFragmentManager().findFragmentByTag("GameFragmentGameADA");
            if(fragmentgotinstance != null)
            {
                ft.replace(R.id.game_container, frag, "PrizeFragment");
            }
            else
            {
                ft.add(R.id.game_container, frag, "PrizeFragment");
            }
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    //
    /**
     * Called on result of speech.
     * treatment of impromptu responses
     * (go to the next sentence, go back to the previous sentence, go to topic)
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     * @see GrammarHelper#splitString
     */
    public void extemporaneousInteractionWithVoice (String eText)
    {
        // break down EditText
        String[] arrWords = GrammarHelper.splitString(eText);
        int arrWordsLength=arrWords.length;
        // search for the presence in eText of the words forward (to the next sentence),
        // backwards (to the previous sentence), (go to) topic
        int i=0;
        while(i < arrWordsLength) {
            if (arrWords[i].equals("avanti")) {
                // go to the next sentence only if this sentence does not contain a question
                RealmResults<Stories> results =
                        realm.where(Stories.class)
                                .beginGroup()
                                .equalTo("story", sharedStory)
                                .equalTo("phraseNumberInt",
                                        phraseToDisplayIndex)
                                .equalTo("wordNumberInt",
                                        999)
                                .endGroup()
                                .findAll();
                int count = results.size();
                if (count == 0) {
                    phraseToDisplayIndex++;
                }
                break;
            }
            if (arrWords[i].equals("indietro")) {
                if (phraseToDisplayIndex > 1)
                {
                    phraseToDisplayIndex--;
                }
                break;
            }
            //
            i++;
        }
        // check if a story topic is in eText
        // (if wordNumber = 9999 the word contain the topic of the sentence)
        RealmResults<Stories> results =
                realm.where(Stories.class)
                        .beginGroup()
                        .equalTo("story", sharedStory)
                        .equalTo("wordNumberInt", 9999)
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            i=0;
            while(i < count) {
                Stories result = results.get(i);
                assert result != null;
                // if the argument is in eText then that is the sentence to display
                if (eText.toLowerCase().contains(result.getWord().toLowerCase())) {
                    phraseToDisplayIndex = result.getPhraseNumber();
                    break;
                }
                //
                i++;
            }
        }
    }
    /**
     * prepares the list of items to be registered on History
     *
     * @return ToBeRecordedInHistory<VoiceToBeRecordedInHistory> list of items to be registered on History
     * @see VoiceToBeRecordedInHistory
     * @see ToBeRecordedInHistory
     * @see ImageSearchHelper#imageSearch(Realm, String)
     */
    public ToBeRecordedInHistory<VoiceToBeRecordedInHistory> gettoBeRecordedInHistory()
    {
        // initializes the list of items to be registered on History
        toBeRecordedInHistory= new ToBeRecordedInHistoryImpl();
        // adds the first entry to be recorded on History to the list
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedLastSession =
                sharedPref.getInt ("preference_LastSession", 1);
        //
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean hasLastPhraseNumber = sharedPref.contains("preference_LastPhraseNumber");
        if (!hasLastPhraseNumber) {
            // is the first recorded sentence and LastPhraseNumber log on sharedpref
            // with the number 1
            sharedLastPhraseNumber = 1;
        }
        else
        {
            // it is not the first sentence recorded and I add 1 to LastPhraseNumber on sharedprefs
            sharedLastPhraseNumber =
                    sharedPref.getInt ("preference_LastPhraseNumber", 1)+1;
        }
        editor.putInt("preference_LastPhraseNumber", sharedLastPhraseNumber);
        editor.apply();
        //
        currentTime = Calendar.getInstance().getTime();
        // SEARCH THE IMAGES OF THE WORDS
        int i=0;
        while(i < sharedPhraseSize) {
            phraseToDisplay = resultsStories.get(i);
            assert phraseToDisplay != null;
            int phraseToDisplayWordNumber = phraseToDisplay.getWordNumber();
            //
            switch(phraseToDisplayWordNumber) {
                case 0:
                    voiceToBeRecordedInHistory =
                            new VoiceToBeRecordedInHistory(sharedLastSession,
                                    sharedLastPhraseNumber, currentTime,
                                    0, " ", phraseToDisplay.getWord(),
                                    " "," ", " ");
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
                    break;
                case 99:
                    voiceToBeRecordedInHistory =
                            new VoiceToBeRecordedInHistory(sharedLastSession,
                                    sharedLastPhraseNumber, currentTime,
                                    99, " ", phraseToDisplay.getWord(),
                                    " "," ", " ");
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
                    break;
                case 999:
                    voiceToBeRecordedInHistory =
                            new VoiceToBeRecordedInHistory(sharedLastSession,
                                    sharedLastPhraseNumber, currentTime,
                                    999, " ", phraseToDisplay.getWord(),
                                    " "," ", " ");
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
                    break;
                case 9999:
                    voiceToBeRecordedInHistory =
                            new VoiceToBeRecordedInHistory(sharedLastSession,
                                    sharedLastPhraseNumber, currentTime,
                                    9999, " ", phraseToDisplay.getWord(),
                                    " "," ", " ");
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
                    break;
                default:
                    // image search
                    ResponseImageSearch image = null;
                    String phraseToDisplayWord = phraseToDisplay.getWord();
                    String phraseToDisplayUriType = phraseToDisplay.getUriType();
                    if (phraseToDisplayUriType != null)
                    {
                        if (phraseToDisplayUriType.equals(" ") || phraseToDisplayUriType.equals(""))
                        {
                            image = ImageSearchHelper.imageSearch(realm, phraseToDisplayWord);
                            if (image!=null) {

                                voiceToBeRecordedInHistory =
                                        new VoiceToBeRecordedInHistory(sharedLastSession,
                                                sharedLastPhraseNumber, currentTime,
                                                1, " ", phraseToDisplayWord,
                                                " ", image.getUriType(), image.getUriToSearch());
                                toBeRecordedInHistory.add(voiceToBeRecordedInHistory);

                            }
                        }
                        else
                        {
                            voiceToBeRecordedInHistory =
                                    new VoiceToBeRecordedInHistory(sharedLastSession,
                                            sharedLastPhraseNumber, currentTime,
                                            1, " ", phraseToDisplayWord,
                                            " ", phraseToDisplayUriType,
                                            phraseToDisplay.getUri());
                            toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
                        }
                    }
                    //
                    break;
            }
            //
            i++;
        }
        return toBeRecordedInHistory;
    }
    /**
     * on callback from GameFragment to this Activity
     *
     * @param v view root fragment view
     * @param t TextToSpeech
     * @param gL ArrayList<Game2ArrayList>
     */
    @Override
    public void receiveResultGameFragment(View v, TextToSpeech t, ArrayList<Game2ArrayList> gL) {
        rootViewImageFragment = v;
        tTS1 = t;
        galleryList = gL;
    }


//
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
}