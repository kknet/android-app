package net.ivpn.client.v2.captcha

/*
 IVPN Android app
 https://github.com/ivpn/android-app

 Created by Oleksandr Mykhailenko.
 Copyright (c) 2020 Privatus Limited.

 This file is part of the IVPN Android app.

 The IVPN Android app is free software: you can redistribute it and/or
 modify it under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any later version.

 The IVPN Android app is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details.

 You should have received a copy of the GNU General Public License
 along with the IVPN Android app. If not, see <https://www.gnu.org/licenses/>.
*/

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import net.ivpn.client.IVPNApplication
import net.ivpn.client.R
import net.ivpn.client.common.billing.addfunds.Plan
import net.ivpn.client.common.extension.navigate
import net.ivpn.client.common.extension.setNavigationResultBoolean
import net.ivpn.client.databinding.FragmentCaptchaBinding
import net.ivpn.client.ui.dialog.DialogBuilder
import net.ivpn.client.ui.dialog.Dialogs
import net.ivpn.client.ui.login.LoginNavigator
import net.ivpn.client.v2.MainActivity
import net.ivpn.client.v2.login.LoginViewModel
import net.ivpn.client.v2.viewmodel.SignUpViewModel
import org.slf4j.LoggerFactory
import javax.inject.Inject

class CaptchaFragment : Fragment(), LoginNavigator {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CaptchaFragment::class.java)
    }

    lateinit var binding: FragmentCaptchaBinding

    @Inject
    lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var signUp: SignUpViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_captcha, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IVPNApplication.getApplication().appComponent.provideActivityComponent().create().inject(this)
        initViews()
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onStart() {
        super.onStart()
        setNavigationResultBoolean(false, "session_limit_dialogue")
        activity?.let {
            if (it is MainActivity) {
                it.setAdjustResizeMode()
                it.setContentSecure(false)
            }
        }
        viewModel.navigator = this
    }

    private fun initViews() {
        binding.contentLayout.viewmodel = viewModel

        binding.contentLayout.editText.onFocusChangeListener = viewModel.captchaFocusListener
        binding.contentLayout.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.submitCaptcha()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.contentLayout.submitButton.setOnClickListener {
            viewModel.submitCaptcha()
        }
    }

    private fun initToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onLogin() {
        LOGGER.info("onLogin")
        val action = CaptchaFragmentDirections.actionCaptchaFragmentToSyncFragment()
        navigate(action)
    }

    override fun onLoginWithBlankAccount() {
        if (viewModel.isAccountNewStyle()) {
            signUp.blankAccountID.set(viewModel.username.get())

            val action = CaptchaFragmentDirections.actionCaptchaFragmentToSignUpFragment()
            navigate(action)
        } else {
            onLogin()
        }
    }

    override fun onLoginWithInactiveAccount() {
        if (viewModel.isAccountNewStyle()) {
            signUp.selectedPlan.set(Plan.getPlanByProductName(viewModel.getAccountType()))

            val action = CaptchaFragmentDirections.actionCaptchaFragmentToSignUpPeriodFragment()
            navigate(action)
        } else {
            onLogin()
        }
    }

    override fun onInvalidAccount() {
        findNavController().popBackStack()
    }

    override fun openSessionLimitReachedDialogue() {
        setNavigationResultBoolean(true, "session_limit_dialogue")
        findNavController().popBackStack()
    }

    override fun openCaptcha() {
        //Do nothing
    }

    override fun openErrorDialogue(dialog: Dialogs) {
        DialogBuilder.createNotificationDialog(context, dialog)
    }

    override fun openCustomErrorDialogue(title: String?, message: String?) {
        DialogBuilder.createFullCustomNotificationDialog(context, title, message)
    }

    override fun openTFAScreen() {
        //This callback should never be fired.
    }
}