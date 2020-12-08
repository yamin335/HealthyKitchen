package com.rtchubs.restohubs.ui.home

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rtchubs.restohubs.AppExecutors
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.FavoriteListItemBinding
import com.rtchubs.restohubs.databinding.MoreShoppingListItemBinding
import com.rtchubs.restohubs.databinding.ProductListItemBinding
import com.rtchubs.restohubs.databinding.ShopListItemBinding

import com.rtchubs.restohubs.models.Product
import com.rtchubs.restohubs.util.DataBoundListAdapter
import com.rtchubs.restohubs.util.toRounded

class FavoriteListAdapter(
    private val appExecutors: AppExecutors,
    private val actionCallback: FavoriteListActionCallback,
    private val itemCallback: ((Product) -> Unit)? = null
) : DataBoundListAdapter<Product, FavoriteListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    val onClicked = MutableLiveData<Product>()
    override fun createBinding(parent: ViewGroup): FavoriteListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_favorite, parent, false
        )
    }


    override fun bind(binding: FavoriteListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item
        binding.imageUrl = item.thumbnail
        binding.price = item.mrp?.toRounded(2).toString()

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }

        binding.remove.setOnClickListener {
            actionCallback.onRemove(item)
        }

        binding.imageRequestListener = object: RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                binding.thumbnail.setImageResource(R.drawable.image_placeholder)
                return true
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                return false
            }
        }
    }

    interface FavoriteListActionCallback {
        fun onRemove(item: Product)
    }
}