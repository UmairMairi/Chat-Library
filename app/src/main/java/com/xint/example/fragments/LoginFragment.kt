package com.xint.example.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.xint.example.activities.LoginActivity
import com.xint.example.databinding.FragmentLoginBinding
import com.xint.example.extentions.pushFragment
import com.xint.example.model.LoginModel
import com.xint.example.utils.Constants
import com.xint.example.utils.LogUtils
import com.xint.example.utils.Singleton
import com.xint.example.viewmodels.LoginActivityViewModel
import com.xint.example.viewmodels.LoginViewModel


class LoginFragment : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sharedViewModel: LoginActivityViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[LoginActivityViewModel::class.java]
        init()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun init(){
        binding.phoneLabel.text = "Enter Phone"
        binding.btnPrimary.tvBtnPrimary.text = "Verify"


        viewModel.let { vm->
            vm.loading?.observe(viewLifecycleOwner){ loading->
                sharedViewModel.loading?.value = loading
            }
            vm.errorMsg?.observe(viewLifecycleOwner){ msg->
                if(msg!=null && msg != ""){
                    sharedViewModel.errorMsg?.value = msg
                }
            }

            vm.sendOTPResponse?.observe(viewLifecycleOwner){rep->
                try{
                    val model =  Gson().fromJson(rep.toString(), LoginModel::class.java)
                    Singleton.instance?.tId = model.data?.tId

                    requireActivity().pushFragment((requireActivity() as LoginActivity).binding.fragmentFrame.id,OTPFragment())
                }catch(e:Exception){
                    LogUtils.debug("Send OTP Response Exception-->",e.message)
                    sharedViewModel.errorMsg?.value = "Something went wrong"
                }
            }
        }
        binding.btnPrimary.btnPrimary.setOnClickListener(this::onClick)
    }

    override fun onClick(view: View) {
        when(view.id){
            binding.btnPrimary.btnPrimary.id->{
                val phone: String = binding.countryCode.selectedCountryCode + binding.edtPhoneNumber.text.toString()
                Singleton.instance?.mobileNo = phone
                viewModel.sendOtp(mobileNo = phone, reason = Constants.OnBoarding.RIDER_LOGIN_REASON)
            }
        }
    }
}