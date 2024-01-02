package com.example.fyp_1.Item

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.R

class ItemLocationAdapter(
    private var itemLocations: List<DataItemLocation>,
    private val context: Context,
    private val listener: ItemLocationClickListener?
) :
    RecyclerView.Adapter<ItemLocationAdapter.ItemLocationViewHolder>() {

    private val db: databaseItemLocate = databaseItemLocate(context)
    private val dbBranch: databaseBranch = databaseBranch(context)
    private val dbItem: databaseItem = databaseItem(context)

    class ItemLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewItemBranchName: TextView = itemView.findViewById(R.id.textViewItemBranchName)
        val textViewItemLocation: TextView = itemView.findViewById(R.id.textViewItemLocation)
        val updateItemLocationButton: ImageView = itemView.findViewById(R.id.updateItemLocationButton)
        val deleteItemLocationButton: ImageView = itemView.findViewById(R.id.deleteItemLocationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemLocationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_location, parent, false)
        return ItemLocationViewHolder(view)
    }

    override fun getItemCount(): Int = itemLocations.size

    override fun onBindViewHolder(holder: ItemLocationViewHolder, position: Int) {
        val itemLocate = itemLocations[position]

        // Get the branch name from the branchId
        val branchName = dbBranch.getBranchNameById(itemLocate.branchID)

        // Set the branch name and item location in TextViews
        holder.textViewItemBranchName.text = branchName
        holder.textViewItemLocation.text = itemLocate.location

        // Below is update branch by clicking the update button and navigating to the page
        holder.updateItemLocationButton.setOnClickListener {
            listener?.onUpdateButtonClick(itemLocate.itemLocateID, itemLocate.itemID)
        }

        // Below is delete branch by clicking the delete button
        holder.deleteItemLocationButton.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, itemLocate)
        }
    }

    // Have to keep refreshing the list whenever a new node is inserted
    fun refreshData(newItemLocation: List<DataItemLocation>) {
        itemLocations = newItemLocation
        notifyDataSetChanged()
    }

    // Function to show the delete confirmation dialog
    private fun showDeleteConfirmationDialog(context: Context, itemLocate: DataItemLocation) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Confirmation")
        builder.setMessage("Are you sure you want to delete this item location?")

        // Confirm button
        builder.setPositiveButton("Yes") { _, _ ->
            // Delete the item location if the user confirms
            db.deleteItemLocation(itemLocate.itemLocateID)
            refreshData(db.getAllItemLocationByItemId(itemLocate.itemID))
            Toast.makeText(context, "Item Location Deleted", Toast.LENGTH_SHORT).show()
        }

        // Cancel button
        builder.setNegativeButton("No") { _, _ ->
            // Do nothing if the user cancels
        }

        val dialog = builder.create()
        dialog.show()
    }
}


interface ItemLocationClickListener {
        fun onUpdateButtonClick(itemLocationId: Int, itemId: Int)
    }
