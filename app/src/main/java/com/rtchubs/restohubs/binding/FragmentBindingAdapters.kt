/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rtchubs.restohubs.binding

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.api.ApiCallStatus
import com.rtchubs.restohubs.util.*


/**
 * Binding adapters that work with a fragment instance.
 */

class FragmentBindingAdapters {

    @BindingAdapter("showLoader")
    fun showLoader(view: View, apiCallStatus: Int?) {
        view.visibility = if (apiCallStatus == ApiCallStatus.LOADING) View.VISIBLE else View.INVISIBLE
    }

    @BindingAdapter(value = ["imageUrl", "imageRequestListener"], requireAll = false)
    fun bindImage(imageView: ImageView, url: String?, listener: RequestListener<Drawable?>?) {
        Glide.with(imageView.context).load(url).listener(listener).into(imageView)
    }

    @BindingAdapter("visibleGone", "animGravity", requireAll = false)
    fun showHide(view: View, show: Boolean, animGravity: Int? = null) {
        animGravity?.let {
            val transition: Transition = Slide().apply {
                slideEdge = animGravity
                mode = if (show) Slide.MODE_IN else Slide.MODE_OUT
            }
            transition.duration = 600
            transition.addTarget(view)

            TransitionManager.beginDelayedTransition(view.parent as ViewGroup, transition)
        }

        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @BindingAdapter("visibleInvisible")
    fun showInvisible(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    @BindingAdapter("android:src")
    fun bindImage(imageView: ImageView, resName: String) {
        val context = imageView.context
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        imageView.setImageResource(if (resId == 0) R.drawable.engineering_logo else resId)
    }

    @BindingAdapter("android:src")
    fun bindImage(imageView: ImageView, @DrawableRes resId: Int?) {
        resId?.let { imageView.setImageResource(it) }
    }


    @BindingAdapter("android:text")
    fun bindText(textView: TextView, @StringRes resId: Int) {
        try {
            textView.text = textView.context.getString(resId)
        } catch (e: Exception) {

        }
    }

 /*   @BindingAdapter(
        "imageUrl", "placeHolder",
        "errorDrawable", requireAll = false
    )
    fun bindImage(
        imageView: ImageView, url: String?,
        placeholder: Drawable?, errorDrawable: Drawable?
    ) {
        if (placeholder != null) imageView.setImageDrawable(placeholder)
        if (!url.isNullOrBlank()) {
            placeholder?.let { imageView.setTag(R.integer.placeholder, placeholder) }

            errorDrawable?.let { imageView.setTag(R.integer.errorDrawable, errorDrawable) }
            val requestCreator = Picasso.get()
                .load(if (url == null || url.contains("http")) url else MEDIA_URL + url)
            val placeholderLatest = imageView.getTag(R.integer.placeholder) as Drawable?
            val errorDrawableLatest = imageView.getTag(R.integer.errorDrawable) as Drawable?

            placeholderLatest?.let { requestCreator.placeholder(it).into(imageView) }
                ?: requestCreator.placeholder(R.drawable.place_holder)
            errorDrawableLatest?.let { requestCreator.error(it).into(imageView) }
            *//* ?: requestCreator.error(R.drawable.no_image_available)*//*

            requestCreator.fit().into(imageView)
        }
    }*/

    @BindingAdapter("imageBitmap")
    fun bindImage(imageView: ImageView, bitmap: Bitmap?) {
        imageView.setImageBitmap(bitmap)
    }

    @BindingAdapter("txtColor")
    fun bindTextColor(textView: TextView, res: Int?) {
        res?.let { textView.setTextColor(ContextCompat.getColor(textView.context, it)) }
    }

    @BindingAdapter("textToFormat", "textArgs")
    fun setFormattedValue(view: TextView, textToFormat: String, value: Any?) {
        view.text = Html.fromHtml(String.format(textToFormat, value))
    }
    @BindingAdapter("pass", "oldPass", requireAll = false)
    fun bindPassValidation(textInputLayout: TextInputLayout, pass: String?, oldPass: String?) {
        when {
            pass.isNullOrBlank() -> {
                textInputLayout.error = null
            }
            pass == oldPass -> {
                //textInputLayout.error = textInputLayout.context.getString(R.string.pin_same_error)
            }
        }
    }

    @BindingAdapter("validation", "errorMsg", requireAll = false)
    fun bindValidation(textInputLayout: TextInputLayout, validator: Validator?, errorMsg: Int?) {
        textInputLayout.editText?.afterTextChanged {
            if (validator == null) {
                if (!textInputLayout.error.isNullOrBlank())
                    textInputLayout.error = null
            } else textInputLayout.error = when {
                validator.validate(it) -> null
                else -> when {
                    it.isBlank() -> null
                    else -> textInputLayout.context.getString(
                        errorMsg
                            ?: validator?.errorMsg
                    )
                }

            }
        }
    }

    @BindingAdapter("android:longClickable")
    fun disableCopyPaste(editText: EditText, enableCopyPaste: Boolean) {
        if (!enableCopyPaste) {
            val actionModeCallBack = object : ActionMode.Callback {

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode) {}

                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    return false
                }
            }
            editText.customSelectionActionModeCallback = actionModeCallBack
            editText.setTextIsSelectable(false)
            editText.isLongClickable = false
        }
    }

    @BindingAdapter("validLength", "errorMsg", requireAll = false)
    fun bindLengthValidation(
        textInputLayout: TextInputLayout,
        validLength: Int,
        errorMsg: String?
    ) {
        textInputLayout.editText?.afterTextChanged {
            textInputLayout.error = when {
                it.isBlank() -> null
                it.length < validLength -> errorMsg ?: String.format(
                    textInputLayout.context
                        .getString(R.string.invalid_length), validLength
                )
                else -> null
            }
        }
    }

    /*@BindingAdapter("newPass", "confirmPass")
    fun bindCheckConfirmPass(
        textInputLayout: TextInputLayout,
        newPass: String?,
        confirmPass: String?
    ) {
        textInputLayout.error = when {
            !confirmPass.isNullOrEmpty() && newPass != confirmPass -> {
                textInputLayout.context.getString(R.string.error_confirm_pin)
            }
            else -> null
        }
    }*/

    @BindingAdapter("android:onClick", "listOfTil")
    fun bindValidationToButton(
        button: Button,
        clickListener: View.OnClickListener,
        textFields: List<View>?
    ) {
        button.setOnClickListener {
            if (textFields.isNullOrEmpty() || textFields.isValid()) {
                clickListener.onClick(button)
            }
        }
    }

    @BindingAdapter("android:drawableRight")
    fun setDrawableRight(view: TextView, resourceId: Int) {
        val drawable = ContextCompat.getDrawable(view.context, resourceId)
        val drawables = view.compoundDrawables
        view.setCompoundDrawablesWithIntrinsicBounds(
            drawables[0],
            drawables[1],
            drawable,
            drawables[3]
        )
    }

    @BindingAdapter("currIndex")
    fun currentIndexBinding(viewFlipper: ViewFlipper, index: Int) {
        val currentDisplayedIndex = viewFlipper.displayedChild
        val context = viewFlipper.context
        if (currentDisplayedIndex != index) {
            val duration = 1000L
            val inAnimation: Int
            val outAnimation: Int
            if (currentDisplayedIndex < index) {
                inAnimation = R.anim.slide_in_right
                outAnimation = R.anim.slide_out_left
            } else {
                inAnimation = R.anim.slide_in_left
                outAnimation = R.anim.slide_out_right
            }
            viewFlipper.inAnimation = AnimationUtils.loadAnimation(
                context, inAnimation
            ).apply { setDuration(duration) }
            viewFlipper.outAnimation = AnimationUtils.loadAnimation(
                context, outAnimation
            ).apply { setDuration(duration) }
            viewFlipper.displayedChild = index
        }
    }

    @BindingAdapter("currIndex")
    fun currentIndexBinding(tabLayout: TabLayout, index: Int) {
        if (index != tabLayout.selectedTabPosition)
            tabLayout.getTabAt(index)?.select()
    }

    /*  @BindingAdapter("selection")
      fun setSelection(spinner: SearchableSpinner, index: Int) {
          if (index != spinner.selection)
              spinner.selection = index
      }

      @BindingAdapter("selectedItem")
      fun setSelection(spinner: SearchableSpinner, selectedItem: Any?) {
          if (selectedItem != spinner.selectedItem)
              spinner.selectedItem = selectedItem
      }*/

    @BindingAdapter("isRefreshing")
    fun setRefreshing(swipeRefreshLayout: SwipeRefreshLayout, isRefreshing: Boolean) {
        swipeRefreshLayout.isRefreshing = isRefreshing
    }


    @BindingAdapter("backgroundTint")
    fun setBackgroundTint(view: View, @ColorRes colorRes: Int?) {
        try {
            colorRes?.let {
                ViewCompat.setBackgroundTintList(
                    view, ContextCompat.getColorStateList(
                        view.context,
                        colorRes
                    )
                )
            }
        } catch (e: Exception) {

        }

    }

    @BindingAdapter("enabled")
    fun setEnabled(view: View, enable: Boolean?) {
        enable?.let {
            view.isEnabled = it
        }
    }

    @BindingAdapter("android:hint")
    fun bindHint(view: TextInputLayout, @StringRes resId: Int) {
        try {
            view.hint = view.context.getString(resId)
        } catch (e: Exception) {

        }

    }

    @BindingAdapter("android:error")
    fun bindError(view: TextInputLayout, error: Any?) {
        try {
            view.error = if (error is Int) view.context.getString(error)
            else if (error is String) error else null
            return
        } catch (e: Exception) {

        }
        view.isErrorEnabled = !view.error.isNullOrBlank()
    }

    @BindingAdapter("websiteLink", "hideWhenEmpty", requireAll = false)
    fun websiteLink(
        button: View,
        url: String?,
        hideWhenEmpty: Boolean = false
    ) {
        if (url.isNullOrEmpty()) {
            if (hideWhenEmpty) {
                button.isVisible = false
            } else {
                button.isClickable = false
            }
            return
        }
        button.isVisible = true
        button.setOnClickListener {
            openWebsiteUrl(it.context, url)
        }
    }

    /*@BindingAdapter(
        "thisNumber",
        "otherNumber",
        "selfNumber",
        "checkSameNumber",
        "checkSelfNumber",
        requireAll = false
    )
    fun bindCheckSameNumber(
        til: TextInputLayout,
        thisNumber: String?,
        otherNumber: String?,
        selfNumber: String?,
        checkSameNumber: Boolean = true,
        checkSelfNumber: Boolean = true
    ) {
        til.error =
            if (thisNumber.isNullOrEmpty())
                null
            else if (!MobileValidator.validate(thisNumber))
                til.context.getString(R.string.error_invalid_mobile)
            else if (checkSelfNumber && thisNumber.sameMobileNumber(selfNumber)) {
                til.context.getString(R.string.own_wallet_error)
            } else if (checkSameNumber && thisNumber.sameMobileNumber(otherNumber))
                til.context.getString(R.string.sender_receiver_match_error)
            else null
    }*/

}



