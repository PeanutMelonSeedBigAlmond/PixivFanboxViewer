package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.FragmentPostBinding

class PostFragment : BaseFragment<FragmentPostBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_post

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
}