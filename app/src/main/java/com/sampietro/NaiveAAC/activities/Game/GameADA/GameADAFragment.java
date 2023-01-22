package com.sampietro.NaiveAAC.activities.Game.GameADA;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2ArrayList;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.history.History;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;


public class GameADAFragment extends GameFragmentAbstractClass {
    public ImageButton hearingImageButton;
    //
    public int sharedLastPhraseNumber;
    //
    public int wordToDisplayIndex = 0;
    //
    public String [] row1debugWord = new String[32];
    public String [] row1debugUrlType = new String[32];
    public String [] row1debugUrl = new String[32];
//    public String question;
    public String sentence;
    //
    public GameADAOnFragmentEventListener listenerADA=null;
    //
    public boolean ttsEnabled = true;
    /**
     * listener setting for game activities callbacks , context annotation, realm get default instance
     * and get print permissions
     *
     * @see Fragment#onAttach
     */
    @Override
    public void onAttach (Context context)
    {
        super.onAttach(context);
        Activity activity = (Activity) context;
        // listener=(OnFragmentEventListenerGame) activity;
        //
        listenerADA=(GameADAOnFragmentEventListener) activity;
        //
        ctext = context;
        // REALM
        realm= Realm.getDefaultInstance();
        // if is print permitted then preference_PrintPermissions = Y
        sharedPref = ctext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        preference_PrintPermissions =
                sharedPref.getString ("preference_PrintPermissions", "DEFAULT");
    }
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
     * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
     *
     * @see Fragment#onCreateView
     * @see Game2ArrayList
     * @see #prepareData1
     * @see GameADARecyclerViewAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_ada_recycler_view, container, false);
        //
        hearingImageButton = (ImageButton)rootView.findViewById(R.id.btn_start);
        hearingImageButton.setImageResource(R.drawable.ic_baseline_hearing_36_red); //set the image programmatically
        //
        Bundle bundle = this.getArguments();
        sharedLastPhraseNumber = 0;
        if (bundle != null) {
            sharedLastPhraseNumber = bundle.getInt("LAST PHRASE NUMBER");
            wordToDisplayIndex = bundle.getInt("WORD TO DISPLAY INDEX");
            ttsEnabled = bundle.getBoolean("TTS ENABLED");
        }
        //
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Game2ArrayList> createLists = prepareData1();
        GameADARecyclerViewAdapter adapter =
                new GameADARecyclerViewAdapter(ctext, realm, createLists);
        recyclerView.setAdapter(adapter);
        //
        if (!(wordToDisplayIndex == 0))
            recyclerView.getLayoutManager().scrollToPosition(wordToDisplayIndex);
        // TTS
        if (ttsEnabled) {
            tTS1=new TextToSpeech(ctext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        if (!tTS1.isSpeaking()) {
                            tTS1.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        else
                        { Toast.makeText(ctext, "is speaking", Toast.LENGTH_SHORT).show(); }
                    }
                    else
                    {
                        Toast.makeText(ctext, status,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //
        // listener.receiveResultGameFragment(rootView);
        //
        listenerADA.receiveResultGameFragment(rootView, tTS1, createLists);
        //
        return rootView;
    }
    //
    /**
     * prepare data for the recyclerview using data from the history table
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
        if (count != 0) {
            int irrh=0;
            while(irrh < count  && irrh < 33) {
                History result = results.get(irrh);
                assert result != null;
                int wordNumber = result.getWordNumber();
                //
                if ((wordNumber != 0) && (wordNumber != 99)
                && (wordNumber != 999) && (wordNumber != 9999))
                {
                    if (preference_TitleWritingType.equals("uppercase"))
                        row1debugWord[irrh - 1] = result.getWord().toUpperCase(Locale.getDefault());
                        else
                        row1debugWord[irrh - 1] = result.getWord().toLowerCase();
                    //
                    row1debugUrlType[irrh - 1] = result.getUriType();
                    row1debugUrl[irrh - 1] = result.getUri();
                    row1debugUrlNumber++;
                }
                if (wordNumber == 0)
                {
                    sentence = result.getWord();
                }
                // nel caso di domanda con attesa risposta
                // inibisco il bottone forward
                if (wordNumber == 99)
                {
                    ImageButton forwardImageButton =
                    (ImageButton)rootView.findViewById(R.id.continuegameadabutton);
                    forwardImageButton.setVisibility(View.INVISIBLE);
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

