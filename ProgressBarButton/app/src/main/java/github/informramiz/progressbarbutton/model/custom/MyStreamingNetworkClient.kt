package github.informramiz.progressbarbutton.model.custom

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URL
import java.net.URLConnection

/**
 * Created by Ramiz Raja on 07/05/2020
 */
object MyStreamingNetworkClient {
    const val CONNECTION_TIMEOUT = 1000 * 15 //15 sec

    suspend fun streamFile(streamUrl: String): StreamResponse  {
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("Downloading repo: $streamUrl")
                val url = URL(streamUrl)
                val urlConnection = url.openConnection()
                urlConnection.connectTimeout = CONNECTION_TIMEOUT
                urlConnection.connect()
                val totalSize = urlConnection.contentLengthSafe()
                Timber.d("UrlConnection contentLength: $totalSize")
                StreamResponse.Ready(totalSize, urlConnection.getInputStream())
            } catch (e: Exception) {
                StreamResponse.Failed(e)
            }
        }
    }

    private fun URLConnection.contentLengthSafe(): Long {
        val bodyContentLength = if (Build.VERSION.SDK_INT >= 24) contentLengthLong else contentLength.toLong()
        val contentLengthInHeader = headerFields["Content-Length"]?.get(0)?.toLong()
        return if (bodyContentLength != -1L) {
            bodyContentLength
        } else {
            if (contentLengthInHeader != null && contentLengthInHeader != -1L) {
                contentLengthInHeader
            } else {
                //we can't find contentLength anywhere, use a hardcoded value
                -1L
            }
        }
    }
}