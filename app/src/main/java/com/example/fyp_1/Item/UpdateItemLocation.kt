package com.example.fyp_1.Item

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.R
import com.example.fyp_1.databinding.ActivityUpdateItemLocationBinding

class UpdateItemLocation : AppCompatActivity(){

    private lateinit var binding: ActivityUpdateItemLocationBinding
    private lateinit var db: databaseItemLocate
    private lateinit var dbBranch: databaseBranch
    private lateinit var dbItem: databaseItem

    private lateinit var imageView: ImageView

    private var selectedBranchId: Int = -1
    private var itemId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateItemLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = databaseItemLocate(this)
        dbBranch = databaseBranch(this)
        dbItem = databaseItem(this)

        // Retrieve the item location ID from the Intent
        val itemLocationId = intent.getIntExtra("item_location_id", -1)

        // Retrieve existing item location details
        val existingItemLocation = db.getItemLocationByID(itemLocationId)

        // Retrieve the item ID from the Intent
        //the name and defaultValue must match with the putExtra() in ViewItemLocation.kt
        itemId = intent.getIntExtra("item_id", -1)

        imageView = binding.imageViewUpdateItemLocateImage
        // Set the image based on itemId
        val itemImage = getItemImageById(itemId)
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(itemImage, 0, itemImage?.size ?: 0))


        // Retrieve the itemName from the database based on itemId
        val itemName = dbItem.getItemNameById(itemId)
        Log.d("UpdateLocation", "Item Name: $itemName")
        Log.d("UpdateLocation", "Item id: $itemId")

        // Set the ItemName in the UI
        binding.textViewItemName3.text = itemName

        // Populate the spinner with branch names
        val branchNames = dbBranch.getAllBranches().map { it.branchName }
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_list, branchNames)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_list)
        binding.spinnerUpdateLocateBranch.adapter = spinnerAdapter

        // Set existing values in UI elements
        binding.itemUpdateLocationEdit.setText(existingItemLocation.location)

        // Set the selected branch ID when an item is selected in the spinner
        val branchNameOfExistingItem = dbBranch.getBranchNameById(existingItemLocation.branchID)
        val branchPosition = branchNames.indexOf(branchNameOfExistingItem)
        binding.spinnerUpdateLocateBranch.setSelection(branchPosition)

        binding.spinnerUpdateLocateBranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedBranchId = dbBranch.getBranchIdByName(branchNames[position])
                Log.d("UpdateLocation", "Selected branchId: $selectedBranchId")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected
                selectedBranchId = -1
                Log.d("UpdateLocation", "No branch selected")
            }
        }

        binding.buttonUpdateItemLocateDone.setOnClickListener {
            val updatedItemLocationText =
                binding.itemUpdateLocationEdit.text.toString()

            // Check if a branch is selected
            if (selectedBranchId != -1) {
                // Update the item location details in the database
                val updatedItemLocation = DataItemLocation(existingItemLocation.itemLocateID, selectedBranchId, itemId, updatedItemLocationText)
                db.updateItemLocation(updatedItemLocation)
                finish()
                Toast.makeText(this, "Item Location Updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select a branch", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun getItemImageById(itemId: Int): ByteArray? {
        val db = databaseItem(this)
        return db.getItemImageById(itemId)
    }

}
