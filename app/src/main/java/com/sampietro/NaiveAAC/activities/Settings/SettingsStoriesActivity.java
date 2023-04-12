package com.sampietro.NaiveAAC.activities.Settings;

import static io.realm.Sort.ASCENDING;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter;
import com.sampietro.NaiveAAC.activities.Graphics.Sounds;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Stories.Stories;
import com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter;
import com.sampietro.NaiveAAC.activities.Stories.StoriesComparator;
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper;
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories;
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * <h1>SettingsStoriesActivity</h1>
 * <p><b>SettingsStoriesActivity</b> app settings.</p>
 * Refer to <a href="https://developer.android.com/guide/fragments/communicate">developer.android.com</a>
 *
 * @version     3.0, 03/12/23
 * @see SettingsFragmentAbstractClass
 * @see com.sampietro.NaiveAAC.activities.WordPairs.WordPairsAdapter
 * @see ListsOfNamesAdapter
 * @see StoriesAdapter
 */
public class SettingsStoriesActivity extends AccountActivityAbstractClass
        implements
        SettingsFragmentAbstractClass.onFragmentEventListenerSettings,
        StoriesAdapter.StoriesAdapterInterface,
        StoriesImageSearchArasaacRecyclerViewAdapterInterface,
        StoriesVideosSearchAdapter.StoriesVideosSearchAdapterInterface,
        StoriesSoundsSearchAdapter.StoriesSoundsSearchAdapterInterface
{
    public String message = "messaggio non formato";
    public TextView textView;
    //
    public FragmentManager fragmentManager;
    //
    private VoiceToBeRecordedInStories voiceToBeRecordedInStories;
    private VoiceToBeRecordedInStoriesViewModel viewModel;
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
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        // In the Activity#onCreate make the only setItem
        voiceToBeRecordedInStories = new VoiceToBeRecordedInStories();
        viewModel = new ViewModelProvider(this).get(VoiceToBeRecordedInStoriesViewModel.class);
        viewModel.setItem(voiceToBeRecordedInStories);
        voiceToBeRecordedInStories.setStory(getString(R.string.nome_storia));
        voiceToBeRecordedInStories.setPhraseNumber(0);
        clearFieldsOfViewmodelDataClass();
        //
        setActivityResultLauncher();
        //
        if (savedInstanceState == null)
        {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment))
                    .add(R.id.settings_container, new StoriesFragment(), "StoriesFragment")
                    .commit();
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm= Realm.getDefaultInstance();
    }
    //
    /**
     * setting callbacks to search for images and videos via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/4147849/muntashir-akon">Muntashir Akon</a>
     * <p>
     * and to <a href="https://stackoverflow.com/questions/7620401/how-to-convert-image-file-data-in-a-byte-array-to-a-bitmap">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/840861/uttam">Uttam</a>
     *
     * @see AccountActivityAbstractClass#getFilePath
     */
    public void setActivityResultLauncher() {
        //
        imageSearchStoriesActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = result.getData();
                            // doSomeOperations();
                            uri = null;
                            filePath = getString(R.string.non_trovato);
                            //
                            if (resultData != null) {
                                uri = Objects.requireNonNull(resultData).getData();
                                //
                                try {
                                    filePath = getFilePath(context, uri);
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                //
                                //
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    final int takeFlags = resultData.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                }
                                //
                                Bitmap bitmap = null;
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byteArray = stream.toByteArray();
                                //
//                                Viewmodel
//                                In the activity, sometimes it is called observe, other times it is limited to performing set directly
//                                (maybe it is not necessary to call observe)
                                voiceToBeRecordedInStories.setUriType("S");
                                voiceToBeRecordedInStories.setUri(filePath);
                            }
                        }
                    }
                });
    }
    /**
     * Called when the user taps the image search button.
     *
     * @param v view of tapped button
     */
    public void imageSearch(View v)
    {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");
        //
        switch(v.getId()){
            case R.id.buttonimagesearchstories:
                /*  the instructions of the button */
                EditText textWord1=(EditText) findViewById(R.id.keywordstorytoadd);
                EditText textWord2=(EditText) findViewById(R.id.phrasenumbertoadd);
                EditText textWord3=(EditText) findViewById(R.id.wordnumbertoadd);
                EditText textWord4=(EditText) findViewById(R.id.wordtoadd);
                // Viewmodel
                // In the activity, sometimes it is called observe, other times it is limited to performing set directly
                // (maybe it is not necessary to call observe)
                voiceToBeRecordedInStories.setStory (textWord1.getText().toString().toLowerCase());
                if ((textWord2 != null) && (!textWord2.getText().toString().equals("")))
                    voiceToBeRecordedInStories.setPhraseNumber (Integer.parseInt(textWord2.getText().toString()));
                if ((textWord3 != null) && (!textWord3.getText().toString().equals("")))
                    voiceToBeRecordedInStories.setWordNumber (Integer.parseInt(textWord3.getText().toString()));
                if (textWord4 != null)
                    voiceToBeRecordedInStories.setWord (textWord4.getText().toString());
                //
                imageSearchStoriesActivityResultLauncher.launch(intent);
                break;
        }
    }
    //
    /**
     * Called when the user taps the video search button.
     *
     * @param v view of tapped button
     */
    public void videoSearch(View v)
    {
        EditText textWord1=(EditText) findViewById(R.id.keywordstorytoadd);
        EditText textWord2=(EditText) findViewById(R.id.phrasenumbertoadd);
        EditText textWord3=(EditText) findViewById(R.id.wordnumbertoadd);
        EditText textWord4=(EditText) findViewById(R.id.wordtoadd);
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories.setStory (textWord1.getText().toString().toLowerCase());
        if ((textWord2 != null) && (!textWord2.getText().toString().equals("")))
            voiceToBeRecordedInStories.setPhraseNumber (Integer.parseInt(textWord2.getText().toString()));
        if ((textWord3 != null) && (!textWord3.getText().toString().equals("")))
            voiceToBeRecordedInStories.setWordNumber (Integer.parseInt(textWord3.getText().toString()));
        if (textWord4 != null)
            voiceToBeRecordedInStories.setWord (textWord4.getText().toString());
        //
        StoriesVideosSearchFragment frag= new StoriesVideosSearchFragment();
        //
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    //
    /**
     * Called when the user taps the sounds search button.
     *
     * @param v view of tapped button
     */
    public void soundsSearch(View v)
    {
        EditText textWord1=(EditText) findViewById(R.id.keywordstorytoadd);
        EditText textWord2=(EditText) findViewById(R.id.phrasenumbertoadd);
        EditText textWord3=(EditText) findViewById(R.id.wordnumbertoadd);
        EditText textWord4=(EditText) findViewById(R.id.wordtoadd);
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories.setStory (textWord1.getText().toString().toLowerCase());
        if ((textWord2 != null) && (!textWord2.getText().toString().equals("")))
            voiceToBeRecordedInStories.setPhraseNumber (Integer.parseInt(textWord2.getText().toString()));
        if ((textWord3 != null) && (!textWord3.getText().toString().equals("")))
            voiceToBeRecordedInStories.setWordNumber (Integer.parseInt(textWord3.getText().toString()));
        if (textWord4 != null)
            voiceToBeRecordedInStories.setWord (textWord4.getText().toString());
        //
        StoriesSoundsSearchFragment frag= new StoriesSoundsSearchFragment();
        //
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
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
    /**
     * Called when the user taps the image search Arasaac button from Stories Fragment.
     *
     * @param v view of tapped button
     */
    public void imageSearchArasaac(View v)
    {
        EditText textWord1=(EditText) findViewById(R.id.keywordstorytoadd);
        EditText textWord2=(EditText) findViewById(R.id.phrasenumbertoadd);
        EditText textWord3=(EditText) findViewById(R.id.wordnumbertoadd);
        EditText textWord4=(EditText) findViewById(R.id.wordtoadd);
        if (textWord4 != null)
        {
            //
            // view the image search Arasaac  initializing ImageSearchArasaacFragment (FragmentTransaction
            // switch between Fragments).
            // Viewmodel
            // In the activity, sometimes it is called observe, other times it is limited to performing set directly
            // (maybe it is not necessary to call observe)
            voiceToBeRecordedInStories.setStory (textWord1.getText().toString().toLowerCase());
            if ((textWord2 != null) && (!textWord2.getText().toString().equals("")))
                voiceToBeRecordedInStories.setPhraseNumber (Integer.parseInt(textWord2.getText().toString()));
            if ((textWord3 != null) && (!textWord3.getText().toString().equals("")))
                voiceToBeRecordedInStories.setWordNumber (Integer.parseInt(textWord3.getText().toString()));
            voiceToBeRecordedInStories.setWord (textWord4.getText().toString());
            //
            StoriesImageSearchArasaacFragment frag= new StoriesImageSearchArasaacFragment();
            //
            Bundle bundle = new Bundle();
            bundle.putString("keywordToSearchArasaac", textWord4.getText().toString());
            frag.setArguments(bundle);
            //
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * Called when the user taps the image search Arasaac button from StoriesImagesSearchArasaac Fragment.
     *
     * @param v view of tapped button
     */
    public void arasaacSearch(View v)
    {
        EditText textWord1=(EditText) findViewById(R.id.keywordarasaactosearch);
        if (textWord1 != null)
        {
            // view the image search Arasaac  initializing ImageSearchArasaacFragment (FragmentTransaction
            // switch between Fragments).
            StoriesImageSearchArasaacFragment frag= new StoriesImageSearchArasaacFragment();
            //
            Bundle bundle = new Bundle();
            bundle.putString("keywordToSearchArasaac", textWord1.getText().toString());
            frag.setArguments(bundle);
            //
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * Called when the user taps the image Arasaac .
     *
     * @param view view of the image Arasaac
     * @param word string whit the keyword of the image Arasaac
     * @param url string whit the url of the image Arasaac
     */
    @Override
    public void onItemClick(View view, String word, String url) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem().observe(this, voiceToBeRecordedInStories -> {
            // Perform an action with the latest item data
            voiceToBeRecordedInStories.setUriType("A");
            voiceToBeRecordedInStories.setUri(url);
            //
            StoriesFragment frag= new StoriesFragment();
            //
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        });
    }
    /**
     * Called when the user taps the video .
     *
     * @param videoKey string whit video key in the videos table
     */
    @Override
    public void reloadStoriesFragmentFromVideoSearch(String videoKey) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem().observe(this, voiceToBeRecordedInStories -> {
            voiceToBeRecordedInStories.setVideo(videoKey);
            //
            StoriesFragment frag= new StoriesFragment();
            //
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        });
    }
    /**
     * Called when the user click the radio button sound_replace_TTS.
     * </p>
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     * @see StoriesSoundsSearchFragment
     */
    public void onStoriesSoundsRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_sound_replace_TTS:
                if (checked) {
                    //
                    voiceToBeRecordedInStories.setSoundReplacesTTS("Y");
                }
                break;
            case R.id.radio_sound_does_not_replace_TTS:
                if (checked) {
                    //
                    voiceToBeRecordedInStories.setSoundReplacesTTS("N"); // default
                }
                break;
        }
    }
    /**
     * Called when the user taps the sound.
     *
     * @param position int whit position of sound key in the sounds table
     */
    @Override
    public void reloadStoriesFragmentFromSoundsSearch(int position) {
        RealmResults<Sounds> results = realm.where(Sounds.class).findAll();
        Sounds selectedSound =results.get(position);
        //
        assert selectedSound != null;
        voiceToBeRecordedInStories.setSound(selectedSound.getDescrizione());
        //
        StoriesFragment frag= new StoriesFragment();
        //
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the add button from stories settings.
     * </p>
     * after the checks it adds the piece of story on realm
     * </p>
     * and the activity is notified to view the stories settings.
     *
     * @param v view of tapped button
     * @see StoriesFragment
     * @see Stories
     */
    public void storyToAdd(View v)
    {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem().observe(this, voiceToBeRecordedInStories -> {
            // Perform an action with the latest item data
            voiceToBeRecordedInStories.setStory(getString(R.string.nome_storia));
            voiceToBeRecordedInStories.setPhraseNumber(0);
            //
            realm= Realm.getDefaultInstance();
            //
            EditText textWord1=(EditText) findViewById(R.id.keywordstorytoadd);
            EditText textWord2=(EditText) findViewById(R.id.phrasenumbertoadd);
            EditText textWord3=(EditText) findViewById(R.id.wordnumbertoadd);
            EditText textWord4=(EditText) findViewById(R.id.wordtoadd);
            if ((textWord1 != null) && (!textWord1.getText().toString().equals(""))
                    && (textWord2 != null) && (!textWord2.getText().toString().equals(""))
                    && (textWord3 != null) && (textWord4 != null))
//                  && (voiceToBeRecordedInStories.getUriType() != null) && (voiceToBeRecordedInStories.getUri() != null))
                {
                if ((textWord3.getText().toString().equals("0"))
                        ||     (!(textWord3.getText().toString().equals("0"))
                                 && (voiceToBeRecordedInStories.getUriType() != null) && (voiceToBeRecordedInStories.getUri() != null)))
                    {
                        // cancello frase vecchia
                        // se textWord3 non è vuoto
                        // copio la frase vecchia fino a textWord3
                        // inserisco la nuova parola
                        // copio il resto della frase vecchia e riordino
                        // se textWord3 è vuoto
                        // copio la frase vecchia
                        // inserisco la nuova parola alla fine e riordino
                        //
                        // clear the table
                        // cancello frase vecchia dopo averne salvato copia
                        RealmResults<Stories> resultsStories =
                                realm.where(Stories.class)
                                        .equalTo("story", textWord1.getText().toString().toLowerCase())
                                        .equalTo("phraseNumberInt", Integer.parseInt(textWord2.getText().toString()))
                                        .findAll();
                        int storiesSize = resultsStories.size();
                        //
                        List<Stories> resultsStoriesList = realm.copyFromRealm(resultsStories);
                        //
                        Collections.sort(resultsStoriesList,new StoriesComparator());
                        //
                        realm.beginTransaction();
                        resultsStories.deleteAllFromRealm();
                        realm.commitTransaction();
                        //
                        int currentWordNumber = 0;
                        if (!textWord3.getText().toString().equals(""))
                        {
                            // copio la frase vecchia fino a textWord3
                            int irrh=0;
                            while(irrh < storiesSize)   {
                                Stories resultStories = resultsStoriesList.get(irrh);
                                assert resultStories != null;
                                currentWordNumber = resultStories.getWordNumber();
                                if (currentWordNumber < Integer.parseInt(textWord3.getText().toString()))
                                {
                                    realm.beginTransaction();
                                    realm.copyToRealm(resultStories);
                                    realm.commitTransaction();
                                }
                                else
                                {
                                    break;
                                }
                                irrh++;
                            }
                            // inserisco la nuova parola
                            // registro nuova parola
                            // Note that the realm object was generated with the createObject method
                            // and not with the new operator.
                            // The modification operations will be performed within a Transaction.
                            //
                            realm.beginTransaction();
                            Stories stories = realm.createObject(Stories.class);
                            // set the fields here
                            stories.setStory(textWord1.getText().toString().toLowerCase());
                            stories.setPhraseNumber(Integer.parseInt(textWord2.getText().toString()));
                            stories.setWordNumber(Integer.parseInt(textWord3.getText().toString()));
                            stories.setWord(textWord4.getText().toString());
                            stories.setUriType(voiceToBeRecordedInStories.getUriType());
                            stories.setUri(voiceToBeRecordedInStories.getUri());
                            stories.setVideo(voiceToBeRecordedInStories.getVideo());
                            stories.setSound(voiceToBeRecordedInStories.getSound());
                            stories.setSoundReplacesTTS(voiceToBeRecordedInStories.getSoundReplacesTTS());
                            stories.setFromAssets("");
                            realm.commitTransaction();
                            // copio la frase vecchia
                            while(irrh < storiesSize)   {
                                Stories resultStories = resultsStoriesList.get(irrh);
                                assert resultStories != null;
                                realm.beginTransaction();
                                realm.copyToRealm(resultStories);
                                realm.commitTransaction();
                                irrh++;
                            }
                        }
                        else
                        {
                            // copio la frase vecchia
                            int irrh=0;
                            while(irrh < storiesSize)   {
                                Stories resultStories = resultsStoriesList.get(irrh);
                                assert resultStories != null;
                                currentWordNumber = resultStories.getWordNumber();
                                //
                                realm.beginTransaction();
                                realm.copyToRealm(resultStories);
                                realm.commitTransaction();
                                //
                                irrh++;
                            }
                            // inserisco la nuova parola
                            // registro nuova parola
                            // Note that the realm object was generated with the createObject method
                            // and not with the new operator.
                            // The modification operations will be performed within a Transaction.
                            //
                            realm.beginTransaction();
                            Stories stories = realm.createObject(Stories.class);
                            // set the fields here
                            stories.setStory(textWord1.getText().toString().toLowerCase());
                            stories.setPhraseNumber(Integer.parseInt(textWord2.getText().toString()));
                            stories.setWordNumber(++currentWordNumber);
                            stories.setWord(textWord4.getText().toString());
                            stories.setUriType(voiceToBeRecordedInStories.getUriType());
                            stories.setUri(voiceToBeRecordedInStories.getUri());
                            stories.setVideo(voiceToBeRecordedInStories.getVideo());
                            stories.setSound(voiceToBeRecordedInStories.getSound());
                            stories.setSoundReplacesTTS(voiceToBeRecordedInStories.getSoundReplacesTTS());
                            stories.setFromAssets("");
                            realm.commitTransaction();
                        }
                        // riordino
                        StoriesHelper.renumberAPhraseOfAStory(realm, textWord1.getText().toString().toLowerCase(), Integer.parseInt(textWord2.getText().toString()));
                        StoriesHelper.renumberAStory(realm, textWord1.getText().toString().toLowerCase());
                        // clear fields of viewmodel data class
                        voiceToBeRecordedInStories.setStory(textWord1.getText().toString().toLowerCase());
                        voiceToBeRecordedInStories.setPhraseNumber(Integer.parseInt(textWord2.getText().toString()));
                        clearFieldsOfViewmodelDataClass();
                    }
                    // view the stories settings fragment initializing StoriesListFragment (FragmentTransaction
                    // switch between Fragments).
                    FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                    //
                    StoriesListFragment frag= new StoriesListFragment();
                    //
                    ft.replace(R.id.settings_container, frag);
                    ft.addToBackStack(null);
                    ft.commit();
                }
        });
    }
    /**
     * clear fields of viewmodel data class
     * </p>
     *
     * @see VoiceToBeRecordedInStories
     */
    public void clearFieldsOfViewmodelDataClass() {
        voiceToBeRecordedInStories.setWordNumber(0);
        voiceToBeRecordedInStories.setWord("");
        voiceToBeRecordedInStories.setUriType("");
        voiceToBeRecordedInStories.setUri("");
        voiceToBeRecordedInStories.setAnswerActionType("");
        voiceToBeRecordedInStories.setAnswerAction("");
        voiceToBeRecordedInStories.setVideo("");
        voiceToBeRecordedInStories.setSound("");
        voiceToBeRecordedInStories.setSoundReplacesTTS("");
    }
    /**
     * Called when the user taps the show list button from the contents settings menu.
     * </p>
     * the activity is notified to view the stories list.
     * </p>
     *
     * @param view view of tapped button
     * @see ContentsFragment
     * @see StoriesListFragment
     */
    public void submitStoriesShowList(View view) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories.setStory(getString(R.string.nome_storia));
        voiceToBeRecordedInStories.setPhraseNumber(0);
        // view the stories list  initializing StoriesListFragment (FragmentTransaction
        // switch between Fragments).
        StoriesListFragment frag= new StoriesListFragment();
        //
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the story search button from the stories list.
     * </p>
     * the activity is notified to view the stories list.
     * </p>
     *
     * @param view view of tapped button
     * @see StoriesListFragment
     */
    public void storiesSearch(View view) {
        EditText textWord1=(EditText) rootViewFragment.findViewById(R.id.keywordstorytosearch);
        EditText textWord2=(EditText) rootViewFragment.findViewById(R.id.phrasenumbertosearch);
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories.setStory(textWord1.getText().toString().toLowerCase());
        voiceToBeRecordedInStories.setPhraseNumber(Integer.parseInt(textWord2.getText().toString()));
        //
        StoriesListFragment frag= new StoriesListFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * on callback from StoriesAdapter to this Activity
     * </p>
     * after deleting a piece of story the activity is notified to view the word pairs settings
     * </p>
     *
     * @see StoriesAdapter
     * @see StoriesFragment
     */
    @Override
    public void reloadStoriesFragmentDeleteStories(int position) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem().observe(this, voiceToBeRecordedInStories -> {
                    // Perform an action with the latest item data
            realm= Realm.getDefaultInstance();
            RealmResults<Stories> results = null;
            if ((voiceToBeRecordedInStories.getStory() == null) || (voiceToBeRecordedInStories.getStory().equals(getString(R.string.nome_storia)))) {
                results = realm.where(Stories.class).findAll();
            }
            //
            else
            if (voiceToBeRecordedInStories.getPhraseNumber() == 0)
            {
                results =
                        realm.where(Stories.class)
                                .equalTo("story", voiceToBeRecordedInStories.getStory())
                                .findAll();
            }
            else
            {
                results =
                        realm.where(Stories.class)
                                .equalTo("story", voiceToBeRecordedInStories.getStory())
                                .equalTo("phraseNumberInt", voiceToBeRecordedInStories.getPhraseNumber())
                                .findAll();
            }
            //
            String[] mStrings1 = {"story","phraseNumberInt", "wordNumberInt" };
            Sort[] mStrings2 = {ASCENDING,ASCENDING, ASCENDING };
            results = results.sort(mStrings1, mStrings2 );
            //
            Stories daCancellare=results.get(position);
            //
            assert daCancellare != null;
            String daCancellareStory = daCancellare.getStory();
            int daCancellarePhraseNumber = daCancellare.getPhraseNumber();
            int daCancellareWordNumber = daCancellare.getWordNumber();
            //
            voiceToBeRecordedInStories.setStory(daCancellareStory);
            voiceToBeRecordedInStories.setPhraseNumber(daCancellarePhraseNumber);
            // delete
            realm.beginTransaction();
            daCancellare.deleteFromRealm();
            realm.commitTransaction();
            //
            StoriesHelper.renumberAPhraseOfAStory(realm, daCancellareStory, daCancellarePhraseNumber);
            StoriesHelper.renumberAStory(realm, daCancellareStory);
            //
            StoriesListFragment frag= new StoriesListFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();

        });
    }
    /**
     * on callback from StoriesAdapter to this Activity
     * </p>
     * for editing a piece of story the activity is notified to view the word pairs settings
     * </p>
     *
     * @see StoriesAdapter
     * @see StoriesFragment
     */
    @Override
    public void reloadStoriesFragmentForEditing(int position) {
        // view the stories settings fragment initializing StoriesFragment (FragmentTransaction
        // switch between Fragments).
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem().observe(this, voiceToBeRecordedInStories -> {
            // Perform an action with the latest item data
            realm= Realm.getDefaultInstance();
            RealmResults<Stories> results = null;
            if ((voiceToBeRecordedInStories.getStory() == null) || (voiceToBeRecordedInStories.getStory().equals(getString(R.string.nome_storia)))) {
                results = realm.where(Stories.class).findAll();
            }
            //
            else
            if (voiceToBeRecordedInStories.getPhraseNumber() == 0)
            {
                results =
                        realm.where(Stories.class)
                                .equalTo("story", voiceToBeRecordedInStories.getStory())
                                .findAll();
            }
            else
            {
                results =
                        realm.where(Stories.class)
                                .equalTo("story", voiceToBeRecordedInStories.getStory())
                                .equalTo("phraseNumberInt", voiceToBeRecordedInStories.getPhraseNumber())
                                .findAll();
            }
            //
            String[] mStrings1 = {"story","phraseNumberInt", "wordNumberInt" };
            Sort[] mStrings2 = {ASCENDING,ASCENDING, ASCENDING };
            results = results.sort(mStrings1, mStrings2 );
            //
            StoriesFragment frag= new StoriesFragment();
            //
            Stories daModificare=results.get(position);
            assert daModificare != null;
            //
            String daModificareStory = daModificare.getStory();
            int daModificarePhraseNumber = daModificare.getPhraseNumber();
            int daModificareWordNumber = daModificare.getWordNumber();
            //
            voiceToBeRecordedInStories.setStory(daModificare.getStory());
            voiceToBeRecordedInStories.setPhraseNumber(daModificare.getPhraseNumber());
            voiceToBeRecordedInStories.setWordNumber(daModificare.getWordNumber());
            voiceToBeRecordedInStories.setWord(daModificare.getWord());
            voiceToBeRecordedInStories.setUriType(daModificare.getUriType());
            voiceToBeRecordedInStories.setUri(daModificare.getUri());
            voiceToBeRecordedInStories.setAnswerActionType(daModificare.getanswerActionType());
            voiceToBeRecordedInStories.setAnswerAction(daModificare.getAnswerAction());
            voiceToBeRecordedInStories.setVideo(daModificare.getVideo());
            voiceToBeRecordedInStories.setSound(daModificare.getSound());
            voiceToBeRecordedInStories.setSoundReplacesTTS(daModificare.getSoundReplacesTTS());
            voiceToBeRecordedInStories.setFromAssets(daModificare.getFromAssets());
            // delete
            realm.beginTransaction();
            daModificare.deleteFromRealm();
            realm.commitTransaction();
            //
            StoriesHelper.renumberAPhraseOfAStory(realm, daModificareStory, daModificarePhraseNumber);
            StoriesHelper.renumberAStory(realm, daModificareStory);
            //
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        });
    }
    /**
     * on callback from StoriesAdapter to this Activity
     * </p>
     * for insertion a piece of story the activity is notified to view the word pairs settings
     * </p>
     *
     * @see StoriesAdapter
     * @see StoriesFragment
     */
    @Override
    public void reloadStoriesFragmentForInsertion(int position) {
        // view the stories settings fragment initializing StoriesFragment (FragmentTransaction
        // switch between Fragments).
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem().observe(this, voiceToBeRecordedInStories -> {
            // Perform an action with the latest item data
            realm= Realm.getDefaultInstance();
            RealmResults<Stories> results = null;
            if ((voiceToBeRecordedInStories.getStory() == null) || (voiceToBeRecordedInStories.getStory().equals(getString(R.string.nome_storia)))) {
                results = realm.where(Stories.class).findAll();
            }
            //
            else
            if (voiceToBeRecordedInStories.getPhraseNumber() == 0)
            {
                results =
                        realm.where(Stories.class)
                                .equalTo("story", voiceToBeRecordedInStories.getStory())
                                .findAll();
            }
            else
            {
                results =
                        realm.where(Stories.class)
                                .equalTo("story", voiceToBeRecordedInStories.getStory())
                                .equalTo("phraseNumberInt", voiceToBeRecordedInStories.getPhraseNumber())
                                .findAll();
            }
            //
            String[] mStrings1 = {"story","phraseNumberInt", "wordNumberInt" };
            Sort[] mStrings2 = {ASCENDING,ASCENDING, ASCENDING };
            results = results.sort(mStrings1, mStrings2 );
            //
            StoriesFragment frag= new StoriesFragment();
            //
            Stories daInserirePrimaDi=results.get(position);
            assert daInserirePrimaDi != null;
            //
            voiceToBeRecordedInStories.setStory(daInserirePrimaDi.getStory());
            voiceToBeRecordedInStories.setPhraseNumber(daInserirePrimaDi.getPhraseNumber());
            voiceToBeRecordedInStories.setWordNumber(daInserirePrimaDi.getWordNumber());
            //
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        });
    }
}