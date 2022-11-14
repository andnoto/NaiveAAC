package com.sampietro.NaiveAAC.activities.Game.Game2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.print.PrintHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;

/**
 * <h1>Game2RecyclerViewAdapter1</h1>
 * <p><b>Game2RecyclerViewAdapter1</b> adapter for game2
 * </p>
 * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
 * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
 *
 * @version     1.4, 19/05/22
 * @see RecyclerView.Adapter<RecyclerView.ViewHolder>
 */
public class Game2RecyclerViewAdapter1
        extends RecyclerView.Adapter<Game2RecyclerViewAdapter1.ViewHolder> {
    private ArrayList<Game2ArrayList> galleryList;
    private Context context;
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
     * used for printing
     */
    public SharedPreferences sharedPref;
    public String preference_PrintPermissions;
    /**
     * used for TTS
     */
    public TextToSpeech tTS1;
    public String toSpeak;
    /**
     * Game2RecyclerViewAdapter1 constructor.
     * set game2 list and context annotation, get print permissions
     *
     * @param context context
     * @param galleryList arraylist<Game2ArrayList> to set
     * @see Game2ArrayList
     */
    public Game2RecyclerViewAdapter1
            (Context context, ArrayList<Game2ArrayList> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
        // if is print permitted then preference_PrintPermissions = Y
        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        preference_PrintPermissions =
                sharedPref.getString (context.getString(R.string.preference_print_permissions), "DEFAULT");
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_game_2_cell_layout_1, viewGroup, false);
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
     * @see #getTargetBitmapFromUrlUsingPicasso
     * @see #getTargetBitmapFromFileUsingPicasso
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
                // TTS
                tTS1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            tTS1.speak(galleryList.get(i).getImage_title(), TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                        }
                        else
                        {
                            Toast.makeText(context, status,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //
                if (preference_PrintPermissions.equals("Y")) {
                    if (galleryList.get(i).getUrlType().equals("A"))
                    {
                        getTargetBitmapFromUrlUsingPicasso(galleryList.get(i).getUrl(), target1);
                    }
                    else
                    {
                        File f = new File(galleryList.get(i).getUrl());
                        getTargetBitmapFromFileUsingPicasso(f, target1);
                    }
                    //
                    PrintHelper photoPrinter = new PrintHelper(context);
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), bitmap1);
                }
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
    /**
     * viewholder for game2 recyclerview.
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
            img = (ImageView) view.findViewById(R.id.img1);
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

