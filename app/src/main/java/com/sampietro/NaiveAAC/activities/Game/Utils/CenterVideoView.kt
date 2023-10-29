package com.sampietro.NaiveAAC.activities.Game.Utils

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

/**
 * <h1>CenterVideoView</h1>
 *
 * **CenterVideoView** Custom VideoView to match parent height and keep aspect ratio
 *
 * Refer to [itecnote.com](https://itecnote.com/tecnote/android-videoview-to-match-parent-height-and-keep-aspect-ratio/)
 *
 * @version     4.0, 09/09/2023
 * @see android.widget.VideoView
 */
class CenterVideoView : VideoView {
    private var mVideoWidth = 0
    private var mVideoHeight = 0

    /**
     * CenterVideoView constructor.
     *
     * @param context context
     * @param attrs attributeSet
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    /**
     * CenterVideoView constructor.
     *
     * @param context context
     * @param attrs attributeSet
     * @param defStyle int defStyleAttr
     */
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    /**
     * CenterVideoView constructor.
     *
     * @param context context
     */
    constructor(context: Context?) : super(context) {}

    /**
     * set width and height to match.
     *
     * @param width int with width to match
     * @param height int with heigth to match
     */
    fun setVideoSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
    }

    /**
     * Measure the view and its content to determine the measured width and the measured height.
     *
     * @param widthMeasureSpec int with horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec int with vertical space requirements as imposed by the parent
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //
        var width = getDefaultSize(mVideoWidth, widthMeasureSpec)
        var height = getDefaultSize(mVideoHeight, heightMeasureSpec)
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (mVideoWidth * height > width * mVideoHeight) {
                // image too tall, correcting
                height = width * mVideoHeight / mVideoWidth
            } else if (mVideoWidth * height < width * mVideoHeight) {
                // image too wide, correcting
                width = height * mVideoWidth / mVideoHeight
            }
        }
        //
        setMeasuredDimension(width, height)
    }
}