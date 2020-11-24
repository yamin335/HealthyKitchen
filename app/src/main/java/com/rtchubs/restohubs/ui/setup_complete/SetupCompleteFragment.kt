package com.rtchubs.restohubs.ui.setup_complete

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.databinding.SetupCompleteBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class SetupCompleteFragment  : BaseFragment<SetupCompleteBinding, SetupCompleteViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_setup_complete
    override val viewModel: SetupCompleteViewModel by viewModels { viewModelFactory }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.btnContinue.setOnClickListener {
        }
    }

}