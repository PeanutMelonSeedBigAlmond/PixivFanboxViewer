package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.tabs.TabLayoutMediator
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.FragmentPostBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.adapter.ViewPagerAdapter

class PostFragment : BaseFragment<FragmentPostBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_post

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = ViewPagerAdapter(requireActivity(), fragments)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = tabText[pos]
        }.attach()
    }

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

    companion object {
        private val fragments = listOf(
            AllPostFragment(),
            SubscribedPostFragment()
        )

        private val tabText = arrayOf(
            "全部投稿",
            "已订阅的投稿"
        )
    }
}