package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    private val activities = mutableListOf<BaseActivity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activities.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        activities.remove(this)
    }

    fun finishAllActivity() {
        for (activity in activities) {
            try {
                activity.finish()
                activities.remove(activity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}