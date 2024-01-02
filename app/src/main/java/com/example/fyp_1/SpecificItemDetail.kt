package com.example.fyp_1

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.Item.databaseItem
import com.example.fyp_1.Item.databaseItemLocate
import com.example.fyp_1.databinding.ActivitySpecificItemDetailBinding

class SpecificItemDetail : AppCompatActivity() {

    private lateinit var binding: ActivitySpecificItemDetailBinding
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpecificItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent
        val itemId = intent.getIntExtra("item_id", 0)
        val itemName = intent.getStringExtra("item_name")
        val itemPrice = intent.getStringExtra("item_price")
        val branchName = intent.getStringExtra("branch_name") ?: "" // Retrieve branchName
        val branchId = getBranchIdByName(branchName)
        val location = getItemLocationById(itemId, branchId)

        // Now you can use these values in your activity as needed
        binding.name.text = itemName
        binding.price.text = itemPrice
        binding.branchName.text = branchName
        binding.location.text = location

        imageView = binding.imageItemDetail
        // Set the image based on itemId
        val itemImage = getItemImageById(itemId)
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(itemImage, 0, itemImage?.size ?: 0))

        //navigation
        //navigate to Main page
        binding.buttonHome1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //navigate to search
        binding.buttonSearch1.setOnClickListener {
            val intentSearch = Intent(this, SearchItem::class.java).apply {
                putExtra("branch_name", branchName)
            }
            startActivity(intentSearch)
        }

        //navigate to chatbot
        val chatbotOnClickListener = View.OnClickListener {
            val intentChatbot = Intent(this, Chatbot::class.java).apply {
                putExtra("branch_name", branchName)
            }
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
}