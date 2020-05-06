package github.informramiz.progressbarbutton.model

/**
 * Created by Ramiz Raja on 06/05/2020
 */
sealed class DownloadStatus {
    object Download : DownloadStatus()
    class Downloading(val progress: Float): DownloadStatus()
    class DownloadFailed(e: Throwable?): DownloadStatus()
}