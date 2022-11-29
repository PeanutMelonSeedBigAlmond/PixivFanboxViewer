package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class PostItemData(
    val id: String,
    val title: String,
    val feeRequired: Int,
    val publishedDatetime: Date,
    val updatedDatetime: Date,
    val tags: List<String>,
    val restricted: Boolean,
    val creatorId: String,
    val hasAdultContent: Boolean,
    val cover: PostCoverData?,
    val user: UserInfoData,
    @SerializedName("excerpt") val briefContent: String,
)