package com.rtchubs.restohubs.ui.home

import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.MoreFragmentBinding
import com.rtchubs.restohubs.databinding.SetAFragmentBinding
import com.rtchubs.restohubs.databinding.SetCFragmentBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class SetCFragment : BaseFragment<SetCFragmentBinding, SetCViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_set_c

    override val viewModel: SetCViewModel by viewModels { viewModelFactory }
}