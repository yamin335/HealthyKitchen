package com.rtchubs.restohubs.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.MoreShoppingListFragmentBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class MoreShoppingMallFragment :
    BaseFragment<MoreShoppingListFragmentBinding, MoreShoppingMallViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_more_shopping_mall
    override val viewModel: MoreShoppingMallViewModel by viewModels {
        viewModelFactory
    }

    lateinit var shoppingMallListAdapter: MoreShoppingMallListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        shoppingMallListAdapter = MoreShoppingMallListAdapter(
                appExecutors
            ) { item ->
            navController.navigate(MoreShoppingMallFragmentDirections.actionMoreShoppingMallFragmentToAllShopListFragment(item))
        }

        viewDataBinding.rvMoreShoppingMallList.layoutManager = StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL)
        viewDataBinding.rvMoreShoppingMallList.adapter = shoppingMallListAdapter

        viewModel.allShoppingMallResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.let { mallList ->
                shoppingMallListAdapter.submitList(mallList)
            }
        })

        viewModel.getAllShoppingMallList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_details, menu)

        val menuItem = menu.findItem(R.id.menu_cart)
        val actionView = menuItem.actionView
        val badge = actionView.findViewById<TextView>(R.id.badge)
        badge.text = viewModel.cartItemCount.value?.toString()
        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }

        viewModel.cartItemCount.observe(viewLifecycleOwner, Observer {
            it?.let { value ->
                if (value < 1) {
                    badge.visibility = View.INVISIBLE
                    return@Observer
                } else {
                    badge.visibility = View.VISIBLE
                    badge.text = value.toString()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }

            R.id.menu_cart -> {
                navController.navigate(MoreShoppingMallFragmentDirections.actionMoreShoppingMallFragmentToCartFragment())
            }
        }

        return true
    }
}