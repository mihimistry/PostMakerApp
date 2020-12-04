package com.maxgen.postmakerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.model.AssetModel

class ImageListAdapter(
    private val imageList: ArrayList<AssetModel>,
    private val context: Context,
    private val listener: OnChooseLogoListener
) : RecyclerView.Adapter<ImageListAdapter.ImageViewModel>() {
    class ImageViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewModel {
        return ImageViewModel(
            LayoutInflater.from(parent.context).inflate(R.layout.image_list_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewModel, position: Int) {
        Glide.with(context).load(imageList[position].dirName).into(holder.imageView)
        holder.itemView.setOnClickListener {
            listener.getLogoFromPostMaker(imageList[position].dirName)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}