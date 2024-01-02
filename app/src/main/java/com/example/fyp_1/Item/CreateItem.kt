package com.example.fyp_1.Item

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fyp_1.BitmapUtility
import com.example.fyp_1.R
import com.example.fyp_1.databinding.ActivityCreateItemBinding

class CreateItem : AppCompatActivity() {

    private lateinit var binding: ActivityCreateItemBinding
    private lateinit var db: databaseItem

    //for image
    private var itemImageByteArray: ByteArray? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val imageStream = contentResolver.openInputStream(it)
            val selectedImage = BitmapFactory.decodeStream(imageStream)

            binding.imageViewAddImage.setImageBitmap(selectedImage)

            // Convert Bitmap to ByteArray using BitmapUtility
            itemImageByteArray = BitmapUtility.getBytes(selectedImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = databaseItem(this)

        binding.buttonInsertImage.setOnClickListener {
            dispatchPickImageIntent()
        }

        val buttonItemDone: Button = findViewById(R.id.buttonItemDone)
        buttonItemDone.setOnClickListener {
            insertItem()
        }
    }

    private fun dispatchPickImageIntent() {
        imagePicker.launch("image/*")
    }

    private fun insertItem() {
        val itemName = binding.itemNameEdit.text.toString().trim()
        val itemPrice = binding.itemPriceEdit.text.toString().trim()

        if (itemName.isEmpty() || itemPrice.isEmpty() || itemImageByteArray == null) {
            Toast.makeText(this, "Please fill in all fields and insert an image", Toast.LENGTH_SHORT).show()
            return
        }

        val newItem = DataItem(0, itemName, itemPrice, itemImageByteArray!!)

        db.insertItem(newItem)
        finish()

        Toast.makeText(this, "Item created successfully", Toast.LENGTH_SHORT).show()
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}