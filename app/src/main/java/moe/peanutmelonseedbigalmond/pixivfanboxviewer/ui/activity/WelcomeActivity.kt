package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity

import android.os.Bundle
import com.dylanc.longan.startActivity
import kotlinx.coroutines.*
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.CookieRepository
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base.BaseActivity

class WelcomeActivity : BaseActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cookie = CookieRepository.fanboxSessionId
        if (cookie.isBlank()) {
            startActivity<LoginActivity>()
        } else {
            startActivity<HomeActivity>()
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}