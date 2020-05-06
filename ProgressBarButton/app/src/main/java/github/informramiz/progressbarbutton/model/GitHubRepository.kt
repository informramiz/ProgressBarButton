package github.informramiz.progressbarbutton.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File

/**
 * Created by Ramiz Raja on 05/05/2020
 */
object GitHubRepository {

    suspend fun getGlide(context: Context): Flow<DownloadStatus> {
        return downloadRepo(context, "bumptech", "glide", "glide.zip")
    }

    private suspend fun downloadRepo(context: Context, owner: String, repo: String, outputFileName: String): Flow<DownloadStatus> = flow {
        withContext(Dispatchers.IO) {
            emit(DownloadStatus.Downloading(0f))
            val response = GitHubAPI.INSTANCE.getRepo(owner, repo)
            if (!response.isSuccessful || response.body() == null) {
                Timber.d("Get $repo failed")
                emit(DownloadStatus.DownloadFailed(Exception(response.errorBody()?.string() ?: "")))
            } else {
                Timber.d("Get $repo is successful, streaming now")
                try {
                    emitAll(streamFile(context, outputFileName, response.body()!!))
                } catch (e: Exception) {
                    Timber.d("Repo streaming failed: $e")
                    emit(DownloadStatus.DownloadFailed(e))
                }
            }
        }
    }

    private fun streamFile(context: Context, filename: String, responseBody: ResponseBody): Flow<DownloadStatus> = flow {
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
                    emit(DownloadStatus.Downloading(progress.toFloat()))

                    outputStream.write(data, 0, bytesRead)
                    bytesRead = inputStream.read(data)
                }

                emit(DownloadStatus.Downloading(1f))
                emit(DownloadStatus.Download)
            }
        }
    }
}