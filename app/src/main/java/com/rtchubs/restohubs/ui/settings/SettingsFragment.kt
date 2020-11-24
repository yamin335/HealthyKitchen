package com.rtchubs.restohubs.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.databinding.SettingsBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class SettingsFragment : BaseFragment<SettingsBinding, SettingsViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_settings
    override val viewModel: SettingsViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}