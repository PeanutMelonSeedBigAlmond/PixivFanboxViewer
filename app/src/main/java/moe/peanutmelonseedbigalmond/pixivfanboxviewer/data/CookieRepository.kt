package moe.peanutmelonseedbigalmond.pixivfanboxviewer.data

import com.dylanc.mmkv.MMKVOwner
import com.dylanc.mmkv.mmkvString

object CookieRepository : MMKVOwner {
    var fanboxSessionId by mmkvString("")
}