package com.rtchubs.restohubs.ui.login

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.rtchubs.restohubs.R
import kotlin.properties.Delegates

class SliderView(context: Context) : BaseSliderView(context) {

    lateinit var sliderTextTitle: String
    lateinit var sliderTextDescription: String
    var sliderImage by Delegates.notNull<Int>()

    override fun getView(): View {
        val v: View = LayoutInflater.from(context).inflate(R.layout.custom_slide_view, null)
        val target: ImageView = v.findViewById(R.id.slideImage)
        val titleText: TextView = v.findViewById(R.id.slideTitleText)
        val descText: TextView = v.findViewById(R.id.slideDescriptionText)

        titleText.text = sliderTextTitle
        descText.text = sliderTextDescription
        target.setImageResource(sliderImage)

        return v
    }
    /**
     * the description of a slider image.
     * @param description
     * @return
     */
    fun sliderImage(res: Int): SliderView? {
        sliderImage = res
        return this
    }
    /**
     * the description of a slider image.
     * @param description
     * @return
     */
    fun sliderTextDescription(description: String): SliderView? {
        sliderTextDescription = description
        return this
    }

    /**
     * the description of a slider image.
     * @param description
     * @return
     */
    fun sliderTextTitle(title: String): SliderView? {
        sliderTextTitle = title
        return this
    }
}