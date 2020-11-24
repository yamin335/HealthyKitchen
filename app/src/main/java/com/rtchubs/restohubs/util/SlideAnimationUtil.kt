package com.rtchubs.restohubs.util

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.rtchubs.restohubs.R


/**
 * Created by Priyanka on 9/19/18.
 */
object SlideAnimationUtil {

    /**
     * Animates a view so that it slides in from the left of it's container.
     *
     * @param context
     * @param view
     */
    fun slideInFromLeft(context: Context, view: View, animEndCallback: (() -> Unit)? = null) {
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            runSimpleAnimation(context, view, R.anim.slide_in_left, animEndCallback)
        }
    }

    /**
     * Animates a view so that it slides from its current position, out of view to the left.
     *
     * @param context
     * @param view
     */
    fun slideOutToLeft(context: Context, view: View, animEndCallback: (() -> Unit)? = null) {
        if (view.visibility == View.VISIBLE) {
            runSimpleAnimation(context, view, R.anim.slide_out_left, animEndCallback)
            view.visibility = View.GONE
        }
    }

    /**
     * Animates a view so that it slides in the from the right of it's container.
     *
     * @param context
     * @param view
     */
    fun slideInFromRight(context: Context, view: View, animEndCallback: (() -> Unit)? = null) {
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            runSimpleAnimation(context, view, R.anim.slide_in_right, animEndCallback)
        }
    }

    fun slideInFromTop(context: Context, view: View, animEndCallback: (() -> Unit)? = null) {
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            runSimpleAnimation(context, view, R.anim.slide_down, animEndCallback)
        }
    }

    fun slideInFromBottom(context: Context, view: View, animEndCallback: (() -> Unit)? = null) {
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            runSimpleAnimation(context, view, R.anim.slide_up, animEndCallback)
        }
    }

    fun slideOutToTop(context: Context, view: View, animEndCallback: (() -> Unit)? = null) {
        if (view.visibility == View.VISIBLE) {
            runSimpleAnimation(context, view, R.anim.slide_up, animEndCallback)
            view.visibility = View.GONE
        }
    }

    fun slideOutToBottom(context: Context, view: View, animEndCallback: (() -> Unit)? = null) {
        if (view.visibility == View.VISIBLE) {
            runSimpleAnimation(context, view, R.anim.slide_down, animEndCallback)
            view.visibility = View.GONE
        }
    }

    /**
     * Animates a view so that it slides from its current position, out of view to the right.
     *
     * @param context
     * @param view
     */
    fun slideOutToRight(context: Context, view: View, animEndCallback: (() -> Unit)? = null) {
        if (view.visibility == View.VISIBLE) {
            runSimpleAnimation(context, view, R.anim.slide_out_right, animEndCallback)
            view.visibility = View.GONE
        }
    }

    /**
     * Runs a simple animation on a View with no extra parameters.
     *
     * @param context
     * @param view
     * @param animationId
     */
    private fun runSimpleAnimation(
        context: Context,
        view: View,
        animationId: Int,
        animEndCallback: (() -> Unit)?
    ) {
        view.startAnimation(
            AnimationUtils.loadAnimation(
                context, animationId
            ).apply {
                animEndCallback?.let {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(p0: Animation?) {
                        }

                        override fun onAnimationEnd(p0: Animation?) {
                            it.invoke()
                        }

                        override fun onAnimationStart(p0: Animation?) {
                        }

                    })
                }

            })
    }

}