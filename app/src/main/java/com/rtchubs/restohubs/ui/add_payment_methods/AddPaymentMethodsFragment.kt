package com.rtchubs.restohubs.ui.add_payment_methods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.LayoutAddPaymentMethodBinding
import com.rtchubs.restohubs.models.payment_account_models.BankOrCardListResponse
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.util.showWarningToast

class AddPaymentMethodsFragment :
    BaseFragment<LayoutAddPaymentMethodBinding, AddPaymentMethodsViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.layout_add_payment_method
    override val viewModel: AddPaymentMethodsViewModel by viewModels {
        viewModelFactory
    }

    lateinit var bankListAdapter: BankOrCardListAdapter

    override fun onResume() {
        super.onResume()

        when (selectedAccountType) {
            "card" -> {
                viewDataBinding.tabs.getTabAt(0)?.select()
            }
            "bank" -> {
                viewDataBinding.tabs.getTabAt(1)?.select()
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerToolbar(viewDataBinding.toolbar)

        bankListAdapter = BankOrCardListAdapter(
                appExecutors
            ) { item ->
                when (selectedAccountType) {
                    "card" -> {
                        navController.navigate(AddPaymentMethodsFragmentDirections.actionAddPaymentMethodsFragmentToAddCardFragment(item))
                    }

                    "bank" -> {
                        navController.navigate(AddPaymentMethodsFragmentDirections.actionAddPaymentMethodsFragmentToAddBankFragment(item))
                    }
                }
        }
        viewDataBinding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> {
                            selectedAccountType = "card"
                            viewModel.requestBankList(selectedAccountType)
                        }

                        1 -> {
                            selectedAccountType = "bank"
                            viewModel.requestBankList(selectedAccountType)
                        }
                    }
                }
            }

        })

//        viewDataBinding.toolbar.setNavigationOnClickListener {
//            Navigation.findNavController(viewDataBinding.root).navigateUp()
//        }

        viewModel.bankOrCardListResponse.observe(
            viewLifecycleOwner,
            Observer<BankOrCardListResponse> { response ->
                response?.let { validResponse ->
                    when {
                        validResponse.isSuccess == true -> {
                            validResponse.body?.banks?.let { list ->
                                bankListAdapter.submitList(list)
                                viewDataBinding.recyclerBankOrCardList.adapter = bankListAdapter
                            }
                        }
                        validResponse.isSuccess == false && validResponse.errorMessage != null -> {
                            showWarningToast(mContext, "")
                        }
                        else -> {
                            showWarningToast(mContext, getString(R.string.something_went_wrong))
                        }
                    }
                }
            })
        viewModel.requestBankList(selectedAccountType)
    }

    companion object {
        var selectedAccountType = "card"
    }
}