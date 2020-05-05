package github.informramiz.progressbarbutton.model

import androidx.annotation.FloatRange

/**
 * Created by Ramiz Raja on 06/05/2020
 */
interface ProgressListener {
    fun onProgress(@FloatRange(from = 0.0, to = 1.0) progress: Double)
    fun onError(exception: Throwable) {}
    fun onDone() {}
}