package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity

import android.os.Bundle
import com.dylanc.longan.startActivity
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.CookieRepository
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.ActivityWelcomeBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base.BaseDataViewBindingActivity

class WelcomeActivity : BaseDataViewBindingActivity<ActivityWelcomeBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (CookieRepository.cookieValid) {
            startActivity<HomeActivity>()
            finish()
        }
        super.onCreate(savedInstanceState)
    }

    override fun createViewDataBinding(): ActivityWelcomeBinding =
        ActivityWelcomeBinding.inflate(layoutInflater)
}