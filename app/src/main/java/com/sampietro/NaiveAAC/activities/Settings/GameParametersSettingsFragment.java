package com.sampietro.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter;
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

import java.io.File;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>GameParametersSettingsFragment</h1>
 * <p><b>GameParametersSettingsFragment</b> UI for game parameters settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
public class GameParametersSettingsFragment extends SettingsFragmentAbstractClass {
    private Realm realm;
    //
    private ListView listView=null;
    private GameParametersAdapter adapter;
    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_game_parameters, container, false);
        // logic of fragment
        realm= Realm.getDefaultInstance();
        // ListView
        // 1) we get a reference to the data structure through the RealmResults class which constitutes
        // the query result set and is an iterable collection accessible with Java constructs:
        // for loop, basic access to position and Iterator.
        // The approach for realm queries is object-oriented.
        // The where method will retrieve the objects from the specified class and the result will
        // be treated with filtrate by other specific methods.
        // At the end of the selection configuration, the findAll method will be invoked to retrieve
        // all the corresponding results.
        // 2) we instantiate an Adapter by assigning it, the collection and the layout related to
        // each single row
        // 3) we retrieve the ListView prepared in the layout and assign it the reference to the adapter
        // which will be your View "supplier".
        RealmResults<GameParameters> results = realm.where(GameParameters.class).findAll();
        //
        results = results.sort("gameName");
        //
        listView=(ListView) rootView.findViewById(R.id.listview);
        //
        adapter=new GameParametersAdapter(ctext, results, listView);
        //
        listView.setAdapter(adapter);
        //
        Bundle bundle = this.getArguments();
        //
        if (bundle != null) {
            EditText gameDescription=(EditText) rootView.findViewById(R.id.gameDescription);
            RadioButton radio_active = (RadioButton) rootView.findViewById(R.id.radio_active);
            RadioButton radio_not_active = (RadioButton) rootView.findViewById(R.id.radio_not_active);
            EditText gamejavaclass=(EditText) rootView.findViewById(R.id.gamejavaclass);
            EditText gameparameter=(EditText) rootView.findViewById(R.id.gameparameter);
            RadioButton radioUseVideoAndSound = (RadioButton) rootView.findViewById(R.id.radio_yes);
            RadioButton radioDoesNotUseVideoAndSound = (RadioButton) rootView.findViewById(R.id.radio_no);
            EditText gameinfo=(EditText) rootView.findViewById(R.id.gameinfo);
            ImageView imageviewgameicon=(ImageView) rootView.findViewById(R.id.imageviewgameicon);
            //
            gameDescription.setText(bundle.getString("GameName"));
            if (bundle.getString("GameActive").equals("A")) {
                radio_active.setChecked(true);
                radio_not_active.setChecked(false);
                }
                else
                {
                radio_active.setChecked(false);
                radio_not_active.setChecked(true);   // default
                }
            gamejavaclass.setText(bundle.getString("GameJavaClass"));
            gameparameter.setText(bundle.getString("GameParameter"));
            //
            radioUseVideoAndSound.setChecked(false);
            radioDoesNotUseVideoAndSound.setChecked(true);   // default
            if (Objects.equals(bundle.getString("GameUseVideoAndSound"), "Y")) {
//            if (bundle.getString("GameUseVideoAndSound").equals("Y")) {
                radioUseVideoAndSound.setChecked(true);
                radioDoesNotUseVideoAndSound.setChecked(false);
            }
            gameinfo.setText(bundle.getString("GameInfo"));
            String gameIconType = bundle.getString("GameIconType");
            String gameIconPath = bundle.getString("GameIconPath");
            //
            File f = new File(gameIconPath);
            GraphicsHelper.addFileImageUsingPicasso(f, (ImageView) imageviewgameicon, 200,200);
            //
        }
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

