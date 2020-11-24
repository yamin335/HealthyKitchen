package com.rtchubs.restohubs.util

import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.util.*

/**
 * Created by Priyanka on 17/4/19.
 */
class ContextWrapper(base: Context) : android.content.ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
            val res = context.resources
            val configuration = res.configuration
            when {
                Build.VERSION.SDK_INT > Build.VERSION_CODES.N -> {
                    configuration.setLocale(newLocale)

                    val localeList = LocaleList(newLocale)
                    LocaleList.setDefault(localeList)
                    configuration.setLocales(localeList)

                    return ContextWrapper(context.createConfigurationContext(configuration))

                }
                Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN -> {
                    configuration.setLocale(newLocale)
                    return ContextWrapper(context.createConfigurationContext(configuration))

                }
                else -> {
                    configuration.locale = newLocale
                    res.updateConfiguration(configuration, res.displayMetrics)
                }
            }

            return ContextWrapper(context)
        }


        @BindingAdapter("android:src")
        @JvmStatic
        fun setImage(imageView:ImageView,url:Any){
            Glide.with(imageView.context).load(url).into(imageView)
        }
    }
}