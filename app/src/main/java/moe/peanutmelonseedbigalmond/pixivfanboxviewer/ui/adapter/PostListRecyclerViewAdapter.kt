package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.dylanc.longan.getCompatColor
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.App
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.CookieRepository
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.LayoutPostItemBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.PostItemData

class PostListRecyclerViewAdapter :
    BaseQuickAdapter<PostItemData, PostListRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(val binding: LayoutPostItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: PostItemData?) {
        item?.let {
            holder.binding.titleText = it.title
            holder.binding.usernameText = it.user.name
            holder.binding.dateText = dateFormatter.format(it.updatedDatetime)
            holder.binding.briefContentText = it.briefContent
            holder.binding.priceText = "${it.feeRequired} 日元"

            Glide.with(holder.binding.userAvatar)
                .load(it.user.iconUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.binding.userAvatar)
            if (it.cover?.url != null) {
                holder.binding.imageView.visibility = View.VISIBLE
                Glide.with(holder.binding.imageView)
                    .load(it.cover.url)
                    .placeholder(R.drawable.image_view_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.binding.imageView)
            } else {
                holder.binding.imageView.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutPostItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    companion object {
        val dateFormatter by lazy { DateFormat.getLongDateFormat(App.context) }
    }
}