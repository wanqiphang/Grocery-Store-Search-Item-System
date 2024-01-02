package com.example.fyp_1

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp_1.Item.DataItem

class MainItemAdapter(private var itemList: List<DataItem>, private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<MainItemAdapter.mainItemViewHolder>(){

    class mainItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemImageView : ImageView = itemView.findViewById(R.id.imageViewMain)
        val itemNameTv : TextView = itemView.findViewById(R.id.textViewMain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mainItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item_rv, parent, false)
        return mainItemViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: mainItemViewHolder, position: Int) {
        val item = itemList[position]

        // Convert ByteArray to Bitmap
        val bitmap = BitmapFactory.decodeByteArray(item.itemImage, 0, item.itemImage.size)
        holder.itemImageView.setImageBitmap(bitmap)

        holder.itemNameTv.text = item.itemName

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SpecificItemDetailAllBranch::class.java).apply {
                putExtra("item_id", item.itemID)
                putExtra("item_image", item.itemImage)
                putExtra("item_name", item.itemName)
                putExtra("item_price", item.itemPrice)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    // Have to keep refreshing the list whenever a new node is inserted
    fun refreshData(newItem: List<DataItem>) {
        itemList = newItem
        notifyDataSetChanged()
    }
}

interface ItemClickListener {
    fun onViewItemLocationClicked(itemId: Int)
}