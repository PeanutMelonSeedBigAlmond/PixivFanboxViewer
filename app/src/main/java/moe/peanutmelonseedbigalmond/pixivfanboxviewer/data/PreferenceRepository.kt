package moe.peanutmelonseedbigalmond.pixivfanboxviewer.data

import com.dylanc.mmkv.MMKVOwner
import com.dylanc.mmkv.mmkvBool
import com.dylanc.mmkv.mmkvString

object PreferenceRepository : MMKVOwner {
    var lastAuthorizedUri by mmkvString()
    var showSupportingOnly by mmkvBool(false)
}