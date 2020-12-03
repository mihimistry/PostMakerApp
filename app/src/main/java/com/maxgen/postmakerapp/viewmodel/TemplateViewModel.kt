package com.maxgen.postmakerapp.viewmodel

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.maxgen.postmakerapp.adapter.OnAddImagesListener
import com.maxgen.postmakerapp.adapter.OnTemplateClickListeners

class TemplateViewModel : ViewModel() {

    var imageListener: OnAddImagesListener? = null
    var clickListeners: OnTemplateClickListeners? = null

    fun onBackgroundImageSelected(view: View) {
        val options = arrayOf<CharSequence>("Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Choose your Background Image")
        builder.setItems(options) { dialog, item ->

            when {
                options[item] == "Choose from Gallery" -> {
                    imageListener?.getBackgroundImage()
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    fun onAddLogoSelected(view: View) {
        val options = arrayOf<CharSequence>("Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Choose your Logo")
        builder.setItems(options) { dialog, item ->

            when {
                options[item] == "Choose from Gallery" -> {
                    imageListener?.getLogo()
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    fun onQuoteEditSelected(view: View) {
        clickListeners?.editText()
    }

    fun onChangeTextColorSelected(view: View) {
        clickListeners?.changeTextColor()
    }

    fun onChangeTextStyleSelected(view: View) {
        clickListeners?.changeTextStyle()
    }

    fun onChangeTextFontSelected(view: View) {
        clickListeners?.changeTextFont()
    }

    fun onTextEditingDoneSelected(view: View) {
        clickListeners?.doneTextEditing()
    }

    fun onFontChangeDoneSelected(view: View) {
        clickListeners?.doneChangeFont()
    }

    fun onDefaultFontSelected(view: View) {
        clickListeners?.setDefaultFont()
    }
}