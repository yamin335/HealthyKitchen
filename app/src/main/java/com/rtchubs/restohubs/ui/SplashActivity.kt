package com.rtchubs.restohubs.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.MainActivityBinding
import com.rtchubs.restohubs.databinding.SplashActivityBinding
import com.rtchubs.restohubs.databinding.SplashBinding
import com.rtchubs.restohubs.ui.splash.SplashViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SplashViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private lateinit var binding: SplashActivityBinding
    private lateinit var animation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the status bar.
        hideSystemUI()

        binding = DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)
        binding.lifecycleOwner = this

        animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                runBlocking {
                    launch {
                        delay(1200L)
                    }

                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    this@SplashActivity.finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                }
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })

        binding.logo.startAnimation(animation)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}