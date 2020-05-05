package github.informramiz.progressbarbutton.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming

/**
 * Created by Ramiz Raja on 05/05/2020
 *
 * https://github.com/bumptech/glide/archive/master.zip
 */
interface GitHubAPI {
    companion object {
        private val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        private val jsonConverter = MoshiConverterFactory.create(moshi)

        val INSTANCE = synchronized(this) {
            Retrofit.Builder()
                .baseUrl("https://github.com/")
                .client(client)
                .addConverterFactory(jsonConverter)
                .build()
                .create(GitHubAPI::class.java)
        }
    }

    @Streaming  //don't download the whole in memory at once, stream it in chunks of bytes
    @GET("bumptech/glide/archive/master.zip")
    suspend fun getRepo(): Response<ResponseBody>
}