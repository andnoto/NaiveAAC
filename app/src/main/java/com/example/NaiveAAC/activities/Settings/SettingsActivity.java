package com.example.NaiveAAC.activities.Settings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
// import android.util.Log;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>SettingsActivity</h1>
 * <p><b>SettingsActivity</b> app settings.</p>
 *
 * @version     1.0, 06/13/22
 * @see com.example.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass
 * @see com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.example.NaiveAAC.activities.Settings.VerifyFragment
 * @see com.example.NaiveAAC.activities.Settings.ChoiseOfGameToSetFragment
 * @see com.example.NaiveAAC.activities.Graphics.ImagesAdapter
 * @see com.example.NaiveAAC.activities.Graphics.VideosAdapter
 * @see com.example.NaiveAAC.activities.WordPairs.WordPairsAdapter
 * @see com.example.NaiveAAC.activities.Grammar.ListsOfNamesAdapter
 * @see com.example.NaiveAAC.activities.Grammar.GrammaticalExceptionsAdapter
 * @see com.example.NaiveAAC.activities.Stories.StoriesAdapter
 * @see com.example.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
 */
public class SettingsActivity extends AccountActivityAbstractClass
        implements
        VerifyFragment.onFragmentEventListenerVerify,
        ImagesAdapter.ImagesAdapterInterface,
        ChoiseOfGameToSetFragment.onFragmentEventListenerChoiseOfGameToSet,
        VideosAdapter.VideosAdapterInterface,
        ListsOfNamesAdapter.ListsOfNamesAdapterInterface,
        SettingsFragmentAbstractClass.onFragmentEventListenerSettings,
        StoriesAdapter.StoriesAdapterInterface,
        GameParametersAdapter.GameParametersAdapterInterface,
        GrammaticalExceptionsAdapter.GrammaticalExceptionsAdapterInterface,
        PictogramsAllToModifyAdapter.PictogramsAllToModifyAdapterInterface
{
    public String message = "messaggio non formato";
    public TextView textView;
    //
    public View rootViewVerifyFragment;
    public int resultToVerify;
    //
    public View rootViewChoiseOfGameToSetFragment;
    public String textGameToSet;
    //
    private static final String TAGPERMISSION = "Permission";
    //
    public FragmentManager fragmentManager;
    //
    public boolean checkboxImagesChecked;
    public boolean checkboxVideosChecked;
    public boolean checkboxPhrasesChecked;
    public boolean checkboxWordPairsChecked;
    public boolean checkboxListsOfNamesChecked;
    public boolean checkboxStoriesChecked;
    public boolean checkboxGrammaticalExceptionsChecked;
    public boolean checkboxGameParametersChecked;
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see #setActivityResultLauncher
     * @see ActionbarFragment
     * @see VerifyFragment
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
        setCsvSearchActivityResultLauncher();
        //
        setExportCsvSearchActivityResultLauncher();
        //
        if (savedInstanceState == null)
            {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment))
                    .add(R.id.settings_container, new VerifyFragment(), "VerifyFragment")
                    .commit();
            }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm= Realm.getDefaultInstance();
    }
    //
    /**
     * receive result to verify for check the right to access the settings.
     *
     * @param v view of calling fragment
     * @param r int with result to verify
     * @see VerifyFragment
     */
    @Override
    public void receiveResultToVerify(View v, int r)
    {
        rootViewVerifyFragment = v;
        resultToVerify = r;
    }
    /**
     * Called when the user taps the submit verification button.
     * </p>
     * check and if the answer is correct the activity is notified to view the fragment settings.
     * </p>
     *
     * @param view view of tapped button
     * @see VerifyFragment
     * @see #receiveResultToVerify
     * @see MenuSettingsFragment
     */
    public void submitVerification(View view) {
        EditText editText = (EditText) rootViewVerifyFragment.findViewById(R.id.calculationToBeVerified);
        String value= editText.getText().toString();
        int calculationToBeVerified=Integer.parseInt(value);
        if (calculationToBeVerified == resultToVerify)
        {
            // view the fragment settings initializing MenuSettingsFragment (FragmentTransaction
            // switch between Fragments).
            MenuSettingsFragment frag= new MenuSettingsFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
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
     * receives calls from ChoiseOfGameToSet fragment listener.
     *
     * @param v view of calling fragment
     * @see ChoiseOfGameToSetFragment
     */
    @Override
    public void receiveResultChoiseOfGameToSet(View v) { rootViewChoiseOfGameToSetFragment = v;    }
    /**
     * receive game to set.
     *
     * @param g string containing the game to be set
     * @see ChoiseOfGameToSetFragment
     */
    @Override
    public void receiveResultGameToSet(String g)
            { textGameToSet = g;   }
    /**
     * Called when the user taps the account button from the settings menu.
     * </p>
     * the activity is notified to view the account settings.
     * </p>
     *
     * @param view view of tapped button
     * @see MenuSettingsFragment
     * @see AccountFragment
     */
    public void submitAccount(View view) {
        // view the fragment account initializing AccountFragment (FragmentTransaction
        // switch between Fragments).
        AccountFragment frag= new AccountFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the save account button from account settings.
     * </p>
     * register the user in the shared preferences and in realm
     * </p>
     * the activity is notified to view the fragment settings.
     *
     * @param view view of tapped button
     * @see AccountFragment
     * @see #registerPersonName(String)
     * @see Images
     * @see MenuSettingsFragment
     */
    public void saveAccount(View view) {
        EditText editText = (EditText) rootViewFragment.findViewById(R.id.editTextTextAccount);
        String textPersonName = editText.getText().toString();
        //
        if (textPersonName.length()>0 && !filePath.equals(getString(R.string.non_trovato)))
        {
            // register the user in the shared preferences
            registerPersonName(textPersonName);
            // delete any old user images from realm
            realm.beginTransaction();
            RealmResults<Images> results =
                    realm.where(Images.class).equalTo("descrizione", textPersonName).findAll();
            results.deleteAllFromRealm();
            realm.commitTransaction();
            //
            realm.beginTransaction();
            RealmResults<Images> resultsIo =
                    realm.where(Images.class).equalTo("descrizione", getString(R.string.io)).findAll();
            resultsIo.deleteAllFromRealm();
            realm.commitTransaction();
            // register the user and the linked image in realm
            realm.beginTransaction();
            Images i = realm.createObject(Images.class);
            i.setDescrizione(textPersonName);
            i.setUri(filePath);
            realm.commitTransaction();
            //
            realm.beginTransaction();
            Images iIo = realm.createObject(Images.class);
            iIo.setDescrizione(getString(R.string.io));
            iIo.setUri(filePath);
            realm.commitTransaction();
            // register the linked word pairs
            realm.beginTransaction();
            WordPairs wordPairs = realm.createObject(WordPairs.class);
            wordPairs.setWord1(getString(R.string.famiglia));
            wordPairs.setWord2(textPersonName);
            wordPairs.setComplement("");
            wordPairs.setIsMenuItem(getString(R.string.slm));
            wordPairs.setAwardType("");
            wordPairs.setUriPremiumVideo("");
            realm.commitTransaction();
        }
        // view the fragment settings initializing MenuSettingsFragment (FragmentTransaction
        // switch between Fragments).
        MenuSettingsFragment frag= new MenuSettingsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the media settings button from the settings menu.
     * </p>
     * the activity is notified to view the media settings.
     * </p>
     *
     * @param view view of tapped button
     * @see MenuSettingsFragment
     * @see ChoiseOfMediaToSetFragment
     */
    public void submitChoiseOfMediaToSet(View view) {
        // view the media settings fragment initializing ChoiseOfMediaToSetFragment (FragmentTransaction
        // switch between Fragments).
        ChoiseOfMediaToSetFragment frag= new ChoiseOfMediaToSetFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the video button from the media settings menu.
     * </p>
     * the activity is notified to view the videos settings.
     * </p>
     *
     * @param view view of tapped button
     * @see ChoiseOfMediaToSetFragment
     * @see VideosFragment
     */
    public void submitVideo(View view) {
        // view the videos settings fragment initializing VideosFragment (FragmentTransaction
        // switch between Fragments).
        VideosFragment frag= new VideosFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.uri), getString(R.string.none));
        frag.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the search video button from the video settings menu.
     * </p>
     *
     * @param v view of tapped button
     * @see VideosFragment
     * @see #setActivityResultLauncher
     */
    public void videoSearch(View v)
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
        videoSearchActivityResultLauncher.launch(intent);
    }
    /**
     * Called when the user taps the add video button from the video settings.
     * </p>
     * after the checks it adds the references of the video on realm
     * </p>
     * and the activity is notified to view the updated video settings.
     *
     * @param v view of tapped button
     * @see VideosFragment
     * @see Videos
     */
    public void videoAdd(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText vidD=(EditText) findViewById(R.id.videoDescription);
        if (vidD.length()>0 && stringUri != null)
        {
            uri = Uri.parse(stringUri);
            try {
                filePath = getFilePath(this, uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            Videos videos = realm.createObject(Videos.class);
            videos.setDescrizione(vidD.getText().toString());
            videos.setUri(filePath);
            realm.commitTransaction();
            // view the videos settings fragment initializing VideosFragment (FragmentTransaction
            // switch between Fragments).
            VideosFragment frag= new VideosFragment();
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.uri), getString(R.string.none));
            frag.setArguments(bundle);
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * on callback from VideosAdapter to this Activity
     * </p>
     * after deleting references to a video the activity is notified to view the updated video settings
     * </p>
     *
     * @see VideosAdapter
     * @see VideosFragment
     */
    @Override
    public void reloadVideosFragment() {
        // view the videos settings fragment initializing VideosFragment (FragmentTransaction
        // switch between Fragments).
        VideosFragment frag= new VideosFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.uri), getString(R.string.none));
        frag.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the images button from the media settings menu.
     * </p>
     * the activity is notified to view the images settings.
     * </p>
     *
     * @param view view of tapped button
     * @see ChoiseOfMediaToSetFragment
     * @see ImagesFragment
     */
    public void submitImages(View view) {
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        ImagesFragment frag= new ImagesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the add image button from the image settings.
     * </p>
     * after the checks it adds the references of the image on realm
     * </p>
     * and the activity is notified to view the updated image settings.
     *
     * @param v view of tapped button
     * @see ImagesFragment
     * @see Images
     */
    public void imageAdd(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText immD=(EditText) findViewById(R.id.imageDescription);
        if (immD.length()>0 && !filePath.equals(getString(R.string.non_trovato)))
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            Images i = realm.createObject(Images.class);
            i.setDescrizione(immD.getText().toString());
            i.setUri(filePath);
            realm.commitTransaction();
            // view the images settings fragment initializing ImagesFragment (FragmentTransaction
            // switch between Fragments).
            ImagesFragment frag= new ImagesFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * on callback from ImagesAdapter to this Activity
     * </p>
     * after deleting references to a image the activity is notified to view the updated image settings
     * </p>
     *
     * @see ImagesAdapter
     * @see ImagesFragment
     */
    @Override
    public void reloadImagesFragment() {
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        ImagesFragment frag= new ImagesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the game settings button from the settings menu.
     * </p>
     * the activity is notified to view the choise of game to set.
     * </p>
     *
     * @param view view of tapped button
     * @see MenuSettingsFragment
     * @see ChoiseOfGameToSetFragment
     */
    public void submitChoiseOfGameToSet(View view) {
        // view the choise of game to set fragment initializing ChoiseOfGameToSetFragment
        // (FragmentTransaction switch between Fragments).
        ChoiseOfGameToSetFragment frag= new ChoiseOfGameToSetFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the Send button from the choise of game to set menu.
     * </p>
     * the activity is notified to view the game to set chosen.
     * </p>
     *
     * @param view view of tapped button
     * @see ChoiseOfGameToSetFragment
     * @see PhrasesFragment
     */
    public void goToGameToSet(View view) {
        // view the fragment relating to the game to be set chosen
        // (FragmentTransaction switch between Fragments).
        FragmentTransaction ft;
        switch(textGameToSet) {
            case "PAROLE IN LIBERTA'":
                PhrasesFragment phrasesFragment= new PhrasesFragment();
                ft=getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.settings_container, phrasesFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            // any other case
            default:
        }
    }
    /**
     * Called when the user taps the save button from the phrases settings (game2 settings).
     * </p>
     * after the checks it adds the phrases on realm
     * </p>
     * and the activity is notified to view the the fragment settings.
     *
     * @param v view of tapped button
     * @see ChoiseOfGameToSetFragment
     * @see Phrases
     * @see MenuSettingsFragment
     */
    public void savePhrases(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText textWPFP=(EditText) findViewById(R.id.edittextwelcomephrasefirstpart);
        if (textWPFP != null)
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            Phrases phraseToSearch = realm.where(Phrases.class)
                    .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_first_part))
                    .findFirst();
            realm.beginTransaction();
            if (phraseToSearch == null) {
                Phrases phrase = realm.createObject(Phrases.class);
                // set the fields here
                phrase.setTipo(getString(R.string.welcome_phrase_first_part));
                phrase.setDescrizione(textWPFP.getText().toString());
            } else {
                phraseToSearch.setDescrizione(textWPFP.getText().toString());
            }
            realm.commitTransaction();
        }
        //
        EditText textWPSP=(EditText) findViewById(R.id.edittextwelcomephrasesecondpart);
        if (textWPSP != null)
        {
            Phrases phraseToSearch = realm.where(Phrases.class)
                    .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_second_part))
                    .findFirst();
            realm.beginTransaction();
            if (phraseToSearch == null) {
                Phrases phrase = realm.createObject(Phrases.class);
                // set the fields here
                phrase.setTipo(getString(R.string.welcome_phrase_second_part));
                phrase.setDescrizione(textWPSP.getText().toString());
            } else {
                phraseToSearch.setDescrizione(textWPSP.getText().toString());
            }
            realm.commitTransaction();
        }
        //
        EditText textRP=(EditText) findViewById(R.id.edittextreminderphrase);
        if (textRP != null)
        {
            Phrases phraseToSearch = realm.where(Phrases.class)
                    .equalTo(getString(R.string.tipo), getString(R.string.reminder_phrase))
                    .findFirst();
            realm.beginTransaction();
            if (phraseToSearch == null) {
                Phrases phrase = realm.createObject(Phrases.class);
                // set the fields here
                phrase.setTipo(getString(R.string.reminder_phrase));
                phrase.setDescrizione(textRP.getText().toString());
            } else {
                phraseToSearch.setDescrizione(textRP.getText().toString());
            }
            realm.commitTransaction();
        }
        //
        EditText textRPP=(EditText) findViewById(R.id.edittextreminderphraseplural);
        if (textRPP != null)
        {
            //
            Phrases phraseToSearch = realm.where(Phrases.class)
                    .equalTo(getString(R.string.tipo), getString(R.string.reminder_phrase_plural))
                    .findFirst();
            realm.beginTransaction();
            if (phraseToSearch == null) {
                Phrases phrase = realm.createObject(Phrases.class);
                // set the fields here
                phrase.setTipo(getString(R.string.reminder_phrase_plural));
                phrase.setDescrizione(textRPP.getText().toString());
            } else {
                phraseToSearch.setDescrizione(textRPP.getText().toString());
            }
            realm.commitTransaction();
        }
        // view the menu settings fragment initializing MenuSettingsFragment (FragmentTransaction
        // switch between Fragments).
        MenuSettingsFragment frag= new MenuSettingsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the contents settings button from the settings menu.
     * </p>
     * the activity is notified to view the contents settings.
     * </p>
     *
     * @param view view of tapped button
     * @see MenuSettingsFragment
     * @see ContentsFragment
     */
    public void submitContents(View view) {
        // view the contents settings fragment initializing ContentsFragment (FragmentTransaction
        // switch between Fragments).
        ContentsFragment frag= new ContentsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    //
    /**
     * Called when the user taps the lists of names button from the contents settings menu.
     * </p>
     * the activity is notified to view the lists of names settings.
     * </p>
     *
     * @param view view of tapped button
     * @see ContentsFragment
     * @see ListsOfNamesFragment
     */
    public void submitListsOfNames(View view) {
        // view the lists of names settings fragment initializing ListsOfNamesFragment (FragmentTransaction
        // switch between Fragments).
        ListsOfNamesFragment frag= new ListsOfNamesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the add button from lists of names settings.
     * </p>
     * after the checks it adds the name on realm
     * </p>
     * and the activity is notified to view the updated lists of names settings.
     *
     * @param v view of tapped button
     * @see ListsOfNamesFragment
     * @see ListsOfNames
     */
    public void nameAddToList(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText textWord1=(EditText) findViewById(R.id.keywordtoadd);
        EditText textWord2=(EditText) findViewById(R.id.nametoadd);
        EditText textWord3=(EditText) findViewById(R.id.uritypenametoadd);
        EditText textWord4=(EditText) findViewById(R.id.urinametoadd);
        if ((textWord1 != null) && (textWord2 != null)
                && (textWord3 != null) && (textWord4 != null))
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            ListsOfNames listsOfNames = realm.createObject(ListsOfNames.class);
            // set the fields here
            listsOfNames.setKeyword(textWord1.getText().toString());
            listsOfNames.setWord(textWord2.getText().toString());
            listsOfNames.setUriType(textWord3.getText().toString());
            listsOfNames.setUri(textWord4.getText().toString());
            realm.commitTransaction();
        }
        // view the lists of names settings initializing ListsOfNamesFragment (FragmentTransaction
        // switch between Fragments).
        ListsOfNamesFragment frag= new ListsOfNamesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * on callback from ListOfNamesAdapter to this Activity
     * </p>
     * after deleting a name the activity is notified to view the updated lists of names settings
     * </p>
     *
     * @see ListsOfNamesAdapter
     * @see ListsOfNamesFragment
     */
    @Override
    public void reloadListOfNamesFragment() {
        // view the lists of names settings initializing ListsOfNamesFragment (FragmentTransaction
        // switch between Fragments).
        ListsOfNamesFragment frag= new ListsOfNamesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the word pairs button from the contents settings menu.
     * </p>
     *
     * @param view view of tapped button
     * @see ContentsFragment
     * @see SettingsWordPairsActivity
     */
    public void submitWordPairs(View view) {
        /*
                navigate to settings word pairs screen
        */
        Intent intent = new Intent(context, SettingsWordPairsActivity.class);
        startActivity(intent);
    }
    /**
     * Called when the user taps the stories button from the contents settings menu.
     * </p>
     * the activity is notified to view the stories settings.
     * </p>
     *
     * @param view view of tapped button
     * @see ContentsFragment
     * @see StoriesFragment
     */
    public void submitStories(View view) {
        // view the stories settings fragment initializing StoriesFragment (FragmentTransaction
        // switch between Fragments).
        StoriesFragment frag= new StoriesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the show list button from the stories settings.
     * </p>
     * the activity is notified to view the stories list.
     * </p>
     *
     * @param view view of tapped button
     * @see StoriesFragment
     * @see StoriesListFragment
     */
    public void storiesShowList(View view) {
        // view the stories list  initializing StoriesListFragment (FragmentTransaction
        // switch between Fragments).
        StoriesListFragment frag= new StoriesListFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the add button from stories settings.
     * </p>
     * after the checks it adds the piece of story on realm
     * </p>
     * and the activity is notified to view the stories settings.
     *
     * @param v view of tapped button
     * @see StoriesFragment
     * @see Stories
     */
    public void storyToAdd(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText textWord1=(EditText) findViewById(R.id.keywordstorytoadd);
        EditText textWord2=(EditText) findViewById(R.id.phrasenumbertoadd);
        EditText textWord3=(EditText) findViewById(R.id.wordnumbertoadd);
        EditText textWord4=(EditText) findViewById(R.id.wordtoadd);
        EditText textWord5=(EditText) findViewById(R.id.uritypetoadd);
        EditText textWord6=(EditText) findViewById(R.id.uritoadd);
        if ((textWord1 != null) && (textWord2 != null)
                && (textWord3 != null) && (textWord4 != null)
                && (textWord5 != null) && (textWord6 != null))
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            Stories stories = realm.createObject(Stories.class);
            // set the fields here
            stories.setStory(textWord1.getText().toString());
            stories.setPhraseNumber(textWord2.getText().toString());
            stories.setWordNumber(textWord3.getText().toString());
            stories.setWord(textWord4.getText().toString());
            stories.setUriType(textWord5.getText().toString());
            stories.setUri(textWord6.getText().toString());
            realm.commitTransaction();
        }
        // view the stories settings fragment initializing StoriesFragment (FragmentTransaction
        // switch between Fragments).
        StoriesFragment frag= new StoriesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * on callback from StoriesAdapter to this Activity
     * </p>
     * after deleting a piece of story the activity is notified to view the word pairs settings
     * </p>
     *
     * @see StoriesAdapter
     * @see StoriesFragment
     */
    @Override
    public void reloadStoriesFragment() {
        // view the stories settings fragment initializing StoriesFragment (FragmentTransaction
        // switch between Fragments).
        StoriesFragment frag= new StoriesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the advanced settings button from the settings menu.
     * </p>
     * the activity is notified to view the advanced settings.
     * </p>
     *
     * @param view view of tapped button
     * @see MenuSettingsFragment
     * @see AdvancedSettingsFragment
     */
    public void submitAdvancedSettings(View view) {
        // view the advanced settings initializing AdvancedSettingsFragment (FragmentTransaction
        // switch between Fragments).
        AdvancedSettingsFragment frag= new AdvancedSettingsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the general settings button from the advanced settings menu.
     * </p>
     * the activity is notified to view the general settings.
     * </p>
     *
     * @param view view of tapped button
     * @see AdvancedSettingsFragment
     * @see GeneralSettingsFragment
     */
    public void generalSettings(View view) {
        // view the general settings initializing GeneralSettingsFragment (FragmentTransaction
        // switch between Fragments).
        GeneralSettingsFragment frag= new GeneralSettingsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user click the enable/disable printing radiobutton from general settings.
     * </p>
     * register in the shared preferences
     *
     * @param view view of clicked radiobutton
     * @see GeneralSettingsFragment
     */
    public void PrintingRadioButtonClicked(View view) {
        //
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.enableprinting:
                if (checked)
                {
                    sharedPref = this.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.preference_print_permissions), getString(R.string.character_y));
                    editor.apply();
                }
                break;
            case R.id.disableprinting:
                if (checked)
                {
                    sharedPref = this.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.preference_print_permissions), getString(R.string.character_n));
                    editor.apply();
                }
                break;
        }
    }
    /**
     * Called when the user click the list mode radiobutton from general settings.
     * </p>
     * register order of presentation of lists of names in the shared preferences
     *
     * @param view view of clicked radiobutton
     * @see GeneralSettingsFragment
     */
    public void listModeRadioButtonClicked(View view) {
         // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.disablelistmode:
                if (checked)
                {
                    sharedPref = this.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.preference_list_mode), getString(R.string.disabled));
                    editor.apply();
                }
                break;
            case R.id.enablesortedlistmode:
                if (checked)
                {
                    sharedPref = this.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.preference_list_mode), getString(R.string.sorted));
                    editor.apply();
                }
                break;
            case R.id.enablerandomlistmode:
                if (checked)
                {
                    sharedPref = this.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.preference_list_mode), getString(R.string.random));
                    editor.apply();
                }
                break;
        }
    }
    /**
     * Called when the user taps the save button from general settings.
     * </p>
     * after the checks register allowed margin of error in the shared preferences
     * </p>
     * and the activity is notified to view the the advanced settings fragment.
     *
     * @param view view of tapped button
     * @see GeneralSettingsFragment
     * @see AdvancedSettingsFragment
     */
    public void generalSettingsSave(View view) {
        EditText textAllowedMarginOfError=(EditText) findViewById(R.id.allowedmarginoferror);
        String valueAllowedMarginOfError= textAllowedMarginOfError.getText().toString();
        //
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int preference_AllowedMarginOfError =
                sharedPref.getInt (getString(R.string.preference_allowed_margin_of_error), 20);
        int finalValueAllowedMarginOfError;
        try {
            finalValueAllowedMarginOfError=Integer.parseInt(valueAllowedMarginOfError);
        } catch (NumberFormatException e) {
            finalValueAllowedMarginOfError = preference_AllowedMarginOfError;
        }
        //
        if (!(finalValueAllowedMarginOfError < 0) && !(finalValueAllowedMarginOfError > 100))
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.preference_allowed_margin_of_error), finalValueAllowedMarginOfError);
            editor.apply();
            // view the advanced settings initializing AdvancedSettingsFragment (FragmentTransaction
            // switch between Fragments).
            AdvancedSettingsFragment frag= new AdvancedSettingsFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * Called when the user taps the game parameters button from the advanced settings menu.
     * </p>
     * the activity is notified to view the game parameters settings.
     * </p>
     *
     * @param view view of tapped button
     * @see AdvancedSettingsFragment
     * @see GameParametersSettingsFragment
     */
    public void gameParametersSettings(View view) {
        // view the game parameters settings initializing GameParametersSettingsFragment (FragmentTransaction
        // switch between Fragments).
        GameParametersSettingsFragment frag= new GameParametersSettingsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the save button from the game parameters settings.
     * </p>
     * after the checks it adds the game parameters on realm
     * </p>
     * and the activity is notified to view the updated game parameters settings.
     *
     * @param v view of tapped button
     * @see GameParametersSettingsFragment
     * @see GameParameters
     */
    public void gameParametersSave(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText gamD=(EditText) findViewById(R.id.gameDescription);
        EditText gamI=(EditText) findViewById(R.id.gameinfo);
        EditText gamJC=(EditText) findViewById(R.id.gamejavaclass);
        EditText gamP=(EditText) findViewById(R.id.gameparameter);
        if (gamD.length()>0 &&
                gamI.length()>0 &&
                gamJC.length()>0 &&
                gamP.length()>0 &&
                !filePath.equals(getString(R.string.non_trovato)))
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            GameParameters gp = realm.createObject(GameParameters.class);
            gp.setGameName(gamD.getText().toString());
            gp.setGameInfo(gamI.getText().toString());
            gp.setGameJavaClass(gamJC.getText().toString());
            gp.setGameParameter(gamP.getText().toString());
            gp.setGameIconType("S");
            gp.setGameIconPath(filePath);
            realm.commitTransaction();
            // view the game parameters settings initializing GameParametersSettingsFragment (FragmentTransaction
            // switch between Fragments).
            GameParametersSettingsFragment frag= new GameParametersSettingsFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * on callback from GameParametersAdapter to this Activity
     * </p>
     * after deleting game parameters the activity is notified to view the updated game parameters settings
     * </p>
     *
     * @see GameParametersAdapter
     * @see GameParametersSettingsFragment
     */
    @Override
    public void reloadGameParametersFragment() {
        // view the game parameters settings initializing GameParametersSettingsFragment (FragmentTransaction
        // switch between Fragments).
        GameParametersSettingsFragment frag= new GameParametersSettingsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();

    }
    /**
     * Called when the user taps import tables button from the advanced settings menu.
     * </p>
     * the activity is notified to view the import tables settings.
     * </p>
     *
     * @param view view of tapped button
     * @see AdvancedSettingsFragment
     * @see DataImportSettingsFragment
     */
    public void importTables(View view) {
        checkboxImagesChecked = false;
        checkboxVideosChecked = false;
        checkboxPhrasesChecked = false;
        checkboxWordPairsChecked = false;
        checkboxListsOfNamesChecked = false;
        checkboxStoriesChecked = false;
        checkboxGrammaticalExceptionsChecked = false;
        // view the import tables settings initializing DataImportSettingsFragment (FragmentTransaction
        // switch between Fragments).
        DataImportSettingsFragment frag= new DataImportSettingsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user click the checkbox from import tables settings.
     * </p>
     * register which checkbox was clicked
     *
     * @param view view of clicked checkbox
     * @see DataImportSettingsFragment
     */
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_images:
                if (checked)
                //
                checkboxImagesChecked = true;
            else
                //
                checkboxImagesChecked = false;
                break;
            case R.id.checkbox_videos:
                if (checked)
                //
                checkboxVideosChecked = true;
                else
                    //
                checkboxVideosChecked = false;
                break;
            case R.id.checkbox_phrases:
                if (checked)
                //
                checkboxPhrasesChecked = true;
                else
                    //
                checkboxPhrasesChecked = false;
                break;
            case R.id.checkbox_wordpairs:
                if (checked)
                //
                checkboxWordPairsChecked = true;
                else
                    //
                checkboxWordPairsChecked = false;
                break;
            case R.id.checkbox_listsofnames:
                if (checked)
                //
                checkboxListsOfNamesChecked = true;
                else
                    //
                checkboxListsOfNamesChecked = false;
                break;
            case R.id.checkbox_stories:
                if (checked)
                //
                checkboxStoriesChecked = true;
                else
                    //
                checkboxStoriesChecked = false;
                break;
            case R.id.checkbox_grammaticalexceptions:
                if (checked)
                    //
                    checkboxGrammaticalExceptionsChecked = true;
                else
                    //
                    checkboxGrammaticalExceptionsChecked = false;
                break;
            case R.id.checkbox_gameparameters:
                if (checked)
                    //
                    checkboxGameParametersChecked = true;
                else
                    //
                    checkboxGameParametersChecked = false;
                break;
            //
        }
    }
    /**
     * Called when the user taps the import button from the import tables settings settings.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/65203681/how-to-create-multiple-files-at-once-using-android-storage-access-framework">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/11355432/ismail-osunlana">Ismail Osunlana</a>
     *
     * @param view view of tapped button
     * @see #isStoragePermissionGranted
     * @see DataImportSettingsFragment
     */
    public void settingsDataImportSave(View view) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
        csvSearchActivityResultLauncher.launch(intent);
    }
    //
    /**
     * setting callbacks to search for csv via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     * </p>
     * import the selected tables to realm
     * </p>
     * and the activity is notified to view the advanced settings menu.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/4147849/muntashir-akon">Muntashir Akon</a>
     * <p>
     * and to <a href="https://stackoverflow.com/questions/7620401/how-to-convert-image-file-data-in-a-byte-array-to-a-bitmap">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/840861/uttam">Uttam</a>
     *
     * @see AccountActivityAbstractClass#copyFileFromSharedToInternalStorage
     * @see Images
     * @see Videos
     * @see GameParameters
     * @see GrammaticalExceptions
     * @see ListsOfNames
     * @see Phrases
     * @see Stories
     * @see WordPairs
     * @see AdvancedSettingsFragment
     */
    public void setCsvSearchActivityResultLauncher() {
            // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
            csvSearchActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = result.getData();
                            // doSomeOperations();
                            csvTreeUri = null;
//                            filePath = getString(R.string.non_trovato);
                            if (resultData != null) {
                                csvTreeUri = Objects.requireNonNull(resultData).getData();
                                DocumentFile inputFolder = DocumentFile.fromTreeUri(context, csvTreeUri);
//
                                if (isStoragePermissionGranted()) {
                                    if (checkboxImagesChecked) {
                                        assert inputFolder != null;
                                        DocumentFile documentFileNewFile = inputFolder.findFile("images.csv");
                                        assert documentFileNewFile != null;
                                        Uri csvFileUri = documentFileNewFile.getUri();
                                        try {
                                            copyFileFromSharedToInternalStorage(csvFileUri,"images.csv");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Images.importFromCsvFromInternalStorage(context, realm);
                                    }
                                    if (checkboxVideosChecked)
                                    {
                                        assert inputFolder != null;
                                        DocumentFile documentFileNewFile = inputFolder.findFile("videos.csv");
                                        assert documentFileNewFile != null;
                                        Uri csvFileUri = documentFileNewFile.getUri();
                                        try {
                                            copyFileFromSharedToInternalStorage(csvFileUri,"videos.csv");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Videos.importFromCsvFromInternalStorage(context, realm);
                                    }
                                    if (checkboxPhrasesChecked)
                                    {
                                        assert inputFolder != null;
                                        DocumentFile documentFileNewFile = inputFolder.findFile("phrases.csv");
                                        assert documentFileNewFile != null;
                                        Uri csvFileUri = documentFileNewFile.getUri();
                                        try {
                                            copyFileFromSharedToInternalStorage(csvFileUri,"phrases.csv");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Phrases.importFromCsvFromInternalStorage(context, realm);
                                    }
                                    if (checkboxWordPairsChecked)
                                    {
                                        assert inputFolder != null;
                                        DocumentFile documentFileNewFile = inputFolder.findFile("wordpairs.csv");
                                        assert documentFileNewFile != null;
                                        Uri csvFileUri = documentFileNewFile.getUri();
                                        try {
                                            copyFileFromSharedToInternalStorage(csvFileUri,"wordpairs.csv");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        WordPairs.importFromCsvFromInternalStorage(context, realm);
                                    }
                                    if (checkboxListsOfNamesChecked)
                                    {
                                        assert inputFolder != null;
                                        DocumentFile documentFileNewFile = inputFolder.findFile("listsofnames.csv");
                                        assert documentFileNewFile != null;
                                        Uri csvFileUri = documentFileNewFile.getUri();
                                        try {
                                            copyFileFromSharedToInternalStorage(csvFileUri,"listsofnames.csv");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        ListsOfNames.importFromCsvFromInternalStorage(context, realm);
                                    }
                                    if (checkboxStoriesChecked)
                                    {
                                        assert inputFolder != null;
                                        DocumentFile documentFileNewFile = inputFolder.findFile("stories.csv");
                                        assert documentFileNewFile != null;
                                        Uri csvFileUri = documentFileNewFile.getUri();
                                        try {
                                            copyFileFromSharedToInternalStorage(csvFileUri,"stories.csv");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Stories.importFromCsvFromInternalStorage(context, realm);
                                    }
                                    if (checkboxGrammaticalExceptionsChecked)
                                    {
                                        assert inputFolder != null;
                                        DocumentFile documentFileNewFile = inputFolder.findFile("grammaticalexceptions.csv");
                                        assert documentFileNewFile != null;
                                        Uri csvFileUri = documentFileNewFile.getUri();
                                        try {
                                            copyFileFromSharedToInternalStorage(csvFileUri,"grammaticalexceptions.csv");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        GrammaticalExceptions.importFromCsvFromInternalStorage(context, realm);
                                    }
                                    if (checkboxGameParametersChecked)
                                    {
                                        assert inputFolder != null;
                                        DocumentFile documentFileNewFile = inputFolder.findFile("gameparameters.csv");
                                        assert documentFileNewFile != null;
                                        Uri csvFileUri = documentFileNewFile.getUri();
                                        try {
                                            copyFileFromSharedToInternalStorage(csvFileUri,"gameparameters.csv");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        GameParameters.importFromCsvFromInternalStorage(context, realm);
                                    }
                                    // removed the import of history because it gives problems:
                                    // phraseNumber on shared preferences always starts from 1
                                    // the shared preferences should be set to the latest phrasenumber
                                    // of imported history
                                }
                                // view the advanced settings initializing AdvancedSettingsFragment (FragmentTransaction
                                // switch between Fragments).
                                AdvancedSettingsFragment frag= new AdvancedSettingsFragment();
                                FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.settings_container, frag);
                                ft.addToBackStack(null);
                                ft.commit();

                            }
                        }
                    }
                });

        //

    }
    /**
     * Called when the user taps export tables button from the advanced settings menu.
     * </p>
     * Refer to <a href="https://stackoverflow.com/questions/65203681/how-to-create-multiple-files-at-once-using-android-storage-access-framework">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/11355432/ismail-osunlana">Ismail Osunlana</a>
     *
     * @param view view of tapped button
     * @see #isStoragePermissionGranted
     */
    public void exportTables(View view) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
        exportCsvSearchActivityResultLauncher.launch(intent);
    }
    /**
     * Called when the user taps export tables button from the advanced settings menu.
     * </p>
     * at the end the activity is notified to view the settings menu.
     *
     * @see #isStoragePermissionGranted
     * @see MenuSettingsFragment
     * @see Images
     * @see Videos
     * @see GameParameters
     * @see GrammaticalExceptions
     * @see ListsOfNames
     * @see Phrases
     * @see Stories
     * @see WordPairs
     * @see History
     * @see PictogramsAllToModify
     */
    public void setExportCsvSearchActivityResultLauncher() {
        //
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        exportCsvSearchActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = result.getData();
                            // doSomeOperations();
                            csvTreeUri = null;
//                            filePath = getString(R.string.non_trovato);
                            if (resultData != null) {
                                csvTreeUri = Objects.requireNonNull(resultData).getData();
                                DocumentFile outputFolder = DocumentFile.fromTreeUri(context, csvTreeUri);
//
                                if (isStoragePermissionGranted()) {
                                    Images.exporttoCsv(context, realm);
                                    Videos.exporttoCsv(context, realm);
                                    Phrases.exporttoCsv(context, realm);
                                    WordPairs.exporttoCsv(context, realm);
                                    ListsOfNames.exporttoCsv(context, realm);
                                    Stories.exporttoCsv(context, realm);
                                    History.exporttoCsv(context, realm);
                                    PictogramsAllToModify.exporttoCsv(context, realm);
                                    GameParameters.exporttoCsv(context, realm);
                                    GrammaticalExceptions.exporttoCsv(context, realm);
                                    //
                                    assert outputFolder != null;
                                    //
                                    try {
                                        copyFileFromInternalToSharedStorage(outputFolder,"images.csv");
                                        copyFileFromInternalToSharedStorage(outputFolder,"videos.csv");
                                        copyFileFromInternalToSharedStorage(outputFolder,"phrases.csv");
                                        copyFileFromInternalToSharedStorage(outputFolder,"wordpairs.csv");
                                        copyFileFromInternalToSharedStorage(outputFolder,"listsofnames.csv");
                                        copyFileFromInternalToSharedStorage(outputFolder,"stories.csv");
                                        copyFileFromInternalToSharedStorage(outputFolder,"history.csv");
                                        copyFileFromInternalToSharedStorage(outputFolder,"pictogramsalltomodify.csv");
                                        copyFileFromInternalToSharedStorage(outputFolder,"gameparameters.csv");
                                        copyFileFromInternalToSharedStorage(outputFolder,"grammaticalexceptions.csv");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //
                                }
                                // view the fragment settings initializing MenuSettingsFragment (FragmentTransaction
                                // switch between Fragments).
                                MenuSettingsFragment frag= new MenuSettingsFragment();
                                FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.settings_container, frag);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        }
                    }
        });
    }
    /**
     * check permissions.
     *
     * @return boolean whit true if permission is granted
     */
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
                return true;
            } else {

                // Log.v(TAGPERMISSION,getString(R.string.permission_is_revoked));
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            // Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
            return true;
        }
    }
    /**
     * Called when the user taps grammatical exceptions button from the advanced settings menu.
     * </p>
     * the activity is notified to view the grammatical exceptions settings.
     * </p>
     *
     * @param view view of tapped button
     * @see AdvancedSettingsFragment
     * @see GrammaticalExceptionsFragment
     */
    public void grammaticalExceptions(View view) {
        // view the grammatical exceptions settings initializing GrammaticalExceptionsFragment (FragmentTransaction
        // switch between Fragments).
        GrammaticalExceptionsFragment frag= new GrammaticalExceptionsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the save button from the grammatical exceptions settings.
     * </p>
     * after the checks it adds the grammatical exceptions on realm
     * </p>
     * and the activity is notified to view the updated grammatical exceptions settings.
     *
     * @param v view of tapped button
     * @see GrammaticalExceptionsFragment
     * @see GrammaticalExceptions
     */
    public void GrammaticalExceptionsSave(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText geK=(EditText) findViewById(R.id.grammaticalexceptionskeyword);
        EditText geET=(EditText) findViewById(R.id.exceptiontype);
        EditText geE1=(EditText) findViewById(R.id.exception1);
        EditText geE2=(EditText) findViewById(R.id.exception2);
        EditText geE3=(EditText) findViewById(R.id.exception3);
        if (geK.length()>0 &&
                geET.length()>0 &&
                geE1.length()>0 &&
                geE2.length()>0 &&
                geE3.length()>0)
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            GrammaticalExceptions ge = realm.createObject(GrammaticalExceptions.class);
            ge.setKeyword(geK.getText().toString());
            ge.setExceptionType(geET.getText().toString());
            ge.setException1(geE1.getText().toString());
            ge.setException2(geE2.getText().toString());
            ge.setException3(geE3.getText().toString());
            realm.commitTransaction();
            // view the grammatical exceptions settings initializing GrammaticalExceptionsFragment
            // (FragmentTransaction switch between Fragments).
            GrammaticalExceptionsFragment frag= new GrammaticalExceptionsFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * on callback from GrammaticalExceptionsAdapter to this Activity
     * </p>
     * after deleting a grammatical exceptions the activity is notified to view the grammatical exceptions settings
     * </p>
     *
     * @see GrammaticalExceptionsAdapter
     * @see GrammaticalExceptionsFragment
     */
    @Override
    public void reloadGrammaticalExceptionsFragment() {
        // view the grammatical exceptions settings initializing GrammaticalExceptionsFragment
        // (FragmentTransaction switch between Fragments).
        GrammaticalExceptionsFragment frag= new GrammaticalExceptionsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the pictograms to modify button from the advanced settings menu.
     * </p>
     * the activity is notified to view the pictograms to modify settings.
     * </p>
     *
     * @param view view of tapped button
     * @see AdvancedSettingsFragment
     * @see PictogramsToModifyFragment
     */
    public void pictogramsToModify(View view) {
        // view the pictograms to modify settings initializing PictogramsToModifyFragment
        // (FragmentTransaction switch between Fragments).
        FragmentTransaction ft;
        PictogramsToModifyFragment pictogramsToModifyFragment= new PictogramsToModifyFragment();
        ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, pictogramsToModifyFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the save button from the pictograms to modify settings.
     * </p>
     * after the checks it adds the pictograms to modify on realm
     * </p>
     * and the activity is notified to view the updated pictograms to modify settings.
     *
     * @param v view of tapped button
     * @see PictogramsToModifyFragment
     * @see PictogramsAllToModify
     */
    public void pictogramsToModifySave(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText textPictogramId=(EditText) findViewById(R.id.pictogramid);
        EditText textModificationType=(EditText) findViewById(R.id.modificationtype);
        EditText textKeyword=(EditText) findViewById(R.id.keyword);
        EditText textPlural=(EditText) findViewById(R.id.plural);
        if ((textPictogramId != null) && (textModificationType != null)
                && (textKeyword != null) && (textPlural != null))
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            PictogramsAllToModify pictogramsAllToModify =
                    realm.createObject(PictogramsAllToModify.class);
            // set the fields here
            pictogramsAllToModify.set_id(textPictogramId.getText().toString());
            pictogramsAllToModify.setModificationType(textModificationType.getText().toString());
            pictogramsAllToModify.setKeyword(textKeyword.getText().toString());
            pictogramsAllToModify.setPlural(textPlural.getText().toString());
            realm.commitTransaction();
        }
        // view the pictograms to modify settings initializing PictogramsToModifyFragment
        // (FragmentTransaction switch between Fragments).
        PictogramsToModifyFragment frag= new PictogramsToModifyFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * on callback from PictogramsToModifyAdapter to this Activity
     * </p>
     * after deleting a pictogram to modify the activity is notified to view the pictograms to modify settings
     * </p>
     *
     * @see PictogramsAllToModifyAdapter
     * @see PictogramsToModifyFragment
     */
    @Override
    public void reloadPictogramsToModifyFragment() {
        // view the pictograms to modify settings initializing PictogramsToModifyFragment
        // (FragmentTransaction switch between Fragments).
        FragmentTransaction ft;
        PictogramsToModifyFragment pictogramsToModifyFragment= new PictogramsToModifyFragment();
        ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, pictogramsToModifyFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the about us button from the settings menu.
     * </p>
     * the activity is notified to view information.
     * </p>
     *
     * @param view view of tapped button
     * @see MenuSettingsFragment
     * @see InformationFragment
     */
    public void submitAboutUs(View view) {
        // view information initializing InformationFragment
        // (FragmentTransaction switch between Fragments).
        InformationFragment frag= new InformationFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
//
}
