package github.informramiz.progressbarbutton.model

import okio.Buffer
import okio.ForwardingSource
import okio.Source
import java.lang.Exception

/**
 * Created by Ramiz Raja on 06/05/2020
 */
class ProgressableSource(source: Source,
                         private val contentLength: Long,
                         private val progressListener: ProgressListener) : ForwardingSource(source) {
    private var totalBytesRead = 0L
    override fun read(sink: Buffer, byteCount: Long): Long {
        return try {
            val bytesRead = super.read(sink, byteCount)
            //TODO: write the bytes somewhere
            totalBytesRead += if (bytesRead != -1L) bytesRead else 0

            progressListener.onProgress(totalBytesRead / contentLength.toDouble())

            if (bytesRead == -1L) {
                //-1 means all ready
                progressListener.onDone()
            }
            bytesRead
        } catch (e: Exception) {
            progressListener.onProgress(0.0)
            progressListener.onError(e)
            0
        }
    }
}