package com.rtchubs.restohubs.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.LayoutAddPaymentMethodBinding
import com.rtchubs.restohubs.databinding.MoreBookListFragmentBinding
import com.rtchubs.restohubs.models.MoreBookItem
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.util.GridRecyclerItemDecorator

class MoreBookListFragment :
    BaseFragment<MoreBookListFragmentBinding, MoreBookListViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_more_book_list
    override val viewModel: MoreBookListViewModel by viewModels {
        viewModelFactory
    }

    lateinit var moreBookListAdapter: MoreBookListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerToolbar(viewDataBinding.toolbar)

        moreBookListAdapter = MoreBookListAdapter(
                appExecutors
            ) { item ->
        }

        viewDataBinding.rvMoreBookList.addItemDecoration(GridRecyclerItemDecorator(2, 40, true))
        viewDataBinding.rvMoreBookList.layoutManager = GridLayoutManager(mContext, 2)
        viewDataBinding.rvMoreBookList.adapter = moreBookListAdapter

        val list = ArrayList<MoreBookItem>()
        list.add(MoreBookItem(1, "Bangla", ""))
        list.add(MoreBookItem(2, "English", ""))
        list.add(MoreBookItem(3, "General Mathematics", ""))
        list.add(MoreBookItem(4, "Biology", ""))
        list.add(MoreBookItem(5, "Physics", ""))
        list.add(MoreBookItem(6, "Information Technology", ""))
        list.add(MoreBookItem(7, "Bangladesh & Global Studies", ""))
        list.add(MoreBookItem(8, "Chemistry", ""))
        list.add(MoreBookItem(9, "Higher Mathematics", ""))
        list.add(MoreBookItem(10, "Islam & Moral Education", ""))
        list.add(MoreBookItem(11, "Agriculture", ""))
        list.add(MoreBookItem(12, "Electrical", ""))
        list.add(MoreBookItem(13, "Mechanical", ""))
        list.add(MoreBookItem(14, "Computer", ""))
        list.add(MoreBookItem(15, "General Knowledge", ""))
        list.add(MoreBookItem(16, "Arabic", ""))
        list.add(MoreBookItem(17, "Bangla Grammar", ""))
        list.add(MoreBookItem(18, "English Grammar", ""))
        list.add(MoreBookItem(19, "Arabic Grammar", ""))
        list.add(MoreBookItem(20, "History", ""))
        moreBookListAdapter.submitList(list)
    }
}