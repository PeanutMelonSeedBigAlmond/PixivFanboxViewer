package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cc.shinichi.library.ImagePreview
import cc.shinichi.library.bean.ImageInfo
import cc.shinichi.library.view.listener.OnDownloadClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dylanc.longan.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.PreferenceRepository
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.ActivityHomeBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.FragmentPostDetailBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.LayoutPostDetailEmbedFanboxBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.LayoutPostDetailUrlEmbedWithBrowserBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.Client
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.PostBodyData
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.HomeActivity
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.PostDetailActivity
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.adapter.PostDetailContentAdapter
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.viewmodel.PostDetailFragmentVM

class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {
    override val layoutId: Int
        get() = R.layout.fragment_post_detail

    private lateinit var vm: PostDetailFragmentVM
    private val postId by safeArguments<String>("postId")
    private val title by safeArguments<String>("title")
    private val username by safeArguments<String>("username")
    private val coverUrl by arguments<String?>("coverUrl")
    private val coverType by arguments<String?>("coverType")

    private lateinit var adapter: PostDetailContentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, windowsInserts ->
            val inserts = windowsInserts.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = inserts.top
                bottomMargin = inserts.bottom
                leftMargin = inserts.left
                rightMargin = inserts.right
            }
            return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
        }

        vm = ViewModelProvider(this)[PostDetailFragmentVM::class.java]
        vm.title.observe(viewLifecycleOwner) {
            binding.titleStr = it
        }
        vm.username.observe(viewLifecycleOwner) {
            binding.subtitleStr = it
        }
        vm.coverUrl.observe(viewLifecycleOwner) {
            if (it != null && coverType == "cover_image") {
                Glide.with(binding.paddingView)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.paddingView)
            }
        }
        vm.postBody.observe(viewLifecycleOwner, this::renderPostBody)
        vm.postId.postValue(postId)
        vm.title.postValue(title)
        vm.username.postValue(username)
        vm.coverUrl.postValue(coverUrl)
        vm.coverType.postValue(coverType)

        loadData()
    }

    private fun loadData() {
        launch {
            binding.swipeRefreshLayout.isRefreshing = true
            try {
                val data = withContext(Dispatchers.IO) { Client.getPostData(postId) }
                val body = data.postBody
                vm.postBody.postValue(body)
            } catch (e: java.lang.Exception) {
                Snackbar.make(binding.container, e.toString(), Snackbar.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun renderPostBody(body: PostBodyData?) {
        if (body == null) {
            Snackbar.make(
                binding.container, "无法获取文章内容，可能是没有订阅", Snackbar.LENGTH_INDEFINITE
            ).show()
            return
        }

        adapter = PostDetailContentAdapter(
            images = body.imageMap,
            files = body.fileMap,
            urlEmbeds = body.urlEmbedMap
        )
        adapter.setOnItemClickListener { adapter, view, position ->
            val data = adapter.getItem(position)!!
            if (data.type == "url_embed") {
                val viewBinding = DataBindingUtil.findBinding<ViewDataBinding?>(view)
                if (viewBinding is LayoutPostDetailUrlEmbedWithBrowserBinding) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewBinding.url))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Snackbar.make(binding.container, "无法打开链接: $e", Snackbar.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else if (viewBinding is LayoutPostDetailEmbedFanboxBinding) {
                    val activity = requireActivity()
                    if (activity is HomeActivity) {
                        val binding =
                            DataBindingUtil.findBinding<ViewDataBinding?>(activity.contentView)

                        if (binding is ActivityHomeBinding) {
                            val rightContainer = binding.rightContainer
                            if (rightContainer != null) {
                                val fm = activity.supportFragmentManager
                                fm.beginTransaction()
                                    .replace(
                                        rightContainer.id, newInstance(
                                            title = viewBinding.titleText!!,
                                            username = viewBinding.usernameText!!,
                                            coverType = viewBinding.coverType,
                                            coverUrl = viewBinding.coverImageUrl,
                                            postId = viewBinding.postId!!
                                        )
                                    )
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(null)
                                    .commit()
                            } else {
                                startActivity<PostDetailActivity>(
                                    "title" to viewBinding.titleText,
                                    "username" to viewBinding.usernameText,
                                    "coverType" to viewBinding.coverType,
                                    "coverUrl" to viewBinding.coverImageUrl,
                                    "postId" to viewBinding.postId
                                )
                            }
                        }
                    } else {
                        startActivity<PostDetailActivity>(
                            "title" to viewBinding.titleText,
                            "username" to viewBinding.usernameText,
                            "coverType" to viewBinding.coverType,
                            "coverUrl" to viewBinding.coverImageUrl,
                            "postId" to viewBinding.postId
                        )
                    }
                } else {
                    return@setOnItemClickListener
                }
            }
            if (data.type != "image") return@setOnItemClickListener
            val itemId = data.content
            val images = vm.postBody.value!!.imageMap
            val index = images.keys.indexOf(itemId)

            val imageInfoList = images.map { im ->
                ImageInfo().also {
                    it.thumbnailUrl = im.value.thumbnailUrl
                    it.originUrl = im.value.originalUrl
                }
            }.toMutableList()

            ImagePreview.instance
                .setContext(requireActivity())
                .setImageInfoList(imageInfoList)
                .setIndex(index)
                .setDownloadClickListener(object : OnDownloadClickListener() {
                    override val isInterceptDownload: Boolean
                        get() = true

                    override fun onClick(activity: Activity?, view: View?, position: Int) {
                        val imageData = imageInfoList[position]
                        Log.i(this@PostDetailFragment::class.simpleName, imageData.toString())
                    }
                })
                .start()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        try {
            val viewData = body.blocks.map {
                return@map PostDetailContentAdapter.ViewData(
                    type = it.type,
                    content = it.content,
                    style = it.styles
                )
            }
            adapter.submitList(viewData)
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(
                binding.container, "渲染内容时出现错误：$e", Snackbar.LENGTH_INDEFINITE
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    companion object {
        fun newInstance(
            postId: String,
            title: String,
            username: String,
            coverUrl: String?,
            coverType: String?
        ): PostDetailFragment {
            return PostDetailFragment().withArguments(
                "postId" to postId,
                "username" to username,
                "title" to title,
                "coverUrl" to coverUrl,
                "coverType" to coverType
            )
        }
    }
}