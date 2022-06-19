package com.example.NaiveAAC.activities.Settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Arasaac.PictogramsAllToModify;
import com.example.NaiveAAC.activities.Arasaac.PictogramsAllToModifyAdapter;
import com.example.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.example.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter;
import com.example.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.example.NaiveAAC.activities.Grammar.GrammaticalExceptions;
import com.example.NaiveAAC.activities.Grammar.GrammaticalExceptionsAdapter;
import com.example.NaiveAAC.activities.Grammar.ListsOfNames;
import com.example.NaiveAAC.activities.Grammar.ListsOfNamesAdapter;
import com.example.NaiveAAC.activities.Graphics.ImageSearchHelper;
import com.example.NaiveAAC.activities.Graphics.Images;
import com.example.NaiveAAC.activities.Graphics.ImagesAdapter;
import com.example.NaiveAAC.activities.Graphics.ResponseImageSearch;
import com.example.NaiveAAC.activities.Graphics.Videos;
import com.example.NaiveAAC.activities.Graphics.VideosAdapter;
import com.example.NaiveAAC.activities.Phrases.Phrases;
import com.example.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.example.NaiveAAC.activities.Stories.Stories;
import com.example.NaiveAAC.activities.Stories.StoriesAdapter;
import com.example.NaiveAAC.activities.WordPairs.WordPairs;
import com.example.NaiveAAC.activities.WordPairs.WordPairsAdapter;
import com.example.NaiveAAC.activities.history.History;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>SettingsWordPairsActivity</h1>
 * <p><b>SettingsWordPairsActivity</b> word pairs settings.</p>
 * Called when the user taps the word pairs button from the contents settings menu
 *
 * @version     1.0, 06/13/22
 * @see AccountActivityAbstractClass
 * @see SettingsFragmentAbstractClass
 * @see WordPairsAdapter
 */
public class SettingsWordPairsActivity extends AccountActivityAbstractClass
        implements
        WordPairsAdapter.WordPairsAdapterInterface,
        SettingsFragmentAbstractClass.onFragmentEventListenerSettings
{
    //
    public FragmentManager fragmentManager;
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see #setActivityResultLauncher
     * @see ActionbarFragment
     * @see WordPairsFragment
     * @see android.app.Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //
        context = this;
        //
        setActivityResultLauncher();
        //
        if (savedInstanceState == null)
            {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment))
                    .add(R.id.settings_container, new WordPairsFragment(), "WordPairsFragment")
                    .commit();
            }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm= Realm.getDefaultInstance();
    }

    /**
     * receives calls from fragment listeners.
     *
     * @param v view of calling fragment
     * @see SettingsFragmentAbstractClass
     */
    @Override
    public void receiveResultSettings(View v) {
        rootViewFragment = v;
    }
    /**
     * Called when the user taps the search video button from the word pairs settings.
     * </p>
     *
     * @param v view of tapped button
     * @see WordPairsFragment
     * @see #setActivityResultLauncher
     */
    public void videoSearchWordPairs(View v)
    {
        // video search
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("video/*");
        videoSearchWordPairsActivityResultLauncher.launch(intent);
    }
    /**
     * Called when the user taps the show list button from the word pairs settings.
     * </p>
     * the activity is notified to view the word pairs list.
     * </p>
     *
     * @param view view of tapped button
     * @see WordPairsFragment
     * @see WordPairsListFragment
     */
    public void wordsToMatchShowList(View view) {
        // view the word pairs list fragment initializing WordPairsListFragment (FragmentTransaction
        // switch between Fragments).
        WordPairsListFragment frag= new WordPairsListFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the save button from word pairs settings.
     * </p>
     * after the checks it adds the word pair on realm
     * </p>
     * and the activity is notified to view the word pairs settings.
     *
     * @param v view of tapped button
     * @see WordPairsFragment
     * @see WordPairs
     */
    public void wordsToMatchSave(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText textWord1=(EditText) findViewById(R.id.firstword);
        EditText textWord2=(EditText) findViewById(R.id.secondword);
        EditText textWord3=(EditText) findViewById(R.id.complement);
        EditText textWord4=(EditText) findViewById(R.id.ismenuitem);
        EditText textWord5=(EditText) findViewById(R.id.awardtype);
        TextView textWord6=(TextView) findViewById(R.id.uripremiumvideo);
        EditText textWord7=(EditText) findViewById(R.id.linkyoutube);
        if ((textWord1 != null) && (textWord2 != null) && (textWord3 != null) && (textWord4 != null)
                && (textWord5 != null) && (textWord6 != null) && (textWord7 != null))
        {
            // check if the images of word1 and word 2 exist
            ResponseImageSearch image1 = null;
            ResponseImageSearch image2 = null;
            image1 = ImageSearchHelper.imageSearch(realm, textWord1.getText().toString());
            image2 = ImageSearchHelper.imageSearch(realm, textWord2.getText().toString());
            if (image1 !=null && image2 !=null) {
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction();
                WordPairs wordPairs = realm.createObject(WordPairs.class);
                // set the fields here
                wordPairs.setWord1(textWord1.getText().toString());
                wordPairs.setWord2(textWord2.getText().toString());
                wordPairs.setComplement(textWord3.getText().toString());
                wordPairs.setIsMenuItem(textWord4.getText().toString());
                wordPairs.setAwardType(textWord5.getText().toString());
                // if textword5 = V and textword6 = no video -> uripremiumvideo = prize otherwise see below
                if (textWord5.getText().toString().equals(getString(R.string.character_v))
                        && textWord6.getText().toString().equals(getString(R.string.nessun_video)))
                    {
                        wordPairs.setUriPremiumVideo(getString(R.string.prize));
                    }
                    else
                    {
                        // if textword5 = Y -> uripremiumvideo = linkyoutube otherwise see below
                        if (textWord5.getText().toString().equals(getString(R.string.character_y)))
                        {
                            wordPairs.setUriPremiumVideo(textWord7.getText().toString());
                        }
                        else
                        {
                            wordPairs.setUriPremiumVideo(textWord6.getText().toString());
                        }
                    }
                realm.commitTransaction();
                // if textword5 = V and textword6 different from no video record video on realm
                if (textWord5.getText().toString().equals("V")
                        && !(textWord6.getText().toString().equals(getString(R.string.nessun_video))))
                    {
                        // Note that the realm object was generated with the createObject method
                        // and not with the new operator.
                        // The modification operations will be performed within a Transaction.
                        realm.beginTransaction();
                        Videos videos = realm.createObject(Videos.class);
                        videos.setDescrizione(textWord6.getText().toString());
                        videos.setUri(filePath);
                        realm.commitTransaction();
                    }
                //
            }
        }
        // view the word pairs settings fragment initializing WordPairsFragment (FragmentTransaction
        // switch between Fragments).
        WordPairsFragment frag= new WordPairsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();

    }
    /**
     * on callback from WordPairsAdapter to this Activity
     * </p>
     * after deleting a word pairs the activity is notified to view the word pairs settings
     * </p>
     *
     * @see WordPairsAdapter
     * @see WordPairsFragment
     */
    @Override
    public void reloadWordPairsFragment() {
        // view the word pairs settings fragment initializing WordPairsFragment (FragmentTransaction
        // switch between Fragments).
        FragmentTransaction ft;
        WordPairsFragment wordPairsFragment = new WordPairsFragment();
        ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, wordPairsFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
//
}
