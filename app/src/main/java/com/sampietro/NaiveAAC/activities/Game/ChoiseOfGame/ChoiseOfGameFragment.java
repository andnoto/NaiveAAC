package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Graphics.Videos;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.RealmResults;

/**
 * <h1>ChoiseOfGameFragment</h1>
 * <p><b>ChoiseOfGameFragment</b> UI for game choice
 * </p>
 *
 * @version     1.1, 04/28/22
 * @see GameFragmentAbstractClass
 * @see ChoiseOfGameActivity
 */
public class ChoiseOfGameFragment extends GameFragmentAbstractClass {
    public String [] row1debugGameName = new String[16];
    public String [] row1debugGameIconType = new String[16];
    public String [] row1debugGameIconPath = new String[16];
    public String [] row1debugGameInfo = new String[16];
    public String [] row1debugGameVideoPath = new String[16];
    public String [] row1debugGameVideoCopyright = new String[16];
    //
    private ChoiseOfGameRecyclerView recyclerView;
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
     * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see ChoiseOfGameRecyclerView
     * @see #initRecyclerView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_choise_of_game_mediaplayer, container, false);
        //
        recyclerView = (ChoiseOfGameRecyclerView)rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        initRecyclerView();
        //
        listener.receiveResultGameFragment(rootView);
        return rootView;
    }
    /**
     * initializes the recyclerview using data from the gameparameters class
     *
     * @see #prepareData
     * @see ChoiseOfGameArrayList
     * @see ChoiseOfGameRecyclerViewAdapter
     */
    private void initRecyclerView(){
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        //
        ArrayList<ChoiseOfGameArrayList> createLists = prepareData();
        recyclerView.setMediaObjects(createLists);
        ChoiseOfGameRecyclerViewAdapter adapter =
                new ChoiseOfGameRecyclerViewAdapter(ctext, createLists);
        recyclerView.setAdapter(adapter);
        //
        recyclerView.smoothScrollBy(1, 0);
    }
    /**
     * prepare data for recyclerview using data from the gameparameters class
     *
     * @return theimage arraylist<GameFragmentChoiseOfGameArrayList> data for recyclerview
     * @see ChoiseOfGameArrayList
     * @see GameParameters
     * @see Videos
     */
    private ArrayList<ChoiseOfGameArrayList> prepareData(){
        int debugUrlNumber = 0;
        //
        RealmResults<GameParameters> results =
                realm.where(GameParameters.class)
                        .beginGroup()
                        .equalTo("gameActive", "A")
                        .endGroup()
                        .findAll();
        results = results.sort("gameName");
        int count = results.size();
        //
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                GameParameters result = results.get(irrh);
                assert result != null;
                row1debugGameName[irrh] = result.getGameName();
                row1debugGameIconType[irrh] = result.getGameIconType();
                row1debugGameIconPath[irrh] = result.getGameIconPath();
                row1debugGameInfo[irrh] = result.getGameInfo();
                debugUrlNumber++;
                //
                RealmResults<Videos> resultsVideo =
                        realm.where(Videos.class).equalTo(getString(R.string.descrizione), row1debugGameName[irrh]).findAll();
                int countVideo = resultsVideo.size();
                if (countVideo != 0) {
                    Videos resultVideo = resultsVideo.get(0);
                    assert resultVideo != null;
                    row1debugGameVideoPath[irrh] = resultVideo.getUri();
                    row1debugGameVideoCopyright[irrh] = resultVideo.getCopyright();}
                    else
                    { row1debugGameVideoPath[irrh] = getString(R.string.non_trovato); }
                irrh++;
            }
        }
        //
        ArrayList<ChoiseOfGameArrayList> theimage = new ArrayList<>();
        for(int i = 0; i< debugUrlNumber; i++){
            ChoiseOfGameArrayList createList = new ChoiseOfGameArrayList();
            createList.setImage_title(row1debugGameName[i].toUpperCase(Locale.getDefault()));
            createList.setUrlType(row1debugGameIconType[i]);
            createList.setUrl(row1debugGameIconPath[i]);
            createList.setUrlVideo(row1debugGameVideoPath[i]);
            createList.setVideoCopyright(row1debugGameVideoCopyright[i]);
            theimage.add(createList);
        }
        return theimage;
    }
    /**
     * release the video player
     *
     * @see androidx.fragment.app.Fragment#onDestroy
     * @see ChoiseOfGameRecyclerView#releasePlayer()
     */
    @Override
    public void onDestroy() {
        if(recyclerView!=null)
            recyclerView.releasePlayer();
        super.onDestroy();
    }
    //
}

