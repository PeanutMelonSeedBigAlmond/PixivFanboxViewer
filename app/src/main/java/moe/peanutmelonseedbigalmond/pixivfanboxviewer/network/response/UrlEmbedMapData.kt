package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

data class UrlEmbedMapData(
    val id:String,
    val type:String,
    val html:String?,
    val postInfo:PostItemData?
)