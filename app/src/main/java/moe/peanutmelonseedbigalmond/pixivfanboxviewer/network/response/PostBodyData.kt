package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

import com.google.gson.Gson
import com.google.gson.JsonObject

sealed class PostBodyData {
    abstract val blocks: List<PostBodyBlockData>
    abstract val imageMap: Map<String, ImageMapData>
    abstract val fileMap: Map<String, FileMapData>

    // embed: https://www.fanbox.cc/@usacafe/posts/2831473
    abstract val embedMap: Map<String, Any>
    abstract val urlEmbedMap: Map<String, UrlEmbedMapData>

    protected companion object {
        val gson = Gson()
    }
}

class ArticlePostBodyData(private val body: JsonObject) : PostBodyData() {
    override val blocks: List<PostBodyBlockData>
        get() {
            return body.getAsJsonArray("blocks")
                .map { gson.fromJson(it, PostBodyBlockData::class.java) }
        }
    override val imageMap: Map<String, ImageMapData>
        get() {
            if (!body.has("imageMap")) return emptyMap()
            val imageMapBody = body.getAsJsonObject("imageMap") ?: return emptyMap()
            val keys = imageMapBody.keySet()
            val map = mutableMapOf<String, ImageMapData>()
            for (k in keys) {
                map[k] = gson.fromJson(imageMapBody[k], ImageMapData::class.java)
            }
            return map
        }
    override val fileMap: Map<String, FileMapData>
        get() {
            if (!body.has("fileMap")) return emptyMap()
            val imageMapBody = body.getAsJsonObject("fileMap") ?: return emptyMap()
            val keys = imageMapBody.keySet()
            val map = mutableMapOf<String, FileMapData>()
            for (k in keys) {
                map[k] = gson.fromJson(imageMapBody[k], FileMapData::class.java)
            }
            return map
        }
    override val embedMap: Map<String, Any>
        get() = TODO("Not yet implemented")
    override val urlEmbedMap: Map<String, UrlEmbedMapData>
        get() {
            if (!body.has("urlEmbedMap")) return emptyMap()
            val imageMapBody = body.getAsJsonObject("urlEmbedMap") ?: return emptyMap()
            val keys = imageMapBody.keySet()
            val map = mutableMapOf<String, UrlEmbedMapData>()
            for (k in keys) {
                map[k] = gson.fromJson(imageMapBody[k], UrlEmbedMapData::class.java)
            }
            return map
        }

    override fun toString(): String {
        return "ArticlePostBodyData(blocks=$blocks, imageMap=$imageMap, fileMap=$fileMap, urlEmbedMap=$urlEmbedMap)"
    }
}

class ImagePostBodyData(private val body: JsonObject) : PostBodyData() {
    override val blocks: List<PostBodyBlockData>
        get() {
            val text = body.getAsJsonPrimitive("text").asString
            return listOf(PostBodyBlockData("p").also {
                it.setText(text)
            })
        }
    override val imageMap: Map<String, ImageMapData>
        get() {
            if (!body.has("images")) return emptyMap()
            val imageMapBody = body.getAsJsonArray("images") ?: return emptyMap()
            return imageMapBody.map {
                val obj = gson.fromJson(it, ImageMapData::class.java)
                val id = obj.id
                return@map Pair(id, obj)
            }.toMap()
        }
    override val fileMap: Map<String, FileMapData>
        get() = emptyMap()
    override val embedMap: Map<String, Any>
        get() = emptyMap()
    override val urlEmbedMap: Map<String, UrlEmbedMapData>
        get() = emptyMap()

    override fun toString(): String {
        return "ImagePostBodyData(blocks=$blocks, imageMap=$imageMap)"
    }
}
