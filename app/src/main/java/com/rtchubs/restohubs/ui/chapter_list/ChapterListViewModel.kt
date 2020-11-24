package com.rtchubs.restohubs.ui.chapter_list

import androidx.lifecycle.ViewModel
import com.rtchubs.restohubs.models.Chapter
import com.rtchubs.restohubs.prefs.PreferencesHelper
import javax.inject.Inject

class ChapterListViewModel @Inject constructor(private val preferencesHelper: PreferencesHelper) :
    ViewModel() {

    val chapterListData = listOf<Chapter>(
        Chapter("1", "Chapter One", null, null),
        Chapter("2", "Chapter Two", null, null),
        Chapter("3", "Chapter Three", null, null),
        Chapter("4", "Chapter Four", null, null),
        Chapter("5", "Chapter Five", null, null),
        Chapter("6", "Chapter Six", null, null),
        Chapter("7", "Chapter Seven", null, null),
        Chapter("8", "Chapter Eight", null, null),
        Chapter("9", "Chapter Nine", null, null),
        Chapter("10", "Chapter Ten", null, null)
    )
}