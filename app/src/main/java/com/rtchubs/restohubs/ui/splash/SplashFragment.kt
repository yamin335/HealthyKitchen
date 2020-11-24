package com.rtchubs.restohubs.ui.splash

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.databinding.SplashBinding
import com.rtchubs.restohubs.ui.LoginHandlerCallback
import com.rtchubs.restohubs.ui.common.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SplashFragment : BaseFragment<SplashBinding, SplashViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_splash

    override val viewModel: SplashViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var animation: Animation

    private var listener: LoginHandlerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginHandlerCallback) {
            listener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (fromLogout) {
            //findNavController().navigate(SplashFragmentDirections.actionSplashToLogin())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateStatusBarBackgroundColor("#1E4356")

        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                runBlocking {
                    launch {
//                        preferences.edit().apply {
//                            putBoolean("goToLogin", true)
//                            apply()
//                        }
                        delay(1200L)
                    }

                    if (preferencesHelper.isLoggedIn) {
                        listener?.onLoggedIn()
                    } else {
                        //findNavController().navigate(SplashFragmentDirections.actionSplashToLogin())
                    }
                }
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })

        viewDataBinding.logo.startAnimation(animation)

//        viewModel.getAuthenticationState().observe(viewLifecycleOwner, Observer {
//            it?.let { authenticateState ->
//                when (authenticateState) {
//                    SplashViewModel.AuthenticationState.AUTHENTICATED -> {
//                        navController().navigate(R.id.action_splash_to_main)
//                    }
//                    SplashViewModel.AuthenticationState.UNAUTHENTICATED -> {
//                        navController().navigate(R.id.action_splash_to_auth)
//                        //redirectToNextScreen()
//                    }
//                }
//            }
//        })
    }

    companion object {
        var fromLogout = false
    }
}