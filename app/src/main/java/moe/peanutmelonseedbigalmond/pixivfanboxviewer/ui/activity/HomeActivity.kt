package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity

import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.ActivityHomeBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base.BaseDataViewBindingActivity

class HomeActivity : BaseDataViewBindingActivity<ActivityHomeBinding>() {
    override fun createViewDataBinding(): ActivityHomeBinding =
        ActivityHomeBinding.inflate(layoutInflater)


}