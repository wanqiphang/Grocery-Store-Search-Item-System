package com.example.fyp_1.Item

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.databinding.ActivityViewItemLocationBinding

class ViewItemLocation : AppCompatActivity(), ItemLocationClickListener {
    private lateinit var binding: ActivityViewItemLocationBinding
    private lateinit var db: databaseItemLocate
    private lateinit var itemLocationAdapter: ItemLocationAdapter
    private lateinit var dbBranch: databaseBranch
    private lateinit var dbItem: databaseItem

    private var itemId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewItemLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = databaseItemLocate(this)
        dbBranch = databaseBranch(this)
        dbItem = databaseItem(this)

        itemId = intent.getIntExtra("Item_id", -1)
        Log.d("ViewItemId", "Item id: $itemId")

        // Initialize RecyclerView for Item Locations using the new function
        itemLocationAdapter = ItemLocationAdapter(db.getAllItemLocationByItemId(itemId), this, this)

        binding.recyclerViewItemLocation.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewItemLocation.adapter = itemLocationAdapter
    }

    override fun onUpdateButtonClick(itemLocationId: Int, itemId: Int) {
        // Launch UpdateItemLocationActivity and pass the itemLocationId
        val intent = Intent(this, UpdateItemLocation::class.java).apply {
            putExtra("item_location_id", itemLocationId)
            putExtra("item_id", itemId)
        }
        startActivity(intent)
    }

    //automatic refresh data
    override fun onResume() {
        super.onResume()
        itemLocationAdapter.refreshData(db.getAllItemLocationByItemId(itemId))
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}