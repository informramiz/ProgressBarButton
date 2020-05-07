package github.informramiz.progressbarbutton.view.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.navigation.NavDeepLinkBuilder
import github.informramiz.progressbarbutton.R
import github.informramiz.progressbarbutton.view.detail.DetailFragmentArgs
import github.informramiz.progressbarbutton.view.detail.DownloadInfo

/**
 * Created by Ramiz Raja on 07/05/2020
 */
object NotificationUtils {
    private enum class NotificationInfo(
        val id: Int,
        @StringRes val channelIdRs: Int,
        @StringRes val channelNameRes: Int,
        @StringRes val channelDescription: Int
    ) {
        RepoDownloaded(
            1,
            R.string.notification_repo_downloaded_channel_id,
            R.string.notification_repo_downloaded_channel_name,
            R.string.notification_repo_downloaded_channel_description
        );

        fun channelId(context: Context): String {
            return context.getString(channelIdRs)
        }
    }

    fun sendRepoDownloadedNotification(context: Context, repo: String, downloadStatus: Boolean) {
        createChannel(context, NotificationInfo.RepoDownloaded)

        val channelId = NotificationInfo.RepoDownloaded.channelId(context)
        val contentText = context.getString(R.string.notification_repo_downloaded_body, repo)

        val args = DetailFragmentArgs(DownloadInfo(repo, downloadStatus))
        val actionIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.detailFragment)
            .setArguments(args.toBundle())
            .createPendingIntent()

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(contentText)
            .setSmallIcon(R.drawable.cloud_download)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setAutoCancel(true)
            .setContentIntent(actionIntent)
            .build()

        val notificationManager: NotificationManager? = context.getSystemService()
        notificationManager?.cancelAll()
        notificationManager?.notify(NotificationInfo.RepoDownloaded.id, notification)
    }

    private fun createChannel(context: Context, notificationInfo: NotificationInfo) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val id = context.getString(notificationInfo.channelIdRs)
        val name = context.getString(notificationInfo.channelNameRes)
        val description = context.getString(notificationInfo.channelDescription)
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = description
        }
        val notificationService: NotificationManager? = context.getSystemService()
        notificationService?.createNotificationChannel(channel)
    }

    fun Context.clearAllNotifications() {
        val notificationManager: NotificationManager? = getSystemService()
        notificationManager?.cancelAll()
    }
}