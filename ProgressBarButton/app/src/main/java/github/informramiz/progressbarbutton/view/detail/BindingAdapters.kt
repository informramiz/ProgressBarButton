package github.informramiz.progressbarbutton.view.detail

import android.widget.TextView
import androidx.databinding.BindingAdapter
import github.informramiz.progressbarbutton.R

/**
 * Created by Ramiz Raja on 07/05/2020
 */
@BindingAdapter("android:text")
fun TextView.setText(success: Boolean) {
    val statusText = if (success)
        R.string.detail_fragment_download_status_success
    else
        R.string.detail_fragment_download_status_failure
    setText(statusText)
}