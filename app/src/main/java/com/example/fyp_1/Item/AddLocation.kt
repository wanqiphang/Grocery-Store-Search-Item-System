package com.example.fyp_1.Item

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.R
import com.example.fyp_1.databinding.ActivityAddLocationBinding

class AddLocation : AppCompatActivity() {

    private lateinit var binding: ActivityAddLocationBinding
    private lateinit var db: databaseItemLocate
    private lateinit var dbBranch: databaseBranch
    private lateinit var dbItem: databaseItem

    private var selectedBranchId: Int = -1
    private var itemId: Int = -1
    private var imageViewResource: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = databaseItemLocate(this)
        dbBranch = databaseBranch(this)
        dbItem = databaseItem(this)

        // Retrieve the itemID from the Intent
        itemId = intent.getIntExtra("Item_id", -1)
        imageViewResource = intent.getIntExtra("ImageViewResource", -1)

        Log.d("AddLocation", "itemId: $itemId, imageViewResource: $imageViewResource")

        // Retrieve the itemName from the database based on itemId
        val itemName = dbItem.getItemNameById(itemId)

        // Set the ItemName in the UI
        binding.textViewItemName2.text = itemName

        // Populate the spinner with branch names
        val branchNames = dbBranch.getAllBranches().map { it.branchName }
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_list, branchNames)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_list)
        binding.spinnerLocateBranch.adapter = spinnerAdapter

        // Set the selected branch ID when an item is selected in the spinner
        binding.spinnerLocateBranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedBranchId = dbBranch.getBranchIdByName(branchNames[position])
                Log.d("AddLocation", "Selected branchId: $selectedBranchId")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected
                selectedBranchId = -1
                Log.d("AddLocation", "No branch selected")
            }
        }

        // Retrieve the itemImage from the database and set it to the ImageView
        val dataItem = dbItem.getItemByID(itemId)
        val bitmap = BitmapFactory.decodeByteArray(dataItem.itemImage, 0, dataItem.itemImage.size)
        binding.imageViewItemLocateImage.setImageBitmap(bitmap)


        binding.buttonItemLocateDone.setOnClickListener {
            val itemLocationText = binding.itemLocationEdit.text.toString()

            // Check if a branch is selected
            if (selectedBranchId != -1 && itemId != -1) {
                val itemLocation = DataItemLocation(0, selectedBranchId, itemId, itemLocationText)
                db.insertItemLocation(itemLocation)
                finish()
                Toast.makeText(this, "Item Location Added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select a branch", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}