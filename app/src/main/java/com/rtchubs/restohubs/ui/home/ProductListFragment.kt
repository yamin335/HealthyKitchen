package com.rtchubs.restohubs.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.MoreShoppingListFragmentBinding
import com.rtchubs.restohubs.databinding.ProductListFragmentBinding
import com.rtchubs.restohubs.models.Product
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.util.showSuccessToast
import com.rtchubs.restohubs.util.showWarningToast

class ProductListFragment :
    BaseFragment<ProductListFragmentBinding, ProductListViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_product_list
    override val viewModel: ProductListViewModel by viewModels {
        viewModelFactory
    }

    lateinit var productCategoryAdapter: ProductCategoryListAdapter
    lateinit var productListAdapter: ProductListAdapter
    val args: ProductListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        viewModel.toastWarning.observe(viewLifecycleOwner, Observer {
            it?.let { message ->
                showWarningToast(requireContext(), message)
                viewModel.toastWarning.postValue(null)
            }
        })

        viewModel.toastSuccess.observe(viewLifecycleOwner, Observer {
            it?.let { message ->
                showSuccessToast(requireContext(), message)
                viewModel.toastSuccess.postValue(null)
            }
        })

        viewDataBinding.toolbar.title = args.merchant.name

        productCategoryAdapter = ProductCategoryListAdapter(
            appExecutors
        ) { item ->
            //viewDataBinding.imageUrl = item
        }

        viewDataBinding.rvProductCategory.adapter = productCategoryAdapter

        productCategoryAdapter.submitList(listOf("T-Shirt", "Shoes", "Pants", "Hats", "Shorts", "Shocks", "Sunglasses"))

        productListAdapter = ProductListAdapter(
            appExecutors,
            object : ProductListAdapter.ProductListActionCallback {
                override fun addToFavorite(item: Product) {
                    viewModel.addToFavorite(item)
                }

                override fun addToCart(item: Product) {
                    viewModel.addToCart(item, 1)
                }

            }) { item ->
            navController.navigate(ProductListFragmentDirections.actionProductListFragmentToProductDetailsFragment(item))
        }

        //viewDataBinding.rvProductList.addItemDecoration(GridRecyclerItemDecorator(2, 40, true))
        viewDataBinding.rvProductList.layoutManager = StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL)
        viewDataBinding.rvProductList.adapter = productListAdapter

        viewModel.productListResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.let { productList ->
                productListAdapter.submitList(productList)
            }
        })

        viewModel.getProductList(args.merchant.id.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_list, menu)

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
                navController.navigate(ProductListFragmentDirections.actionProductListFragmentToCartFragment())
            }
        }

        return true
    }
}