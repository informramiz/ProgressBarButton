package github.informramiz.progressbarbutton.model

/**
 * Created by Ramiz Raja on 06/05/2020
 */
sealed class DownloadStatus {
    class Downloaded(val name: String) : DownloadStatus()
    class Downloading(val progress: Float): DownloadStatus()
    class DownloadFailed(val e: Throwable?): DownloadStatus()
}

val <T> T.exhaustive: T
    get() = this