package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.SubtitleCollapsingToolbarLayout

class MySubtitleCollapseToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : SubtitleCollapsingToolbarLayout(context, attrs, defStyle) {

    init {
        ViewCompat.setOnApplyWindowInsetsListener(this,null)
    }
}