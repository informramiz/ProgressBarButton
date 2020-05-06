package github.informramiz.progressbarbutton.view.main

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.BindingAdapter
import github.informramiz.progressbarbutton.model.DownloadStatus
import github.informramiz.progressbarbutton.model.exhaustive

/**
 * Created by Ramiz Raja on 06/05/2020
 */
@BindingAdapter("motionProgress")
fun MotionLayout.setDownloadStatus(status: DownloadStatus?) {
    when (status) {
        null -> progress = 0f
        DownloadStatus.Downloaded -> progress = 1f
        is DownloadStatus.Downloading -> progress = status.progress
        is DownloadStatus.DownloadFailed -> progress = 0f
    }.exhaustive
}