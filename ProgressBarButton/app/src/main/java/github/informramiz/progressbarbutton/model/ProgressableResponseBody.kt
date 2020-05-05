package github.informramiz.progressbarbutton.model

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.buffer

/**
 * Created by Ramiz Raja on 06/05/2020
 */
class ProgressableResponseBody(
    private val responseBody: ResponseBody,
    private val progressListener: ProgressListener
) : ResponseBody() {
    private lateinit var bufferedSource: BufferedSource

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun source(): BufferedSource {
        if (!this::bufferedSource.isInitialized) {
            bufferedSource = ProgressableSource(responseBody.source(), responseBody.contentLength(), progressListener).buffer()
        }

        return bufferedSource
    }
}

suspend fun ResponseBody.toProgressableResponseBody(progressListener: ProgressListener): ProgressableResponseBody {
    return ProgressableResponseBody(this, progressListener)
}