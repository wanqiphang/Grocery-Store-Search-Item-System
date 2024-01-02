package com.example.fyp_1.Admin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp_1.Branch.CreateBranch
import com.example.fyp_1.Branch.ViewBranch
import com.example.fyp_1.Item.CreateItem
import com.example.fyp_1.Item.ViewItem
import com.example.fyp_1.MainActivity
import com.example.fyp_1.R
import com.example.fyp_1.databinding.ActivityAdminBinding


class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Navigate to Create, Read branch data
        binding.createBranchButton.setOnClickListener {
            val branchIntent = Intent(this, CreateBranch::class.java)
            startActivity(branchIntent)
        }

        binding.viewBranchButton.setOnClickListener {
            val viewBranchIntent = Intent(this, ViewBranch::class.java)
            startActivity(viewBranchIntent)
        }

        //Navigate to Create, Read item data
        binding.createItemButton.setOnClickListener {
            val itemIntent = Intent(this, CreateItem::class.java)
            startActivity(itemIntent)
        }

        binding.viewItemButton.setOnClickListener {
            val viewItemIntent = Intent(this, ViewItem::class.java)
            startActivity(viewItemIntent)
        }

        //Logout
        val logoutButton: ImageButton = findViewById(R.id.imageButtonLogout)

        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.logout_confirmation, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Yes") { dialog, which ->
                // User clicked "Yes," perform logout
                Toast.makeText(this, "Logout confirmed", Toast.LENGTH_SHORT).show()

                // Add your logout logic here
                navigateToMainActivity()
            }
            .setNegativeButton("No") { dialog, which ->
                // User clicked "No," do nothing
                Toast.makeText(this, "Logout canceled", Toast.LENGTH_SHORT).show()
            }
            .create()

        alertDialog.show()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: Close the current activity after navigating
    }
}