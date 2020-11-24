package com.rtchubs.restohubs.ui.profiles

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.databinding.ProfilesBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class ProfilesFragment : BaseFragment<ProfilesBinding, ProfilesViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_profiles
    override val viewModel: ProfilesViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.profile_menu, menu)
        /*if (viewModel.editable.value ?: editable) {
            menu.findItem(R.id.action_item_edit).setTitle(R.string.save)
        }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*if (item.itemId == R.id.action_item_edit) {
            if (item.title == getString(R.string.edit)) {
                item.setTitle(R.string.save)
                viewModel.editable.postValue(true)
            } else {
                if (listOf(viewDataBinding.tilPostCode,
                        viewDataBinding.tilAddressLine1,
                        viewDataBinding.spDistrict,
                        viewDataBinding.spThana).isValid()) {
                    viewModel.updateAddress()
                }

            }
            return true
        }*/
        return super.onOptionsItemSelected(item)
    }
}