package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.dylanc.longan.startActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.PreferenceRepository
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()

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

    private fun setupActionBar() {
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(binding.toolbar)

            activity.addMenuProvider(MyMenuProvider(), viewLifecycleOwner)
        }
    }

    private fun loadPage(url: String? = null) {
        launch {
            try {
                binding.swipeRefreshLayout.isRefreshing = true
                val data = withContext(Dispatchers.IO) {
                    if (PreferenceRepository.showSupportingOnly) {
                        Client.getSupportingPostData(url)
                    } else {
                        Client.getSubscribedPostData(url)
                    }
                }
                val list = data.items
                if (url == null) {
                    // 第一页
                    adapter.submitList(emptyList())
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

    private inner class MyMenuProvider : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_all_post_fragment, menu)
            menu.findItem(R.id.menu_all_post_fragment_show_supporting_only).isChecked =
                PreferenceRepository.showSupportingOnly
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.menu_all_post_fragment_show_supporting_only -> {
                    menuItem.isChecked = !menuItem.isChecked
                    PreferenceRepository.showSupportingOnly = menuItem.isChecked
                    loadPage()
                }
            }
            return true
        }
    }
}