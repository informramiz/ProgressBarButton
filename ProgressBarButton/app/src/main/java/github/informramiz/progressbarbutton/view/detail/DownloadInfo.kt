package github.informramiz.progressbarbutton.view.detail

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Ramiz Raja on 07/05/2020
 */
@Parcelize
data class DownloadInfo(val repoName: String, val downloadStatus: Boolean) : Parcelable