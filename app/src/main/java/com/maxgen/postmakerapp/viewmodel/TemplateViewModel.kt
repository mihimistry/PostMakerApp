package com.maxgen.postmakerapp.viewmodel

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.adapter.OnAddImagesListener
import com.maxgen.postmakerapp.adapter.OnCornerSelectionListener
import com.maxgen.postmakerapp.adapter.OnTemplateClickListeners

class TemplateViewModel : ViewModel() {

    var imageListener: OnAddImagesListener? = null
    var clickListeners: OnTemplateClickListeners? = null
    var cornerSelectionListener: OnCornerSelectionListener? = null

    fun onBackgroundImageSelected(view: View) {
        val options = arrayOf<CharSequence>("from Gallery", "from PostMaker", "Cancel")
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Choose Background Image")
        builder.setItems(options) { dialog, item ->

            when {
                options[item] == "from Gallery" -> {
                    imageListener?.getBackgroundImage(view.context.resources.getString(R.string.from_gallery))
                }

                options[item] == "from PostMaker" -> {
                    imageListener?.getBackgroundImage(view.context.resources.getString(R.string.from_app))
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
        builder.setTitle("Choose Logo")
        builder.setItems(options) { dialog, item ->

            when {
                options[item] == "from Gallery" -> {
                    imageListener?.getLogo(context.resources.getString(R.string.from_gallery))
                }

                options[item] == "from PostMaker" -> {
                    imageListener?.getLogo(context.resources.getString(R.string.from_app))
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    fun onEditLogoSelected(context: Context) {
        val options = arrayOf<CharSequence>(
                "Change logo",
                "Change logo Position",
                "Resize logo",
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
                        options[which] == "Resize logo" -> {
                            cornerSelectionListener?.resizeLogo()
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
        ColorPickerDialogBuilder
                .with(view.context)
                .setTitle("Choose color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    clickListeners?.changeTextColor(it)
                }
                .setPositiveButton("ok", ColorPickerClickListener { d, lastSelectedColor, allColors ->
                    clickListeners?.changeTextColor(lastSelectedColor)

                })
                .setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .build()
                .show()
    }

    fun onChangeTextStyleSelected(view: View) {
        clickListeners?.changeTextStyle()
    }

    fun onChangeTextSizeSelected(view: View) {
        clickListeners?.changeTextSize()
    }

    fun onChangeTextFontSelected(view: View) {
        clickListeners?.changeTextFont()
    }

    fun onTextShadowSelected(view: View) {
        clickListeners?.addTextShadow()
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

    fun onSeekDoneSelected(view: View) {
        clickListeners?.onSeekDone()
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

    fun onWebEditClosed(view: View) {
        clickListeners?.closeWebEdit()
    }

    fun onQuoteEditClosed(view: View) {
        clickListeners?.closeQuoteEdit()
    }

    fun onWebEditSelected(view: View) {
        clickListeners?.editWebText()

    }

    fun onEdit1LogoSelected(context: Context) {
        val options = arrayOf<CharSequence>(
                "Change logo",
                "Resize logo",
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
                        options[which] == "Resize logo" -> {
                            cornerSelectionListener?.resizeLogo()
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

    fun onDeleteItemsSelected(view: View) {
        val deleteDialog = AlertDialog.Builder(view.context)
                .setTitle("Are You Sure?")
                .setMessage("It will clear all edited items and will not be Restored")
                .setPositiveButton("Yes, Clear", DialogInterface.OnClickListener { dialog, which ->
                    clickListeners?.deleteAllItems()
                }).setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

        deleteDialog.show()
    }


}