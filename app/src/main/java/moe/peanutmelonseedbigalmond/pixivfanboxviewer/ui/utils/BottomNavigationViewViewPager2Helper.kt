package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.utils

import android.view.MenuItem
import androidx.core.view.forEachIndexed
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationViewViewPager2Helper(
    private val bottomNavigationView: BottomNavigationView,
    private val viewPager: ViewPager2,
    private val config: ((BottomNavigationView, ViewPager2) -> Unit)? = null
) {
    private val map = mutableMapOf<MenuItem, Int>()

    init {
        bottomNavigationView.menu.forEachIndexed { index, item ->
            map[item] = index
        }
    }

    fun bind() {
        config?.invoke(bottomNavigationView, viewPager)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.selectedItemId =
                    bottomNavigationView.menu.getItem(position).itemId
            }
        })

        bottomNavigationView.setOnItemSelectedListener {
            viewPager.currentItem=map[it]!!
            return@setOnItemSelectedListener true
        }
    }
}