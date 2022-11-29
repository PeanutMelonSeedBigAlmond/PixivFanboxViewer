package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val cookie: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("Cookie", cookie)
            .addHeader("Referer", "https://www.fanbox.cc")
            .addHeader("Origin", "https://www.fanbox.cc")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.7,zh-TW;q=0.6")
            .build()
        return chain.proceed(newRequest)
    }
}