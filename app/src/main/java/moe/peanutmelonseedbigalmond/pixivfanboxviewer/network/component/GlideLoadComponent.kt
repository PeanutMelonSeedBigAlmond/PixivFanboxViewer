package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.component

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.data.CookieRepository
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import java.io.InputStream

@GlideModule
class GlideLoadComponent : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(
                OkHttpClient.Builder()
                    .addInterceptor(HeaderInterceptor(CookieRepository.cookie))
                    .build()
            )
        )
    }
}