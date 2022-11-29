package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

data class PostBodyBlockData(
    val type: String,
) {
    private var text: String? = null
    private var urlEmbedId: String? = null
    private var imageId: String? = null
    private var fileId: String? = null
    var styles: List<ParagraphStyle>? = null
        private set

    val content: String
        get() = when (type) {
            "p" -> text!!
            "file" -> fileId!!
            "image" -> imageId!!
            "url_embed" -> urlEmbedId!!
            else -> text!!
        }

    fun setText(text: String) {
        this.text = text
    }

    fun setUrlEmbedId(urlEmbedId: String) {
        this.urlEmbedId = urlEmbedId
    }

    fun setFileId(fileId: String) {
        this.fileId = fileId
    }

    data class ParagraphStyle(
        val type: String,
        val offset: Int,
        val length: Int,
    )
}