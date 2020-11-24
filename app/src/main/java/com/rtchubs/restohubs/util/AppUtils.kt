package com.rtchubs.restohubs.util

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.rtchubs.restohubs.R
import kotlinx.android.synthetic.main.toast_custom_error.view.*
import kotlinx.android.synthetic.main.toast_custom_success.view.*
import kotlinx.android.synthetic.main.toast_custom_warning.view.*

fun showErrorToast(context: Context, message: String) {
    val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val toastView = inflater.inflate(R.layout.toast_custom_error, null)
    toastView.errorMessage.text = message
    toast.view = toastView
    toast.show()
}

fun showWarningToast(context: Context, message: String) {
    val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val toastView = inflater.inflate(R.layout.toast_custom_warning, null)
    toastView.warningMessage.text = message
    toast.view = toastView
    toast.show()
}



fun showSuccessToast(context: Context, message: String) {
    val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val toastView = inflater.inflate(R.layout.toast_custom_success, null)
    toastView.successMessage.text = message
    toast.view = toastView
    toast.show()
}