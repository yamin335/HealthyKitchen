package com.rtchubs.restohubs.ui.exams

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.databinding.ExamsFragmentBinding
import com.rtchubs.restohubs.databinding.SettingsBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class ExamsFragment : BaseFragment<ExamsFragmentBinding, ExamsViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_exams
    override val viewModel: ExamsViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}