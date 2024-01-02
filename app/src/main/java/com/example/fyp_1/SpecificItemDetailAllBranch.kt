package com.example.fyp_1

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.Item.databaseItem
import com.example.fyp_1.Item.databaseItemLocate
import com.example.fyp_1.databinding.ActivitySpecificItemDetailAllBranchBinding

class SpecificItemDetailAllBranch : AppCompatActivity() {

    private lateinit var binding: ActivitySpecificItemDetailAllBranchBinding
    private lateinit var imageView: ImageView

    private lateinit var dbBranch: databaseBranch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpecificItemDetailAllBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database helper
        dbBranch = databaseBranch(this)

        // Retrieve data from Intent
        val itemId = intent.getIntExtra("item_id", 0)
        val itemName = intent.getStringExtra("item_name")
        val itemPrice = intent.getStringExtra("item_price")

        // Now you can use these values in your activity as needed
        binding.name.text = itemName
        binding.price.text = itemPrice

        imageView = binding.imageItemDetail
        // Set the image based on itemId
        val itemImage = getItemImageById(itemId)
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(itemImage, 0, itemImage?.size ?: 0))

        // Set up the spinner with branch names
        val branchNames = getItemBranches(itemId)
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_list, branchNames)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_list)
        binding.spinnerBranch.adapter = spinnerAdapter

        // Set a listener to capture the selected branch name
        binding.spinnerBranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedBranchName = branchNames[position]
                // Update the location based on the selected branch name
                updateLocation(selectedBranchName, itemId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected
                // You can leave this empty or add additional logic as needed
            }
        }

        //navigation
        //navigate to Main page
        binding.buttonHome1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //navigate to search
        binding.buttonSearch1.setOnClickListener {
            val intentSearch = Intent(this, SearchItem::class.java)
            startActivity(intentSearch)
        }

        //navigate to chatbot
        val chatbotOnClickListener = View.OnClickListener {
            val intentChatbot = Intent(this, Chatbot::class.java)
            startActivity(intentChatbot)
        }
        binding.buttonChatbot1.setOnClickListener(chatbotOnClickListener)
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    // Helper methods to get branchId
    private fun getBranchIdByName(branchName: String): Int {
        val db = databaseBranch(this)
        return db.getBranchIdByName(branchName)
    }

    // Helper method to get item location based on itemId and branchId
    private fun getItemLocationById(itemId: Int, branchId: Int): String {
        val db = databaseItemLocate(this)
        return db.getItemLocationById(itemId, branchId)
    }

    private fun getItemImageById(itemId: Int): ByteArray? {
        val db = databaseItem(this)
        return db.getItemImageById(itemId)
    }

    // Helper method to get all branch names
    private fun getBranchNames(): List<String> {
        val db = databaseBranch(this)
        return db.getAllBranches().map { it.branchName }
    }

    // Helper method to update the location based on the selected branch name
    private fun updateLocation(selectedBranchName: String, itemId: Int) {
        val branchId = getBranchIdByName(selectedBranchName)
        val location = getItemLocationById(itemId, branchId)
        binding.location.text = location
    }

    // Helper method to get branch names associated with the item
    private fun getItemBranches(itemId: Int): List<String> {
        val dbItemLocate = databaseItemLocate(this)
        val branchIds = dbItemLocate.getBranchIdsByItemId(itemId)
        return branchIds.map { dbBranch.getBranchNameById(it) }
    }
}