package github.informramiz.progressbarbutton.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by Ramiz Raja on 05/05/2020
 */
object GitHubRepository {
    suspend fun getGlide() {
        withContext(Dispatchers.IO) {
            val response = GitHubAPI.INSTANCE.getRepo()
            if (response.isSuccessful) {
                Timber.d("Get Glide is successful")
            } else {
                Timber.d("Get Glide failed")
            }
        }
    }
}