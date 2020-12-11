package com.maxgen.postmakerapp.adapter

interface OnTemplateClickListeners {
    fun editText()
    fun changeTextColor(lastSelectedColor: Int)
    fun changeTextStyle()
    fun changeTextFont()
    fun doneTextEditing()
    fun doneChangeFont()
    fun setDefaultFont()
    fun getPreviousActivity()
    fun addTextShadow()
    fun onSeekDone()
    fun changeTextSize()
    fun deleteAllItems()
    fun closeWebEdit()
    fun closeQuoteEdit()
    fun editWebText()
}