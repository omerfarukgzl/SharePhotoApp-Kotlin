package com.example.sharephoto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PhotoRecyclerAdapter (val postList : ArrayList<Post>) : RecyclerView.Adapter<PhotoRecyclerAdapter.PostHolder>(){
    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return PostHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.recycler_row_kullanici_email).text=postList[position].email
        holder.itemView.findViewById<TextView>(R.id.recycler_row_yorum_text).text=postList[position].comment
        Picasso.get().load(postList.get(position).url).into(holder.itemView.findViewById<ImageView>(R.id.recycler_row_imageview))
    }

}