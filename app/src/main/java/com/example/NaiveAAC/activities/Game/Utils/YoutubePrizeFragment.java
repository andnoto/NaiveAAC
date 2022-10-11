package com.example.NaiveAAC.activities.Game.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.NaiveAAC.R;

/**
 * <h1>YoutubePrizeFragment</h1>
 * <p><b>YoutubePrizeFragment</b> UI for prize
 * upload a Youtube video as a reward
 * </p>
 * Refer to <a href="https://stackoverflow.com/questions/4654878/how-to-play-youtube-video-in-my-android-application">stackoverflow</a>
 * answer of <a href="https://stackoverflow.com/users/957925/janb">JanB</a>
 *
 * @version     1.3, 05/05/22
 */
public class YoutubePrizeFragment extends Fragment {
    public View rootView;
    public TextView textView;
    public Context ctext;
    //
    WebView mWebView;
    String videoStr1Portrait = "<html><body>Promo video<br><iframe width=\"426\" height=\"240\" src=\"https://www.youtube.com/embed/";
    String videoStr1Landscape = "<html><body>Promo video<br><iframe width=\"213\" height=\"120\" src=\"https://www.youtube.com/embed/";
    String videoStr2 = "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
    String wwwUrlSplitBy = "v=";
    String androidUrlSplitBy = "be/";
    //
    public onFragmentEventListenerYoutubePrize listener=null;
    /**
     * <h1>onFragmentEventListenerYoutubePrize</h1>
     * <p><b>onFragmentEventListenerYoutubePrize</b>
     * interface used to refer to the Activity without having to explicitly use its class
     * <p>
     *
     * @see #onAttach
     */
    public interface onFragmentEventListenerYoutubePrize
    {
        // insert here any references to the Activity
        void receiveResultImagesFromYoutubePrizeFragment(View v);
        void receiveResultOnCompletatioVideoFromYoutubePrizeFragment(View v);
    }
    /**
     * listener setting for game activities callbacks , context annotation
     *
     * @see Fragment#onAttach
     */
    @Override
    public void onAttach (Context context)
    {
        super.onAttach(context);
        Activity activity = (Activity) context;
        listener=(onFragmentEventListenerYoutubePrize) activity;
        //
        ctext = context;
    }
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     *
     * @see Fragment#onCreateView
     * @see WebViewClient
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_prize_youtube, container, false);
        //
        mWebView= (WebView) rootView.findViewById(R.id.youtubevideoview);
        //
        Bundle bundle = this.getArguments();
        String uriPremiumVideo = "";
        if (bundle != null) {
            uriPremiumVideo = bundle.getString(getString(R.string.uri_premium_video));
        }
        //
        if (!uriPremiumVideo.toUpperCase().contains(wwwUrlSplitBy.toUpperCase()) &&
                !uriPremiumVideo.toUpperCase().contains(androidUrlSplitBy.toUpperCase()))
        {
            // if the video award does not exist, it warns the activity that the award
            // has been completed and therefore you can continue
            mWebView.setVisibility(View.INVISIBLE);
            //
            listener.receiveResultOnCompletatioVideoFromYoutubePrizeFragment(rootView);
        }
        //
        else
        {
            // build your own src link with your video ID
            String[] oneWord = new String[0];
            if (uriPremiumVideo.toUpperCase().contains(wwwUrlSplitBy.toUpperCase()))
            {
                oneWord= uriPremiumVideo.split(wwwUrlSplitBy, -1);
            }
            else
            {
                oneWord= uriPremiumVideo.split(androidUrlSplitBy, -1);
            }
            // builds the src link (including size and address)
            // The Right YouTube Dimensions = 426X240
            int orientation = this.getResources().getConfiguration().orientation;
            String videoStr;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // code for portrait mode
                videoStr = videoStr1Portrait + oneWord[1] + videoStr2;
            } else {
                // code for landscape mode
                videoStr = videoStr1Landscape + oneWord[1] + videoStr2;
            }
            //
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    int TIME_OUT = 180000;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.receiveResultOnCompletatioVideoFromYoutubePrizeFragment(rootView);
                        }
                    }, TIME_OUT);
                }
            });
            WebSettings ws = mWebView.getSettings();
            ws.setJavaScriptEnabled(true);
            mWebView.loadData(videoStr, "text/html", "utf-8");
        }
        listener.receiveResultImagesFromYoutubePrizeFragment(rootView);
        return rootView;
    }
    /**
     * destroy WebView
     * <p>
     *
     * @see Fragment#onDestroyView
     */
    @Override
    public void onDestroyView() {
        mWebView.destroy();
        mWebView = null;
        super.onDestroyView();
    }
}

