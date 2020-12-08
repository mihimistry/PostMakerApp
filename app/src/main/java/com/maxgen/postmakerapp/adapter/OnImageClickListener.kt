package com.maxgen.postmakerapp.adapter

interface OnImageClickListener {
    fun getImageFromCamera()
    fun getImageFromGallery()
    fun removeImage()
}