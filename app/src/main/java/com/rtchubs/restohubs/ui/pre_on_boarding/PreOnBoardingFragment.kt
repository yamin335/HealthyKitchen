package com.rtchubs.restohubs.ui.pre_on_boarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.databinding.PreOnBoardBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class PreOnBoardingFragment() : BaseFragment<PreOnBoardBinding, PreOnBoardingViewModel>() {
    override val bindingVariable: Int
        get() = BR._all
    override val layoutId: Int
        get() = R.layout.fragment_pre_onbarding
    override val viewModel: PreOnBoardingViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#689F38")
        viewDataBinding.btnOnboardingStart.setOnClickListener {
        }
    }
}