package com.example.fyp_1.Branch

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp_1.R

class BranchAdapter(private var branches: List<DataBranch>, context: Context) :
    RecyclerView.Adapter<BranchAdapter.BranchViewHolder>() {

    private val db: databaseBranch = databaseBranch(context)

    class BranchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewBranchName: TextView = itemView.findViewById(R.id.textViewBranchName)
        val textViewBranchLocation: TextView = itemView.findViewById(R.id.textViewBranchLocation)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_all_branch_rv, parent, false)
        return BranchViewHolder(view)
    }

    override fun getItemCount(): Int = branches.size

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        val branch = branches[position]
        holder.textViewBranchName.text = branch.branchName
        holder.textViewBranchLocation.text = branch.branchLocation

        // Below is the update branch by clicking the update button and navigating to the page
        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateBranch::class.java).apply {
                putExtra("branch_id", branch.branchID)
            }
            holder.itemView.context.startActivity(intent)
        }

        // Below is the delete branch by clicking the delete button
        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, branch)
        }
    }

    // Have to keep refreshing the list whenever a new node is inserted
    fun refreshData(newBranch: List<DataBranch>) {
        branches = newBranch
        notifyDataSetChanged()
    }

    // Function to show the delete confirmation dialog
    private fun showDeleteConfirmationDialog(context: Context, branch: DataBranch) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Confirmation")
        builder.setMessage("Are you sure you want to delete this branch?")

        // Confirm button
        builder.setPositiveButton("Yes") { _, _ ->
            // Delete the branch if the user confirms
            db.deleteBranch(branch.branchID)
            refreshData(db.getAllBranches())
            Toast.makeText(context, "Branch Deleted", Toast.LENGTH_SHORT).show()
        }

        // Cancel button
        builder.setNegativeButton("No") { _, _ ->
            // Do nothing if the user cancels
        }

        val dialog = builder.create()
        dialog.show()
    }
}
