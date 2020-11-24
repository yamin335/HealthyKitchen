package com.rtchubs.restohubs.ui.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.databinding.RegistrationBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class RegistrationFragment  : BaseFragment<RegistrationBinding, RegistrationViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_registration
    override val viewModel: RegistrationViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.btnGetOtp.setOnClickListener {
        }
    }
}