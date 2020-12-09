package com.maxgen.postmakerapp.utils

import android.widget.EditText

object MyUtils {


    fun getEDTText(edt: EditText?): String {
        return edt?.text?.toString()?.trim { it <= ' ' } ?: ""
    }

    fun setEDTError(edt: EditText, error: String) {
        edt.error = error
    }

}