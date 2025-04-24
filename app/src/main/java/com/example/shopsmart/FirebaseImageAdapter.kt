package com.example.shopsmart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import com.bumptech.glide.Glide

class FirebaseImageAdapter(private val context: Context, private val imageUrls: List<String>) : android.widget.BaseAdapter() {

    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun getItem(position: Int): Any {
        return imageUrls[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_image, parent, false)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        Glide.with(context)
            .load(imageUrls[position])
            .into(imageView)

        return view
    }
}
