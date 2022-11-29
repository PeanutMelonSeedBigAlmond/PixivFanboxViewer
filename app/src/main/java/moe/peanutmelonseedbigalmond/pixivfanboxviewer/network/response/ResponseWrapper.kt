package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

data class ResponseWrapper<Body>(
    val error: String?,
    val body: Body,
)