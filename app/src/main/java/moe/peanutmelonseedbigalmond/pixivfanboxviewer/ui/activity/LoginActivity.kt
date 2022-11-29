package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.dylanc.longan.startActivity
import com.google.android.material.snackbar.Snackbar
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.CookieRepository
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.ActivityLoginBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base.BaseDataViewBindingActivity
import java.time.Duration

class LoginActivity : BaseDataViewBindingActivity<ActivityLoginBinding>() {
    override fun createViewDataBinding(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.loginToolbar)

        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.loginWebView, true)
        CookieManager.getInstance().setAcceptCookie(true)
        binding.loginWebView.settings.javaScriptEnabled = true
        binding.loginWebView.webViewClient = MyWebViewClient()
        binding.loginWebView.webChromeClient = MyChromeWebViewClient()
        val url =
            "https://www.fanbox.cc/login?return_to=https%3A%2F%2Fwww.fanbox.cc%2Fcreators%2Ffind"
        binding.loginWebView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (binding.loginWebView.canGoBack()) {
            binding.loginWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            Log.d(this::class.simpleName, "Page finished: $url")
            super.onPageFinished(view, url)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            Log.d(this::class.simpleName, "Page started: $url")

            if (url == "https://www.fanbox.cc/creators/find") {
                val cookie = CookieManager.getInstance().getCookie(url)
                val cookieMap = cookie.split(';')
                    .map {
                        val str = it.trim()
                        val keyPair = str.split('=')
                        val k = keyPair[0]
                        val v = keyPair[1]
                        return@map Pair(k, v)
                    }.toMap()
                val sessionId = cookieMap["FANBOXSESSID"]
                if (sessionId.isNullOrBlank()) {
                    Snackbar.make(binding.loginWebView, "无法解析Cookie", Snackbar.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, "登录成功，正在跳转", Toast.LENGTH_SHORT).show()
                    CookieRepository.fanboxSessionId = sessionId
                    startActivity<HomeActivity>()
                    finish()
                }
                Log.d(this::class.simpleName, "Cookie: $cookie")
            }
            super.onPageStarted(view, url, favicon)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_login_page, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_back -> {
                if (binding.loginWebView.canGoBack()) {
                    binding.loginWebView.goBack()
                }
                true
            }
            R.id.menu_forward -> {
                if (binding.loginWebView.canGoForward()) {
                    binding.loginWebView.goForward()
                }
                true
            }
            R.id.menu_reload -> {
                binding.loginWebView.reload()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class MyChromeWebViewClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            binding.loginProgressBar.progress = newProgress
            if (newProgress >= 100) {
                binding.loginProgressBar.visibility = View.GONE
            } else {
                binding.loginProgressBar.visibility = View.VISIBLE
            }
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            binding.loginToolbar.subtitle = title
            super.onReceivedTitle(view, title)
        }
    }
}