package com.rtchubs.restohubs.ui.shops

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.tabs.TabLayoutMediator
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.ShopDetailsFragmentBinding
import com.rtchubs.restohubs.models.Product
import com.rtchubs.restohubs.ui.common.BaseFragment
import com.rtchubs.restohubs.ui.home.FragmentViewPagerAdapter

class ShopDetailsFragment :
    BaseFragment<ShopDetailsFragmentBinding, ShopDetailsViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_shop_details
    override val viewModel: ShopDetailsViewModel by viewModels {
        viewModelFactory
    }

    val args: ShopDetailsFragmentArgs by navArgs()

    private lateinit var viewPagerFragments: Array<Fragment>
    private val viewPagerPageTitles = arrayOf("e-Commerce", "Contact Us")

    private lateinit var pagerAdapter: FragmentViewPagerAdapter

    private var viewPagerCurrentItem = 0

    private lateinit var viewPager2PageChangeCallback: ShopDetailsViewPager2PageChangeCallback

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0


    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(requireContext())
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSizeSd()
        )

        player = SimpleExoPlayer.Builder(requireContext()).setTrackSelector(trackSelector).build()
        viewDataBinding.videoView.player = player

        val mediaItem = MediaItem.Builder()
            .setUri("https://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0")
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()

        player?.setMediaItem(mediaItem)
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        player?.prepare()
    }

    private fun releasePlayer() {
        player?.let {
            playWhenReady = it.playWhenReady
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.release()
            player = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if ((Build.VERSION.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            viewDataBinding.openArLocation.visibility = View.VISIBLE
            viewDataBinding.openArLocation.setOnClickListener {
                navController.navigate(ShopDetailsFragmentDirections.actionShopDetailsFragmentToARLocationFragment())
            }
        } else {
            viewDataBinding.openArLocation.visibility = View.GONE
        }

        viewDataBinding.toolbar.title = args.merchant.name

        viewPagerFragments = arrayOf(ShopDetailsProductListFragment.newInstance(args.merchant), ShopDetailsContactUsFragment.newInstance(args.merchant))

        pagerAdapter = FragmentViewPagerAdapter(viewPagerFragments, childFragmentManager, viewLifecycleOwner.lifecycle)

        viewDataBinding.viewPager.adapter = pagerAdapter

        viewPager2PageChangeCallback = ShopDetailsViewPager2PageChangeCallback {
            setCurrentPageItemPosition(it)
        }

        viewDataBinding.viewPager.registerOnPageChangeCallback(viewPager2PageChangeCallback)

        TabLayoutMediator(viewDataBinding.tabs, viewDataBinding.viewPager) { tab, position ->
            tab.text = viewPagerPageTitles[position]
            //tab.icon = ContextCompat.getDrawable(requireContext(), viewPagerPageIcons[position])
        }.attach()

        childFragmentManager.setFragmentResultListener("fromProductList", viewLifecycleOwner,
            FragmentResultListener { _, _ -> navController.navigate(ShopDetailsFragmentDirections.actionShopDetailsFragmentToProductListFragment(args.merchant)) })

        childFragmentManager.setFragmentResultListener(
            "goToProductDetails",
            viewLifecycleOwner, FragmentResultListener { key, bundle ->
                val product = bundle.getSerializable("product") as Product?
                product?.let {
                    navController.navigate(ShopDetailsFragmentDirections.actionShopDetailsFragmentToProductDetailsFragment(it))
                }
            }
        )

    }

    private fun setCurrentPageItemPosition(position: Int) {
        viewPagerCurrentItem = position
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
                navController.navigate(ShopDetailsFragmentDirections.actionShopDetailsFragmentToCartFragment())
            }
        }

        return true
    }
}

class ShopDetailsViewPager2PageChangeCallback(private val listener: (Int) -> Unit) : ViewPager2.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        listener.invoke(position)
    }
}