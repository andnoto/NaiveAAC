package com.sampietro.NaiveAAC.activities.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;

/**
 * <h1>AccountActivityAbstractClass</h1>
 * <p><b>AccountActivityAbstractClass</b>
 * abstract class containing common methods that is extended by
 * </p>
 * 1) AccountActivity</p>
 * 2) AccountActivityRealmCreation</p>
 * 3) SettingsActivity</p>
 *
 * @version     1.1, 04/22/22
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivity
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivityRealmCreation
 * @see SettingsActivity
 */
public class VerifyActivity extends AppCompatActivity
        implements
        VerifyFragment.onFragmentEventListenerVerify
{
    //
    public View rootViewVerifyFragment;
    public int resultToVerify;
    //
    public Context context;
    public SharedPreferences sharedPref;
    //
    public FragmentManager fragmentManager;
    //
    private ViewGroup mContentView;
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     // * @see ActionbarFragment
     * @see VerifyFragment
     * @see Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //
        mContentView = findViewById(R.id.activity_settings_id);
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
        context = this;
        //
        if (savedInstanceState == null)
        {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.settings_container, new VerifyFragment(), "VerifyFragment")
                    .commit();
        }
    }
    /**
     * Hide the Navigation Bar
     *
     * @see Activity#onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }
    /**
     * This method is responsible to transfer MainActivity into fullscreen mode.
     */
    private void setToFullScreen() {
        findViewById(R.id.activity_settings_id).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
        int calculationToBeVerified = 99;
        try {
            calculationToBeVerified=Integer.parseInt(value);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        if (calculationToBeVerified == resultToVerify)
        {
                /*
                navigate to settings screen
                */
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }
}
