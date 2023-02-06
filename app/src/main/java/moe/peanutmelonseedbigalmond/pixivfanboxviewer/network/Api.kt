package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network

import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.*
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

typealias PagedPostList = ResponseWrapper<List<String>>

interface Api {
    @GET("/post.paginateCreator")
    suspend fun getPagedPostList(
        @Query("creatorId") username: String
    ): PagedPostList

    @GET("/post.listHome")
    suspend fun getSubscribedPosts(
        @Query("limit") pageSize: Int = 10
    ): ResponseWrapper<SubscribedPostsData>

    @GET("/post.listSupporting")
    suspend fun getSupportingPosts(
        @Query("limit") pageSize: Int = 10
    ): ResponseWrapper<SubscribedPostsData>

    @GET
    suspend fun getPostsByUrl(
        @Url url: String
    ): ResponseWrapper<SubscribedPostsData>

    @GET
    suspend fun userPosts(
        @Url pageUrl: String
    ): ResponseWrapper<PostPageData>

    @GET("/creator.get")
    suspend fun getFanboxInfo(
        @Query("creatorId") username: String
    ): ResponseWrapper<FanboxInfoData>

    @GET("/post.info")
    suspend fun getPostDataAsJsonString(
        @Query("postId") postId: String,
    ): String

    @GET("/post.get")
    suspend fun getPostBriefData(
        @Query("postId") postId: String
    ):ResponseWrapper<PostItemData>

    companion object {
        const val baseUrl = "https://api.fanbox.cc/"
    }
}