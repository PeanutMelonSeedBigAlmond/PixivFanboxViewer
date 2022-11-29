package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

// https://www.fanbox.cc/@mochipeach/posts/2308683
data class FileMapData(
    val id: String,
    val extension: String,
    val name:String,
    val size:Long,
    val url: String,
)