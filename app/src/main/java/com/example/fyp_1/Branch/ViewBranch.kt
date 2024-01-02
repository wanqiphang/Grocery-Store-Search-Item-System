package com.example.fyp_1.Branch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp_1.R
import com.example.fyp_1.databinding.ActivityViewBranchBinding

class ViewBranch : AppCompatActivity() {

    private lateinit var binding: ActivityViewBranchBinding
    private lateinit var db: databaseBranch
    private lateinit var branchAdapter: BranchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = databaseBranch(this)
        branchAdapter = BranchAdapter(db.getAllBranches(), this)

        binding.recyclerViewBranch.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewBranch.adapter = branchAdapter
    }

    //automatically refresh the data
    override fun onResume(){
        super.onResume()
        branchAdapter.refreshData(db.getAllBranches())
    }

    // Handle back button click
    fun onBackButtonClick(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }
}