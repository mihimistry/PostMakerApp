package com.maxgen.postmakerapp.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.model.AssetModel

class FontAdapter(
    private val list: ArrayList<AssetModel>,
    private val context: Context,
    private val listener: OnFontChangeListener
) :
    RecyclerView.Adapter<FontAdapter.FontViewHolder>() {

    class FontViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.item_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontViewHolder {
        return FontViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FontViewHolder, position: Int) {
        holder.textView.tag = "" + position
        val typeface = Typeface.createFromAsset(
            context.assets, list[position].dirName
        )
        holder.textView.typeface = typeface
        holder.textView.text = "Abcd"

        holder.textView.setOnClickListener {
            listener.onFontChange(typeface)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}