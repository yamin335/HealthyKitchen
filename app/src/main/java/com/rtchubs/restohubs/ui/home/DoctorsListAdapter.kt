package com.rtchubs.restohubs.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rtchubs.restohubs.AppExecutors
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.models.Book
import com.rtchubs.restohubs.models.SubBook
import com.rtchubs.restohubs.databinding.ItemDoctorsListBinding
import com.rtchubs.restohubs.util.DataBoundListAdapter

class DoctorsListAdapter(
    private val appExecutors: AppExecutors,
    private var itemCallback: ((SubBook) -> Unit)? = null
) : DataBoundListAdapter<Book, ItemDoctorsListBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem?.id == newItem?.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Book,
            newItem: Book
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    lateinit var topSpecialistAdapter: TopSpecialistAdapter
    lateinit var subDoctorsListAdapter: SubDoctorsListAdapter
    lateinit var mergeAdapter: MergeAdapter

    override fun createBinding(parent: ViewGroup): ItemDoctorsListBinding {
        val binding: ItemDoctorsListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_doctors_list, parent, false
        )
        binding.rvDoctorsList.setRecycledViewPool(viewPool)

        topSpecialistAdapter = TopSpecialistAdapter(appExecutors, itemCallback)
        subDoctorsListAdapter = SubDoctorsListAdapter(appExecutors, itemCallback)
        mergeAdapter = MergeAdapter(topSpecialistAdapter, subDoctorsListAdapter)

        //binding.rvDoctorsList.adapter = SubDoctorsListAdapter(appExecutors , itemCallback)
        binding.rvDoctorsList.adapter = mergeAdapter
        return binding
    }


    override fun bind(binding: ItemDoctorsListBinding, position: Int) {
        val item = getItem(position)
        binding.tvTitle.text = item.title
        if (position == 0) {
            topSpecialistAdapter.submitList(item.listOfSubDoctors)
        } else {
            subDoctorsListAdapter.submitList(item.listOfSubDoctors)
        }
        //(binding.rvDoctorsList.adapter as SubDoctorsListAdapter).submitList(item.listOfSubDoctors)
    }

    fun getSpecialistAdapter(): TopSpecialistAdapter {
        return topSpecialistAdapter
    }

    fun getDoctorsListAdapter(): SubDoctorsListAdapter {
        return subDoctorsListAdapter
    }

}