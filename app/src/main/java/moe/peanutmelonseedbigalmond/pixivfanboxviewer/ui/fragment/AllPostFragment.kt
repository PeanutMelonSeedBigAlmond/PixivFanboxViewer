package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.dylanc.longan.startActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.FragmentAllPostBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.Client
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.PostDetailActivity
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.adapter.PostListRecyclerViewAdapter

class AllPostFragment : BaseFragment<FragmentAllPostBinding>(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {
    private lateinit var adapter: PostListRecyclerViewAdapter
    private var nextPageUrl: String? = null
        set(value) {
            Log.i(this::class.simpleName, "nextPageUrl=$value")
            field = value
        }
    private lateinit var helper: QuickAdapterHelper
    override val layoutId: Int
        get() = R.layout.fragment_all_post

    override fun onResume() {
        super.onResume()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { v, windowsInserts ->
            val inserts = windowsInserts.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = inserts.top
                bottomMargin = inserts.bottom
                leftMargin = inserts.left
                rightMargin = inserts.right
            }
            return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.submitList(emptyList())
            nextPageUrl = null
            loadPage(null)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostListRecyclerViewAdapter()
        adapter.animationEnable = true
        adapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.getItem(position)
            Log.i(this@AllPostFragment::class.simpleName, data.toString())
            if (data != null) {
                startActivity<PostDetailActivity>(
                    "postId" to data.id,
                    "title" to data.title,
                    "username" to data.user.name,
                    "coverUrl" to data.cover?.url,
                    "coverType" to data.cover?.type
                )
            } else {
                Snackbar.make(binding.container, "data is null", Snackbar.LENGTH_SHORT).show()
            }
        }
        helper = QuickAdapterHelper.Builder(adapter)
            .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                override fun onLoad() {
                    loadPage(nextPageUrl)
                }

                override fun onFailRetry() {
                    loadPage(nextPageUrl)
                }

                override fun isAllowLoading(): Boolean {
                    return !binding.swipeRefreshLayout.isRefreshing
                }
            }).build()
        binding.recyclerView.adapter = helper.adapter

        loadPage(nextPageUrl)
    }

    private fun loadPage(url: String? = null) {
        launch {
            try {
                binding.swipeRefreshLayout.isRefreshing = true
                val data = withContext(Dispatchers.IO) { Client.getSubscribedPostData(url) }
                val list = data.items
                if (url == null) {
                    // 第一页
                    adapter.submitList(list)
                } else {
                    adapter.addAll(list)
                }
                nextPageUrl = data.nextUrl
            } catch (e: Exception) {
                showError(e.toString())
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
                helper.trailingLoadState = LoadState.NotLoading(nextPageUrl == null)
            }
        }
    }

    private fun showError(text: String) {
        Snackbar.make(binding.container, text, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}