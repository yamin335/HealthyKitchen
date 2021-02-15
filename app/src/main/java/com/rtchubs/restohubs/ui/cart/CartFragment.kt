package com.rtchubs.restohubs.ui.cart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.CartFragmentBinding
import com.rtchubs.restohubs.databinding.MoreFragmentBinding
import com.rtchubs.restohubs.databinding.SetAFragmentBinding
import com.rtchubs.restohubs.local_db.dbo.CartItem
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.ui.order.OrderDialogFragment
import com.rtchubs.restohubs.util.toRounded

class CartFragment : BaseFragment<CartFragmentBinding, CartViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_cart

    override val viewModel: CartViewModel by viewModels { viewModelFactory }

    lateinit var cartItemListAdapter: CartItemListAdapter
    lateinit var checkoutOptionBottomDialog: CheckoutOptionBottomDialog

    var total: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        cartItemListAdapter = CartItemListAdapter(
            appExecutors,
            object : CartItemListAdapter.CartItemActionCallback {
                override fun incrementCartItemQuantity(id: Int) {
                    viewModel.incrementCartItemQuantity(id)
                }

                override fun decrementCartItemQuantity(id: Int) {
                    viewModel.decrementCartItemQuantity(id)
                }

            }
        ) { item ->
            viewModel.deleteCartItem(item)
        }

        checkoutOptionBottomDialog = CheckoutOptionBottomDialog(object : CheckoutOptionBottomDialog.CheckoutOptionBottomDialogCallback {
            override fun onGuestSelected() {
                checkoutOptionBottomDialog.dismiss()

                val orderDialog = OrderDialogFragment(object : OrderDialogFragment.PlaceOrderCallback {
                    override fun onOrderPlaced() {

                    }
                }, viewModel.cartItems.value as ArrayList<CartItem>, total.toInt())

                orderDialog.show(childFragmentManager, "#Order_Dialog_Fragment")
            }

            override fun onLoginSelected() {
                checkoutOptionBottomDialog.dismiss()
            }

        })

        viewDataBinding.rvCartItems.adapter = cartItemListAdapter

        viewModel.cartItems.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                if (list.isEmpty()) {
                    viewDataBinding.container.visibility = View.GONE
                    viewDataBinding.emptyView.visibility = View.VISIBLE
                } else {
                    viewDataBinding.container.visibility = View.VISIBLE
                    viewDataBinding.emptyView.visibility = View.GONE
                    cartItemListAdapter.submitList(list)

                    total = 0.0
                    list.forEach { item ->
                        val price = item.product.mrp ?: 0.0
                        val quantity = item.quantity ?: 0
                        total += price * quantity
                    }
                    total = total.toRounded(2)
                    viewDataBinding.totalPrice = total.toString()
                }
            }
        })

        viewDataBinding.btnCheckout.setOnClickListener {
            checkoutOptionBottomDialog.show(childFragmentManager, "#Checkout_Option_Dialog")
        }

//        viewDataBinding.toolbar.title = args.merchant.name
//
//        productListAdapter = ProductListAdapter(
//            appExecutors
//        ) { item ->
//            navController.navigate(ProductListFragmentDirections.actionProductListFragmentToProductDetailsFragment(item))
//        }
//
//        viewDataBinding.rvProductList.addItemDecoration(GridRecyclerItemDecorator(2, 20, true))
//        viewDataBinding.rvProductList.layoutManager = GridLayoutManager(mContext, 2)
//        viewDataBinding.rvProductList.adapter = productListAdapter
//
//        viewModel.productListResponse.observe(viewLifecycleOwner, Observer { response ->
//            response?.data?.let { productList ->
//                productListAdapter.submitList(productList)
//            }
//        })
//
//        viewModel.getProductList(args.merchant.id.toString())
    }
}