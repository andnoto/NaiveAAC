package com.sampietro.NaiveAAC.activities.Game.Game1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.history.History;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * <h1>Game1SecondLevelFragment</h1>
 * <p><b>Game1SecondLevelFragment</b> UI for game1
 * display second level menu
 * the search for words takes place via a two-level menu
 * the first level contains the main research classes such as games, food, family, animals
 * the second level contains the subclasses of the first level for example
 * for the game class subclasses ball, tablet, running, etc.
 * </p>
 *
 * @version     1.3, 05/05/22
 * @see GameFragmentAbstractClass
 * @see Game1Activity
 */
public class Game1SecondLevelFragment extends GameFragmentAbstractClass {
    public ImageButton hearingImageButton;
    //
    public ImageButton listenAgainButton;
    public ImageButton continueGameButton;
    //
    public int leftColumnMenuPhraseNumber;
    public int middleColumnMenuPhraseNumber;
    public int rightColumnMenuPhraseNumber;
    public String leftColumnContent;
    public String leftColumnContentUrlType;
    public String leftColumnContentUrl;
    public String middleColumnContent;
    public String middleColumnContentUrlType;
    public String middleColumnContentUrl;
    public String rightColumnContent;
    public String rightColumnContentUrlType;
    public String rightColumnContentUrl;
    //
    public String [] column1debugWord = new String[32];
    public String [] column1debugUrlType = new String[32];
    public String [] column1debugUrl = new String[32];
    //
    public String [] column2debugWord = new String[32];
    public String [] column2debugUrlType = new String[32];
    public String [] column2debugUrl = new String[32];
    //
    public String [] column3debugWord = new String[32];
    public String [] column3debugUrlType = new String[32];
    public String [] column3debugUrl = new String[32];
    //
    public Integer reminderPhraseCounter = 0;
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
     * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see Game1ArrayList
     * @see #prepareData1
     * @see Game1RecyclerViewAdapter1
     * @see #prepareData2
     * @see Game1RecyclerViewAdapter2
     * @see #prepareData3
     * @see Game1RecyclerViewAdapter3
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_1, container, false);
        //
        hearingImageButton = (ImageButton)rootView.findViewById(R.id.btn_start);
        //
        listenAgainButton =
                (ImageButton)rootView.findViewById(R.id.listenagainbutton);
        continueGameButton =
                (ImageButton)rootView.findViewById(R.id.continuegamebutton);
        hearingImageButton.setImageResource(R.drawable.ic_baseline_hearing_36_red);
        //
        Bundle bundle = this.getArguments();
        //
        if (bundle != null) {
            leftColumnMenuPhraseNumber = bundle.getInt(getString(R.string.left_column_menu_phrase_number));
            middleColumnMenuPhraseNumber = bundle.getInt(getString(R.string.middle_column_menu_phrase_number));
            rightColumnMenuPhraseNumber = bundle.getInt(getString(R.string.right_column_menu_phrase_number));
            leftColumnContent = bundle.getString(getString(R.string.left_column_content));
            leftColumnContentUrlType = bundle.getString(getString(R.string.left_column_content_url_type));
            leftColumnContentUrl = bundle.getString(getString(R.string.left_column_content_url));
            middleColumnContent = bundle.getString(getString(R.string.middle_column_content));
            middleColumnContentUrlType = bundle.getString(getString(R.string.middle_column_content_url_type));
            middleColumnContentUrl = bundle.getString(getString(R.string.middle_column_content_url));
            rightColumnContent = bundle.getString(getString(R.string.right_column_content));
            rightColumnContentUrlType = bundle.getString(getString(R.string.right_column_content_url_type));
            rightColumnContentUrl = bundle.getString(getString(R.string.right_column_content_url));
        }
        //
        if (!leftColumnContent.equals(getString(R.string.nessuno))
                && !middleColumnContent.equals(getString(R.string.nessuno))
                && !rightColumnContent.equals(getString(R.string.nessuno))) {
            hearingImageButton.setVisibility(View.VISIBLE);
            listenAgainButton.setVisibility(View.VISIBLE);
            continueGameButton.setVisibility(View.VISIBLE);
            }
            else
            {
            hearingImageButton.setVisibility(View.INVISIBLE);
            listenAgainButton.setVisibility(View.INVISIBLE);
            continueGameButton.setVisibility(View.VISIBLE);
            }
        //
        RecyclerView recyclerView1 = (RecyclerView)rootView.findViewById(R.id.imagegallery1);
        recyclerView1.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager1 =
                new LinearLayoutManager(ctext);
        recyclerView1.setLayoutManager(layoutManager1);
        ArrayList<Game1ArrayList> createLists1 = prepareData1();
        Game1RecyclerViewAdapter1 adapter1 =
                new Game1RecyclerViewAdapter1(ctext, createLists1);
        recyclerView1.setAdapter(adapter1);
        //
        RecyclerView recyclerView2 = (RecyclerView)rootView.findViewById(R.id.imagegallery2);
        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager2 =
                new LinearLayoutManager(ctext);
        recyclerView2.setLayoutManager(layoutManager2);
        ArrayList<Game1ArrayList> createLists2 = prepareData2();
        Game1RecyclerViewAdapter2 adapter2 =
                new Game1RecyclerViewAdapter2(ctext, createLists2);
        recyclerView2.setAdapter(adapter2);
        //
        RecyclerView recyclerView3 = (RecyclerView)rootView.findViewById(R.id.imagegallery3);
        recyclerView3.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager3 =
                new LinearLayoutManager(ctext);
        recyclerView3.setLayoutManager(layoutManager3);
        ArrayList<Game1ArrayList> createLists3 = prepareData3();
        Game1RecyclerViewAdapter3 adapter3 =
                new Game1RecyclerViewAdapter3(ctext, createLists3);
        recyclerView3.setAdapter(adapter3);
        //
        listener.receiveResultGameFragment(rootView);
        return rootView;
    }
    /**
     * prepare data for the first recyclerview (left recyclerview) using data from the history table
     *
     * @return theimage arraylist<GameFragmentChoiseOfGameArrayList> data for recyclerview
     * @see Game1ArrayList
     * @see History
     */
    private ArrayList<Game1ArrayList> prepareData1(){
        int column1debugUrlNumber = 0;
        //
        if (!leftColumnContent.equals(getString(R.string.nessuno)) && !leftColumnContent.equals(" ") ) {
            column1debugUrlNumber = 1;
            column1debugWord[0] = leftColumnContent;
            column1debugUrlType[0] = leftColumnContentUrlType;
            column1debugUrl[0] = leftColumnContentUrl;
            }
            else {
            //
                if (leftColumnMenuPhraseNumber != 0) {
                    RealmResults<History> results =
                            realm.where(History.class)
                                    .equalTo(getString(R.string.phrase_number),
                                            String.valueOf(leftColumnMenuPhraseNumber)).findAll();
                    int count = results.size();
                    column1debugUrlNumber = count-1;
                    if (count != 0) {
                        int irrh=0;
                        while((irrh < count  )) {
                            History result = results.get(irrh);
                            assert result != null;
                            int wordNumber = result.getWordNumber();
                            //

                            if (wordNumber != 0) {
                                column1debugWord[irrh - 1] = result.getWord();
                                column1debugUrlType[irrh - 1] = result.getUriType();
                                column1debugUrl[irrh - 1] = result.getUri();
                            }
                            irrh++;
                        }
                    }
                }
            }
        //
        ArrayList<Game1ArrayList> theimage = new ArrayList<>();
        for(int i = 0; i< column1debugUrlNumber; i++){
            Game1ArrayList createList = new Game1ArrayList();
            createList.setImage_title(column1debugWord[i]);
            createList.setUrlType(column1debugUrlType[i]);
            createList.setUrl(column1debugUrl[i]);
            theimage.add(createList);
        }
        return theimage;
    }
    /**
     * prepare data for the second recyclerview (middle recyclerview) using data from the history table
     *
     * @return theimage arraylist<GameFragmentChoiseOfGameArrayList> data for recyclerview
     * @see Game1ArrayList
     * @see History
     */
    private ArrayList<Game1ArrayList> prepareData2(){
        int column2debugUrlNumber = 0;
        //
        if (!middleColumnContent.equals(getString(R.string.nessuno))) {
            column2debugUrlNumber = 1;
            column2debugWord[0] = middleColumnContent;
            column2debugUrlType[0] = middleColumnContentUrlType;
            column2debugUrl[0] = middleColumnContentUrl;
        }
        else {
            //
            if (middleColumnMenuPhraseNumber != 0) {
                RealmResults<History> results =
                        realm.where(History.class)
                                .equalTo(getString(R.string.phrase_number),
                                        String.valueOf(middleColumnMenuPhraseNumber)).findAll();
                int count = results.size();
                column2debugUrlNumber = count-1;
                if (count != 0) {
                    int irrh=0;
                    while((irrh < count  )) {
                        History result = results.get(irrh);
                        assert result != null;
                        int wordNumber = result.getWordNumber();
                        //
                        if (wordNumber != 0) {
                            column2debugWord[irrh - 1] = result.getWord();
                            column2debugUrlType[irrh - 1] = result.getUriType();
                            column2debugUrl[irrh - 1] = result.getUri();
                        }
                        irrh++;
                    }
                }
            }
        }
        //
        ArrayList<Game1ArrayList> theimage = new ArrayList<>();
        for(int i = 0; i< column2debugUrlNumber; i++){
            Game1ArrayList createList = new Game1ArrayList();
            createList.setImage_title(column2debugWord[i]);
            createList.setUrlType(column2debugUrlType[i]);
            createList.setUrl(column2debugUrl[i]);
            theimage.add(createList);
        }
        return theimage;
    }
    /**
     * prepare data for the third recyclerview (right recyclerview) using data from the history table
     *
     * @return theimage arraylist<GameFragmentChoiseOfGameArrayList> data for recyclerview
     * @see Game1ArrayList
     * @see History
     */
    private ArrayList<Game1ArrayList> prepareData3(){
        int column3debugUrlNumber = 0;
        //
        if (!rightColumnContent.equals(getString(R.string.nessuno)) && !rightColumnContent.equals(" ")) {
            column3debugUrlNumber = 1;
            column3debugWord[0] = rightColumnContent;
            column3debugUrlType[0] = rightColumnContentUrlType;
            column3debugUrl[0] = rightColumnContentUrl;
        }
        else {
            //
            if (rightColumnMenuPhraseNumber != 0) {
                RealmResults<History> results =
                        realm.where(History.class)
                                .equalTo(getString(R.string.phrase_number),
                                        String.valueOf(rightColumnMenuPhraseNumber)).findAll();
                int count = results.size();
                column3debugUrlNumber = count-1;
                if (count != 0) {
                    int irrh=0;
                    while((irrh < count  )) {
                        History result = results.get(irrh);
                        assert result != null;
                        int wordNumber = result.getWordNumber();
                        //
                        if (wordNumber != 0) {
                            column3debugWord[irrh - 1] = result.getWord();
                            column3debugUrlType[irrh - 1] = result.getUriType();
                            column3debugUrl[irrh - 1] = result.getUri();
                        }
                        irrh++;
                    }
                }
            }
        }
        //
        ArrayList<Game1ArrayList> theimage = new ArrayList<>();
        for(int i = 0; i< column3debugUrlNumber; i++){
            Game1ArrayList createList = new Game1ArrayList();
            createList.setImage_title(column3debugWord[i]);
            createList.setUrlType(column3debugUrlType[i]);
            createList.setUrl(column3debugUrl[i]);
            theimage.add(createList);
        }
        return theimage;
    }
}

