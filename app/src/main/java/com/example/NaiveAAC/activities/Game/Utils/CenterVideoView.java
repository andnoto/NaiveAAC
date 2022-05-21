package com.example.NaiveAAC.activities.Game.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * <h1>CenterVideoView</h1>
 * <p><b>CenterVideoView</b> Custom VideoView to match parent height and keep aspect ratio
 * </p>
 * Refer to <a href="https://itecnote.com/tecnote/android-videoview-to-match-parent-height-and-keep-aspect-ratio/">itecnote.com</a>
 *
 * @version     1.3, 05/05/22
 * @see VideoView
 */
public class CenterVideoView extends VideoView {
    private int mVideoWidth;
    private int mVideoHeight;
    /**
     * CenterVideoView constructor.
     *
     * @param context context
     * @param attrs attributeSet
     */
    public CenterVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * CenterVideoView constructor.
     *
     * @param context context
     * @param attrs attributeSet
     * @param defStyle int defStyleAttr
     */
    public CenterVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * CenterVideoView constructor.
     *
     * @param context context
     */
    public CenterVideoView(Context context) {
        super(context);
    }
    /**
     * set width and height to match.
     *
     * @param width int with width to match
     * @param height int with heigth to match
     */
    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }
    /**
     * Measure the view and its content to determine the measured width and the measured height.
     *
     * @param widthMeasureSpec int with horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec int with vertical space requirements as imposed by the parent
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (mVideoWidth * height > width * mVideoHeight) {
                // image too tall, correcting
                height = width * mVideoHeight / mVideoWidth;
            } else if (mVideoWidth * height < width * mVideoHeight) {
                // image too wide, correcting
                width = height * mVideoWidth / mVideoHeight;
            }
        }
        //
        setMeasuredDimension(width, height);
    }
}