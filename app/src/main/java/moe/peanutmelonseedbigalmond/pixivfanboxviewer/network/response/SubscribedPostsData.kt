package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

data class SubscribedPostsData(
    val items: List<PostItemData>,
    val nextUrl: String?,
)