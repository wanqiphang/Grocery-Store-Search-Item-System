package com.example.fyp_1.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fyp_1.databinding.ActivityAdminLoginBinding

class AdminLogin : AppCompatActivity() {
    private lateinit var binding: ActivityAdminLoginBinding
    private lateinit var databaseAdmin: databaseAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseAdmin = databaseAdmin(this)

        binding.loginButton.setOnClickListener {
            val loginEmail = binding.loginEmail.text.toString()
            val loginPassword = binding.loginPassword.text.toString()
            loginDatabase(loginEmail, loginPassword)
        }

        binding.signupRedirectText.setOnClickListener {
            val intent = Intent(this, AdminSignUp::class.java)
            startActivity(intent)
            finish()
        }

    }

    //help of read function will check the credentials
    //is present in the database or not
    private fun loginDatabase(email: String, password: String){
        val adminExists = databaseAdmin.readAdmin(email, password)
        if(adminExists){
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
            finish()
        } else{
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }
}