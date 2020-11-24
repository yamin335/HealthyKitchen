package com.rtchubs.restohubs.ui.otp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.databinding.OtpBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class OtpFragment  : BaseFragment<OtpBinding, OtpViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_otp
    override val viewModel: OtpViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.btnVerify.setOnClickListener {
        }
    }
}