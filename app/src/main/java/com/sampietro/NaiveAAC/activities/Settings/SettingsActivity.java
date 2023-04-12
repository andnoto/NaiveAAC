package com.sampietro.NaiveAAC.activities.Settings;

import static com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberStories;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModify;
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModifyAdapter;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptions;
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptionsAdapter;
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames;
import com.sampietro.NaiveAAC.activities.Graphics.Images;
import com.sampietro.NaiveAAC.activities.Graphics.Sounds;
import com.sampietro.NaiveAAC.activities.Graphics.Videos;
import com.sampietro.NaiveAAC.activities.Phrases.Phrases;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Stories.Stories;
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs;
import com.sampietro.NaiveAAC.activities.history.History;

import java.io.IOException;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>SettingsActivity</h1>
 * <p><b>SettingsActivity</b> app settings.</p>
 *
 * @version     1.0, 06/13/22
 * @see AccountActivityAbstractClass
 * @see SettingsFragmentAbstractClass
 * @see VerifyFragment
 * @see ChoiseOfGameToSetFragment
 * @see GrammaticalExceptionsAdapter
 * @see GameParametersAdapter
 */
public class SettingsActivity extends AccountActivityAbstractClass
        implements
        ChoiseOfGameToSetFragment.onFragmentEventListenerChoiseOfGameToSet,
        SettingsFragmentAbstractClass.onFragmentEventListenerSettings,
        GameParametersAdapter.GameParametersAdapterInterface,
        GrammaticalExceptionsAdapter.GrammaticalExceptionsAdapterInterface,
        PictogramsAllToModifyAdapter.PictogramsAllToModifyAdapterInterface
{
    public String message = "messaggio non formato";
    public TextView textView;
    //
    public View rootViewChoiseOfGameToSetFragment;
    public String textGameToSet;
    //
    private static final String TAGPERMISSION = "Permission";
    //
    public FragmentManager fragmentManager;
    //
    public boolean radiobuttonGameParametersActiveClicked = false;
    public boolean radiobuttonGameParametersNotActiveClicked = false;
    //
    public boolean radiobuttonGameParametersUseVideoAndSoundClicked = false;
    public boolean radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = false;
    //
    public boolean radiobuttonDataImportReplaceClicked = false;
    public boolean radiobuttonDataImportAppendClicked = false;
    //
    public boolean checkboxImagesChecked;
    public boolean checkboxVideosChecked;
    public boolean checkboxSoundsChecked;
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
     * @see Activity#onCreate(Bundle)
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
                    .add(R.id.settings_container, new MenuSettingsFragment(), "MenuSettingsFragment")
                    .commit();
            }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm= Realm.getDefaultInstance();
    }
    //
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
            i.setFromAssets("");
            realm.commitTransaction();
            //
            realm.beginTransaction();
            Images iIo = realm.createObject(Images.class);
            iIo.setDescrizione(getString(R.string.io));
            iIo.setUri(filePath);
            i.setFromAssets("");
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
            wordPairs.setFromAssets("");
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
     * @see SettingsMediaActivity
     * @see ChoiseOfMediaToSetFragment
     */
     public void submitChoiseOfMediaToSet(View view) {
        /*
                navigate to settings word pairs screen
        */
        Intent intent = new Intent(context, SettingsMediaActivity.class);
        startActivity(intent);
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
            case "COMUNICATORE":
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
                phrase.setFromAssets("Y");
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
                phrase.setFromAssets("Y");
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
                phrase.setFromAssets("Y");
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
                phrase.setFromAssets("Y");
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
     * @see SettingsContentsActivity
     * @see ContentsFragment
     */
    public void submitContents(View view) {
        Intent intent = new Intent(context, SettingsContentsActivity.class);
        startActivity(intent);
    }
    //
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
     * Called when the user click the title writing type radiobutton from general settings.
     * </p>
     * register in the shared preferences
     *
     * @param view view of clicked radiobutton
     * @see GeneralSettingsFragment
     */
    public void titleWritingTypeRadioButtonClicked(View view) {
        //
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.uppercase:
                if (checked)
                {
                    sharedPref = this.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("preference_title_writing_type", "uppercase");
                    editor.apply();
                }
                break;
            case R.id.lowercase:
                if (checked)
                {
                    sharedPref = this.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("preference_title_writing_type", "lowercase");
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
     * Called when the user click the radio button from the game parameters settings.
     * </p>
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     * @see GameParametersSettingsFragment
     */
    public void onGameParametersRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_active:
                if (checked) {
                    //
                    radiobuttonGameParametersActiveClicked = true;
                    radiobuttonGameParametersNotActiveClicked = false;
                }
                break;
            case R.id.radio_not_active:
                if (checked) {
                    //
                    radiobuttonGameParametersActiveClicked = false;
                    radiobuttonGameParametersNotActiveClicked = true;
                }
                break;
        }
    }
    /**
     * Called when the user click the radio button UseVideoAndSound from the game parameters settings.
     * </p>
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     * @see GameParametersSettingsFragment
     */
    public void onGameParametersUseVideoAndSoundClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_yes:
                if (checked) {
                    //
                    radiobuttonGameParametersUseVideoAndSoundClicked = true;
                    radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = false;
                }
                break;
            case R.id.radio_no:
                if (checked) {
                    //
                    radiobuttonGameParametersUseVideoAndSoundClicked = false;
                    radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = true;
                    }
                break;
        }
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
                filePath != null &&
                !filePath.equals(getString(R.string.non_trovato)))
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            GameParameters gp = realm.createObject(GameParameters.class);
            gp.setGameName(gamD.getText().toString());
            if (radiobuttonGameParametersActiveClicked)
                gp.setGameActive("A");
                else
                gp.setGameActive("N"); // default
            gp.setGameInfo(gamI.getText().toString());
            gp.setGameJavaClass(gamJC.getText().toString());
            gp.setGameParameter(gamP.getText().toString());
            if (radiobuttonGameParametersUseVideoAndSoundClicked)
                gp.setGameUseVideoAndSound("Y");
            else
                gp.setGameUseVideoAndSound("N"); // default
            gp.setGameIconType(gameIconType);
            gp.setGameIconPath(filePath);
            gp.setFromAssets("");
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
    public void reloadGameParametersFragmentDeleteGameParameters(int position) {
        // delete
        realm= Realm.getDefaultInstance();
        RealmResults<GameParameters> results = realm.where(GameParameters.class).findAll();
        results = results.sort("gameName");
        realm.beginTransaction();
        GameParameters daCancellare=results.get(position);
        assert daCancellare != null;
        daCancellare.deleteFromRealm();
        realm.commitTransaction();
        // view the game parameters settings initializing GameParametersSettingsFragment (FragmentTransaction
        // switch between Fragments).
        GameParametersSettingsFragment frag= new GameParametersSettingsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * on callback from GameParametersAdapter to this Activity
     * </p>
     * for editing a Game Parameter the activity is notified to view the Game Parameters settings
     * </p>
     *
     * @see GameParametersAdapter
     * @see GameParametersSettingsFragment
     */
    @Override
    public void reloadGameParametersFragmentForEditing(int position) {
        // view the Game Parameters settings fragment initializing GameParametersSettingsFragment (FragmentTransaction
        // switch between Fragments).
        realm= Realm.getDefaultInstance();
        RealmResults<GameParameters> results = null;
        results = realm.where(GameParameters.class).findAll();
        results = results.sort("gameName");
        //
        GameParametersSettingsFragment frag= new GameParametersSettingsFragment();
        //
        Bundle bundle = new Bundle();
        GameParameters daModificare=results.get(position);
        assert daModificare != null;
        //
        bundle.putString("GameName", daModificare.getGameName());
        bundle.putString("GameActive", daModificare.getGameActive());
        if (Objects.equals(daModificare.getGameActive(), "A")) {
            //
            radiobuttonGameParametersActiveClicked = true;
            radiobuttonGameParametersNotActiveClicked = false;
            }
            else {
            //
            radiobuttonGameParametersActiveClicked = false;
            radiobuttonGameParametersNotActiveClicked = true;
            }
        //
        gameIconType = daModificare.getGameIconType();
        filePath = daModificare.getGameIconPath();
        //
        bundle.putString("GameIconType", daModificare.getGameIconType());
        bundle.putString("GameIconPath", daModificare.getGameIconPath());
        bundle.putString("GameInfo", daModificare.getGameInfo());
        bundle.putString("GameJavaClass", daModificare.getGameJavaClass());
        bundle.putString("GameParameter", daModificare.getGameParameter());
        bundle.putString("GameUseVideoAndSound", daModificare.getGameUseVideoAndSound());
        if (Objects.equals(daModificare.getGameUseVideoAndSound(), "Y")) {
            //
            radiobuttonGameParametersUseVideoAndSoundClicked = true;
            radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = false;
        }
        else {
            //
            radiobuttonGameParametersUseVideoAndSoundClicked = false;
            radiobuttonGameParametersDoesNotUseVideoAndSoundClicked = true;
        }
        frag.setArguments(bundle);
        // delete
        realm.beginTransaction();
        daModificare.deleteFromRealm();
        realm.commitTransaction();
        //
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
        checkboxSoundsChecked = false;
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
     * Called when the user click the radio button from import tables settings.
     * </p>
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     * @see DataImportSettingsFragment
     */
    public void onDataImportRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_replace:
                if (checked)
                    //
                    radiobuttonDataImportReplaceClicked = true;
                    break;
            case R.id.radio_append:
                if (checked)
                    //
                    radiobuttonDataImportAppendClicked = true;
                    break;
        }
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
            case R.id.checkbox_sounds:
                if (checked)
                //
                checkboxSoundsChecked = true;
                else
                //
                checkboxSoundsChecked = false;
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
                                    String importMode = "Append"; // default import mode
                                    if (radiobuttonDataImportReplaceClicked)
                                        importMode = "Replace";
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
                                        Images.importFromCsvFromInternalStorage(context, realm, importMode);
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
                                        Videos.importFromCsvFromInternalStorage(context, realm, importMode);
                                    }
                                    if (checkboxSoundsChecked)
                                    {
                                        assert inputFolder != null;
                                        DocumentFile documentFileNewFile = inputFolder.findFile("sounds.csv");
                                        assert documentFileNewFile != null;
                                        Uri csvFileUri = documentFileNewFile.getUri();
                                        try {
                                            copyFileFromSharedToInternalStorage(csvFileUri,"sounds.csv");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Sounds.importFromCsvFromInternalStorage(context, realm, importMode);
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
                                        Phrases.importFromCsvFromInternalStorage(context, realm, importMode);
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
                                        WordPairs.importFromCsvFromInternalStorage(context, realm, importMode);
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
                                        ListsOfNames.importFromCsvFromInternalStorage(context, realm, importMode);
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
                                        Stories.importFromCsvFromInternalStorage(context, realm, importMode);
                                        if (Objects.equals(importMode, "Replace"))
                                        {
                                            renumberStories(realm);
                                        }
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
                                        GrammaticalExceptions.importFromCsvFromInternalStorage(context, realm, importMode);
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
                                        GameParameters.importFromCsvFromInternalStorage(context, realm, importMode);
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
                                    Sounds.exporttoCsv(context, realm);
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
                                        copyFileFromInternalToSharedStorage(outputFolder,"sounds.csv");
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
            ge.setFromAssets("Y");
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
//
    /**
     * Called when the user taps the test crash button from the advanced settings menu.
     * </p>
     *
     * @param view view of tapped button
     * @see AdvancedSettingsFragment
     */
    public void testCrash(View view) {
        // Crash Test with Firebase Crashlytics
        throw new RuntimeException("Test Crash"); // Force a crash
    }
}
