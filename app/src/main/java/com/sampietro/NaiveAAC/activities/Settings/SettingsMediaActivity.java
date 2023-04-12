package com.sampietro.NaiveAAC.activities.Settings;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Graphics.Images;
import com.sampietro.NaiveAAC.activities.Graphics.ImagesAdapter;
import com.sampietro.NaiveAAC.activities.Graphics.Sounds;
import com.sampietro.NaiveAAC.activities.Graphics.SoundsAdapter;
import com.sampietro.NaiveAAC.activities.Graphics.Videos;
import com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

import java.net.URISyntaxException;

import io.realm.Realm;

/**
 * <h1>SettingsMediaActivity</h1>
 * <p><b>SettingsMediaActivity</b> app media settings.</p>
 *
 * @version     3.0, 03/12/23
 * @see AccountActivityAbstractClass
 * @see SettingsFragmentAbstractClass
 * @see ImagesAdapter
 * @see VideosAdapter
 * @see SoundsAdapter
 */
public class SettingsMediaActivity extends AccountActivityAbstractClass
        implements
        ImagesAdapter.ImagesAdapterInterface,
        VideosAdapter.VideosAdapterInterface,
        SoundsAdapter.SoundsAdapterInterface,
        SettingsFragmentAbstractClass.onFragmentEventListenerSettings
{
    public String message = "messaggio non formato";
    public TextView textView;
    //
    private static final String TAGPERMISSION = "Permission";
    //
    public FragmentManager fragmentManager;
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see #setActivityResultLauncher
     * @see ActionbarFragment
     * @see VerifyFragment
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
                    .add(R.id.settings_container, new ChoiseOfMediaToSetFragment(), "ChoiseOfMediaToSetFragment")
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
     /**
     * Called when the user taps the video button from the media settings menu.
     * </p>
     * the activity is notified to view the videos settings.
     * </p>
     *
     * @param view view of tapped button
     * @see ChoiseOfMediaToSetFragment
     * @see VideosFragment
     */
    public void submitVideo(View view) {
        // view the videos settings fragment initializing VideosFragment (FragmentTransaction
        // switch between Fragments).
        VideosFragment frag= new VideosFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.uri), getString(R.string.none));
        frag.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the search video button from the video settings menu.
     * </p>
     *
     * @param v view of tapped button
     * @see VideosFragment
     * @see #setActivityResultLauncher
     */
    public void videoSearch(View v)
    {
        // video search
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
        intent.setType("video/*");
        videoSearchActivityResultLauncher.launch(intent);
    }
    /**
     * Called when the user taps the add video button from the video settings.
     * </p>
     * after the checks it adds the references of the video on realm
     * </p>
     * and the activity is notified to view the updated video settings.
     *
     * @param v view of tapped button
     * @see VideosFragment
     * @see Videos
     */
    public void videoAdd(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText vidD=(EditText) findViewById(R.id.videoDescription);
        if (vidD.length()>0 && stringUri != null)
        {
            uri = Uri.parse(stringUri);
            try {
                filePath = getFilePath(this, uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            Videos videos = realm.createObject(Videos.class);
            videos.setDescrizione(vidD.getText().toString());
            videos.setUri(filePath);
            videos.setFromAssets("");
            realm.commitTransaction();
            // view the videos settings fragment initializing VideosFragment (FragmentTransaction
            // switch between Fragments).
            VideosFragment frag= new VideosFragment();
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.uri), getString(R.string.none));
            frag.setArguments(bundle);
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * on callback from VideosAdapter to this Activity
     * </p>
     * after deleting references to a video the activity is notified to view the updated video settings
     * </p>
     *
     * @see VideosAdapter
     * @see VideosFragment
     */
    @Override
    public void reloadVideosFragment() {
        // view the videos settings fragment initializing VideosFragment (FragmentTransaction
        // switch between Fragments).
        VideosFragment frag= new VideosFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.uri), getString(R.string.none));
        frag.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the images button from the media settings menu.
     * </p>
     * the activity is notified to view the images settings.
     * </p>
     *
     * @param view view of tapped button
     * @see ChoiseOfMediaToSetFragment
     * @see ImagesFragment
     */
    public void submitImages(View view) {
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        ImagesFragment frag= new ImagesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the add image button from the image settings.
     * </p>
     * after the checks it adds the references of the image on realm
     * </p>
     * and the activity is notified to view the updated image settings.
     *
     * @param v view of tapped button
     * @see ImagesFragment
     * @see Images
     */
    public void imageAdd(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText immD=(EditText) findViewById(R.id.imageDescription);
        if (immD.length()>0 && !filePath.equals(getString(R.string.non_trovato)))
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            Images i = realm.createObject(Images.class);
            i.setDescrizione(immD.getText().toString());
            i.setUri(filePath);
            i.setFromAssets("");
            realm.commitTransaction();
            // view the images settings fragment initializing ImagesFragment (FragmentTransaction
            // switch between Fragments).
            ImagesFragment frag= new ImagesFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * on callback from ImagesAdapter to this Activity
     * </p>
     * after deleting references to a image the activity is notified to view the updated image settings
     * </p>
     *
     * @see ImagesAdapter
     * @see ImagesFragment
     */
    @Override
    public void reloadImagesFragment() {
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        ImagesFragment frag= new ImagesFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the sounds button from the media settings menu.
     * </p>
     * the activity is notified to view the sounds settings.
     * </p>
     *
     * @param view view of tapped button
     * @see ChoiseOfMediaToSetFragment
     * @see SoundsFragment
     */
    public void submitSounds(View view) {
        // view the sounds settings fragment initializing SoundsFragment (FragmentTransaction
        // switch between Fragments).
        SoundsFragment frag= new SoundsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps the search sound button from the sound settings menu.
     * </p>
     *
     * @param v view of tapped button
     * @see SoundsFragment
     * @see #setActivityResultLauncher
     */
    public void soundSearch(View v)
    {
        // sound search
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
        intent.setType("audio/*");
        soundSearchActivityResultLauncher.launch(intent);
    }
    /**
     * Called when the user taps the listen sound button from the sound settings menu.
     * </p>
     * Refer to <a href="https://stackoverflow.com/questions/24104506/android-code-to-select-audio-mp3-and-play-it">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/5160077/ali-sherafat">Ali Sherafat</a>
     *
     * @param v view of tapped button
     * @see SoundsFragment
     */
    public void listenSoundButton(View v)
    {
        //
        try {
            // play audio file using MediaPlayer
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Called when the user taps the add sound button from the sounds settings.
     * </p>
     * after the checks it adds the references of the sound on realm
     * </p>
     * and the activity is notified to view the updated sound settings.
     *
     * @param v view of tapped button
     * @see SoundsFragment
     * @see Sounds
     */
    public void soundAdd(View v)
    {
        realm= Realm.getDefaultInstance();
        //
        EditText immD=(EditText) findViewById(R.id.soundDescription);
        if (immD.length()>0 && !filePath.equals(getString(R.string.non_trovato)))
        {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction();
            Sounds s = realm.createObject(Sounds.class);
            s.setDescrizione(immD.getText().toString());
            s.setUri(filePath);
            s.setFromAssets("");
            realm.commitTransaction();
            // view the sounds settings fragment initializing SoundsFragment (FragmentTransaction
            // switch between Fragments).
            SoundsFragment frag= new SoundsFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, frag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    /**
     * on callback from SoundsAdapter to this Activity
     * </p>
     * after deleting references to a sound the activity is notified to view the updated sound settings
     * </p>
     *
     * @see SoundsAdapter
     * @see SoundsFragment
     */
    @Override
    public void reloadSoundsFragment() {
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        SoundsFragment frag= new SoundsFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
}
