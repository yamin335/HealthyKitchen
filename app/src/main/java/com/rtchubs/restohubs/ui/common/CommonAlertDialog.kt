package com.rtchubs.restohubs.ui.common

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rtchubs.restohubs.R

class CommonAlertDialog internal constructor(private val callBack: YesCallback, private val title: String, private val subTitle: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val exitDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setIcon(R.mipmap.ic_launcher)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                callBack.onYes()
                dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        if (subTitle.isNotBlank()) {
            exitDialog.setMessage(subTitle)
        }
        return exitDialog.create()
    }

    interface YesCallback{
        fun onYes()
    }
}