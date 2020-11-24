package com.rtchubs.restohubs.ui.home

import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.MoreFragmentBinding
import com.rtchubs.restohubs.databinding.SetAFragmentBinding
import com.rtchubs.restohubs.databinding.SetBFragmentBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class SetBFragment : BaseFragment<SetBFragmentBinding, SetBViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_set_b

    override val viewModel: SetBViewModel by viewModels { viewModelFactory }
}