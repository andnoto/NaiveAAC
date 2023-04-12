package com.sampietro.NaiveAAC.activities.Game.GameADA;

import static android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchNegationAdverb;
import static com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Utils.CenterVideoView;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper;
import com.sampietro.NaiveAAC.activities.Graphics.Sounds;
import com.sampietro.NaiveAAC.activities.Graphics.Videos;
import com.sampietro.NaiveAAC.activities.Stories.Stories;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>GameADAViewPagerFragment</h1>
 * <p><b>GameADAViewPagerFragment</b> UI for GameADAViewPager
 *
 * </p>
 *
 * @version     3.0, 03/12/23
 * @see GameFragmentAbstractClass
 * @see GameADAViewPagerActivity
 */
public class GameADAViewPagerFragment extends GameFragmentAbstractClass {
    public String sharedStory;
    public RealmResults<Stories> resultsStories;
    public Stories wordToDisplay;
    public int phraseToDisplayIndex;
    public int wordToDisplayIndex;
    //
    public Activity activity;
    //
    private ImageView image;
    private FrameLayout frameLayout;
    private CenterVideoView centerVideoView;
    private boolean isVideoViewAdded;
    private MediaPlayer soundMediaPlayer;
    //
    public GameADAViewPagerOnFragmentEventListener gameADAViewPagerOnFragmentEventlistener=null;
    public GameADAViewPagerOnFragmentSoundMediaPlayerListener gameADAViewPagerOnFragmentSoundMediaPlayerlistener=null;
    public GameADAViewPagerMediaContainerOnClickListener media_containerOnClickListener=null;
    FrameLayout media_container;
    //
    private String gameUseVideoAndSound;
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
        activity = (Activity) context;
        gameADAViewPagerOnFragmentEventlistener=(GameADAViewPagerOnFragmentEventListener) activity;
        gameADAViewPagerOnFragmentSoundMediaPlayerlistener=(GameADAViewPagerOnFragmentSoundMediaPlayerListener) activity;
        media_containerOnClickListener=(GameADAViewPagerMediaContainerOnClickListener) activity;
        //
        ctext = context;
        // REALM
        realm= Realm.getDefaultInstance();
        // if is print permitted then preference_PrintPermissions = Y
        sharedPref = ctext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        preference_PrintPermissions =
                sharedPref.getString ("preference_PrintPermissions", "DEFAULT");
        preference_TitleWritingType =
                sharedPref.getString ("preference_title_writing_type", "uppercase");
        //
        /*
        ADAPTED FOR VIDEO AND SOUND
         */
//        soundMediaPlayer = new MediaPlayer();
        //
        centerVideoView = new CenterVideoView(context);
        centerVideoView.setOnInfoListener (new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                if (i == MEDIA_INFO_VIDEO_RENDERING_START)
                {
                    centerVideoView.setBackgroundResource(0);
                }
                return false;
            }
        });
    }
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
     * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
     *
     * @see Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_ada_viewpager_content, container, false);
        return rootView;
    }
    //
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
     * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
     * Refer to <a href="https://stackoverflow.com/questions/70486297/how-to-recreate-or-refresh-fragment-while-swiping-viewpager2-tabs">stackoverflow.com</a>
     * By <a href="https://stackoverflow.com/users/11950155/nimaazhd">NimaAzhd</a>
     * @see Fragment#onResume
     * @see #addImage
     */
    @Override
    public void onResume() {
        super.onResume();
        //
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sharedStory = bundle.getString("STORY TO DISPLAY");
//            phraseToDisplayIndex = bundle.getInt("PHRASE TO DISPLAY INDEX");
            wordToDisplayIndex = bundle.getInt("WORD TO DISPLAY INDEX");
            gameUseVideoAndSound = bundle.getString("GAME USE VIDEO AND SOUND");
            //
            resultsStories =
                    realm.where(Stories.class)
                            .beginGroup()
                            .equalTo("story", sharedStory)
                            .equalTo("wordNumberIntInTheStory", wordToDisplayIndex+1)
//                            .equalTo("phraseNumberInt", phraseToDisplayIndex)
//                            .notEqualTo("wordNumberInt", 0)
//                            .lessThan("wordNumberInt", 99)
                            .endGroup()
                            .findAll();
            //
            //
//            resultsStories = resultsStories.sort("wordNumberInt");
            //
            wordToDisplay = resultsStories.get(0);
            //
            TextView textTitleImage = (TextView) rootView.findViewById(R.id.titleimage);
            textTitleImage.setText(wordToDisplay.getWord().toUpperCase(Locale.getDefault()));
            Log.e("TAGWORDTODISPLAY", wordToDisplay.getWord());
            // ricerca immagine
            ImageView imageGameImage = rootView.findViewById(R.id.gameimage);
            imageGameImage.setContentDescription(wordToDisplay.getWord().toUpperCase(Locale.getDefault()));
            addImage(wordToDisplay.getUriType(), wordToDisplay.getUri(), imageGameImage, 200, 200);
            // search for negation adverbs
            ImageView imageGameImage2 = rootView.findViewById(R.id.gameimage2);
            imageGameImage2.setVisibility(INVISIBLE);
            String negationAdverbImageToSearchFor = searchNegationAdverb(wordToDisplay.getWord().toLowerCase(), realm);
            if (!(negationAdverbImageToSearchFor.equals("non trovato"))) {
                // INTERNAL MEMORY IMAGE SEARCH
                String uriToSearch = searchUri(realm, negationAdverbImageToSearchFor);
                // imageGameImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                addImage("S", uriToSearch, imageGameImage2,200, 200);
                imageGameImage2.setVisibility(VISIBLE);
            }
            // TTS
            if (!Objects.equals(wordToDisplay.getSoundReplacesTTS(), "Y"))
            {
                tTS1 = new TextToSpeech(ctext, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            tTS1.speak(wordToDisplay.getWord(), TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                        } else {
                            Toast.makeText(ctext, status, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        /*
        ADAPTED FOR VIDEO AND SOUND
         */
        if (centerVideoView == null) {
            return;
        }
        // remove old video views
        removeVideoView(centerVideoView);
        //
        RealmResults<Sounds> resultsSounds =
                realm.where(Sounds.class).equalTo("descrizione", wordToDisplay.getSound()).findAll();
        String soundPath = null;
        if (resultsSounds.size() != 0) {
            soundPath = resultsSounds.get(0).getUri();
        }
        //
        if ((soundPath != null) && (Objects.equals(gameUseVideoAndSound, "Y"))) {
            if (!soundPath.equals(ctext.getString(R.string.non_trovato)))  {
                // play audio file using MediaPlayer
                try {
                    soundMediaPlayer = new MediaPlayer();
                    soundMediaPlayer.setDataSource(soundPath);
                    soundMediaPlayer.prepare();
                    soundMediaPlayer.start();
                    gameADAViewPagerOnFragmentSoundMediaPlayerlistener.receiveResultGameFragment(rootView, soundMediaPlayer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //
        image = rootView.findViewById(R.id.gameimage);
        frameLayout = rootView.findViewById(R.id.gameimageFL);
        //
        RealmResults<Videos> resultsVideos =
                realm.where(Videos.class).equalTo("descrizione", wordToDisplay.getVideo()).findAll();
        String videoPath = null;
        if (resultsVideos.size() != 0) {
            videoPath = resultsVideos.get(0).getUri();
        }
        if ((videoPath != null) && (Objects.equals(gameUseVideoAndSound, "Y"))) {
            if (!videoPath.equals(ctext.getString(R.string.non_trovato)))  {
                //
                if(!isVideoViewAdded){
                    addVideoView();
                }
                //
                int imageSize = calculateImageSize();
                centerVideoView.getLayoutParams().height = imageSize;
                centerVideoView.getLayoutParams().width = imageSize;
                //
                centerVideoView.setBackgroundResource(R.drawable.white_background);
                centerVideoView.setVideoPath(videoPath);
                centerVideoView.seekTo(1);
                centerVideoView.start();
                centerVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setVolume(0, 0);
                        mp.setLooping(true);
                    }
                });
                //
                media_container = rootView.findViewById(R.id.gameimageFL);
                media_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        media_containerOnClickListener.receiveOnClickGameImage(view);
                    }
                });
            }
        }
        /*

         */
        gameADAViewPagerOnFragmentEventlistener.receiveWordToDisplayIndexGameFragment(rootView,wordToDisplayIndex+1);
        //
        listener.receiveResultGameFragment(rootView);
    }
    //
    /**
     * load an image in imageview from a url or from a file
     *
     * @param urlType if string equal to "A" the image is loaded from a url otherwise it is loaded from a file
     * @param url string with url or file path of origin
     * @param img target imageview
     * @param width int with the width of the target imageview
     * @param height int with the height of the target imageview
     * @see GraphicsHelper#addImageUsingPicasso
     */
    public void addImage(String urlType, String url, ImageView img, int width, int height){
        int imageSize = calculateImageSize();
        if (urlType.equals("A"))
        {
            GraphicsHelper.addImageUsingPicasso(url,img, imageSize, imageSize);
        }
        else
        {
            File f = new File(url);
            GraphicsHelper.addFileImageUsingPicasso(f, (ImageView) img, imageSize, imageSize);
        }
    }
     /*
        ADAPTED FOR VIDEO AND SOUND
    */
    /**
     * calculate video size.
     *
     * @return imageSize int image size
     */
    public int calculateImageSize(){
        Configuration configuration = activity.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        int screenHeightDp = configuration.screenHeightDp;
        int orientation = activity.getResources().getConfiguration().orientation;
        int imageSize = 0;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            imageSize = screenWidthDp;
        } else {
            // code for landscape mode
            imageSize = screenHeightDp*80/100;
        }
        return imageSize;
    }
    /**
     * Remove the old player.
     *
     * @param videoView videoview to remove
     */
    private void removeVideoView(VideoView videoView) {
        centerVideoView.setBackgroundResource(R.drawable.white_background);
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }
        int indexOfChild = parent.indexOfChild(videoView);
        if (indexOfChild >= 0) {
            parent.removeViewAt(indexOfChild);
            isVideoViewAdded = false;
        }
        //
        image.setVisibility(VISIBLE);
    }
    /**
     * Add the new videoview.
     *
     */
    private void addVideoView(){
        frameLayout.addView(centerVideoView);
        isVideoViewAdded = true;
        centerVideoView.setVisibility(VISIBLE);
        centerVideoView.setAlpha(1);
    }
    /**
     * stop playback
     *
     */
    public void releasePlayer() {
        // release sound mediaplayer
        try {
            if(soundMediaPlayer != null) {
                if (soundMediaPlayer.isPlaying()) {
                    soundMediaPlayer.stop();
                }
                soundMediaPlayer.release();
                soundMediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        centerVideoView.stopPlayback();
    }
    //
    /**
     * release the video player
     *
     * @see Fragment#onStop
     * @see #releasePlayer()
     */
    @Override
    public void onStop() {
        releasePlayer();
        super.onStop();
    }
    /*

     */
}

