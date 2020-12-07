package com.rtchubs.restohubs.ui.topup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.databinding.TopUpPinFragmentBinding
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.util.hideKeyboard

class TopUpPinFragment : BaseFragment<TopUpPinFragmentBinding, TopUpPinViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_topup_pin
    override val viewModel: TopUpPinViewModel by viewModels {
        viewModelFactory
    }

    val args: TopUpAmountFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        val helper = args.topUpHelper

        viewModel.pin.observe(viewLifecycleOwner, Observer {  pin ->
            pin?.let {
                helper.mobile = pin
                viewDataBinding.btnProceed.isEnabled = (it.length == 6)
            }
        })

        viewDataBinding.btnProceed.setOnClickListener {
            hideKeyboard()
            navController.navigate(TopUpPinFragmentDirections.actionTopUpPinFragmentToHome2Fragment())
        }
    }
}