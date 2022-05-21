package com.example.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Phrases.Phrases;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

import io.realm.Realm;

/**
 * <h1>PhrasesFragment</h1>
 * <p><b>PhrasesFragment</b> UI for phrases settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class PhrasesFragment extends SettingsFragmentAbstractClass {
    // Realm
    public Realm realm;
    //
    public TextView textView1;
    public TextView textView2;
    public TextView textView3;
    public TextView textView4;
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see com.example.NaiveAAC.activities.Phrases.Phrases
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_phrases, container, false);
        // logic of fragment
        textView1 = rootView.findViewById(R.id.edittextwelcomephrasefirstpart);
        textView2 = rootView.findViewById(R.id.edittextwelcomephrasesecondpart);
        textView3 = rootView.findViewById(R.id.edittextreminderphrase);
        textView4 = rootView.findViewById(R.id.edittextreminderphraseplural);
        // REALM
        realm= Realm.getDefaultInstance();
        //
        Phrases phraseToSearch1 = realm.where(Phrases.class)
                .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_first_part))
                .findFirst();
        if (phraseToSearch1 != null) {
            textView1.setText(phraseToSearch1.getDescrizione());
            }
        Phrases phraseToSearch2 = realm.where(Phrases.class)
                .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_second_part))
                .findFirst();
        if (phraseToSearch2 != null) {
            textView2.setText(phraseToSearch2.getDescrizione());
            }
        Phrases phraseToSearch3 = realm.where(Phrases.class)
                .equalTo(getString(R.string.tipo), getString(R.string.reminder_phrase))
                .findFirst();
        if (phraseToSearch3 != null) {
            textView3.setText(phraseToSearch3.getDescrizione());
            }
        Phrases phraseToSearch4 = realm.where(Phrases.class)
                .equalTo(getString(R.string.tipo), getString(R.string.reminder_phrase_plural))
                .findFirst();
        if (phraseToSearch4 != null) {
            textView4.setText(phraseToSearch4.getDescrizione());
        }
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

