package github.informramiz.progressbarbutton.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File

/**
 * Created by Ramiz Raja on 05/05/2020
 */
object GitHubRepository {

    suspend fun getGlide(context: Context) {
        downloadRepo(context, "bumptech", "glide", "glide.zip")
    }

    private suspend fun downloadRepo(context: Context, owner: String, repo: String, outputFileName: String) {
        withContext(Dispatchers.IO) {
            val response = GitHubAPI.INSTANCE.getRepo(owner, repo)
            if (response.isSuccessful) {
                Timber.d("Get Glide is successful")
                streamFile(context, outputFileName, response.body()!!)
            } else {
                Timber.d("Get Glide failed")
            }
        }
    }

    private fun streamFile(context: Context, filename: String, responseBody: ResponseBody) {
        val progressListener = object : ProgressListener {
            override fun onProgress(progress: Double) {
                Timber.d("On progress update: $progress")
            }

            override fun onDone() {
                Timber.d("onDone")
            }
        }

        val file = File(context.getExternalFilesDir(null), filename)
        file.outputStream().use { outputStream ->
            responseBody.byteStream().use { inputStream ->
                val data = ByteArray(1024 * 16)
                var bytesRead: Int
                var totalBytesRead = 0L
                val totalSize =
                    if (responseBody.contentLength() != -1L) responseBody.contentLength() else 50_000L

                bytesRead = inputStream.read(data)
                while (bytesRead != -1) {
                    totalBytesRead += bytesRead

                    var progress = totalBytesRead / totalSize.toDouble()
                    if (responseBody.contentLength() == -1L && bytesRead != -1 && progress >= 0.99) {
                        //in this case we don't know the totalSize (as contentLength == -1) so
                        //we keep progress at 99% until we are done
                        progress = 0.99
                    }
                    progressListener.onProgress(progress)

                    outputStream.write(data, 0, bytesRead)
                    bytesRead = inputStream.read(data)
                }

                progressListener.onProgress(1.0)
                progressListener.onDone()
            }
        }
    }
}