package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1Activity;
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2Activity;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * <h1>ChoiseOfGameActivity</h1>
 * <p><b>ChoiseOfGameActivity</b> allows game choice
 * </p>
 *
 * @version     1.2, 04/28/22
 * @see GameActivityAbstractClass
 * @see ChoiseOfGameRecyclerViewAdapterInterface
 */
public class ChoiseOfGameActivity extends GameActivityAbstractClass implements
        ChoiseOfGameRecyclerViewAdapterInterface {

    public static final String EXTRA_MESSAGE_GAME_PARAMETER ="GameParameter" ;
    //
    public RealmResults<GameParameters> resultsGameParameters;
    //
    public View rootViewGameFragment;
    //
    /**
     * configurations of game choice start screen.
     * <p>
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see ActionbarFragment
     * @see android.app.Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_container);
        //
        realm= Realm.getDefaultInstance();
        //
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment)).commit();
        }
        //
        resultsGameParameters =
                realm.where(GameParameters.class).findAll();
    }
    /**
     * configurations of game choice start screen.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/43520688/findfragmentbyid-and-findfragmentbytag">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/4266957/ricardo">Ricardo</a>
     *
     * @see ChoiseOfGameFragment
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume(){
        super.onResume();
        //
        ChoiseOfGameFragment frag= new ChoiseOfGameFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ChoiseOfGameFragment fragmentgotinstance =
                (ChoiseOfGameFragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.choise_of_game_fragment));
        if(fragmentgotinstance != null)
        {
            ft.replace(R.id.game_container, frag, getString(R.string.choise_of_game_fragment));
            // ok, we got the fragment instance, but should we manipulate its view?
        }
        else
        {
            ft.add(R.id.game_container, frag, getString(R.string.choise_of_game_fragment));
        }
        ft.addToBackStack(null);
        ft.commit();
    }
    //
    /**
     * Called on result of speech.
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     */
    @Override
    public void onResult(String eText) {
    }
    /**
     * on callback from GameFragment to this Activity
     *
     * @param v view root fragment view
     */
    @Override
    public void receiveResultGameFragment(View v) {
        rootViewGameFragment = v;
    }
    /**
     * called when a game list item is clicked.
     * </p>
     * launch the corresponding game or the corresponding info.</p>
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     * @see ChoiseOfGameRecyclerViewAdapterInterface
     * @see GameParameters
     * @see ChoiseOfGameRecyclerView
     * @see Game1Activity
     * @see Game2Activity
     */
    @Override
    public void onItemClick(View view, int i) {
        GameParameters result;
        result = resultsGameParameters.get(i);
        switch(view.getId()){
            case R.id.media_container:
                // game choice
                ChoiseOfGameRecyclerView recyclerView =
                        (ChoiseOfGameRecyclerView)rootViewGameFragment.findViewById(R.id.recycler_view);
                if(recyclerView!=null)
                    recyclerView.releasePlayer();
                String gameJavaClass = null;
                String gameParameter = null;
                Intent intent = null;
                assert result != null;
                gameJavaClass = result.getGameJavaClass();
                gameParameter = result.getGameParameter();
                //
                switch(gameJavaClass) {
                    case "NAVIGATORE":
                        intent = new Intent(this,
                                Game1Activity.class);
                        intent.putExtra(EXTRA_MESSAGE_GAME_PARAMETER, gameParameter);
                        startActivity(intent);
                        break;
                    case "COMUNICATORE":
                        intent = new Intent(this,
                                Game2Activity.class);
                        intent.putExtra(EXTRA_MESSAGE_GAME_PARAMETER, gameParameter);
                        startActivity(intent);
                        break;
                    // any other cases
                    default:
                }
                break;
            case R.id.infomediaplayer:
                // game info request
                String gameInfo = null;
                assert result != null;
                gameInfo = result.getGameInfo();
                TextView textinfo = (TextView)rootViewGameFragment.findViewById(R.id.textinfoEP);
                textinfo.setText(gameInfo);
                break;
        }
    }
//
}