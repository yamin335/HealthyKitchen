package com.rtchubs.restohubs.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
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
import kotlinx.android.synthetic.main.list_item_category.view.*

class CategoryWiseProductsAdapter(
    private val appExecutors: AppExecutors,
    private val itemActionCallback: RProductListAdapter.RProductListActionCallback,
    private val itemCallback: ((RProduct) -> Unit)? = null
) : RecyclerView.Adapter<CategoryWiseProductsAdapter.ItemViewHolder>() {

    private var ategoryWithProductsList: ArrayList<CategoryWithProducts> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_category, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return ategoryWithProductsList.size
    }

    fun submitList(ategoryWithProductsList: ArrayList<CategoryWithProducts>) {
        this.ategoryWithProductsList = ategoryWithProductsList
        notifyDataSetChanged()
    }

    fun changeItemAt(tile: CategoryWithProducts, position: Int) {
        this.ategoryWithProductsList[position] = tile
        notifyItemChanged(position)
    }

    fun removeItemAt(position: Int) {
        this.ategoryWithProductsList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getTotalItemCount(): Int {
        return ategoryWithProductsList.size
    }

    inner class ItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val categoryWithProducts = ategoryWithProductsList[position]

            itemView.categoryName.text = categoryWithProducts.category.name

            val rProductListAdapter = RProductListAdapter(
                appExecutors,
                itemActionCallback
            ) { item ->
                itemCallback?.invoke(item)
            }

            itemView.rvProductList.adapter = rProductListAdapter
            rProductListAdapter.submitList(categoryWithProducts.products)
        }
    }
}