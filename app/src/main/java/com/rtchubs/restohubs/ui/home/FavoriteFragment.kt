package com.rtchubs.restohubs.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.FavoriteFragmentBinding
import com.rtchubs.restohubs.databinding.ProductListFragmentBinding
import com.rtchubs.restohubs.models.Product
import com.rtchubs.restohubs.ui.NavDrawerHandlerCallback
import com.rtchubs.restohubs.ui.common.BaseFragment

class FavoriteFragment : BaseFragment<FavoriteFragmentBinding, FavoriteViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_favorite
    override val viewModel: FavoriteViewModel by viewModels {
        viewModelFactory
    }

    lateinit var favoriteListAdapter: FavoriteListAdapter

    private var drawerListener: NavDrawerHandlerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavDrawerHandlerCallback) {
            drawerListener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        drawerListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        viewDataBinding.cartMenu.setOnClickListener {
            navController.navigate(FavoriteFragmentDirections.actionFavoriteFragmentToCartFragment2())
        }

        viewModel.cartItemCount.observe(viewLifecycleOwner, Observer {
            it?.let { value ->
                if (value < 1) {
                    viewDataBinding.badge.visibility = View.INVISIBLE
                    return@Observer
                } else {
                    viewDataBinding.badge.visibility = View.VISIBLE
                    viewDataBinding.badge.text = value.toString()
                }
            }
        })

        favoriteListAdapter = FavoriteListAdapter(
            appExecutors,
            object : FavoriteListAdapter.FavoriteListActionCallback {
                override fun onRemove(item: Product) {
                    viewModel.deleteFavoriteItem(item)
                }
            }
        ) { item ->
            navController.navigate(FavoriteFragmentDirections.actionFavoriteFragmentToProductDetailsFragment2(item))
        }

        viewDataBinding.rvFavoriteList.adapter = favoriteListAdapter

        viewModel.favoriteItems.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                if (list.isEmpty()) {
                    viewDataBinding.container.visibility = View.GONE
                    viewDataBinding.emptyView.visibility = View.VISIBLE
                } else {
                    viewDataBinding.container.visibility = View.VISIBLE
                    viewDataBinding.emptyView.visibility = View.GONE
                    favoriteListAdapter.submitList(list)
                }
            }
        })
    }

}