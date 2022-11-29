package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.base

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

abstract class BaseDataViewBindingActivity<T : ViewDataBinding> : BaseActivity() {
    protected val binding by lazy { createViewDataBinding() }
    protected abstract fun createViewDataBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}