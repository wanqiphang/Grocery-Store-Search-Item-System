package com.example.fyp_1.Branch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import android.widget.Toast
import com.example.fyp_1.R
import com.example.fyp_1.databinding.ActivityUpdateBranchBinding

class UpdateBranch : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBranchBinding
    private lateinit var db: databaseBranch
    private var branchId: Int = -1 //represent the value is empty

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = databaseBranch(this)

        branchId = intent.getIntExtra("branch_id", -1)
        if (branchId == -1){
            finish()
            return
        }

        val branch = db.getBranchByID(branchId)
        binding.updatebranchNameEdit.setText(branch.branchName)
        binding.updatebranchLocationEdit.setText(branch.branchLocation)

        binding.buttonEditDone.setOnClickListener {
            val newBranchName = binding.updatebranchNameEdit.text.toString()
            val newBranchLocation = binding.updatebranchLocationEdit.text.toString()
            val updatedBranch = DataBranch(branchId, newBranchName, newBranchLocation)
            db.updateBranch(updatedBranch)
            finish()
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}