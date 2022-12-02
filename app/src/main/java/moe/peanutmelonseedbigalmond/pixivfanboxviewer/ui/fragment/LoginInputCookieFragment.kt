package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.dylanc.longan.startActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.CookieRepository
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.FragmentLoginInputCookieBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.LayoutLoadingDialogBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.interceptor.HeaderInterceptor
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.HomeActivity
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base.BaseActivity
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginInputCookieFragment : BaseFragment<FragmentLoginInputCookieBinding>(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {
    override val layoutId: Int
        get() = R.layout.fragment_login_input_cookie

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding.helpImportCookie.setOnClickListener {
            val cookieKv = readCookieKVFromClipboard()
            if (!cookieKv.containsKey("FANBOXSESSID")) {
                showImportHelp()
                return@setOnClickListener
            }

            val fanboxSessid = cookieKv["FANBOXSESSID"]!!
            binding.fanboxSessidInput = fanboxSessid
            tryLogin()
        }

        binding.btnLogin.setOnClickListener { tryLogin() }
    }

    private fun tryLogin() {
        if (binding.fanboxSessidInput.isNullOrBlank()) return

        var dialog: AlertDialog? = null
        val job = launch(start = CoroutineStart.LAZY) {
            try {
                hideSoftKeyboard()
                dialog?.show()
                withContext(Dispatchers.IO) { checkCookieValid(mapOf("FANBOXSESSID" to binding.fanboxSessidInput!!.trim())) }
                CookieRepository.fanboxSessionId = binding.fanboxSessidInput!!.trim()
                (requireActivity() as BaseActivity).finishAllActivity()
                startActivity<HomeActivity>()
            } catch (e: Exception) {
                Snackbar.make(binding.container, e.toString(), Snackbar.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                dialog?.cancel()
            }
        }
        dialog = AlertDialog.Builder(requireContext())
            .setView(
                LayoutLoadingDialogBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    null,
                    false
                ).root
            )
            .setCancelable(false)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                if (job.isActive) {
                    job.cancel()
                }
            }
            .create()
        job.start()
    }

    private fun hideSoftKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                ?: return
        imm.hideSoftInputFromWindow(binding.root.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun showImportHelp() {
        AlertDialog.Builder(requireContext())
            .setMessage("必须是包含 FANBOXSESSID 的，以分号分隔的键值对")
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()
            .show()
    }

    private fun readCookieKVFromClipboard(): Map<String, String> {
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val str = clipboardManager?.primaryClip?.getItemAt(0)?.text ?: return emptyMap()
        try {
            if (str.contains(";")) {
                val kvs = str.split(";")
                return kvs.map {
                    val arr = it.trim().split("=")
                    val k = arr[0]
                    val v = arr[1]
                    return@map Pair(k, v)
                }.toMap()
            } else {
                return emptyMap()
            }
        } catch (_: Exception) {
            return emptyMap()
        }
    }

    /**
     * 检查Cookies是否有效
     * @see [moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.Api.getSubscribedPosts]
     */
    @Throws(Exception::class)
    private suspend fun checkCookieValid(cookies: Map<String, String>) {
        val client = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor(buildCookieStr(cookies)))
            .build()

        val url = "https://api.fanbox.cc/post.listHome?limit=10"
        val request = Request.Builder()
            .url(url)
            .build()
        val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
        val content = withContext(Dispatchers.IO) { response.body()!!.string() }
        Log.i(this::class.simpleName, content)

        val json = Gson().fromJson(content, JsonObject::class.java)
        if (json.has("error") && json.get("error").asString.isNotBlank()) {
            throw Exception("Error message: ${json.get("error").asString}")
        }
    }

    private fun buildCookieStr(cookies: Map<String, String>): String {
        val sb = StringBuffer()
        for ((k, v) in cookies) {
            sb.append(k)
            sb.append("=")
            sb.append(v)
            sb.append(";")
        }
        return sb.toString()
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}