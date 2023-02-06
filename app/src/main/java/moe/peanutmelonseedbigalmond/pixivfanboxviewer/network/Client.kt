package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.CookieRepository
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.interceptor.HeaderInterceptor
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.FanboxInfoData
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.PostData
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.PostItemData
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.SubscribedPostsData
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object Client {
    private val client = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor(CookieRepository.cookie))
        .build()
    private val service = Retrofit.Builder()
        .baseUrl(Api.baseUrl)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Api::class.java)

    private val gson by lazy { Gson() }

    suspend fun getPagedPostList(username: String): List<String> {
        val result = service.getPagedPostList(username)
        return result.body
    }

    suspend fun getUserPostsByUrl(url: String): List<PostItemData> {
        val result = service.userPosts(url)
        return result.body.items
    }

    suspend fun getFanboxInfo(username: String): FanboxInfoData {
        val result = service.getFanboxInfo(username)
        return result.body
    }

    suspend fun getPostData(postId: String): PostData {
        val responseWrapper =
            gson.fromJson(getPostDataAsJsonString(postId), JsonObject::class.java)
        val body = responseWrapper.getAsJsonObject("body")
        return gson.fromJson(body, PostData::class.java)
    }

    suspend fun getPostDataAsJsonString(postId: String): String {
        return service.getPostDataAsJsonString(postId)
    }

    suspend fun getSubscribedPostData(url: String? = null): SubscribedPostsData {
        val response = if (url == null) {
            service.getSubscribedPosts()
        } else {
            service.getPostsByUrl(url)
        }

        if (response.error != null) {
            throw Exception(response.error)
        }

        return response.body
    }

    suspend fun getSupportingPostData(url: String? = null): SubscribedPostsData {
        val response = if (url == null) {
            service.getSupportingPosts()
        } else {
            service.getPostsByUrl(url)
        }

        if (response.error != null) {
            throw Exception(response.error)
        }

        return response.body
    }
}