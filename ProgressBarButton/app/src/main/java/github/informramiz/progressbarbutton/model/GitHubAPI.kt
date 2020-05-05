package github.informramiz.progressbarbutton.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url
import timber.log.Timber

/**
 * Created by Ramiz Raja on 05/05/2020
 *
 * https://github.com/bumptech/glide/archive/master.zip
 */
interface GitHubAPI {
    companion object {
        private val client = OkHttpClient.Builder()
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY
//            })
            .build()


        val INSTANCE = synchronized(this) {
            Retrofit.Builder()
                .baseUrl("https://github.com/")
                .client(client)
                .build()
                .create(GitHubAPI::class.java)
        }
    }

      //don't download the whole in memory at once, stream it in chunks of bytes
    @GET("{owner}/{repo}/archive/master.zip")
    @Streaming
    suspend fun getRepo(@Path("owner") repoOwner: String, @Path("repo") repoName: String): Response<ResponseBody>
}