package com.xint.example.activities

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.xint.example.utils.LogUtils
import com.xint.example.R

open class BaseActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    private fun showProgressIndicator() {
        try {
            if (progressDialog == null || !progressDialog!!.isShowing) {
                progressDialog = ProgressDialog.show(this, null, null, true, false)
                progressDialog!!.setContentView(R.layout.progress_dialogue_layout)
                progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val img = progressDialog!!.findViewById<ImageView>(R.id.progress_img)

                val rotate = RotateAnimation(
                    0F, 360F,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                )

                rotate.duration = 2000
                rotate.repeatCount = Animation.INFINITE
                img.startAnimation(rotate)

            }
        } catch (e: Exception) {
            LogUtils.debug("Loader Exception-->", e.message)
        }
    }

    private fun dismissProgressIndicator() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun showDismissLoader(isLoading: Boolean) {
        if (isLoading) {
            this.showProgressIndicator()
        } else {
            this.dismissProgressIndicator()
        }
    }

}