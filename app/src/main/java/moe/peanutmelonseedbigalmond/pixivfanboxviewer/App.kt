package moe.peanutmelonseedbigalmond.pixivfanboxviewer

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors

class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}