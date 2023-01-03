package moe.peanutmelonseedbigalmond.pixivfanboxviewer.util

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

object SAFUtil {
    @JvmStatic
    fun checkUriPermission(context: Context, uri: String?): Boolean {
        return if (uri?.isNotBlank() == true) {
            try {
                val u = Uri.parse(uri)
                val root = DocumentFile.fromTreeUri(context, u)
                val canRead = root?.canRead() ?: false
                val canWrite = root?.canWrite() ?: false
                return canRead && canWrite
            } catch (_: SecurityException) {
                false
            }
        } else {
            false
        }
    }
}