package github.informramiz.progressbarbutton.model.custom

import java.io.InputStream

/**
 * Created by Ramiz Raja on 07/05/2020
 */
sealed class StreamResponse {
    companion object {
        const val STREAM_LENGTH_UNKNOWN = -1L
    }
    class Ready(val length: Long, val inputStream: InputStream): StreamResponse()
    class Failed(val e: Throwable): StreamResponse()
}