package com.rtchubs.restohubs.ui.topup

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
import com.rtchubs.restohubs.databinding.ItemDoctorsListBinding
import com.rtchubs.restohubs.databinding.LayoutBankListRowBinding

import com.rtchubs.restohubs.models.PaymentMethod
import com.rtchubs.restohubs.models.payment_account_models.BankOrCard
import com.rtchubs.restohubs.util.DataBoundListAdapter

class TopUpBankCardListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((BankOrCard) -> Unit)? = null

) : DataBoundListAdapter<BankOrCard, LayoutBankListRowBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<BankOrCard>() {
        override fun areItemsTheSame(oldItem: BankOrCard, newItem: BankOrCard): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: BankOrCard,
            newItem: BankOrCard
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    val onClicked = MutableLiveData<PaymentMethod>()
    override fun createBinding(parent: ViewGroup): LayoutBankListRowBinding {
        val binding: LayoutBankListRowBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_my_account_list_row, parent, false
        )
        return binding
    }


    override fun bind(binding: LayoutBankListRowBinding, position: Int) {
        val item = getItem(position)
        binding.model = item

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }

        binding.imageRequestListener = object: RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                binding.logo.setImageResource(R.drawable.engineering_logo)
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                return false
            }
        }

        // binding.tvCardNumber.text = item.title
        // Glide.with(binding.ivIcon.context).load(R.drawable.plus).into(binding.ivIcon)
    }


}