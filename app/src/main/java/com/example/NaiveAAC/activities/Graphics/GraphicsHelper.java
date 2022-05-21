package com.example.NaiveAAC.activities.Graphics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>GraphicsHelper</h1>
 *
 * <p><b>GraphicsHelper</b> utility class for graphics.</p>
 *
 * @version     1.3, 05/05/22
 */
public class GraphicsHelper {
    /**
     * create a crossfade animation
     * Refer to <a href="https://developer.android.com/training/animation/reveal-or-hide-view">developer.android.com</a>
     *
     * @param fadingInView view that is fading in
     * @param loadingView view that was fading out
     */
    public static void crossfade(View fadingInView, View loadingView) {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        fadingInView.setAlpha(0f);
        fadingInView.setVisibility(View.VISIBLE);
        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        fadingInView.animate()
                .alpha(1f)
                // duration in millis
                .setDuration(5000)
                .setListener(null);
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        loadingView.animate()
                .alpha(0f)
                .setDuration(5000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.GONE);
                    }
                });
    }
    /**
     * inserts an image from url into a view
     * Refer to <a href="https://square.github.io/picasso/#license">picasso</a>
     *
     * @param url string with url of the image
     * @param img view with target view
     * @see Picasso
     * @deprecated  replaced by {@link #addImageUsingPicasso(String,ImageView,int,int)}
     */
    public static void addImageUsingPicasso(String url, ImageView img){
        Picasso.get()
                .load(url)
                .resize(200, 200)
                .into(img);
    }
    /**
     * inserts an image from file into a view
     * Refer to <a href="https://square.github.io/picasso/#license">picasso</a>
     *
     * @param file file containing the image
     * @param img view with target view
     * @see Picasso
     * @deprecated  replaced by {@link #addFileImageUsingPicasso(File,ImageView,int,int)}
     */
    public static void addFileImageUsingPicasso(File file, ImageView img){
        Picasso.get()
                .load(file)
                .resize(200, 200)
                .into(img);
    }
    /**
     * inserts an image from url into a view
     * Refer to <a href="https://square.github.io/picasso/#license">picasso</a>
     *
     * @param url string with url of the image
     * @param img view with target view
     * @param width int with the image width for resizing
     * @param height int with the image height for resizing
     * @see Picasso
     */
    public static void addImageUsingPicasso(String url, ImageView img, int width, int height){
        Picasso.get()
                .load(url)
                .resize(width, height)
                .into(img);
    }
    /**
     * inserts an image from file into a view
     * Refer to <a href="https://square.github.io/picasso/#license">picasso</a>
     *
     * @param file file containing the image
     * @param img view with target view
     * @param width int with the image width for resizing
     * @param height int with the image height for resizing
     * @see Picasso
     */
    public static void addFileImageUsingPicasso(File file, ImageView img, int width, int height){
        Picasso.get()
                .load(file)
                .resize(width, height)
                .into(img);
    }
    /**
     * inserts an image whose address is found via realm into a view
     * Refer to <a href="https://square.github.io/picasso/#license">picasso</a>
     *
     * @param realm realm
     * @param word string with word for which you need to find the image
     * @param img view with target view
     * @param width int with the image width for resizing
     * @param height int with the image height for resizing
     * @see Picasso
     * @see Images
     * @see #addFileImageUsingPicasso(File, ImageView, int, int)
     */
    public static void addImageUsingRealm(Realm realm,
                                          String word, ImageView img, int width, int height)
    {
        RealmResults<Images> results =
                realm.where(Images.class).equalTo("descrizione", word).findAll();
        int count = results.size();
        if (count != 0) {
            Images result = results.get(0);
            if (result != null) {
                String filePath = result.getUri();
                File f = new File(filePath);
                addFileImageUsingPicasso(f, (ImageView) img, width, height);
            }
        }
    }
}
