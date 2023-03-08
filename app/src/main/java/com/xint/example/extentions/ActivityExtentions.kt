package com.xint.example.extentions

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.xint.example.R
import com.xint.example.utils.Constants

fun FragmentActivity.pushFragment(
    containerId: Int,
    fragment: Fragment,
    bundle: Bundle? = null,
    addToStack: Boolean = false,
) {
    if (bundle != null) {
        fragment.arguments = bundle
    }
    val transaction = supportFragmentManager.beginTransaction()
        .setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        ).replace(containerId, fragment)
    if(addToStack){
        transaction.addToBackStack(Constants.Activity.stack)
    }
    transaction.commit()
}