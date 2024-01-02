package com.example.fyp_1.Item

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fyp_1.BitmapUtility
import com.example.fyp_1.databinding.ActivityUpdateItemBinding

class UpdateItem : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateItemBinding
    private lateinit var db: databaseItem
    private var itemId: Int = -1 //represent the value is empty

    //for image
    private var itemImageByteArray: ByteArray? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val imageStream = contentResolver.openInputStream(it)
            val selectedImage = BitmapFactory.decodeStream(imageStream)

            binding.imageViewUpdateImage.setImageBitmap(selectedImage)

            // Convert Bitmap to ByteArray using BitmapUtility
            itemImageByteArray = BitmapUtility.getBytes(selectedImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = databaseItem(this)

        itemId = intent.getIntExtra("item_id", -1)
        if (itemId == -1){
            finish()
            return
        }

        binding.buttonUpdateImage.setOnClickListener {
            dispatchPickImageIntent()
        }

        val item = db.getItemByID(itemId)
        // Set item details
        binding.updateitemNameEdit.setText(item.itemName)
        binding.updateitemPriceEdit.setText(item.itemPrice)
        // Set the image to the ImageView if item.itemImage is not null
        item.itemImage?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.imageViewUpdateImage.setImageBitmap(bitmap)
        }

        binding.buttonItemEditDone.setOnClickListener {
            val newItemName = binding.updateitemNameEdit.text.toString()
            val newItemPrice = binding.updateitemPriceEdit.text.toString()

            // Check if itemImageByteArray is null
            val updatedItem = if (itemImageByteArray != null) {
                DataItem(itemId, newItemName, newItemPrice, itemImageByteArray!!)
            } else {
                // If itemImageByteArray is null, keep the existing image in the database
                DataItem(itemId, newItemName, newItemPrice, item.itemImage)
            }

            db.updateItem(updatedItem)
            finish()
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }

    }

    private fun dispatchPickImageIntent() {
        imagePicker.launch("image/*")
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}