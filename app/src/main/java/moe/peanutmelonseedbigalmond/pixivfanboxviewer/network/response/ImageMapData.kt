package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

data class ImageMapData(
    val id: String,
    val extension: String,
    val width: Int,
    val height: Int,
    val originalUrl: String,
    val thumbnailUrl: String,
)