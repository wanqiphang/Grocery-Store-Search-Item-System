package com.example.fyp_1.Item

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp_1.databinding.ActivityViewItemBinding

class ViewItem : AppCompatActivity(), ItemClickListener {
    private lateinit var binding: ActivityViewItemBinding
    private lateinit var db: databaseItem
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = databaseItem(this)
        // Set the listener in the ItemAdapter
        itemAdapter = ItemAdapter(db.getAllItems(), this, this)

        binding.recyclerViewItem.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewItem.adapter = itemAdapter
    }

    // automatically refresh the data
    override fun onResume() {
        super.onResume()
        itemAdapter.refreshData(db.getAllItems())
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onViewItemLocationClicked(itemId: Int) {
        val viewLocationIntent = Intent(this, ViewItemLocation::class.java).apply {
            putExtra("Item_id", itemId)
        }
        startActivity(viewLocationIntent)
    }
}