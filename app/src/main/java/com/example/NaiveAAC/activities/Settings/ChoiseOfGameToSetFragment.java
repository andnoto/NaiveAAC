package com.example.NaiveAAC.activities.Settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.NaiveAAC.R;

/**
 * <h1>ChoiseOfGameToSetFragment</h1>
 * <p><b>ChoiseOfGameToSetFragment</b> UI for choise of game to set
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class ChoiseOfGameToSetFragment extends Fragment {
    public View rootView;
    public TextView textView;
    //
    public String textGameToSet;
    private ArrayAdapter<String> spinnerAdapter;
    public Context ctext;
    /**
     * <h1>onFragmentEventListenerChoiseOfGameToSet</h1>
     * <p><b>onFragmentEventListenerChoiseOfGameToSet</b>
     * interface used to refer to the Activity without having to explicitly use its class
     * <p>
     *
     * @see #onAttach
     */
    interface onFragmentEventListenerChoiseOfGameToSet
    {
        //  insert here any references to the Settings Activity
        void receiveResultChoiseOfGameToSet(View v);
        void receiveResultGameToSet(String g);

    }
    //
    private onFragmentEventListenerChoiseOfGameToSet listener=null;
    /**
     * listener setting for settings activity callbacks and context annotation
     *
     * @see Fragment#onAttach
     */
    @Override
    public void onAttach (Context context)
    {
        super.onAttach(context);
        Activity activity = (Activity) context;
        listener=(onFragmentEventListenerChoiseOfGameToSet) activity;
        //
        ctext = context;
    }
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_games_menu, container, false);
        // logic of fragment
        // SPINNER
        // an Adapter has been prepared that has been connected to spinner with the setAdapter method
        // the onItemSelected method manages the selection of an item of the Spinner
        String[] arraySpinner = new String[] {
                getString(R.string.parole_in_liberta)
        };
        spinnerAdapter=new ArrayAdapter<String>(ctext,
                R.layout.activity_settings_choiseofgametoset_row, arraySpinner);
        Spinner sp= rootView.findViewById(R.id.gametoset);
        sp.setAdapter(spinnerAdapter);
        //
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                TextView txt= arg1.findViewById(R.id.rowtext);
                textGameToSet=txt.getText().toString();
                listener.receiveResultGameToSet(textGameToSet);

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            { }
        });
        //
        listener.receiveResultChoiseOfGameToSet(rootView);
        //
        return rootView;
    }
}

