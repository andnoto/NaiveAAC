package com.sampietro.NaiveAAC.activities.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Game2.SettingsStoriesQuickRegistrationActivity;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames;
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter;

import io.realm.Realm;

/**
 * <h1>SettingsContentsActivity</h1>
 * <p><b>SettingsContentsActivity</b> app settings.</p>
 *
 * @version     3.0, 03/12/23
 * @see SettingsFragmentAbstractClass
 * @see com.sampietro.NaiveAAC.activities.WordPairs.WordPairsAdapter
 * @see ListsOfNamesAdapter
 * @see StoriesAdapter
 */
public class SettingsContentsActivity extends AccountActivityAbstractClass
        implements
        ListsOfNamesAdapter.ListsOfNamesAdapterInterface,
        SettingsFragmentAbstractClass.onFragmentEventListenerSettings
{
    public String message = "messaggio non formato";
    public TextView textView;
    //
    public FragmentManager fragmentManager;
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see #setActivityResultLauncher
     * @see ActionbarFragment
     * @see ContentsFragment
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
        if (savedInstanceState == null)
            {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment))
                    .add(R.id.settings_container, new ContentsFragment(), "ContentsFragment")
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
            listsOfNames.setFromAssets("");
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
     * Called when the user taps the stories add word button from the contents settings menu.
     * </p>
     * the activity is notified to view the stories settings.
     * </p>
     *
     * @param view view of tapped button
     * @see ContentsFragment
     * @see SettingsStoriesActivity
     */
    public void submitStories(View view) {
        /*
                navigate to settings stories screen
        */
        Intent intent = new Intent(context, SettingsStoriesActivity.class);
        startActivity(intent);
    }
    /**
     * Called when the user taps the Stories - Quick Registration button from the contents settings menu.
     * </p>
     *
     * @param view view of tapped button
     * @see ContentsFragment
     * @see SettingsStoriesQuickRegistrationActivity
     */
    public void submitStoriesQuickRegistration(View view) {
        /*
                navigate to settings Stories Quick Registration screen
        */
        Intent intent = new Intent(context, SettingsStoriesQuickRegistrationActivity.class);
        startActivity(intent);
    }
    /**
     * Called when the user taps the Stories - import/export button from the contents settings menu.
     * </p>
     *
     * @param view view of tapped button
     * @see ContentsFragment
     * @see SettingsStoriesImportExportActivity
     */
    public void submitStoriesImportExport(View view) {
        /*
                navigate to settings Stories Import/Export screen
        */
        Intent intent = new Intent(context, SettingsStoriesImportExportActivity.class);
        startActivity(intent);
    }
}
