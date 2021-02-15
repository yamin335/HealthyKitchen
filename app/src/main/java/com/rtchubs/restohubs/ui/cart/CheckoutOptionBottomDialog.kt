package com.rtchubs.restohubs.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.binding.FragmentDataBindingComponent
import com.rtchubs.restohubs.databinding.CheckoutOptionBottomDialogBinding
import com.rtchubs.restohubs.util.autoCleared

class CheckoutOptionBottomDialog constructor(private val callback: CheckoutOptionBottomDialogCallback) : BottomSheetDialogFragment() {

    private var binding by autoCleared<CheckoutOptionBottomDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_bottom_checkout_options,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGuest.setOnClickListener {
            callback.onGuestSelected()
        }

        binding.btnLogin.setOnClickListener {
            callback.onLoginSelected()
        }
    }

    interface CheckoutOptionBottomDialogCallback {
        fun onGuestSelected()
        fun onLoginSelected()
    }
}