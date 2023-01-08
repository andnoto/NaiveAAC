package com.sampietro.NaiveAAC.activities.Game.GameADA;

import static com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchNegationAdverb;
import static com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Game.Utils.OnFragmentEventListenerGame;
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper;
import com.sampietro.NaiveAAC.activities.Stories.Stories;

import java.io.File;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>GameADAViewPagerFragment</h1>
 * <p><b>GameADAViewPagerFragment</b> UI for GameADAViewPager
 *
 * </p>
 *
 * @version     1.3, 05/05/22
 * @see GameFragmentAbstractClass
 * @see GameADAViewPagerActivity
 */
public class GameADAViewPagerFragment extends GameFragmentAbstractClass {
    public String sharedStory;
    public RealmResults<Stories> resultsStories;
    public Stories wordToDisplay;
    public int phraseToDisplayIndex;
    public int wordToDisplayIndex;
    //
    public Activity activity;
    /**
     * listener setting for game activities callbacks , context annotation, realm get default instance
     * and get print permissions
     *
     * @see Fragment#onAttach
     */
    @Override
    public void onAttach (Context context)
    {
        super.onAttach(context);
        activity = (Activity) context;
        listener=(OnFragmentEventListenerGame) activity;
        //
        ctext = context;
        // REALM
        realm= Realm.getDefaultInstance();
        // if is print permitted then preference_PrintPermissions = Y
        sharedPref = ctext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        preference_PrintPermissions =
                sharedPref.getString ("preference_PrintPermissions", "DEFAULT");
        preference_TitleWritingType =
                sharedPref.getString ("preference_title_writing_type", "uppercase");
    }
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
     * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
     *
     * @see Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_ada_viewpager_content, container, false);
        //
        listener.receiveResultGameFragment(rootView);
        return rootView;
    }
    //
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
     * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
     * Refer to <a href="https://stackoverflow.com/questions/70486297/how-to-recreate-or-refresh-fragment-while-swiping-viewpager2-tabs">stackoverflow.com</a>
     * By <a href="https://stackoverflow.com/users/11950155/nimaazhd">NimaAzhd</a>
     * @see Fragment#onResume
     * @see #addImage
     */
    @Override
    public void onResume() {
        super.onResume();
        //
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sharedStory = bundle.getString("STORY TO DISPLAY");
            phraseToDisplayIndex = bundle.getInt("PHRASE TO DISPLAY INDEX");
            wordToDisplayIndex = bundle.getInt("WORD TO DISPLAY INDEX");
            //
            resultsStories =
                    realm.where(Stories.class)
                            .beginGroup()
                            .equalTo("story", sharedStory)
                            .equalTo("phraseNumberInt", phraseToDisplayIndex)
                            .notEqualTo("wordNumberInt", 0)
                            .lessThan("wordNumberInt", 99)
                            .endGroup()
                            .findAll();
            //
            //
            resultsStories = resultsStories.sort("wordNumberInt");
            //
            wordToDisplay = resultsStories.get(wordToDisplayIndex);
            //
            TextView textTitleImage = (TextView) rootView.findViewById(R.id.titleimage);
            textTitleImage.setText(wordToDisplay.getWord().toUpperCase(Locale.getDefault()));
            Log.e("TAGWORDTODISPLAY", wordToDisplay.getWord());
            // ricerca immagine
            ImageView imageGameImage = rootView.findViewById(R.id.gameimage);
            imageGameImage.setContentDescription(wordToDisplay.getWord().toUpperCase(Locale.getDefault()));
            addImage(wordToDisplay.getUriType(), wordToDisplay.getUri(), imageGameImage, 200, 200);
            // search for negation adverbs
            ImageView imageGameImage2 = rootView.findViewById(R.id.gameimage2);
            imageGameImage2.setVisibility(View.INVISIBLE);
            String negationAdverbImageToSearchFor = searchNegationAdverb(wordToDisplay.getWord().toLowerCase(), realm);
            if (!(negationAdverbImageToSearchFor.equals("non trovato"))) {
                // INTERNAL MEMORY IMAGE SEARCH
                String uriToSearch = searchUri(realm, negationAdverbImageToSearchFor);
                // imageGameImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                addImage("S", uriToSearch, imageGameImage2,200, 200);
                imageGameImage2.setVisibility(View.VISIBLE);
            }
            // TTS
            tTS1 = new TextToSpeech(ctext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tTS1.speak(wordToDisplay.getWord(), TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                    } else {
                        Toast.makeText(ctext, status, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //
        listener.receiveResultGameFragment(rootView);
    }
    //
    /**
     * load an image in imageview from a url or from a file
     *
     * @param urlType if string equal to "A" the image is loaded from a url otherwise it is loaded from a file
     * @param url string with url or file path of origin
     * @param img target imageview
     * @param width int with the width of the target imageview
     * @param height int with the height of the target imageview
     * @see GraphicsHelper#addImageUsingPicasso
     */
    public void addImage(String urlType, String url, ImageView img, int width, int height){
        Configuration configuration = activity.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        int screenHeightDp = configuration.screenHeightDp;
        int orientation = activity.getResources().getConfiguration().orientation;
        int imageSize = 0;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            imageSize = screenWidthDp;
        } else {
            // code for landscape mode
            imageSize = screenHeightDp*80/100;
        }
        if (urlType.equals("A"))
        {
            GraphicsHelper.addImageUsingPicasso(url,img, imageSize, imageSize);
        }
        else
        {
            File f = new File(url);
            GraphicsHelper.addFileImageUsingPicasso(f, (ImageView) img, imageSize, imageSize);
        }
    }
}

