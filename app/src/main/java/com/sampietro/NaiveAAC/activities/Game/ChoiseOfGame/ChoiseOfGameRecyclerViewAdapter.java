package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;

import java.util.ArrayList;

/**
 * <h1>ChoiseOfGameRecyclerViewAdapter</h1>
 * <p><b>ChoiseOfGameRecyclerViewAdapter</b> adapter for game choice
 * </p>
 * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
 * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
 * Refer to <a href="https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/">codingwithmitch</a>
 * by <a href="https://codingwithmitch.com/">Mitch Tabian</a>
 *
 * @version     1.1, 04/28/22
 * @see RecyclerView.Adapter<RecyclerView.ViewHolder>
 */
public class ChoiseOfGameRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ChoiseOfGameRecyclerViewAdapterInterface listener=null;
    //
    private ArrayList<ChoiseOfGameArrayList> gameArrayList;
    /**
     * ChoiseOfGameRecyclerViewAdapter constructor.
     * set game list, listener setting for settings activity callbacks and context annotation
     *
     * @param context context
     * @param @param gameArrayList arraylist<ChoiseOfGameArrayList> to set
     * @see ChoiseOfGameArrayList
     */
    public ChoiseOfGameRecyclerViewAdapter
            (Context context, ArrayList<ChoiseOfGameArrayList> gameArrayList) {
        this.gameArrayList = gameArrayList;
        //
        Activity activity = (Activity) context;
        listener=(ChoiseOfGameRecyclerViewAdapterInterface) activity;
    }
    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param viewGroup viewGroup into which the new View will be added after it is bound to an adapter position
     * @param i int with the view type of the new View.
     * @return RecyclerView.ViewHolder new View created inflating it from an XML layout file
     * @see RecyclerView.Adapter#onCreateViewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ChoiseOfGameRecyclerViewViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_game_choise_of_game_cell_layout, viewGroup, false));
    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     * set on click listeners
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/49969278/recyclerview-item-click-listener-the-right-way">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/3145960/reaz-murshed">Reaz Murshed</a>
     *
     * @param viewHolder RecyclerView.ViewHolder ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * @param i int with the position of the item within the adapter's data set.
     * @see RecyclerView.Adapter#onBindViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((ChoiseOfGameRecyclerViewViewHolder)viewHolder).onBind(gameArrayList.get(i));
        //
        ((ChoiseOfGameRecyclerViewViewHolder) viewHolder).media_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, i);
            }
        });
        ((ChoiseOfGameRecyclerViewViewHolder) viewHolder).info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, i);
            }
        });
    }
    /**
     * returns number of item within the adapter's data set.
     *
     * @return int with number of item within the adapter's data set
     * @see RecyclerView.Adapter#getItemCount
     */
    @Override
    public int getItemCount() {
        return gameArrayList.size();
    }
}
