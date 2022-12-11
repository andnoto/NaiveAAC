package com.sampietro.NaiveAAC.activities.Game.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameFragment;
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1FirstLevelFragment;
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1SecondLevelFragment;
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper;
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2Fragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import io.realm.Realm;

/**
 * <h1>GameFragmentAbstractClass</h1>
 * <p><b>GameFragmentAbstractClass</b>
 * abstract class containing common methods that is extended by game fragments
 * </p>
 * 1) GameFragmentChoiseOfGameMediaPlayer</p>
 * 2) Game1ViewPagerFirstLevelFragment</p>
 * 3) Game1ViewPagerSecondLevelFragment</p>
 * 4) GameFragmentGame2</p>
 *
 * @version     1.1, 04/28/22
 * @see ChoiseOfGameFragment
 * @see Game1FirstLevelFragment
 * @see Game1SecondLevelFragment
 * @see Game2Fragment
 */
public abstract class GameFragmentAbstractClass extends Fragment {
    public Realm realm;
    //
    public View rootView;
    public TextView textView;
    public Context ctext;
    public SharedPreferences sharedPref;
    public String preference_PrintPermissions;
    //
    public String preference_TitleWritingType;
    /**
     * used for printing
     */
    public Bitmap bitmap1;
    /**
     * used for printing
     */
    public Target target1 = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bitmap1 = bitmap;
        }
        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
    /**
     * used for TTS
     */
    public TextToSpeech tTS1;
    public String toSpeak;
    //
    public OnFragmentEventListenerGame listener=null;
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
        listener=(OnFragmentEventListenerGame) activity;
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
    }
    /**
     * TTS shutdown
     *
     * @see Fragment#onDestroy
     */
    @Override
    public void onDestroy() {
        if (tTS1 != null) {
            tTS1.stop();
            tTS1.shutdown();
        }
        //
        super.onDestroy();
    }
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
        if (urlType.equals("A"))
        {
            GraphicsHelper.addImageUsingPicasso(url,img, width, height);
        }
        else
        {
            File f = new File(url);
            GraphicsHelper.addFileImageUsingPicasso(f, (ImageView) img, width, height);
        }
    }
    /**
     * used for printing load an image in a target bitmap from a file
     *
     * @param file file of origin
     * @param target target bitmap
     * @see Picasso
     */
    public void getTargetBitmapFromFileUsingPicasso(File file, Target target){
        Picasso.get()
                .load(file)
                .resize(200, 200)
                .into(target);
    }
    /**
     * used for printing load an image in a target bitmap from a url
     *
     * @param url string with url of origin
     * @param target target bitmap
     * @see Picasso
     */
    public void getTargetBitmapFromUrlUsingPicasso(String url, Target target){
        Picasso.get()
                .load(url)
                .resize(200, 200)
                .into(target);
    }
}

