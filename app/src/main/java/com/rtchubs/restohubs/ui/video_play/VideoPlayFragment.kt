package com.rtchubs.restohubs.ui.video_play

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.MediaController
import androidx.fragment.app.viewModels
import com.rtchubs.restohubs.BR
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.databinding.FragmentVideoPlayBinding
import com.rtchubs.restohubs.ui.common.BaseFragment

class VideoPlayFragment : BaseFragment<FragmentVideoPlayBinding, VideoPlayViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_video_play
    override val viewModel: VideoPlayViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Creating MediaController

        //Creating MediaController
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(viewDataBinding.videoView)

        //specify the location of media file

        //specify the location of media file
        val uri: Uri = Uri.parse(
            Environment.getExternalStorageDirectory().path.toString() + "/media/1.mp4"
        )

        val uriRawRes: Uri = Uri.parse(
            "android.resource://" + requireContext().packageName + "/" + R.raw.math_video
        )
        //simpleVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fishvideo))

        //Setting MediaController and URI, then starting the videoView

        //Setting MediaController and URI, then starting the videoView
        viewDataBinding.videoView.setMediaController(mediaController)
        viewDataBinding.videoView.setVideoURI(uriRawRes)
        viewDataBinding.videoView.requestFocus()
        viewDataBinding.videoView.start()
    }
}