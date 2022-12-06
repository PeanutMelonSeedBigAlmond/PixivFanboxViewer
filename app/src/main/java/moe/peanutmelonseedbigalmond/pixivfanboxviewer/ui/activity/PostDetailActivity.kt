package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity

import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.FragmentTransaction
import com.dylanc.longan.intentExtras
import com.dylanc.longan.safeIntentExtras
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base.BaseActivity
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment.PostDetailFragment

class PostDetailActivity : BaseActivity() {
    private val rootViewId = "PostActivityContainer".hashCode()
    private val postId by safeIntentExtras<String>("postId")
    private val title by safeIntentExtras<String>("title")
    private val username by safeIntentExtras<String>("username")
    private val coverUrl by intentExtras<String?>("coverUrl")
    private val coverType by intentExtras<String?>("coverType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = FrameLayout(this)
        rootView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        rootView.id = rootViewId
        setContentView(rootView)

        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(
                rootViewId, PostDetailFragment.newInstance(
                    postId, title, username, coverUrl, coverType
                )
            )
            .commit()
    }
}