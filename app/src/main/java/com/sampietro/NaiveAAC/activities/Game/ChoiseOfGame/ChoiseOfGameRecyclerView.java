package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame;

import static android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;

import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Utils.CenterVideoView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * <h1>ChoiseOfGameRecyclerView</h1>
 * <p><b>ChoiseOfGameRecyclerView</b> Custom RecyclerView for game choice
 * </p>
 * Refer to <a href="https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/">codingwithmitch</a>
 * by <a href="https://codingwithmitch.com/">Mitch Tabian</a>
 *
 * @version     1.1, 04/28/22
 * @see RecyclerView
 * @see CenterVideoView
 * @see ChoiseOfGameArrayList
 */
public class ChoiseOfGameRecyclerView extends RecyclerView {
    //
    private ImageView image;
    private View viewHolderItemView;
    private FrameLayout frameLayout;
    private CenterVideoView centerVideoView;
    //
    private ArrayList<ChoiseOfGameArrayList> gameArrayList = new ArrayList<>();
    private int videoSurfaceDefaultWidth = 0;
    private Context context;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    /**
     * ChoiseOfGameRecyclerView constructor.
     * init RecyclerView
     *
     * @param context context
     */
    public ChoiseOfGameRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }
    /**
     * ChoiseOfGameRecyclerView constructor.
     * init RecyclerView
     *
     * @param context context
     * @param attrs attributeSet
     */
    public ChoiseOfGameRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    /**
     * init RecyclerView , set listeners
     *
     * @param context context
     * @see #playVideo
     * @see #resetVideoView
     */
    private void init(Context context){
        this.context = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        // getSize Gets the size of the display, in pixels.
        // Value returned by this method does not necessarily represent the actual raw size
        // (native resolution) of the display.
        videoSurfaceDefaultWidth = point.x;
        //
        centerVideoView = new CenterVideoView(this.context);
        centerVideoView.setVideoSize(200,200);
        centerVideoView.setOnInfoListener (new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                if (i == MEDIA_INFO_VIDEO_RENDERING_START)
                    {
                        centerVideoView.setBackgroundResource(0);
                    }
                return false;
            }
        });
        //
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(image != null){
                        image.setVisibility(VISIBLE);
                    }

                    // special case when the end of the list has been reached.
                    if(!recyclerView.canScrollHorizontally(1))
//                            || !recyclerView.canScrollHorizontally(-1))
                    {
                        playVideo(true);
                    }
                    else{
                        playVideo(false);
                    }
                }
            }
            //
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //
        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
            //    all'apertura, inserire qui video primo item?
            }
            //
            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (viewHolderItemView != null && viewHolderItemView.equals(view)) {
                    resetVideoView();
                }

            }
        });
    }
    /**
     * init RecyclerView , set listeners
     *
     * @param isEndOfList boolean true when position is at end of list
     * @see #getVisibleVideoSurfaceWidth
     * @see #removeVideoView
     * @see #addVideoView
     */
    public void playVideo(boolean isEndOfList) {
        int targetPosition;
        //
        if(!isEndOfList){
            int startPosition = ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }
            if (startPosition != endPosition) {
                // there is more than 1 list-item on the screen
                // he chooses between the first and second list-items
                // the one that occupies the screen the most
                int startPositionVideoWidth = getVisibleVideoSurfaceWidth(startPosition);
                int endPositionVideoWidth = getVisibleVideoSurfaceWidth(endPosition);
                targetPosition = startPositionVideoWidth > endPositionVideoWidth ? startPosition : endPosition;
                }
            else {
                // there is 1 list-item on the screen
                targetPosition = startPosition;
                }
            }
        else {
            targetPosition = gameArrayList.size() - 1;
        }
        // video is already playing
        if (targetPosition == playPosition) {
            return;
        }
        // set the position of the list-item that is to be played
        playPosition = targetPosition;
        if (centerVideoView == null) {
            return;
        }
        // remove old video views
        removeVideoView(centerVideoView);
        //
        int targetPositionAmongTheVisibleItems = targetPosition
                - ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
        View child = getChildAt(targetPositionAmongTheVisibleItems);
        if (child == null) {
            return;
        }
        ChoiseOfGameRecyclerViewViewHolder holder = (ChoiseOfGameRecyclerViewViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }
        image = holder.imageView;
        viewHolderItemView = holder.itemView;
        frameLayout = holder.itemView.findViewById(R.id.media_container);
        //
        String videoPath = gameArrayList.get(targetPosition).getUrlVideo();
        if (!videoPath.equals(context.getString(R.string.non_trovato))) {
        //
            if(!isVideoViewAdded){
                addVideoView();
            }
            centerVideoView.setBackgroundResource(R.drawable.white_background);
            centerVideoView.setVideoPath(videoPath);
            centerVideoView.seekTo(1);
            centerVideoView.start();
            centerVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
        }
    }
    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     *
     * @param position int with position in game list
     * @return int visible video surface width for the video in the param position
     */
    private int getVisibleVideoSurfaceWidth(int position) {
        int positionAmongTheVisibleItems = position
                - ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
        View child = getChildAt(positionAmongTheVisibleItems);
        if (child == null) {
            return 0;
        }
        //
        int[] location = new int[2];
        // getLocationInWindow Computes the coordinates of this view in its window.
        // The argument must be an array of two integers.
        // After the method returns, the array contains the x and y location in that order.
        child.getLocationInWindow(location);
        //
        if (location[0] < 0) {
            return location[0] + videoSurfaceDefaultWidth;
        } else {
            return videoSurfaceDefaultWidth - location[0];
        }
    }
    /**
     * Remove the old player.
     *
     * @param videoView videoview to remove
     */
    private void removeVideoView(VideoView videoView) {
        centerVideoView.setBackgroundResource(R.drawable.white_background);
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }
        int indexOfChild = parent.indexOfChild(videoView);
        if (indexOfChild >= 0) {
            parent.removeViewAt(indexOfChild);
            isVideoViewAdded = false;
            viewHolderItemView.setOnClickListener(null);
        }
        //
        image.setVisibility(VISIBLE);
    }
    /**
     * Add the new videoview.
     *
     */
    private void addVideoView(){
        frameLayout.addView(centerVideoView);
        isVideoViewAdded = true;
//        centerVideoView.requestFocus();
        centerVideoView.setVisibility(VISIBLE);
        centerVideoView.setAlpha(1);
    }
    /**
     * Remove the old player and reset video view
     *
     */
    private void resetVideoView(){

        if(isVideoViewAdded){
            removeVideoView(centerVideoView);
            playPosition = -1;
            centerVideoView.setVisibility(INVISIBLE);
            image.setVisibility(VISIBLE);
        }
    }
    /**
     * stop playback
     *
     */
    public void releasePlayer() {
        centerVideoView.stopPlayback();
        viewHolderItemView = null;
    }
    /**
     * set game list
     *
     * @param gameArrayList arraylist<ChoiseOfGameArrayList> to set
     * @see ChoiseOfGameArrayList
     */
    public void setMediaObjects(ArrayList<ChoiseOfGameArrayList> gameArrayList){
        this.gameArrayList = gameArrayList;
    }
    //
}
