package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.util.*

data class PostData(
    val id: String,
    val title: String,
    val coverImageUrl: String?,
    val feeRequired: Int,
    val publishedDatetime: Date,
    val updatedDatetime: Date,
    val type: String,
    val tags: List<String>,
    val restricted: Boolean,
    @SerializedName("excerpt") val briefContent: String,
    val creatorId: String,
    val hasAdultContent: Boolean,
    val nextPost: NextOrPrevPostInfoData,
    val prevPost: NextOrPrevPostInfoData,

    // 这里 body 可以是 JsonObject/null，null 会被反序列化成 JsonNull
    @SerializedName("body") private val mBody: JsonElement?
) {
    data class NextOrPrevPostInfoData(
        val id: String,
        val title: String,
        val publishedDatetime: Date,
    )

    val postBody: PostBodyData?
        get() {
            if (mBody is JsonNull) return null
            mBody as JsonObject
            return when (type) {
                "article" -> ArticlePostBodyData(mBody)
                "image" -> ImagePostBodyData(mBody)
                else -> throw UnsupportedOperationException("不支持的文章类型：$type")
            }
        }
}