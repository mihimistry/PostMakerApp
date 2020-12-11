package com.maxgen.postmakerapp.adapter

interface OnCornerSelectionListener {
    fun changeLogoPosition()
    fun setLogoOnTSCorner()
    fun setLogoOnTECorner()
    fun setLogoOnBSCorner()
    fun setLogoOnBECorner()
    fun removeLogo()
    fun resizeLogo()
}