package com.xint.example.watchers;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.xint.example.R;
import com.xint.example.utils.LogUtils;

public class MyTextWatcher implements TextWatcher {
    private final EditText[] editText;
    private final View view;

    public MyTextWatcher(View view, EditText[] editText) {
        this.editText = editText;
        this.view = view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        if (editText.length == 4) {
            switch (view.getId()) {
                case R.id.otp_edit_box1:
                    if (text.length() == 1)
                        editText[1].requestFocus();
                    break;
                case R.id.otp_edit_box2:
                    if (text.length() == 1)
                        editText[2].requestFocus();
                    else if (text.length() == 0)
                        editText[0].requestFocus();
                    break;
                case R.id.otp_edit_box3:
                    if (text.length() == 1)
                        editText[3].requestFocus();
                    else if (text.length() == 0)
                        editText[1].requestFocus();
                    break;
                case R.id.otp_edit_box4:
                    if (text.length() == 0)
                        editText[2].requestFocus();
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.otp_edit_box1:
                    if (text.length() == 1)
                        editText[1].requestFocus();
                    break;
                case R.id.otp_edit_box2:
                    if (text.length() == 1)
                        editText[2].requestFocus();
                    else if (text.length() == 0)
                        editText[0].requestFocus();
                    break;
                case R.id.otp_edit_box3:
                    if (text.length() == 1)
                        editText[3].requestFocus();
                    else if (text.length() == 0)
                        editText[1].requestFocus();
                    break;
                case R.id.otp_edit_box4:
                    if (text.length() == 1)
                        editText[3].clearFocus();
                    else if (text.length() == 0)
                        editText[2].requestFocus();
                    break;

            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence text, int arg1, int arg2, int arg3) {

    }

    @Override
    public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
        LogUtils.INSTANCE.debug("On Text change--> ",text.toString());

    }

}