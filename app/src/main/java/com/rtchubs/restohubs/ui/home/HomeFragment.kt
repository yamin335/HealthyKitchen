package com.rtchubs.restohubs.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.navOptions
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.HomeBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class HomeFragment : BaseFragment<HomeBinding, HomeViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModels {
        viewModelFactory
    }

    lateinit var doctorsListAdapter: DoctorsListAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doctorsListAdapter = DoctorsListAdapter(appExecutors) {
            //navController.navigate(HomeFragmentDirections.actionBooksToChapterList(it))
        }
        doctorsListAdapter.submitList(viewModel.doctorList)

        viewDataBinding.rvDoctorsList.adapter = doctorsListAdapter

        viewDataBinding.bottomNavigationView.setOnNavigationItemSelectedListener {
            clickQuickAccessMenuItem(it.itemId)
            true
        }


    }

    private fun clickQuickAccessMenuItem(navigateId: Int) {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
            launchSingleTop = true
        }
        navController.navigate(navigateId, null, options)
    }

}