package com.example.gan_image

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gan_image.model.Items

class GridListAdapter(var ItemList: ArrayList<Items>) : RecyclerView.Adapter<GridListAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.storylist_item,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return ItemList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data=ItemList.get(position)
        holder.setListData(data)

        holder.itemView.setOnClickListener{
            val intent=Intent(holder.itemView.context,ClickBookActivity::class.java)
            var storyID:Int=data.id
            val storyTitle:String=data.title
           // val storyImage:String=data.image

            intent.putExtra("storyID",storyID)
            intent.putExtra("storytitle",storyTitle)

            ContextCompat.startActivity(holder.itemView.context,intent,null)
        }
    }
    class Holder(itemView:View):RecyclerView.ViewHolder(itemView) {

        val titleText = itemView.findViewById<TextView>(R.id.TitleText)
        val firstImage = itemView.findViewById<ImageView>(R.id.FirstImage)

        fun setListData(item: Items) {
            titleText.setText(item.title)

            var encodeByte= Base64.decode(item.image, Base64.DEFAULT)
            var bitmapDecode= BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.size)
            firstImage.setImageBitmap(bitmapDecode)
        }
    }
}


