package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.ActivityHomeBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base.BaseDataViewBindingActivity
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.adapter.ViewPagerAdapter
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment.PostFragment
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment.UserFragment
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.utils.BottomNavigationViewViewPager2Helper

class HomeActivity : BaseDataViewBindingActivity<ActivityHomeBinding>() {
    override fun createViewDataBinding(): ActivityHomeBinding =
        ActivityHomeBinding.inflate(layoutInflater)

    override fun onResume() {
        // 多窗口模式强制单列
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode) {
            binding.rootView.removeView(binding.rightContainer)
        }
        super.onResume()
    }

    companion object {
        private val fragments = listOf(
            PostFragment(),
            UserFragment()
        )
    }
}