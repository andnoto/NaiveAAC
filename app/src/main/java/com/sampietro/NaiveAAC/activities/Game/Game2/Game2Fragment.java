package com.sampietro.NaiveAAC.activities.Game.Game2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.history.History;

import java.util.ArrayList;

import io.realm.RealmResults;


/**
 * <h1>Game2Fragment</h1>
 * <p><b>GameFragment</b> UI for game2
 * </p>
 *
 * @version     1.4, 19/05/22
 * @see GameFragmentAbstractClass
 * @see Game2Activity
 */
public class Game2Fragment extends GameFragmentAbstractClass {
    public ImageButton hearingImageButton;
    public EditText sentenceToAdd;
    //
    public int sharedLastPhraseNumber;
    //
    public String [] row1debugWord = new String[32];
    public String [] row1debugUrlType = new String[32];
    public String [] row1debugUrl = new String[32];
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
     * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see Game2ArrayList
     * @see #prepareData1
     * @see Game2RecyclerViewAdapter1
     * @see #prepareData2
     * @see Game2RecyclerViewAdapter2
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_2, container, false);
        //
        hearingImageButton = (ImageButton)rootView.findViewById(R.id.btn_start);
        hearingImageButton.setImageResource(R.drawable.ic_baseline_hearing_36_red); //set the image programmatically
        //
        sentenceToAdd = (EditText)rootView.findViewById(R.id.sentencetoadd);
        //
        Bundle bundle = this.getArguments();
        sharedLastPhraseNumber = 0;
        if (bundle != null) {
            sharedLastPhraseNumber = bundle.getInt(getString(R.string.last_phrase_number));
            sentenceToAdd.setText(bundle.getString("eText"));
        }
        //
        RecyclerView recyclerView1 = (RecyclerView)rootView.findViewById(R.id.imagegallery1);
        recyclerView1.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager1 =
                new LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager1);
        ArrayList<Game2ArrayList> createLists1 = prepareData1();
        Game2RecyclerViewAdapter1 adapter1 =
                new Game2RecyclerViewAdapter1(ctext, createLists1);
        recyclerView1.setAdapter(adapter1);
        //
        RecyclerView recyclerView2 = (RecyclerView)rootView.findViewById(R.id.imagegallery2);
        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager2 =
                new LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager2);
        ArrayList<Game2ArrayList> createLists2 = prepareData2();
        Game2RecyclerViewAdapter2 adapter2 =
                new Game2RecyclerViewAdapter2(ctext, createLists2);
        recyclerView2.setAdapter(adapter2);
        //
        listener.receiveResultGameFragment(rootView);
        return rootView;
    }
    //
    /**
     * prepare data for the first recyclerview (first recyclerview from above) using data from the history table
     *
     * @return theimage arraylist<Game2ArrayList> data for recyclerview
     * @see Game2ArrayList
     * @see History
     */
    private ArrayList<Game2ArrayList> prepareData1(){
        int row1debugUrlNumber = 0;
        //
        RealmResults<History> results =
                realm.where(History.class).equalTo(getString(R.string.phrase_number), String.valueOf(sharedLastPhraseNumber)).findAll();
        int count = results.size();
        row1debugUrlNumber = count-1;
        if (count != 0) {
            int irrh=0;
            while(irrh < count  && irrh < 33) {
                History result = results.get(irrh);
                assert result != null;
                int wordNumber = result.getWordNumber();
                //
                if (wordNumber != 0) {
                    row1debugWord[irrh - 1] = result.getWord();
                    row1debugUrlType[irrh - 1] = result.getUriType();
                    row1debugUrl[irrh - 1] = result.getUri();
                }
                irrh++;
            }
        }
        //
        ArrayList<Game2ArrayList> theimage = new ArrayList<>();
        for(int i = 0; i< row1debugUrlNumber; i++){
            Game2ArrayList createList = new Game2ArrayList();
            createList.setImage_title(row1debugWord[i]);
            createList.setUrlType(row1debugUrlType[i]);
            createList.setUrl(row1debugUrl[i]);
            theimage.add(createList);
        }
        return theimage;
    }
    //
    /**
     * prepare data for the second recyclerview (last recyclerview from above) using data from the history table
     *
     * @return theimage arraylist<Game2ArrayList> data for recyclerview
     * @see Game2ArrayList
     * @see History
     */
    private ArrayList<Game2ArrayList> prepareData2(){
        int row1debugUrlNumber = 0;
        //
        RealmResults<History> results =
                realm.where(History.class).equalTo(getString(R.string.phrase_number), String.valueOf(sharedLastPhraseNumber-1)).findAll();
        int count = results.size();
        row1debugUrlNumber = count-1;
        if (count != 0) {
            int irrh=0;
            while(irrh < count  && irrh < 33) {
                History result = results.get(irrh);
                assert result != null;
                int wordNumber = result.getWordNumber();
                //
                if (wordNumber != 0) {
                    row1debugWord[irrh - 1] = result.getWord();
                    row1debugUrlType[irrh - 1] = result.getUriType();
                    row1debugUrl[irrh - 1] = result.getUri();
                }
                irrh++;
            }
        }
        //
        ArrayList<Game2ArrayList> theimage = new ArrayList<>();
        for(int i = 0; i< row1debugUrlNumber; i++){
            Game2ArrayList createList = new Game2ArrayList();
            createList.setImage_title(row1debugWord[i]);
            createList.setUrlType(row1debugUrlType[i]);
            createList.setUrl(row1debugUrl[i]);
            theimage.add(createList);
        }
        return theimage;
    }
//
}
