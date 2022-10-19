package com.sampietro.NaiveAAC.activities.Game.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper;
import com.sampietro.NaiveAAC.activities.Graphics.Videos;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>PrizeFragment</h1>
 * <p><b>PrizeFragment</b> UI for prize
 * upload a video as a reward
 * </p>
 *
 * @version     1.3, 05/05/22
 */
public class PrizeFragment extends Fragment {
    private Realm realm;
    //
    public Uri uri;
    public String stringUri;
    //
    public View rootView;
    public TextView textView;
    public Context ctext;
    //
    private MediaPlayer mediaPlayer;
    //
    CenterVideoView prizeVideoView;
    int videoWidth;
    int videoHeight;
    /**
     * <h1>onFragmentEventListenerPrize</h1>
     * <p><b>onFragmentEventListenerPrize</b>
     * interface used to refer to the Activity without having to explicitly use its class
     * <p>
     *
     * @see #onAttach
     */
    public interface onFragmentEventListenerPrize
    {
        // insert here any references to the Activity
        void receiveResultImagesFromPrizeFragment(View v);
        void receiveResultOnCompletatioVideoFromPrizeFragment(View v);
    }
    /**
     * listener setting for game activities callbacks , context annotation, realm get default instance
     *
     * @see Fragment#onAttach
     */
    private onFragmentEventListenerPrize listener=null;
    @Override
    public void onAttach (Context context)
    {
        super.onAttach(context);
        Activity activity = (Activity) context;
        listener=(onFragmentEventListenerPrize) activity;
        //
        ctext = context;
        //
        realm= Realm.getDefaultInstance();
    }
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     *
     * @see Fragment#onCreateView
     * @see ImageSearchHelper#imageSearch
     * @see MediaPlayer
     * @see Videos
     * @see #getVideoWidthOrHeight
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_prize, container, false);
        //
        textView = rootView.findViewById(R.id.copyright);
        // Find your VideoView in your video_main.xml layout
        prizeVideoView = (CenterVideoView) rootView.findViewById(R.id.prizeVideoView);
        //
        prizeVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //
                prizeVideoView.setVisibility(View.INVISIBLE);
                //
                mediaPlayer.release();
                // remove the VideoView otherwise it exits the Activity
                ViewGroup parent = (ViewGroup) prizeVideoView.getParent();
                if (parent != null) {
                    int index = parent.indexOfChild(prizeVideoView);
                    if (index >= 0) {
                        parent.removeViewAt(index);
                    }
                }
                //
                listener.receiveResultOnCompletatioVideoFromPrizeFragment(rootView);
            }
        });
        //
        Bundle bundle = this.getArguments();
        String uriPremiumVideo = "";
        if (bundle != null) {
            uriPremiumVideo = bundle.getString(getString(R.string.uri_premium_video));
        }
        //
        if (uriPremiumVideo.equals("") || uriPremiumVideo.equals(" "))
        {
            uriPremiumVideo = getString(R.string.prize);
        }
            // I create a random integer 0 or 1 to be used to dispense the prize 50% of the time
            // Random rn = new Random();
            // int rn01 = rn.nextInt(2);
            //
            // if (rn01 == 1) {
        RealmResults<Videos> results =
                realm.where(Videos.class).equalTo(getString(R.string.descrizione), uriPremiumVideo).findAll();
        int count = results.size();
        if (count != 0) {
                    Videos result = results.get(0);
                    assert result != null;
                    stringUri = result.getUri();
                    textView.setText(result.getCopyright());
                    //
                    prizeVideoView.setVideoURI(Uri.parse(stringUri));
                    //
                    videoWidth = getVideoWidthOrHeight(stringUri, getString(R.string.width));
                    videoHeight = getVideoWidthOrHeight(stringUri, getString(R.string.height));
                    prizeVideoView.setVideoSize(videoWidth, videoHeight);
                    // start a video
                    prizeVideoView.start();
                    //
                    }
                    else {
                        // if the video award does not exist, it warns the activity that the award
                        // has been completed and therefore you can continue
                        prizeVideoView.setVisibility(View.INVISIBLE);
                        //
                        listener.receiveResultOnCompletatioVideoFromPrizeFragment(rootView);
                    }
        listener.receiveResultImagesFromPrizeFragment(rootView);
        return rootView;
    }
    /**
     * determine video width and height
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/9077436/how-to-determine-video-width-and-height-on-android">stackoverflow</a>
     * answer of  <a href="https://stackoverflow.com/users/493244/user493244">user493244</a>
     *
     * @param file string with file pathname to use
     * @param widthOrHeight string with size required (width or height)
     * @return mWidthHeight int with video width or height
     */
    public int getVideoWidthOrHeight(String file, String widthOrHeight) {
        MediaMetadataRetriever retriever = null;
        Bitmap bmp = null;
        //
        int mWidthHeight = 0;
        try {
            retriever = new  MediaMetadataRetriever();
            //
            retriever.setDataSource(file);
            bmp = retriever.getFrameAtTime();
            if (widthOrHeight.equals(getString(R.string.width))){
                mWidthHeight = bmp.getWidth();
            }else {
                mWidthHeight = bmp.getHeight();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally{
            if (retriever != null){
                retriever.release();
            }
        }
        return mWidthHeight;
    }
}

