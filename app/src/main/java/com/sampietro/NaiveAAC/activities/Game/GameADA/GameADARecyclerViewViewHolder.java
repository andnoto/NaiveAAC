package com.sampietro.NaiveAAC.activities.Game.GameADA;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;

/**
 * <h1>GameADARecyclerViewViewHolder</h1>
 * <p><b>GameADARecyclerViewViewHolder</b> viewholder for game ada
 * </p>
 * Refer to <a href="https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/">codingwithmitch</a>
 * by <a href="https://codingwithmitch.com/">Mitch Tabian</a>
 * THIS CLASS WAS CREATED TO ASSOCIATE VIDEOS AND SOUNDS WITH WORDS (AS WELL AS IMAGES AND TTS)
 *
 * @version     3.0, 03/12/23
 * @see RecyclerView.ViewHolder
 */
public class GameADARecyclerViewViewHolder extends RecyclerView.ViewHolder {
    FrameLayout media_container;
    TextView title;
    public ImageView img, img2;
    //
    View parent;
    /**
     * GameADARecyclerViewViewHolder constructor.
     * init RecyclerView.ViewHolder finding views
     *
     * @param itemView view
     */
    public GameADARecyclerViewViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.game_ada_media_container);
        title = (TextView)itemView.findViewById(R.id.title);
        img = (ImageView) itemView.findViewById(R.id.img1);
        img2 = (ImageView) itemView.findViewById(R.id.img2);
    }
    /**
     * set views
     *
     * @param gameArrayList GameADAArrayList with game list
     * @see GameADAArrayList
     */
    public void onBind(GameADAArrayList gameArrayList) {
        parent.setTag(this);
    }
}
