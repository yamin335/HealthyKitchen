package com.rtchubs.restohubs.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.HomeFragmentBinding
import com.rtchubs.restohubs.models.CategoryWithProducts
import com.rtchubs.restohubs.models.Product
import com.rtchubs.restohubs.models.RProduct
import com.rtchubs.restohubs.ui.LogoutHandlerCallback
import com.rtchubs.restohubs.ui.NavDrawerHandlerCallback
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.util.showSuccessToast
import com.rtchubs.restohubs.util.showWarningToast
import java.util.*
import kotlin.collections.ArrayList

class Home2Fragment : BaseFragment<HomeFragmentBinding, HomeViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_main2
    override val viewModel: HomeViewModel by viewModels {
        viewModelFactory
    }

    private var listener: LogoutHandlerCallback? = null

    private var drawerListener: NavDrawerHandlerCallback? = null

    lateinit var categoryWiseProductsAdapter: CategoryWiseProductsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LogoutHandlerCallback) {
            listener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }

        if (context is NavDrawerHandlerCallback) {
            drawerListener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        drawerListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mActivity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.showSnackbar.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    val snackBar = Snackbar.make(viewDataBinding.root, "Checkout Now?", Snackbar.LENGTH_LONG)
                    snackBar.setAction("Yes") {
                        // Go to cart for checkout
                        navController.navigate(Home2FragmentDirections.actionHome2FragmentToCartFragment())
                    }
                    snackBar.setActionTextColor(Color.YELLOW)
                    snackBar.show()
                }
            }
            viewModel.showSnackbar.postValue(null)
        })

        categoryWiseProductsAdapter = CategoryWiseProductsAdapter(appExecutors, object : RProductListAdapter.RProductListActionCallback {
            override fun addToFavorite(item: RProduct) {
                val product = Product(item.id ?: 0, item.name, "", Html.fromHtml(item.description).toString().toLowerCase(
                    Locale.ROOT), 0.0, 0.0,
                    item.price?.toDouble(), "", item.images?.first()?.src,
                    "", "", "", "",
                    "", 0, 0, "", "", null)
                viewModel.addToFavorite(product)
            }

            override fun addToCart(item: RProduct) {
                val product = Product(item.id ?: 0, item.name, "", Html.fromHtml(item.description).toString().toLowerCase(
                    Locale.ROOT), 0.0, 0.0,
                    item.price?.toDouble(), "", item.images?.first()?.src,
                    "", "", "", "",
                    "", 0, 0, "", "", null)
                viewModel.addToCart(product, 1)
            }

        }) { rProduct ->
            val product = Product(rProduct.id ?: 0, rProduct.name, "", Html.fromHtml(rProduct.description).toString().toLowerCase(
                Locale.ROOT), 0.0, 0.0,
                rProduct.price?.toDouble(), "", rProduct.images?.first()?.src,
                "", "", "", "",
                "", 0, 0, "", "", null)
            navigateTo(Home2FragmentDirections.actionHome2FragmentToProductDetailsFragment(product))
        }

        viewDataBinding.rvCategoryList.adapter = categoryWiseProductsAdapter

        viewModel.loadDataToAdapter.observe(viewLifecycleOwner, Observer {
            it?.let { itemCount ->
                if (itemCount == viewModel.totalCategory) {
                    HomeViewModel.isDataLoaded = true
                    categoryWiseProductsAdapter.submitList(HomeViewModel.allCategoryWiseProducts as ArrayList<CategoryWithProducts>)
                    viewDataBinding.loader.visibility = View.GONE
                    viewModel.loadDataToAdapter.postValue(null)
                    viewModel.filteredProduct.postValue(null)
                    viewModel.filteredProductCategories.postValue(null)
                }
            }
        })

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        viewDataBinding.cartMenu.setOnClickListener {
            navController.navigate(Home2FragmentDirections.actionHome2FragmentToCartFragment())
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

        viewModel.filteredProduct.observe(viewLifecycleOwner, Observer {
            it?.let { product ->
                HomeViewModel.allCategoryWiseProducts?.add(product)
            }
        })

        viewModel.filteredProductCategories.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                val filteredList = list.filter { value -> value.name != "Uncategorized" }
                viewModel.totalCategory = filteredList.size
                for (category in filteredList) {
                    viewModel.getFilteredProduct(category)
                }
            }
        })

        if (HomeViewModel.isDataLoaded) {
            categoryWiseProductsAdapter.submitList(HomeViewModel.allCategoryWiseProducts as ArrayList<CategoryWithProducts>)
        } else {
            viewDataBinding.loader.visibility = View.VISIBLE
            HomeViewModel.allCategoryWiseProducts = ArrayList()
            viewModel.getFilteredProductCategories(mapOf("per_page" to "50"))
        }
    }
}