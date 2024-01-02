package com.example.fyp_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.Item.databaseItem
import com.example.fyp_1.Item.databaseItemLocate
import com.example.fyp_1.databinding.ActivitySearchItemBinding

class SearchItem : AppCompatActivity() {

    private lateinit var binding: ActivitySearchItemBinding
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchResultAdapter: SearchResultAdapter
    private lateinit var dbItem: databaseItem
    private lateinit var dbItemLocate: databaseItemLocate
    private lateinit var dbBranch: databaseBranch

    //get the branchName from intent
    private lateinit var branchName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database helper
        dbItem = databaseItem(this)
        dbItemLocate = databaseItemLocate(this)
        dbBranch = databaseBranch(this)

        // Retrieve the branch name from the intent
        branchName = intent.getStringExtra("branch_name") ?: ""

        searchView = binding.searchView1
        recyclerView = binding.recyclerViewSearchResults

        // Set up RecyclerView with an empty list initially
        searchResultAdapter = SearchResultAdapter(emptyList(), this, branchName)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchResultAdapter

        // Set up SearchView listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission (if needed)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query changes
                if (newText != null) {
                    // Perform search with the new query
                    performSearch(newText)
                }
                return true
            }
        })

        // Retrieve the scan result from the intent
        val scanResult = intent.getStringExtra("scan_result")
        if (!scanResult.isNullOrEmpty()) {
            Log.d("Search Item Name", "Scan Result: $scanResult")
            performSearch(scanResult)
        }

        //navigation
        //navigate to camera
        val cameraOnClickListener =  View.OnClickListener {
            val intentCamera = Intent(this@SearchItem, ScanItem::class.java).apply {
                putExtra("branch_name", branchName)
            }
            startActivity(intentCamera)
        }
        binding.imageButtonCamera1.setOnClickListener(cameraOnClickListener)

        //navigate to Main page
        binding.buttonHome1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

    private fun performSearch(query: String) {
        val branchId = dbBranch.getBranchIdByName(branchName)

        if (branchId != -1) {
            // Get the itemIds associated with the branch
            val itemIds = dbItemLocate.getItemIdsByBranch(branchId)

            // Perform search with the new query and branchId
            val searchResults = dbItem.searchItems(query, branchId, itemIds)

            // Update RecyclerView with the search results
            searchResultAdapter.updateData(searchResults)
        } else {
            // Handle the case when branchId is not available
        }
    }


    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

}