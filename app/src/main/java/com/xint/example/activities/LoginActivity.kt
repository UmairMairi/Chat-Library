package com.xint.example.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.xint.chatlibrary.core.ChatCore
import com.xint.example.databinding.ActivityLoginBinding
import com.xint.example.extentions.getParcelable
import com.xint.example.extentions.pushFragment
import com.xint.example.fragments.LoginFragment
import com.xint.example.model.VerifyOTPModel
import com.xint.example.utils.Constants
import com.xint.example.utils.LogUtils
import com.xint.example.utils.PrefManager
import com.xint.example.viewmodels.LoginActivityViewModel

class LoginActivity : BaseActivity() {
    lateinit var binding:ActivityLoginBinding
    lateinit var viewModel:LoginActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginActivityViewModel::class.java]
        setContentView(binding.root)
        init()
        val model = PrefManager.getString(PrefManager.loginModel)
        model.let {
            if(it.isNotEmpty()){
                val data =  Gson().fromJson(model, VerifyOTPModel::class.java)
                startActivity(Intent(this@LoginActivity,MainActivity::class.java).putExtra(Constants.OnBoarding.loginModel,data))
            }
        }

        viewModel.let { vm->
            vm.loading?.observe(this@LoginActivity){ loading->
                showDismissLoader(isLoading = loading)
            }
            vm.errorMsg?.observe(this@LoginActivity){ msg->
                if(msg!=null && msg != ""){
                    LogUtils.error("ViewModel Error--->$msg")
                }
            }
        }
        ChatCore.instance?.initialization(
            context = this@LoginActivity,
            socketUrl = Constants.socketUrl,
            baseUrl = Constants.serverUrl,
            sessionId =  "",
            userId =  "",
        )
    }

    private fun init(){


        pushFragment(containerId = binding.fragmentFrame.id, fragment = LoginFragment())
    }
}