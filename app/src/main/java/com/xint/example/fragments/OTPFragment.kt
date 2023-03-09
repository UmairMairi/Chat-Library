package com.xint.example.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.xint.example.R
import com.xint.example.activities.MainActivity
import com.xint.example.databinding.FragmentOTPBinding
import com.xint.example.model.LoginModel
import com.xint.example.model.VerifyOTPModel
import com.xint.example.utils.Constants
import com.xint.example.utils.LogUtils
import com.xint.example.utils.PrefManager
import com.xint.example.utils.Singleton
import com.xint.example.viewmodels.LoginActivityViewModel
import com.xint.example.viewmodels.OTPViewModel
import com.xint.example.watchers.MyTextWatcher

class OTPFragment : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentOTPBinding
    private lateinit var viewModel: OTPViewModel
    private lateinit var sharedViewModel: LoginActivityViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[OTPViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[LoginActivityViewModel::class.java]
        init()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        binding.title.text = "Enter OTP"
        binding.tvEnterOtp.text = "Code Sent To ${Singleton.instance?.mobileNo}"
        binding.btnPrimary.tvBtnPrimary.text = "continue"

        val edit = arrayOf(
            binding.otpFields.otpEditBox1,
            binding.otpFields.otpEditBox2,
            binding.otpFields.otpEditBox3,
            binding.otpFields.otpEditBox4
        )
        binding.otpFields.otpEditBox1.addTextChangedListener(MyTextWatcher(binding.otpFields.otpEditBox1, edit))
        binding.otpFields.otpEditBox2.addTextChangedListener(MyTextWatcher(binding.otpFields.otpEditBox2, edit))
        binding.otpFields.otpEditBox3.addTextChangedListener(MyTextWatcher(binding.otpFields.otpEditBox3, edit))
        binding.otpFields.otpEditBox4.addTextChangedListener(MyTextWatcher(binding.otpFields.otpEditBox4, edit))
        binding.otpFields.otpEditBox1.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.otpFields.otpEditBox1.setBackgroundResource(R.drawable.edit_text_background)
            } else {
                if (binding.otpFields.otpEditBox1.text.isEmpty()) {
                    binding.otpFields.otpEditBox1.setBackgroundResource(R.drawable.red_border)
                } else {
                    binding.otpFields.otpEditBox1.setBackgroundResource(R.drawable.green_border)
                }
            }
        }
        binding.otpFields.otpEditBox2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.otpFields.otpEditBox2.setBackgroundResource(R.drawable.edit_text_background)
            } else {
                if (binding.otpFields.otpEditBox2.text.isEmpty()) {
                    binding.otpFields.otpEditBox2.setBackgroundResource(R.drawable.red_border)
                } else {
                    binding.otpFields.otpEditBox2.setBackgroundResource(R.drawable.green_border)
                }
            }
        }
        binding.otpFields.otpEditBox3.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.otpFields.otpEditBox3.setBackgroundResource(R.drawable.edit_text_background)
            } else {
                if (binding.otpFields.otpEditBox3.text.isEmpty()) {
                    binding.otpFields.otpEditBox3.setBackgroundResource(R.drawable.red_border)
                } else {
                    binding.otpFields.otpEditBox3.setBackgroundResource(R.drawable.green_border)
                }
            }
        }
        binding.otpFields.otpEditBox4.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.otpFields.otpEditBox4.setBackgroundResource(R.drawable.edit_text_background)
            } else {
                if (binding.otpFields.otpEditBox4.text.isEmpty()) {
                    binding.otpFields.otpEditBox4.setBackgroundResource(R.drawable.red_border)
                } else {
                    binding.otpFields.otpEditBox4.setBackgroundResource(R.drawable.green_border)
                }
            }
        }

        binding.otpFields.otpEditBox2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.otpFields.otpEditBox2.text.toString().isEmpty()) {
                    binding.otpFields.otpEditBox1.requestFocus()
                }
            }
            false
        }
        binding.otpFields.otpEditBox3.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.otpFields.otpEditBox3.text.toString().isEmpty()) {
                    binding.otpFields.otpEditBox2.requestFocus()
                }
            }
            false
        }
        binding.otpFields.otpEditBox4.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.otpFields.otpEditBox4.text.toString().isEmpty()) {
                    binding.otpFields.otpEditBox3.requestFocus()
                }
            }
            false
        }

        viewModel.let { vm->
            vm.loading?.observe(viewLifecycleOwner){ loading->
                sharedViewModel.loading?.value = loading
            }
            vm.errorMsg?.observe(viewLifecycleOwner){ msg->
                if(msg!=null && msg != ""){
                    sharedViewModel.errorMsg?.value = msg
                }
            }

            vm.verifyOTPResponse?.observe(viewLifecycleOwner){rep->
                try{
                    PrefManager.putString(PrefManager.loginModel,"$rep")
                    val model =  Gson().fromJson(rep.toString(), VerifyOTPModel::class.java)
                    requireActivity().startActivity(Intent(requireActivity(),MainActivity::class.java).putExtra(Constants.OnBoarding.loginModel,model))
                }catch(e:Exception){
                    LogUtils.error("Verify Otp Exception---> ${e.message}")
                }
            }
        }
        binding.btnPrimary.btnPrimary.setOnClickListener(this::onClick)
    }

    override fun onClick(view: View) {
        when(view.id){
            binding.btnPrimary.btnPrimary.id->{
                val otp = "${binding.otpFields.otpEditBox1.text}${binding.otpFields.otpEditBox2.text}${binding.otpFields.otpEditBox3.text}${binding.otpFields.otpEditBox4.text}"
                viewModel.verifyOTP(
                    mobileNo = Singleton.instance?.mobileNo ?: "",
                    otp = otp,
                    tId = Singleton.instance?.tId ?: "",
                )
            }
        }
    }
}