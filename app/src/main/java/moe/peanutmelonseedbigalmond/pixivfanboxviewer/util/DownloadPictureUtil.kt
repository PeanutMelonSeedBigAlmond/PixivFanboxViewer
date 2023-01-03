package moe.peanutmelonseedbigalmond.pixivfanboxviewer.util

import android.content.Context
import android.widget.Toast
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import cc.shinichi.library.tool.image.ImageUtil
import com.anggrayudi.storage.extension.closeStreamQuietly
import com.anggrayudi.storage.file.openOutputStream
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File

object DownloadPictureUtil {
    @JvmStatic
    fun startDownload(context: Context, root: String, url: String) {
        Glide.with(context).downloadOnly().load(url).listener(object : RequestListener<File> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<File>?,
                isFirstResource: Boolean
            ): Boolean {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show()
                e?.printStackTrace()
                return false
            }

            override fun onResourceReady(
                resource: File,
                model: Any?,
                target: Target<File>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                try {
                    val mimeType = ImageUtil.getImageTypeWithMime(resource.absolutePath)
                    val name = System.currentTimeMillis().toString()
                    val targetFile = DocumentFile
                        .fromTreeUri(context, root.toUri())!!
                        .createFile("image/$mimeType", name)
                    val outputStream = targetFile!!.openOutputStream(context)!!
                    val inputStream = resource.inputStream()
                    inputStream.copyTo(outputStream)
                    inputStream.closeStreamQuietly()
                    outputStream.closeStreamQuietly()
                    Toast.makeText(context, "下载成功：${targetFile.name}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
                return false
            }
        })
            .preload()
    }
}