package com.sampietro.NaiveAAC.activities.Game.Game1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * <h1>Game1RecyclerViewAdapter3</h1>
 * <p><b>Game1RecyclerViewAdapter3</b> adapter for game1
 * </p>
 * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
 * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
 *
 * @version     1.3, 05/05/22
 * @see RecyclerView.Adapter<RecyclerView.ViewHolder>
 */
public class Game1RecyclerViewAdapter3
        extends RecyclerView.Adapter<Game1RecyclerViewAdapter3.ViewHolder> {
    //
    public Game1RecyclerViewAdapterInterface listener=null;
    //
    private ArrayList<Game1ArrayList> galleryList;
    private Context context;
    /**
     * Game1RecyclerViewAdapter3 constructor.
     * set game1 list, listener setting for settings activity callbacks and context annotation
     *
     * @param context context
     * @param galleryList arraylist<Game1ArrayList> to set
     * @see Game1ArrayList
     * @see Game1RecyclerViewAdapterInterface
     */
    public Game1RecyclerViewAdapter3
            (Context context, ArrayList<Game1ArrayList> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
        //
        Activity activity = (Activity) context;
        listener=(Game1RecyclerViewAdapterInterface) activity;
    }
    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param viewGroup viewGroup into which the new View will be added after it is bound to an adapter position
     * @param i int with the view type of the new View.
     * @return RecyclerView.ViewHolder new View created inflating it from an XML layout file
     * @see RecyclerView.Adapter#onCreateViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_game_1_cell_layout_3, viewGroup, false);
        return new ViewHolder(view);
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
     * @see #addImage
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.title.setText(galleryList.get(i).getImage_title());
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setContentDescription(galleryList.get(i).getImage_title());
        //
        addImage((galleryList.get(i).getUrlType()),
                (galleryList.get(i).getUrl()), viewHolder.img);
        //
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, i);
            }
        });
    }
    /**
     * add an image to a right view as indicated in the parameters
     *
     * @param urlType string representing the type of icon displayed in the right view.
     * @param url string representing the path of icon displayed in the right view.
     * @param img imageview where the image will be added
     * @see GraphicsHelper#addImageUsingPicasso
     * @see GraphicsHelper#addFileImageUsingPicasso
     */
    public void addImage(String urlType, String url, ImageView img){
        if (urlType.equals("A"))
        {
            GraphicsHelper.addImageUsingPicasso(url,img, 200,200);
        }
        else
        {
            File f = new File(url);
            GraphicsHelper.addFileImageUsingPicasso(f, (ImageView) img, 200,200);
        }
    }
    /**
     * returns number of item within the adapter's data set.
     *
     * @return int with number of item within the adapter's data set
     * @see RecyclerView.Adapter#getItemCount
     */
    @Override
    public int getItemCount() {
        return galleryList.size();
    }
    /**
     * viewholder for game1 right recyclerview.
     * </p>
     *
     * @see RecyclerView.ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView img;
        public ViewHolder(View view) {
            super(view);
            //
            title = (TextView)view.findViewById(R.id.title);
            img = (ImageView) view.findViewById(R.id.img3);
        }
    }
}

