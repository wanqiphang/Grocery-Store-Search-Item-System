package com.example.fyp_1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.fyp_1.Admin.AdminLogin
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.Item.DataItem
import com.example.fyp_1.Item.databaseItem
import com.example.fyp_1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbBranch: databaseBranch

    private lateinit var recyclerView: RecyclerView
    private lateinit var mainItemAdapter: MainItemAdapter
    private lateinit var dbItem: databaseItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database helper
        dbBranch = databaseBranch(this)
        dbItem = databaseItem(this)

        //navigate to admin site
        val adminOnClickListener = View.OnClickListener {
            val intentAdmin = Intent(this, AdminLogin::class.java)
            startActivity(intentAdmin)
        }
        binding.imageButtonAdmin.setOnClickListener(adminOnClickListener)

        // Set up the spinner with branch names
        val branchNames = dbBranch.getAllBranches().map { it.branchName }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, branchNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBranchMain.adapter = spinnerAdapter

        // Set a listener to capture the selected branch name
        binding.spinnerBranchMain.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedBranchName = branchNames[position]
                // You can use selectedBranchName as needed
                Toast.makeText(this@MainActivity, "Selected Branch: $selectedBranchName", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected
                Log.d("AddLocation", "No branch selected")
            }
        }

        // Set up click listeners outside of the spinner listener
        val searchOnClickListener = View.OnClickListener {
            val selectedBranchName = binding.spinnerBranchMain.selectedItem as String
            val intentSearch = Intent(this@MainActivity, SearchItem::class.java).apply {
                putExtra("branch_name", selectedBranchName)
            }
            startActivity(intentSearch)
        }

        //navigate to search
        binding.buttonSearch.setOnClickListener(searchOnClickListener)
        binding.buttonSearch1.setOnClickListener(searchOnClickListener)


        // recycler view
        val itemData: List<DataItem> = dbItem.getAllItems() // Replace with your method to get item data
        mainItemAdapter = MainItemAdapter(itemData, object : ItemClickListener {
            override fun onViewItemLocationClicked(itemId: Int) {
                val intent = Intent(this@MainActivity, SpecificItemDetailAllBranch::class.java)
                intent.putExtra("item_id", itemId)
                startActivity(intent)
            }
        })


        recyclerView = binding.recyclerViewMain
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val snapHelper : SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = mainItemAdapter


        //navigate to camera
        val cameraOnClickListener =  View.OnClickListener {
            val selectedBranchName = binding.spinnerBranchMain.selectedItem as String
            val intentCamera = Intent(this@MainActivity, ScanItem::class.java).apply {
                putExtra("branch_name", selectedBranchName)
            }
            startActivity(intentCamera)
        }
        binding.imageButtonCamera1.setOnClickListener(cameraOnClickListener)

        //navigate to main (main to main no need to write unless have things change)

        //navigate to chatbot
        val chatbotOnClickListener = View.OnClickListener {
            val selectedBranchName = binding.spinnerBranchMain.selectedItem as String
            val intentChatbot = Intent(this@MainActivity, Chatbot::class.java).apply {
                putExtra("branch_name", selectedBranchName)
            }
            startActivity(intentChatbot)
        }
        binding.buttonChatbot1.setOnClickListener(chatbotOnClickListener)
    }


}