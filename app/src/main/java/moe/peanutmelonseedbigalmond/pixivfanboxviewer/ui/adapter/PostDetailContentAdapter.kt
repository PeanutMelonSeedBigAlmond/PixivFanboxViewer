package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.adapter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseMultiItemAdapter
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.App
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.*
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.FileMapData
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.ImageMapData
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.PostBodyBlockData
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.UrlEmbedMapData
import org.jsoup.Jsoup

class PostDetailContentAdapter(
    private val images: Map<String, ImageMapData> = emptyMap(),
    private val files: Map<String, FileMapData> = emptyMap(),
    private val urlEmbeds: Map<String, UrlEmbedMapData> = emptyMap()
) :
    BaseMultiItemAdapter<PostDetailContentAdapter.ViewData>() {

    init {
        addItemType(ITEM_TYPE_PARAGRAPH,
            object : OnMultiItemAdapterListener<ViewData, ParagraphViewHolder> {
                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): ParagraphViewHolder = ParagraphViewHolder(
                    LayoutPostDetailPragraphBinding.inflate(
                        LayoutInflater.from(context), parent, false
                    )
                )

                override fun onBind(holder: ParagraphViewHolder, position: Int, item: ViewData?) {
                    holder.viewBinding.text = item?.content
                }
            })
            .addItemType(ITEM_TYPE_FILE,
                object : OnMultiItemAdapterListener<ViewData, FileViewHolder> {
                    override fun onBind(holder: FileViewHolder, position: Int, item: ViewData?) {
                        val f = files[item!!.content]!!
                        holder.viewBinding.fileSizeText = convertFileSizeToString(f.size)
                        holder.viewBinding.fileUrl = f.url
                        holder.viewBinding.filename = "${f.name}.${f.extension}"
                    }

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): FileViewHolder = FileViewHolder(
                        LayoutPostDetailFileBinding.inflate(
                            LayoutInflater.from(context), parent, false
                        )
                    )

                    fun convertFileSizeToString(size: Long): String {
                        if (size < 1024) return "$size B"

                        val kb = size.toDouble() / 1024
                        if (kb < 1024) return "${String.format("%.2f", kb)} KB"

                        val mb = kb / 1024
                        if (mb < 1024) return "${String.format("%.2f", mb)} MB"

                        val gb = mb / 1024
                        return "${String.format("%.2f", gb)} GB"
                    }
                })
            .addItemType(ITEM_TYPE_IMAGE,
                object : OnMultiItemAdapterListener<ViewData, ImageViewHolder> {
                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): ImageViewHolder = ImageViewHolder(
                        LayoutPostDetailImageBinding.inflate(
                            LayoutInflater.from(context), parent, false
                        )
                    )

                    override fun onBind(holder: ImageViewHolder, position: Int, item: ViewData?) {
                        val extraDataId = item?.content!!
                        val imageItem = images[extraDataId]

                        val viewWidth =
                            recyclerView.width - recyclerView.paddingLeft - recyclerView.paddingRight - recyclerView.marginRight - recyclerView.marginLeft
                        val height =
                            (viewWidth.toDouble() * imageItem!!.height / imageItem.width).toInt()
                        holder.viewBinding.imageView.updateLayoutParams {
                            this.height = height
                        }

                        Glide.with(holder.viewBinding.imageView)
                            .load(imageItem.thumbnailUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .placeholder(R.drawable.image_view_placeholder)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(16)))
                            .into(holder.viewBinding.imageView)
                    }
                })
            .addItemType(ITEM_TYPE_HEADER,
                object : OnMultiItemAdapterListener<ViewData, ParagraphHeaderViewHolder> {
                    override fun onBind(
                        holder: ParagraphHeaderViewHolder,
                        position: Int,
                        item: ViewData?
                    ) {
                        item?.let {
                            holder.viewBinding.text = it.content
                        }
                    }

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): ParagraphHeaderViewHolder = ParagraphHeaderViewHolder(
                        LayoutPostDetailParagraphHeaderBinding.inflate(
                            LayoutInflater.from(context),
                            parent,
                            false
                        )
                    )
                })
            .addItemType(
                ITEM_TYPE_URL_EMBED,
                object : OnMultiItemAdapterListener<ViewData, UrlEmbedWithBrowserViewHolder> {
                    override fun onBind(
                        holder: UrlEmbedWithBrowserViewHolder,
                        position: Int,
                        item: ViewData?
                    ) {
                        val id = item?.content ?: return
                        val html = urlEmbeds[id]?.html ?: return

                        val realUrl = Jsoup.parse(html)
                            .selectFirst("iframe")
                            ?.attr("src")
                        if (realUrl.isNullOrBlank()) return

                        holder.viewBinding.url = realUrl
                    }

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): UrlEmbedWithBrowserViewHolder =
                        UrlEmbedWithBrowserViewHolder(
                            LayoutPostDetailUrlEmbedWithBrowserBinding.inflate(
                                LayoutInflater.from(context), parent, false
                            )
                        )
                })
            .addItemType(ITEM_TYPE_URL_EMBED_FANBOX,
                object : OnMultiItemAdapterListener<ViewData, UrlEmbedFanboxViewHolder> {
                    override fun onBind(
                        holder: UrlEmbedFanboxViewHolder,
                        position: Int,
                        item: ViewData?
                    ) {
                        val id = item?.content ?: return
                        val postData = urlEmbeds[id]?.postInfo ?: return

                        holder.viewBinding.briefContentText = postData.briefContent
                        holder.viewBinding.dateText =
                            dateFormatter.format(postData.publishedDatetime)
                        holder.viewBinding.usernameText = postData.user.name
                        holder.viewBinding.priceText = "${postData.feeRequired} 日元"
                        holder.viewBinding.titleText = postData.title
                        holder.viewBinding.postId = postData.id
                        holder.viewBinding.coverImageUrl = postData.cover?.url
                        holder.viewBinding.coverType = postData.cover?.type

                        Glide.with(holder.viewBinding.userAvatar)
                            .load(postData.user.iconUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.viewBinding.userAvatar)
                        if (postData.cover?.url != null) {
                            holder.viewBinding.imageView.visibility = View.VISIBLE
                            Glide.with(holder.viewBinding.imageView)
                                .load(postData.cover.url)
                                .placeholder(R.drawable.image_view_placeholder)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.viewBinding.imageView)
                        } else {
                            holder.viewBinding.imageView.visibility = View.GONE
                        }
                    }

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): UrlEmbedFanboxViewHolder =
                        UrlEmbedFanboxViewHolder(
                            LayoutPostDetailEmbedFanboxBinding.inflate(
                                LayoutInflater.from(context), parent, false
                            )
                        )
                })
            .onItemViewType { pos, list ->
                val type = list[pos].type
                val typeCode = ITEM_TYPE_MAPPING[type] ?: 0
                if (typeCode == ITEM_TYPE_URL_EMBED && urlEmbeds[list[pos].content]?.type == "fanbox.post") {
                    // 特殊判断站内链接
                    return@onItemViewType ITEM_TYPE_URL_EMBED_FANBOX
                }
                return@onItemViewType typeCode
            }
    }

    data class ViewData(
        val type: String,
        val content: String,
        val style: List<PostBodyBlockData.ParagraphStyle>?,
    )

    private class ParagraphViewHolder(val viewBinding: LayoutPostDetailPragraphBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private class ImageViewHolder(val viewBinding: LayoutPostDetailImageBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private class FileViewHolder(val viewBinding: LayoutPostDetailFileBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private class ParagraphHeaderViewHolder(val viewBinding: LayoutPostDetailParagraphHeaderBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private class UrlEmbedWithBrowserViewHolder(val viewBinding: LayoutPostDetailUrlEmbedWithBrowserBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private class UrlEmbedFanboxViewHolder(val viewBinding: LayoutPostDetailEmbedFanboxBinding) :
        RecyclerView.ViewHolder(viewBinding.root)


    private companion object {
        const val ITEM_TYPE_PARAGRAPH = 0
        const val ITEM_TYPE_FILE = 1
        const val ITEM_TYPE_EMBED = 0
        const val ITEM_TYPE_URL_EMBED = 3
        const val ITEM_TYPE_IMAGE = 4
        const val ITEM_TYPE_HEADER = 5
        const val ITEM_TYPE_URL_EMBED_FANBOX = 6

        val ITEM_TYPE_MAPPING = mapOf(
            "p" to ITEM_TYPE_PARAGRAPH,
            "image" to ITEM_TYPE_IMAGE,
            "file" to ITEM_TYPE_FILE,
            "url_embed" to ITEM_TYPE_URL_EMBED,
            "embed" to ITEM_TYPE_EMBED,
            "header" to ITEM_TYPE_HEADER,
        )

        val dateFormatter by lazy { DateFormat.getLongDateFormat(App.context) }
    }
}