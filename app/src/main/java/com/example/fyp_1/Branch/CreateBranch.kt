package com.example.fyp_1.Branch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.fyp_1.R
import com.example.fyp_1.databinding.ActivityCreateBranchBinding

class CreateBranch : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBranchBinding
    private lateinit var db: databaseBranch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = databaseBranch(this)

        binding.buttonDone.setOnClickListener {
            val branchName = binding.branchNameEdit.text.toString()
            val branchLocation = binding.branchLocationEdit.text.toString()
            val branch = DataBranch(0, branchName, branchLocation)

            if (db.insertBranch(branch)) {
                // Insertion successful
                finish()
                Toast.makeText(this, "Branch Added", Toast.LENGTH_SHORT).show()
            } else {
                // Insertion failed due to duplicate branch location
                Toast.makeText(this, "Branch location already exists", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}