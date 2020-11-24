package com.rtchubs.restohubs.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.prefs.PreferencesHelper
import com.rtchubs.restohubs.util.autoCleared
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

/**
 * Created by Priyanka on 8/4/19.
 */
abstract class BaseDialogFragment<T : ViewDataBinding> : DaggerAppCompatDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    abstract val viewModel: ViewModel
    abstract val bindingVariable: Int
    open val isFullScreen = true
    open val showAnim = true
    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    var viewDataBinding by autoCleared<T>()

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    val navController by lazy {
        findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isFullScreen)
            setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
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


    override fun onStart() {
        super.onStart()
        dialog?.let {
            if (isFullScreen) {
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                it.window?.setLayout(width, height)
            }
            if (showAnim) {
                it.window?.setWindowAnimations(R.style.AppTheme_Slide)
            }
        }
    }
/*
    protected fun gotoHome() {
        if (!navController.popBackStack(R.id.homeFragment, false)) {
            val options = NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_left)
                    .setExitAnim(R.anim.slide_out_right)
                    .setPopEnterAnim(R.anim.slide_in_right)
                    .setPopExitAnim(R.anim.slide_out_left)
                    .setPopUpTo(R.id.nav_start, true)
                    .build()
            navController.navigate(R.id.nav_main, null, options)
        }
    }*/

}