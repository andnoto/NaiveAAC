package com.sampietro.NaiveAAC.activities.Game.Balloon

import android.animation.Animator
import android.annotation.SuppressLint
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation.ValueAnimator
import android.content.Context
import com.sampietro.NaiveAAC.R
import android.view.ViewGroup
import com.sampietro.NaiveAAC.activities.Game.Balloon.Utils.PixelHelper
import android.view.animation.LinearInterpolator
import android.view.MotionEvent
import android.widget.ImageView

/**
 * <h1>Balloon</h1>
 *
 * **Balloon** class represent balloon game object.
 * Refer to [github.com](https://github.com/Vukan-Markovic/Pop_Balloon)
 * By [Vukan Marković](https://github.com/Vukan-Markovic)
 *
 * @version     4.0, 09/09/2023
 * @see android.widget.ImageView
 *
 * @see ValueAnimator.AnimatorUpdateListener
 *
 * @see Animator.AnimatorListener
 *
 * @see PixelHelper
 */
@SuppressLint("AppCompatCustomView")
class Balloon : ImageView, AnimatorUpdateListener, Animator.AnimatorListener {
    private var mAnimator: ValueAnimator? = null
    private var mListener: BalloonListener? = null
    private var mPopped = false

    //
    constructor(context: Context?) : super(context) {}

    //
    constructor(context: Context, color: Int, rawHeight: Int) : super(context) {
        mListener = context as BalloonListener
        setImageResource(R.drawable.balloon)
        this.setColorFilter(color)
        layoutParams = ViewGroup.LayoutParams(
            PixelHelper.pixelsToDp(rawHeight / 2, context),
            PixelHelper.pixelsToDp(rawHeight, context)
        )
    }

    /**
     * This method is responsible for create releasing balloons animation during gameplay.
     *
     * @param screenHeight represents screen height
     * @param duration     represents number of milliseconds for animation duration. With increasing level, this number is reduces.
     * @see ValueAnimator
     */
    fun releaseBalloon(screenHeight: Int, duration: Int) {
        mAnimator = ValueAnimator()
        mAnimator!!.duration = duration.toLong()
        mAnimator!!.setFloatValues(screenHeight.toFloat(), 0f)
        mAnimator!!.interpolator = LinearInterpolator()
        mAnimator!!.setTarget(this)
        mAnimator!!.addListener(this)
        mAnimator!!.addUpdateListener(this)
        mAnimator!!.start()
    }

    /**
     * This method is calling every time when animation is update. Y axis of balloon is then increasing.
     *
     * @param valueAnimator represent object to animate.
     * @see ValueAnimator.getAnimatedValue
     * @see ValueAnimator.AnimatorUpdateListener.onAnimationUpdate
     */
    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        y = valueAnimator.animatedValue as Float
    }

    //
    override fun onAnimationStart(animator: Animator) {}

    /**
     * This method is called when animation is finished. If user failed to pop balloon, balloon is popped mannualy and user
     * lose one life or lose the game depends on number of life that he has.
     */
    override fun onAnimationEnd(animator: Animator) {
        if (!mPopped) mListener!!.popBalloon(this, false)
    }

    //
    override fun onAnimationCancel(animator: Animator) {}

    //
    override fun onAnimationRepeat(animator: Animator) {}

    /**
     * This method is called when user touch the screen. If user pop balloon, method popBalloon is called.
     *
     * @param event represent which action user take.
     * @return boolean which return method onTouchEvent of superclass.
     * @see BalloonListener.popBalloon
     * @see android.app.Activity.onTouchEvent
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mPopped && event.action == MotionEvent.ACTION_DOWN) {
            mListener!!.popBalloon(this, true)
            mPopped = true
            mAnimator!!.cancel()
        }
        //
        return super.onTouchEvent(event)
    }

    /**
     * This method set state that represent if balloon is popped or not and if it is cancel animation.
     *
//     * @param isBalloonPopped indicate if balloon is popped or not.
     * @see ValueAnimator.cancel
     */
//    fun setPopped(isBalloonPopped: Boolean) {
//        mPopped = isBalloonPopped
//        if (isBalloonPopped) mAnimator!!.cancel()
//    }

    /**
     * This interface define one method, popBalloon which is implemented by GamePlayActivity.
     *
     * @see BalloonGameplayActivity.popBalloon
     */
    interface BalloonListener {
        /**
         * @param balloon   represent Balloon object which should be popped.
         * @param userTouch boolean which indicate if user pop balloon or not.
         */
        fun popBalloon(balloon: Balloon?, userTouch: Boolean)
    }
}