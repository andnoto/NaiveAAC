package com.sampietro.NaiveAAC.activities.Info;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Account.AccountActionbarFragment;
import com.sampietro.NaiveAAC.activities.Account.AccountActivity;
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameFragment;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptions;
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames;
import com.sampietro.NaiveAAC.activities.Graphics.Images;
import com.sampietro.NaiveAAC.activities.Graphics.Sounds;
import com.sampietro.NaiveAAC.activities.Graphics.Videos;
import com.sampietro.NaiveAAC.activities.Main.MainActivity;
import com.sampietro.NaiveAAC.activities.Phrases.Phrases;
import com.sampietro.NaiveAAC.activities.Settings.AccountFragment;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Stories.Stories;
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs;
import com.sampietro.NaiveAAC.activities.history.History;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>RequestConsentToTheProcessingOfPersonalDataActivity</h1>
 * <p><b>RequestConsentToTheProcessingOfPersonalDataActivity</b> requires consent to the processing of personal data</p>
 *
 * @version     3.0, 03/12/23
 * @see AccountActivityAbstractClass
 * @see SettingsFragmentAbstractClass
 */
public class RequestConsentToTheProcessingOfPersonalDataActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE ="helloworldandroidMessage" ;
    //
    public View rootViewFragment;
    public Context context;
    public SharedPreferences sharedPref;
    //
    public FragmentManager fragmentManager;
    //
    /**
     * configurations of account start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see RequestConsentToTheProcessingOfPersonalDataFragment
     * @see android.app.Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //
        context = this;
        //
        if (savedInstanceState == null)
        {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.game_container, new RequestConsentToTheProcessingOfPersonalDataFragment(), "RequestConsentToTheProcessingOfPersonalDataFragment")
                    .commit();
        }
        //
    }
    /**
     * Called when the user taps "Information on the processing of personal data" text.
     * </p>
     *
     * @param view view of tapped text
     */
    public void onClickInformativa(View view) {
        InformativaFragment frag= new InformativaFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.game_container, frag, "InformativaFragment");
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps "Information on the processing of personal data" text.
     * </p>
     *
     * @param view view of tapped text
     */
    public void onClickTermini(View view) {
        TerminiFragment frag= new TerminiFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.game_container, frag, "TerminiFragment");
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the consent to the processing of personal data button.
     * </p>
     *
     * @param view view of tapped button
     */
    public void iAccept(View view) {
        //
        // registers consent to the processing of personal data
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_ConsentToTheProcessingOfPersonalData), getString(R.string.character_y));
        editor.apply();
        //
        Intent i = new Intent(context,
                RequestConsentFirebaseActivity.class);
        startActivity(i);
    }
    /**
     * Called when the user taps the i do not accept the processing of personal data button.
     * </p>
     *
     * @param view view of tapped button
     */
    public void iDoNotAccept(View view) {
        //
        // does not allow access to the app
        finishAndRemoveTask();
    }
}
