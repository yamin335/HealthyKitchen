package com.rtchubs.restohubs.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rtchubs.restohubs.AppExecutors
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.CategoryItemBinding
import com.rtchubs.restohubs.models.*

import com.rtchubs.restohubs.util.DataBoundListAdapter

class CategoryWiseProductsAdapter(
    private val appExecutors: AppExecutors,
    private val viewModel: HomeViewModel,
    private val itemActionCallback: RProductListAdapter.RProductListActionCallback,
    private val itemCallback: ((RProduct) -> Unit)? = null
) : DataBoundListAdapter<RProductCategory, CategoryItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<RProductCategory>() {
        override fun areItemsTheSame(oldItem: RProductCategory, newItem: RProductCategory): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: RProductCategory,
            newItem: RProductCategory
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup): CategoryItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_category, parent, false
        )
    }

    override fun bind(binding: CategoryItemBinding, position: Int) {
        val category = getItem(position)
        binding.level = category.name

        val rProductListAdapter = RProductListAdapter(
            appExecutors,
            itemActionCallback
        ) { item ->
            itemCallback?.invoke(item)
        }

        binding.rvProductList.adapter = rProductListAdapter
        viewModel.getFilteredProduct(category) { products ->
            rProductListAdapter.submitList(products)
        }
    }
}