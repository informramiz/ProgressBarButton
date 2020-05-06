package github.informramiz.progressbarbutton.model

import android.content.Context
import android.os.Build
import androidx.annotation.WorkerThread
import github.informramiz.progressbarbutton.model.custom.MyStreamingNetworkClient
import github.informramiz.progressbarbutton.model.custom.StreamResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

/**
 * Created by Ramiz Raja on 05/05/2020
 */
object GitHubRepository {
    private const val MIN_PROGRESS = 0.01
    private const val MAX_PROGRESS = 0.9
    const val TOTAL_SIZE_DEFAULT = 35_000_000L

    suspend fun downloadRepo(
        context: Context,
        owner: String,
        repo: String,
        outputFileName: String
    ): Flow<DownloadStatus> = flow {
        withContext(Dispatchers.IO) {
            emit(DownloadStatus.Downloading(MIN_PROGRESS.toFloat()))
//            streamFileManually(context, outputFileName)
            val response = GitHubAPI.INSTANCE.getRepo(owner, repo)
            if (!response.isSuccessful || response.body() == null) {
                Timber.d("Get $repo failed")
                emit(DownloadStatus.DownloadFailed(Exception(response.errorBody()?.string() ?: "")))
            } else {
                Timber.d("Get $repo is successful, streaming now")
                try {
                    emitAll(
                        streamFile(
                            context,
                            outputFileName,
                            response.body()!!,
                            response.headers()
                        )
                    )
                } catch (e: Exception) {
                    Timber.d("Repo streaming failed: $e")
                    emit(DownloadStatus.DownloadFailed(e))
                }
            }
        }
    }

    suspend fun downloadRepoManually(context: Context, repoUrl: String, outputFileName: String): Flow<DownloadStatus> = flow {
        try {
            emit(DownloadStatus.Downloading(MIN_PROGRESS.toFloat()))
            emitAll(
                streamFileManually(context, repoUrl, outputFileName)
            )
        } catch (e: Exception) {
            Timber.d("Repo streaming failed: $e")
            emit(DownloadStatus.DownloadFailed(e))
        }
    }

    private fun streamFile(
        context: Context,
        filename: String,
        responseBody: ResponseBody,
        headers: Headers
    ): Flow<DownloadStatus> {
        val totalSize = getContentLength(responseBody, headers)
        Timber.d("Content Length: $totalSize")
        return readAndSaveStream(context, filename, responseBody.byteStream(), totalSize)
    }

    private fun getContentLength(
        responseBody: ResponseBody,
        headers: Headers
    ): Long {
        return if (responseBody.contentLength() != -1L) {
            responseBody.contentLength()
        } else {
            //try to fetch content length from headers
            val contentLengthInHeader = headers["Content-Length"]?.toLong()
            if (contentLengthInHeader != null && contentLengthInHeader != -1L) {
                contentLengthInHeader
            } else {
                //we can't find contentLength anywhere, use a hardcoded value
                TOTAL_SIZE_DEFAULT
            }
        }
    }

    @WorkerThread
    private fun streamFileManually(context: Context, repoUrl: String, filename: String): Flow<DownloadStatus> = flow {
        val streamResponse = MyStreamingNetworkClient.streamFile(repoUrl)
        when (streamResponse) {
            is StreamResponse.Ready -> {
                val totalSize = if (streamResponse.length != StreamResponse.STREAM_LENGTH_UNKNOWN) streamResponse.length else TOTAL_SIZE_DEFAULT
                emitAll(readAndSaveStream(context, filename, streamResponse.inputStream, totalSize))
            }
            is StreamResponse.Failed -> emit(DownloadStatus.DownloadFailed(streamResponse.e))
        }.exhaustive
    }

    @WorkerThread
    private fun readAndSaveStream(context: Context, filename: String, inputStream: InputStream, totalSize: Long): Flow<DownloadStatus> = flow {
        val file = File(context.getExternalFilesDir(null), filename)
        file.outputStream().use { outputStream ->
            inputStream.use { inputStream ->
                val bufferSize = if (totalSize > 10000_000) {
                    1024 * 16
                } else {
                    1024 * 8
                }
                val data = ByteArray(bufferSize)
                var bytesRead: Int
                var totalBytesRead = 0L
                Timber.d("Content Length: $totalSize")

                bytesRead = inputStream.read(data)
                while (bytesRead != -1) {
                    totalBytesRead += bytesRead

                    var progress = totalBytesRead / totalSize.toDouble()
                    if (progress < MIN_PROGRESS) {
                        progress = MIN_PROGRESS
                    }
                    if (totalSize == TOTAL_SIZE_DEFAULT && bytesRead != -1 && progress >= MAX_PROGRESS) {
                        //in this case we don't know the totalSize (as contentLength == -1) so
                        //we keep progress at 90% until we are done
                        progress = MAX_PROGRESS
                    }
                    emit(DownloadStatus.Downloading(progress.toFloat()))

                    outputStream.write(data, 0, bytesRead)
                    bytesRead = inputStream.read(data)
                }

                emit(DownloadStatus.Downloading(1f))
                emit(DownloadStatus.Downloaded)
            }
        }
    }
}