package com.rtchubs.restohubs.ui.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.Home2Binding
import com.rtchubs.restohubs.models.ShoppingMall
import com.rtchubs.restohubs.ui.LogoutHandlerCallback
import com.rtchubs.restohubs.ui.NavDrawerHandlerCallback
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.ui.login.SliderView

class Home2Fragment : BaseFragment<Home2Binding, HomeViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_main2
    override val viewModel: HomeViewModel by viewModels {
        viewModelFactory
    }

    lateinit var paymentListAdapter: PaymentMethodListAdapter

    private var listener: LogoutHandlerCallback? = null

    private var drawerListener: NavDrawerHandlerCallback? = null

    private var allShoppingMall = ArrayList<ShoppingMall>()

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

        //registerToolbar(viewDataBinding.toolbar)

//        viewDataBinding.cardTopUp.setOnClickListener {
//            navController.navigate(Home2FragmentDirections.actionHome2FragmentToTopUpMobileFragment(
//                TopUpHelper()
//            ))
//        }
//
//        val token = preferencesHelper.getAccessTokenHeader()
//
//        paymentListAdapter = PaymentMethodListAdapter(appExecutors) {
//            //navController.navigate(HomeFragmentDirections.actionBooksToChapterList(it))
//        }
//
//
//
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

        viewModel.slideDataList.forEach { slideData ->
            val slide = SliderView(requireContext())
            slide.sliderTextTitle = slideData.textTitle
            slide.sliderTextDescription = slideData.descText
            slide.sliderImage(slideData.slideImage)
            viewDataBinding.sliderLayout.addSlider(slide)
        }

        viewDataBinding.item1.setOnClickListener {
            if (allShoppingMall.isNotEmpty()) {
                navController.navigate(Home2FragmentDirections.actionHome2FragmentToAllShopListFragment(allShoppingMall[0]))
            }
        }

        viewDataBinding.item2.setOnClickListener {
            if (allShoppingMall.size > 1) {
                navController.navigate(Home2FragmentDirections.actionHome2FragmentToAllShopListFragment(allShoppingMall[1]))
            }
        }

        viewDataBinding.item3.setOnClickListener {
            if (allShoppingMall.size > 2) {
                navController.navigate(Home2FragmentDirections.actionHome2FragmentToAllShopListFragment(allShoppingMall[2]))
            }
        }

        viewDataBinding.item4.setOnClickListener {
            if (allShoppingMall.size > 3) {
                navController.navigate(Home2FragmentDirections.actionHome2FragmentToAllShopListFragment(allShoppingMall[3]))
            }
        }

        viewDataBinding.item5.setOnClickListener {
            if (allShoppingMall.size > 4) {
                navController.navigate(Home2FragmentDirections.actionHome2FragmentToAllShopListFragment(allShoppingMall[4]))
            }
        }

        viewDataBinding.item6.setOnClickListener {
            navController.navigate(Home2FragmentDirections.actionHome2FragmentToMoreShoppingMallFragment())
        }

        viewModel.allShoppingMallResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.let { mallList ->
                allShoppingMall = mallList as ArrayList<ShoppingMall>
                if (mallList.isNotEmpty()) {
                    viewDataBinding.label1.text = mallList[0].name
                    Glide.with(requireContext()).load(mallList[0].thumbnail).listener(object :
                        RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewDataBinding.logo1.setImageResource(R.drawable.shopping_mall)
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(viewDataBinding.logo1)
                }

                if (mallList.size > 1) {
                    viewDataBinding.label2.text = mallList[1].name
                    Glide.with(requireContext()).load(mallList[1].thumbnail).listener(object :
                        RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewDataBinding.logo2.setImageResource(R.drawable.shopping_mall)
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(viewDataBinding.logo2)
                }

                if (mallList.size > 2) {
                    viewDataBinding.label3.text = mallList[2].name
                    Glide.with(requireContext()).load(mallList[2].thumbnail).listener(object :
                        RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewDataBinding.logo3.setImageResource(R.drawable.shopping_mall)
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(viewDataBinding.logo3)
                }

                if (mallList.size > 3) {
                    viewDataBinding.label4.text = mallList[3].name
                    Glide.with(requireContext()).load(mallList[3].thumbnail).listener(object :
                        RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewDataBinding.logo4.setImageResource(R.drawable.shopping_mall)
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(viewDataBinding.logo4)
                }

                if (mallList.size > 4) {
                    viewDataBinding.label5.text = mallList[4].name
                    Glide.with(requireContext()).load(mallList[4].thumbnail).listener(object :
                        RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewDataBinding.logo5.setImageResource(R.drawable.shopping_mall)
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(viewDataBinding.logo5)
                }
            }
        })

        viewModel.getAllShoppingMallList()
//
//        Log.e("res", preferencesHelper.getAccessTokenHeader())
//        paymentListAdapter.submitList(viewModel.paymentMethodList)
//        viewDataBinding.recyclerPaymentMethods.adapter = paymentListAdapter
//
//
//
//        paymentListAdapter.onClicked.observe(viewLifecycleOwner, Observer {
//            if (it != null) {
//                if (it.id == "-1") {
//                    /**
//                     * add payment method
//                     */
//                    val action = Home2FragmentDirections.actionHome2FragmentToAddPaymentMethodsFragment()
//                    navController.navigate(action)
//                }
//            }
//        })

        viewModel.filteredProductCategories.observe(viewLifecycleOwner, Observer {
            val tt = it
            val rr = tt
            viewModel.getFilteredProduct(mapOf("category" to tt[0].id.toString()))
        })

        viewModel.filteredProduct.observe(viewLifecycleOwner, Observer {
            val tt = it
            val rr = tt
        })

        viewModel.getFilteredProductCategories(mapOf("per_page" to "50"))
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.toolbar_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
}