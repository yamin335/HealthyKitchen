package com.rtchubs.restohubs.ui.shops

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.ShopDetailsProductListFragmentBinding
import com.rtchubs.restohubs.models.Merchant
import com.rtchubs.restohubs.models.Product
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.ui.home.*
import com.rtchubs.restohubs.util.showSuccessToast
import com.rtchubs.restohubs.util.showWarningToast

private const val MERCHANT = "merchant"

class ShopDetailsProductListFragment :
    BaseFragment<ShopDetailsProductListFragmentBinding, ProductListViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_shop_details_product_list
    override val viewModel: ProductListViewModel by viewModels {
        viewModelFactory
    }

    private var merchant: Merchant? = null

    lateinit var productListAdapter: ProductListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            merchant = it.getSerializable(MERCHANT) as Merchant
        }
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
            setFragmentResult("goToProductDetails", bundleOf("product" to item))
        }

        viewDataBinding.rvProductList.layoutManager = StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL)
        viewDataBinding.rvProductList.adapter = productListAdapter

        viewModel.productListResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.let { productList ->
                if (productList.size < 7) {
                    productListAdapter.submitList(productList)
                } else {
                    productList.subList(0, 6)
                }
            }
        })

        viewModel.getProductList(merchant?.id.toString())

        viewDataBinding.moreProduct.setOnClickListener {
            merchant?.let { merchant ->
                setFragmentResult("fromProductList", bundleOf("merchant" to merchant))
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param merchant Selected Merchant.
         * @return A new instance of fragment 'ShopDetailsProductListFragment'.
         */

        @JvmStatic
        fun newInstance(merchant: Merchant) =
            ShopDetailsProductListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MERCHANT, merchant)
                }
            }
    }
}