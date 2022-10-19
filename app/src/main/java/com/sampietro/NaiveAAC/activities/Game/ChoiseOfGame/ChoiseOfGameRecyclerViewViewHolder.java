package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;

import java.io.File;

/**
 * <h1>ChoiseOfGameRecyclerViewViewHolder</h1>
 * <p><b>ChoiseOfGameRecyclerViewViewHolder</b> viewholder for game choice
 * </p>
 * Refer to <a href="https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/">codingwithmitch</a>
 * by <a href="https://codingwithmitch.com/">Mitch Tabian</a>
 *
 * @version     1.1, 04/28/22
 * @see RecyclerView.ViewHolder
 */
public class ChoiseOfGameRecyclerViewViewHolder extends RecyclerView.ViewHolder {
    FrameLayout media_container;
    TextView title, info, copyright;
    public ImageView imageView;
    public ProgressBar progressBar;
    View parent;
    /**
     * ChoiseOfGameRecyclerViewViewHolder constructor.
     * init RecyclerView.ViewHolder finding views
     *
     * @param itemView view
     */
    public ChoiseOfGameRecyclerViewViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.media_container);
        imageView = itemView.findViewById(R.id.imageView);
        title = itemView.findViewById(R.id.title);
        progressBar = itemView.findViewById(R.id.progressBar);
        info = itemView.findViewById(R.id.infomediaplayer);
        copyright = itemView.findViewById(R.id.copyright);
    }
    /**
     * set views
     *
     * @param gameArrayList ChoiseOfGameArrayList with game list
     * @see ChoiseOfGameArrayList
     * @see #addImage
     */
    public void onBind(ChoiseOfGameArrayList gameArrayList) {
        parent.setTag(this);
        title.setText(gameArrayList.getImage_title());
        info.setText(R.string.info);
        copyright.setText(gameArrayList.getVideoCopyright());
        addImage(gameArrayList.getUrlType(),gameArrayList.getUrl(),imageView);
    }
    /**
     * add an image to a view as indicated in the parameters
     *
     * @param urlType string representing the type of icon displayed in the view for game choice.
     * @param url string representing the path of icon displayed in the view for game choice.
     * @param img imageview where the image will be added
     * @see GameParameters
     * @see GraphicsHelper#addImageUsingPicasso
     * @see GraphicsHelper#addFileImageUsingPicasso
     */
    public void addImage(String urlType, String url, ImageView img){
        if (urlType.equals("AS"))
        {
            String assetsUrl = "file:///android_asset/" + url;
            GraphicsHelper.addImageUsingPicasso(assetsUrl,img,200,200);
        }
        else
        { if (urlType.equals("A"))
        {
            GraphicsHelper.addImageUsingPicasso(url,img,200,200);
        }
        else
        {
            File f = new File(url);
            GraphicsHelper.addFileImageUsingPicasso(f, (ImageView) img, 200,200);
        }
        }
    }
}
