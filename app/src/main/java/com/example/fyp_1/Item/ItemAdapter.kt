package com.example.fyp_1.Item

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.R

class ItemAdapter(
    private var items: List<DataItem>,
    context: Context,
    private val itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val db: databaseItem = databaseItem(context)

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewItemName: TextView = itemView.findViewById(R.id.textViewItemName)
        val textViewItemPrice: TextView = itemView.findViewById(R.id.textViewItemPrice)
        val imageViewImage: ImageView = itemView.findViewById(R.id.imageViewImage)
        val updateItemButton: ImageView = itemView.findViewById(R.id.updateItemButton)
        val deleteItemButton: ImageView = itemView.findViewById(R.id.deleteItemButton)
        val addLocationButton: Button = itemView.findViewById(R.id.buttonAddLocation)
        val buttonViewLocation: Button = itemView.findViewById(R.id.buttonViewLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_rv, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.textViewItemName.text = item.itemName
        holder.textViewItemPrice.text = item.itemPrice

        // Convert ByteArray to Bitmap
        val bitmap = BitmapFactory.decodeByteArray(item.itemImage, 0, item.itemImage.size)
        holder.imageViewImage.setImageBitmap(bitmap)

        holder.itemView.setOnClickListener {
            itemClickListener.onViewItemLocationClicked(item.itemID)
        }

        // Below is the update branch by clicking the update button and navigating to the page
        holder.updateItemButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateItem::class.java).apply {
                putExtra("item_id", item.itemID)
            }
            holder.itemView.context.startActivity(intent)
        }

        // Below is the delete branch by clicking the delete button
        holder.deleteItemButton.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, item)
        }

        // Below is add item location for a specific branch by clicking add location button and navigating to the page
        holder.addLocationButton.setOnClickListener {
            val addLocationIntent = Intent(holder.itemView.context, AddLocation::class.java).apply {
                putExtra("Item_id", item.itemID)
                // Pass the ImageView resource ID to AddLocation activity
                putExtra("ImageViewResource", holder.imageViewImage.id)
            }
            holder.itemView.context.startActivity(addLocationIntent)
        }

        // Below is view all the locations of the item which have been recorded in data
        holder.buttonViewLocation.setOnClickListener {
            itemClickListener.onViewItemLocationClicked(item.itemID)
        }
    }

    // Have to keep refreshing the list whenever a new node is inserted
    fun refreshData(newItem: List<DataItem>) {
        items = newItem
        notifyDataSetChanged()
    }

    // Function to show the delete confirmation dialog
    private fun showDeleteConfirmationDialog(context: Context, item: DataItem) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Confirmation")
        builder.setMessage("Are you sure you want to delete this item?")

        // Confirm button
        builder.setPositiveButton("Yes") { _, _ ->
            // Delete the item if the user confirms
            db.deleteItem(item.itemID)
            refreshData(db.getAllItems())
            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
        }

        // Cancel button
        builder.setNegativeButton("No") { _, _ ->
            // Do nothing if the user cancels
        }

        val dialog = builder.create()
        dialog.show()
    }
}


interface ItemClickListener {
    fun onViewItemLocationClicked(itemId: Int)
}
