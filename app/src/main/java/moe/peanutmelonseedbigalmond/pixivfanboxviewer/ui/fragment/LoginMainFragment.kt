package moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.dylanc.longan.startActivity
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.R
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.databinding.FragmentLoginMainBinding
import moe.peanutmelonseedbigalmond.pixivfanboxviewer.ui.activity.LoginActivity

class LoginMainFragment : BaseFragment<FragmentLoginMainBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_login_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLoginViaBrowser.setOnClickListener {
            startActivity<LoginActivity>()
        }

        binding.btnInputCookie.setOnClickListener {
            findNavController()
                .navigate(R.id.action_loginMainFragment_to_loginInputCookieFragment)
        }
    }
}