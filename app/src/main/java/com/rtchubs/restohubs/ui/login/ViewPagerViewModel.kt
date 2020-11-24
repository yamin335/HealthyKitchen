package com.rtchubs.restohubs.ui.login

import androidx.lifecycle.ViewModel
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.prefs.PreferencesHelper
import javax.inject.Inject

class ViewPagerViewModel @Inject constructor(private val preferencesHelper: PreferencesHelper) :
    ViewModel() {
    val slideDataList = listOf<SlideData>(
        SlideData(R.drawable.slider_image_1, "Learn with online tutor", "A Complete Solution for e-learning"),
        SlideData(R.drawable.slider_image_1, "Learn with online tutor", "A Complete Solution for e-learning"),
        SlideData(R.drawable.slider_image_1, "Learn with online tutor", "A Complete Solution for e-learning"),
        SlideData(R.drawable.slider_image_1, "Learn with online tutor", "A Complete Solution for e-learning"),
        SlideData(R.drawable.slider_image_1, "Learn with online tutor", "A Complete Solution for e-learning")
    )

    inner class SlideData(
        var slideImage: Int,
        var textTitle: String,
        var descText: String
    )
}