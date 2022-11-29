package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response.PostBodyData

class PostDetailFragmentVM : ViewModel() {
    val postId by lazy { MutableLiveData<String>() }
    val title by lazy { MutableLiveData<String>() }
    val username by lazy { MutableLiveData<String>() }
    val coverUrl by lazy { MutableLiveData<String?>() }
    val coverType by lazy { MutableLiveData<String?>() }

    val postBody by lazy { MutableLiveData<PostBodyData?>() }
}