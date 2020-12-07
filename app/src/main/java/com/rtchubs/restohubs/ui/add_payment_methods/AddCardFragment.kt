package com.rtchubs.restohubs.ui.add_payment_methods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.AddBankFragmentBinding
import com.rtchubs.restohubs.databinding.AddCardFragmentBinding
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.util.AppConstants
import com.rtchubs.restohubs.util.CreditCardFormatTextWatcher
import com.rtchubs.restohubs.util.showSuccessToast
import com.rtchubs.restohubs.util.showWarningToast

class AddCardFragment : BaseFragment<AddCardFragmentBinding, AddCardViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_add_card
    override val viewModel: AddCardViewModel by viewModels {
        viewModelFactory
    }

    lateinit var creditCardFormatTextWatcher: CreditCardFormatTextWatcher

    val args: AddCardFragmentArgs by navArgs()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mActivity.window?.setSoftInputMode(
//            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//        )
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        requireActivity().window?.setSoftInputMode(
//            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
//        )
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        viewModel.bankId = args.selectedCard.id ?: 0

        creditCardFormatTextWatcher = CreditCardFormatTextWatcher(viewDataBinding.etCardNumber)
        viewDataBinding.etCardNumber.addTextChangedListener(creditCardFormatTextWatcher)

        viewModel.cardNumber.observe(viewLifecycleOwner, Observer { cardNummber ->
            cardNummber?.let { validCardNumber ->
                val year = viewModel.expiryYear.value
                val month = viewModel.expiryMonth.value
                if (year != null && month != null) {
                    viewDataBinding.btnAdd.isEnabled = validCardNumber.length == 16 && year.length == 2 && month.length == 2
                }
            }
        })

        viewModel.expiryMonth.observe(viewLifecycleOwner, Observer { expiryMonth ->
            expiryMonth?.let { validExpiryMonth ->
                val year = viewModel.expiryYear.value
                val number = viewModel.cardNumber.value
                if (year != null && number != null) {
                    viewDataBinding.btnAdd.isEnabled = validExpiryMonth.length == 2 && year.length == 2 && number.length == 16
                }
            }
        })

        viewModel.expiryYear.observe(viewLifecycleOwner, Observer { expiryYear ->
            expiryYear?.let { validExpiryYear ->
                val number = viewModel.cardNumber.value
                val month = viewModel.expiryMonth.value
                if (number != null && month != null) {
                    viewDataBinding.btnAdd.isEnabled = validExpiryYear.length == 2 && month.length == 2 && number.length == 16
                }
            }
        })

        viewModel.addCardAccountResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.let { validResponse ->
                when {
                    validResponse.isSuccess == true && validResponse.body?.isSuccess == true-> {
                        showSuccessToast(mContext, validResponse.body.message ?: AppConstants.saveSuccessfulMessage)
                        navController.navigate(AddBankFragmentDirections.actionAddBankFragmentToHome2Fragment())
                    }
                    validResponse.isSuccess == false && validResponse.errorMessage != null -> {
                        showWarningToast(mContext, validResponse.errorMessage)
                    }
                    else -> {
                        showWarningToast(mContext, AppConstants.commonErrorMessage)
                    }
                }
            }
        })
    }
}