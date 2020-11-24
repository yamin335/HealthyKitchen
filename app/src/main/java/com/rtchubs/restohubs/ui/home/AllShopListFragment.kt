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
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.AllShopListFragmentBinding
import com.rtchubs.restohubs.databinding.MoreShoppingListFragmentBinding
import com.rtchubs.restohubs.models.LevelWiseShops
import com.rtchubs.restohubs.models.Merchant
import com.rtchubs.restohubs.models.ShoppingMallLevel
import com.rtchubs.restohubs.ui.common.BaseFragment

class AllShopListFragment :
    BaseFragment<AllShopListFragmentBinding, AllShopListViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_all_shop_list
    override val viewModel: AllShopListViewModel by viewModels {
        viewModelFactory
    }

    val args: AllShopListFragmentArgs by navArgs()

    lateinit var allShopListAdapter: AllShopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        viewDataBinding.toolbar.title = args.shoppingMall.name

        allShopListAdapter = AllShopListAdapter(
                appExecutors
            ) { item ->
            navController.navigate(AllShopListFragmentDirections.actionAllShopListFragmentToShopDetailsFragment(item))
        }

        viewDataBinding.rvAllShopList.adapter = allShopListAdapter

        viewModel.allShopListResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.let { shopList ->
                if (shopList.isEmpty())
                    return@Observer

                val mallId = args.shoppingMall.id ?: -1
                val shops = ArrayList<Merchant>()
                shopList.forEach { merchant ->
                    if (merchant.shopping_mall_id == mallId)
                        shops.add(merchant)
                }

                if (shops.isEmpty())
                    return@Observer

                var levels = ArrayList<ShoppingMallLevel>()
                val levelsMap = HashMap<Int, ArrayList<Merchant>>()
                shops.forEach { merchant ->
                    val id = merchant.shopping_mall_level_id ?: -1
                    if (levelsMap.containsKey(id)) {
                        val arrayList = levelsMap[id]
                        arrayList?.add(merchant)
                    } else if (id != -1) {
                        val arrayList = ArrayList<Merchant>()
                        arrayList.add(merchant)
                        levelsMap[id] = arrayList
                        val lvls = merchant.shopping_mall?.levels
                        if (levels.isEmpty() && !lvls.isNullOrEmpty()) {
                            levels = lvls as ArrayList<ShoppingMallLevel>
                        }
                    }
                }

                if (shops.isEmpty() || levels.isEmpty())
                    return@Observer

                val lvlMap = HashMap<Int, ShoppingMallLevel>()
                levels.forEach { level ->
                    val id = level.id
                    if (id != null && levelsMap.containsKey(id)) {
                        lvlMap[id] = level
                    }
                }

                val levelWiseShops = ArrayList<LevelWiseShops>()
                val keys = levelsMap.keys.sorted()
                keys.forEach { key ->
                    levelWiseShops.add(LevelWiseShops(lvlMap[key], levelsMap[key]))
                }
                allShopListAdapter.submitList(levelWiseShops)
            }
        })

        viewModel.getAllShopList()
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
                navController.navigate(AllShopListFragmentDirections.actionAllShopListFragmentToCartFragment())
            }
        }

        return true
    }
}