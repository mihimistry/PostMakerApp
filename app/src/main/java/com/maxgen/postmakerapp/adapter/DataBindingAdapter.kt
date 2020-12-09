package com.maxgen.postmakerapp.adapter

import android.text.TextUtils
import android.util.Patterns

import android.widget.EditText

import androidx.databinding.BindingAdapter

class DataBindingAdapter {
    @BindingAdapter("usernameValidator")
    fun usernameValidator(editText: EditText, username: String?) {
        // ignore infinite loops
        val minimumLength = 6
        if (TextUtils.isEmpty(username)) {
            editText.error = null
            return
        }
        if (editText.text.toString().isEmpty()) {
            editText.error = "Enter Username"
        } else editText.error = null
    }

    @BindingAdapter("emailValidator")
    fun emailValidator(editText: EditText, email: String?) {
        // ignore infinite loops
        if (TextUtils.isEmpty(email)) {
            editText.error = null
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText.error = "Enter Valid Email Address"
        } else editText.error = null
    }

    @BindingAdapter("passwordValidator")
    fun passwordValidator(editText: EditText, password: String?) {
        // ignore infinite loops
        val minimumLength = 6
        if (TextUtils.isEmpty(password)) {
            editText.error = null
            return
        }
        if (editText.text.toString().length < minimumLength) {
            editText.error = "Password must be minimum $minimumLength length"
        } else editText.error = null
    }

    @BindingAdapter("mobileValidator")
    fun mobileValidator(editText: EditText, password: String?) {
        // ignore infinite loops
        val minimumLength = 10
        if (TextUtils.isEmpty(password)) {
            editText.error = null
            return
        }
        if (editText.text.toString().length < minimumLength) {
            editText.error = "Password must be minimum $minimumLength length"
        } else editText.error = null
    }

    @BindingAdapter("websiteValidator")
    fun websiteValidator(editText: EditText, website: String?) {
        // ignore infinite loops
        if (TextUtils.isEmpty(website)) {
            editText.error = null
            return
        }
        if (editText.text.toString().isEmpty()) {
            editText.error = "Enter Website"
        } else editText.error = null
    }
}