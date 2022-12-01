package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity

import android.os.Bundle
import com.dylanc.longan.startActivity
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.CookieRepository
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base.BaseActivity

class WelcomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (CookieRepository.cookieValid) {
            startActivity<HomeActivity>()
        } else {
            startActivity<LoginActivity>()
        }
        finish()
    }
}