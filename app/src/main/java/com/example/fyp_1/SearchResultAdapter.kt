package com.example.fyp_1

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp_1.Item.DataItem
import com.example.fyp_1.Item.databaseItem

class SearchResultAdapter (private var searchItems: List<DataItem>, context: Context, private val branchName: String) : RecyclerView.Adapter<SearchResultAdapter.SearchItemViewHolder>(){

    private val db: databaseItem = databaseItem(context)

    class SearchItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageResult: ImageView = itemView.findViewById(R.id.imageResult)
        val titleName: TextView = itemView.findViewById(R.id.titleName)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val linearLayoutSearchItem: LinearLayout = itemView.findViewById((R.id.linearLayoutSearchItem))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result_rv_item, parent, false)
        return SearchItemViewHolder(view)
    }

    override fun getItemCount(): Int = searchItems.size

    override fun onBindViewHolder(holder: SearchResultAdapter.SearchItemViewHolder, position: Int) {
        val searchItem = searchItems[position]
        holder.titleName.text = searchItem.itemName
        holder.itemPrice.text = searchItem.itemPrice

        // Convert ByteArray to Bitmap
        val bitmap = BitmapFactory.decodeByteArray(searchItem.itemImage, 0, searchItem.itemImage.size)
        holder.imageResult.setImageBitmap(bitmap)

        holder.linearLayoutSearchItem.setOnClickListener{
            val intent = Intent(holder.itemView.context, SpecificItemDetail::class.java).apply {
                putExtra("item_id", searchItem.itemID)
                putExtra("item_image", searchItem.itemImage)
                putExtra("item_name", searchItem.itemName)
                putExtra("item_price", searchItem.itemPrice)
                // Pass the branchName to SpecificItemDetail
                putExtra("branch_name", branchName)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    fun updateData(newData: List<DataItem>) {
        searchItems = newData
        notifyDataSetChanged()
    }

}