package com.maxgen.postmakerapp.viewmodel

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.maxgen.postmakerapp.adapter.OnAddImagesListener
import com.maxgen.postmakerapp.adapter.OnCornerSelectionListener
import com.maxgen.postmakerapp.adapter.OnTemplateClickListeners

class TemplateViewModel : ViewModel() {

    var imageListener: OnAddImagesListener? = null
    var clickListeners: OnTemplateClickListeners? = null
    var cornerSelectionListener: OnCornerSelectionListener? = null

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

    fun onAddLogoSelected(context: Context) {
        val options = arrayOf<CharSequence>("from Gallery", "from PostMaker", "Cancel")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your Logo")
        builder.setItems(options) { dialog, item ->

            when {
                options[item] == "Choose from Gallery" -> {
                    imageListener?.getLogo("Gallery")
                }

                options[item] == "from PostMaker" -> {
                    imageListener?.getLogo("PostMaker")
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    fun onEditLogoSelectd(context: Context) {
        val options = arrayOf<CharSequence>(
            "Change logo",
            "Change logo Position",
            "Remove logo",
            "Cancel"
        )
        AlertDialog.Builder(context)
            .setTitle("Choose Action")
            .setItems(options, DialogInterface.OnClickListener { dialog, which ->
                when {
                    options[which] == "Change logo" -> {
                        onAddLogoSelected(context)
                    }
                    options[which] == "Change logo Position" -> {
                        cornerSelectionListener?.changeLogoPosition()
                    }
                    options[which] == "Remove logo" -> {
                        cornerSelectionListener?.removeLogo()
                    }
                    options[which] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }).show()
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

    fun onTSCornerSelected(view: View) {
        cornerSelectionListener?.setLogoOnTSCorner()
    }

    fun onTECornerSelected(view: View) {
        cornerSelectionListener?.setLogoOnTECorner()
    }

    fun onBSCornerSelected(view: View) {
        cornerSelectionListener?.setLogoOnBSCorner()
    }

    fun onBECornerSelected(view: View) {
        cornerSelectionListener?.setLogoOnBECorner()
    }

}