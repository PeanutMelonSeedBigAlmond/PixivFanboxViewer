package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.PostItemData

data class PostPageData(
    val items:List<PostItemData>,
    val nextUrl:String,
)