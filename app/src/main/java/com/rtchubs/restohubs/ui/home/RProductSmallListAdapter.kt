package com.rtchubs.restohubs.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rtchubs.restohubs.AppExecutors
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.ProductItemBinding
import com.rtchubs.restohubs.databinding.ProductSmallItemBinding
import com.rtchubs.restohubs.models.RProduct
import com.rtchubs.restohubs.util.DataBoundListAdapter
import kotlinx.android.synthetic.main.popup_menu_product_item.view.*

class RProductSmallListAdapter(
    appExecutors: AppExecutors,
    private val itemActionCallback: RProductListActionCallback?,
    private val itemCallback: ((RProduct) -> Unit)? = null

) : DataBoundListAdapter<RProduct, ProductSmallItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<RProduct>() {
        override fun areItemsTheSame(oldItem: RProduct, newItem: RProduct): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: RProduct,
            newItem: RProduct
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup): ProductSmallItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_r_product_small, parent, false
        )
    }


    override fun bind(binding: ProductSmallItemBinding, position: Int) {
        val context = binding.root.context
        val item = getItem(position)
        binding.productName = item.name
        //binding.productDescription = Html.fromHtml(item.description).toString().toLowerCase(Locale.ROOT)
        binding.imageUrl = item.images?.first()?.src
        binding.productPrice = "$ ${item.price}"

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }

        binding.menu.setOnClickListener {
            Toast.makeText(binding.root.context, "PopUp Menu Working", Toast.LENGTH_LONG).show()
        }

        binding.imageRequestListener = object: RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                binding.root.visibility = View.VISIBLE
                binding.logo.setImageResource(R.drawable.image_placeholder)
                return true
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                binding.root.visibility = View.VISIBLE
                return false
            }
        }

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewPopupMenu = inflater.inflate(R.layout.popup_menu_product_item, null)
        val popupMenu = PopupWindow(viewPopupMenu, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupMenu.isOutsideTouchable = true
        //popupMenu.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        popupMenu.animationStyle = android.R.style.Animation_Dialog
        popupMenu.elevation = 20F

        popupMenu.setOnDismissListener {
            //        Toast.makeText(context, "Dismissed!!!", Toast.LENGTH_LONG).show()
        }

        val popupMenuView = popupMenu.contentView
        popupMenuView.menuFavorite.setOnClickListener {
            itemActionCallback?.addToFavorite(item)
            popupMenu.dismiss()
        }

        popupMenuView.menuCart.setOnClickListener {
            itemActionCallback?.addToCart(item)
            popupMenu.dismiss()
        }

        binding.menu.setOnClickListener {
            popupMenu.showAsDropDown(binding.menu,-200, -150, Gravity.NO_GRAVITY)
        }
    }

    interface RProductListActionCallback {
        fun addToFavorite(item: RProduct)
        fun addToCart(item: RProduct)
    }
}