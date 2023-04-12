package com.sampietro.NaiveAAC.activities.Game.GameADA;

import static com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchNegationAdverb;
import static com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;

/**
 * <h1>GameADARecyclerViewAdapter</h1>
 * <p><b>GameADARecyclerViewAdapter</b> adapter for games
 * </p>
 * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
 * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
 *
 * @version     3.0, 03/12/23
 * @see RecyclerView.Adapter<RecyclerView.ViewHolder>
 */
public class GameADARecyclerViewAdapter
        extends RecyclerView.Adapter<GameADARecyclerViewViewHolder> {
    public GameADARecyclerViewAdapterInterface listener=null;
    //
    private ArrayList<GameADAArrayList> galleryList;
    private Context context;
    //
    private Realm realm;
    /**
     * used for TTS
     */
    public TextToSpeech tTS1;
    public String toSpeak;
    /**
     * GameRecyclerViewAdapter constructor.
     * set game list and context annotation, get print permissions
     *
     * @param context context
     * @param galleryList arraylist<GameADAArrayList> to set
     * @see GameADAArrayList
     */
    public GameADARecyclerViewAdapter
            (Context context, Realm realm, ArrayList<GameADAArrayList> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
        //
        this.realm = realm;
        //
        Activity activity = (Activity) context;
        listener=(GameADARecyclerViewAdapterInterface) activity;
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
    public GameADARecyclerViewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_game_ada_cell_layout, viewGroup, false);
        return new GameADARecyclerViewViewHolder(view);
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
     * @see com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper#searchNegationAdverb
     * @see #addImage
     */
    @Override
    public void onBindViewHolder(GameADARecyclerViewViewHolder viewHolder, int i) {
        //
        viewHolder.onBind(galleryList.get(i));
        //
        viewHolder.title.setText(galleryList.get(i).getImage_title());
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //
        addImage((galleryList.get(i).getUrlType()),
                (galleryList.get(i).getUrl()), viewHolder.img);
        // search for negation adverbs
        viewHolder.img2.setVisibility(View.INVISIBLE);
        String negationAdverbImageToSearchFor = searchNegationAdverb(galleryList.get(i).getImage_title().toLowerCase(), realm);
        if (!(negationAdverbImageToSearchFor.equals("non trovato"))) {
            // INTERNAL MEMORY IMAGE SEARCH
            String uriToSearch = searchUri(realm, negationAdverbImageToSearchFor);
            viewHolder.img2.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addImage("S", uriToSearch, viewHolder.img2);
            viewHolder.img2.setVisibility(View.VISIBLE);
        }
        //
        viewHolder.media_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, i, galleryList);
            }
        });
    }
    /**
     * add an image to a view as indicated in the parameters
     *
     * @param urlType string representing the type of icon displayed in the left view.
     * @param url string representing the path of icon displayed in the left view.
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

}

