/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rtchubs.restohubs.ui.common

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.appbar.MaterialToolbar
import com.rtchubs.restohubs.AppExecutors
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.prefs.PreferencesHelper
import com.rtchubs.restohubs.ui.NavigationHost
import com.rtchubs.restohubs.util.NetworkUtils
import com.rtchubs.restohubs.util.autoCleared
import com.rtchubs.restohubs.util.showErrorToast
import dagger.android.support.DaggerFragment
import javax.inject.Inject


abstract class BaseFragment<T : ViewDataBinding, V : ViewModel> : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors
    var dialog: AlertDialog? = null

    var viewDataBinding by autoCleared<T>()

    var navHost: NavigationHost? = null

    val navController: NavController
        get() = findNavController()

    val mContext: Context
        get() = requireContext()

    val mActivity: FragmentActivity
        get() = requireActivity()

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    fun checkNetworkStatus() = if (NetworkUtils.isNetworkConnected(context)) {
        true
    } else {
        showErrorToast(requireContext(), requireContext().getString(R.string.internet_error_msg))
        false
    }

    //    val isNetworkConnected: Boolean
//        get() = NetworkUtils.isNetworkConnected(context)

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHost) {
            navHost = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        navHost = null
    }

    fun registerToolbar(toolbar: MaterialToolbar) {
        val host = navHost ?: return
        toolbar.apply {
            host.registerToolbarWithNavigation(this)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            DataBindingUtil.inflate(inflater, layoutId, container, false)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.setVariable(bindingVariable, viewModel)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.executePendingBindings()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       /* if (item.itemId == R.id.itemNotification) {
            navController().navigate(R.id.notificationListFragment)
            return true
        }
        return super.onOptionsItemSelected(item)*/
        return true
    }

    protected fun goBackToHome() {
        /*val options = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_out_right)
                .setPopEnterAnim(R.anim.slide_in_right)
                .setPopExitAnim(R.anim.slide_out_left)
                .setPopUpTo(R.id.nav_start, true)
                .build()
        navController().navigate(R.id.nav_main, null, options)*/
    }

    protected fun goToHome() {
        /*val options = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .setPopUpTo(R.id.nav_start, true)
                .build()
        navController().navigate(R.id.nav_main, null, options)*/
    }

    /**
     * Starts SmsRetriever, which waits for ONE matching SMS message until timeout
     * (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
     * action SmsRetriever#SMS_RETRIEVED_ACTION.
     */
    /*fun startSMSListener(otpListener: CBSMSBroadcastReceiver.OTPReceiveListener) {
        val client = SmsRetriever.getClient(requireContext())

        val task = client.startSmsRetriever()

        task.addOnSuccessListener { _ ->
            Log.d("BaseFragment", "Sms listener started!")
            CBSMSBroadcastReceiver.initOTPListener(otpListener)
        }
        task.addOnFailureListener { e ->
            Log.e("BaseFragment", "Failed to start sms retriever: ${e.message}")
        }
    }*/

//    fun updateStatusBarBackgroundColor(color: String) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            try {
//                requireActivity().window.statusBarColor = Color.parseColor(color)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    protected fun showToast(msg: Int) {
        try {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    protected fun showToast(msg: String) {
        try {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    protected fun showErrorSnack(msg: String) {
        try {
            //CommonUtils.showErrorSnack(viewDataBinding.root, msg)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    protected fun showErrorSnack(msg: Int) {
        try {
            //CommonUtils.showErrorSnack(viewDataBinding.root, msg)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    protected fun showSnack(msg: String) {
        try {
            //CommonUtils.showSnack(viewDataBinding.root, msg)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    protected fun showSnack(msg: Int) {
        try {
            //CommonUtils.showSnack(viewDataBinding.root, msg)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    protected fun showSuccess(msg: String? = null, callback: (() -> Unit)? = null) {
        /*if (navController().currentDestination?.id != R.id.other_page_success_dialog)
            try {
                val bundleArgs = bundleOf("message" to (msg
                        ?: "${(activity as? MainActivity1)?.supportActionBar?.title
                                ?: "Transaction"} is Successful!"), "callback" to callback)
                navigateTo(R.id.other_page_success_dialog, bundleArgs)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }*/
    }

    protected fun showFailed(msgRes: Int, tryCallback: (() -> Unit)? = null) {
        showFailed(getString(msgRes), tryCallback)
    }

    protected fun showFailed(msg: String? = null, tryCallback: (() -> Unit)? = null) {
        /*val message = if (NetworkUtils.isNetworkConnected(requireContext()))
            msg ?: "${(activity as? MainActivity1)?.supportActionBar?.title
                    ?: "Transaction"} failed!"
        else {
            getString(R.string.no_internet_error)
        }
        if (navController().currentDestination?.id != R.id.other_page_failed_dialog)
            try {
                val bundleArgs = bundleOf("message" to message, "callback" to tryCallback)
                navigateTo(R.id.other_page_failed_dialog, bundleArgs)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }*/
    }

    fun showMsg(view: View?, title: String?, message: String?, vararg listeners: DialogInterface.OnClickListener) {
        try {
            if (dialog != null && dialog!!.isShowing()) dialog?.dismiss()
            val builder = context?.let { AlertDialog.Builder(it) }
            if (title != null) builder?.setTitle(title)
            if (message != null) builder?.setMessage(message)
            if (view != null) {
                builder?.setView(view)
            }
            if (listeners.isNotEmpty()) {
                builder?.setPositiveButton(android.R.string.ok, listeners[0])
                if (listeners.size > 1) {
                    builder?.setNegativeButton(android.R.string.no, listeners[1])
                }
            }
            builder?.setCancelable(false)
            dialog = builder?.create()
            if (view != null) {
                dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
            dialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun navigateTo(navId: Int, args: Bundle? = null) {
        try {
            if (navId == navController.currentDestination?.id) {
                return // user tapped the current item
            }
            val options = navOptions {
                /*anim {
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_right
                }*/
                launchSingleTop = true
            }
            navController.navigate(navId, args, options)
        } catch (e: Exception) {

        }
    }

    fun navigateTo(direction: NavDirections) {
        try {
            navController.navigate(direction)
        } catch (e: Exception) {

        }
    }

    open fun backFromAuth(isVerified: Boolean) {
        /*if (!isVerified) {
            Toast.makeText(requireContext(),
                    getString(R.string.user_unauthorized), Toast.LENGTH_LONG).show()
        }*/
    }

    fun redirectToNextScreen() {
        /*profileStatusViewModel.profileInfo.observeTillNotNull(this
                , Observer {
            it?.let { data ->
                val fragmentId = when {
                    data.userName.isNullOrEmpty() -> R.id.nav_auth
                    !data.isMobileVerified -> R.id.mobileVerificationFragment
                    !data.pinSet -> R.id.setPinFragment
                    !data.isEmailVerified -> R.id.emailVerificationFragment
                    !data.getIsNationalIdProvide() -> R.id.nidFragment
                    data.profileImage.isNullOrBlank() -> R.id.profilePicFragment
                    else -> R.id.nav_main
                }
                val navController = findNavController()
                if (navController.currentDestination?.id != fragmentId)
                    try {
                        val options = NavOptions.Builder()
                                .setEnterAnim(R.anim.slide_in_right)
                                .setExitAnim(R.anim.slide_out_left)
                                .setPopEnterAnim(R.anim.slide_in_left)
                                .setPopExitAnim(R.anim.slide_out_right)
                                .setPopUpTo(R.id.nav_start, true)
                                .build()
                        navController.navigate(fragmentId, null, options)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
            }

        })*/
    }


    companion object {
        const val REFRESH = "refresh"
    }


}