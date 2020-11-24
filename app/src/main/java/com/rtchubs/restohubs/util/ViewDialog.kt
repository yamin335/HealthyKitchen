package com.rtchubs.restohubs.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.Window
import android.widget.ImageView
import com.rtchubs.restohubs.R

/**
 * Created by Priyanka on 13/11/18.
 */
class ViewDialog(context: Context) {
    var dialog: Dialog? = Dialog(context)
    fun showDialog() {
        dialog?.show()
    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        dialog?.dismiss()
    }

    val isShowing: Boolean
        get() = dialog?.isShowing == true

    //..we need the context else we can not create the dialog so get context in constructor
    init {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //...set cancelable false so that it's never get hidden
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        //...that's the layout i told you will inflate later
        dialog?.setContentView(R.layout.alert_loading)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //...initialize the imageView form infalted layout
        val imageView =
            dialog?.findViewById<ImageView>(R.id.custom_loading_imageView)
        /* */ /*
        it was never easy to load gif into an ImageView before Glide or Others library
        and for doing this we need DrawableImageViewTarget to that ImageView
        */
/*
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);

        //...now load that gif which we put inside the drawble folder here with the help of Glide

        Glide.with(activity).load(R.drawable.loading_new).placeholder(R.drawable.loading_new).centerCrop().crossFade().into(imageViewTarget);*/
//...finaly show it
        dialog?.setOnShowListener {
            try {
                (imageView?.background as AnimationDrawable?)?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dialog?.setOnCancelListener {
            try {
                (imageView?.background as AnimationDrawable?)?.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}