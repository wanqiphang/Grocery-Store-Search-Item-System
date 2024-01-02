package com.example.fyp_1.Admin

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.fyp_1.databinding.ActivityAdminSignUpBinding

class AdminSignUp : AppCompatActivity() {
    private lateinit var binding: ActivityAdminSignUpBinding
    private lateinit var databaseAdmin: databaseAdmin

    // Move the variable declarations here
    var signupName: String? = null
    var signupPhoneNo: String? = null
    var signupEmail: String? = null
    var signupPassword: String? = null
    var confirmPassword: String? = null

    // one boolean variable to check whether all the text fields
    // are filled by the user, properly or not.
    var isAllFieldsChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseAdmin = databaseAdmin(this)

        binding.signupButton.setOnClickListener {
            // Initialize the variables after inflating the layout
            signupName = binding.signupName.text.toString()
            signupPhoneNo = binding.signupPhoneNo.text.toString()
            signupEmail = binding.signupEmail.text.toString()
            signupPassword = binding.signupPassword.text.toString()
            confirmPassword = binding.signupConfirm.text.toString()

            // store the returned value of the dedicated function which checks
            // whether the entered data is valid or if any fields are left blank.
            isAllFieldsChecked = CheckAllFields()

            // Check if all fields are filled
            if (isAllFieldsChecked) {
                signupDatabase(signupName!!, signupPhoneNo!!.toInt(), signupEmail!!, signupPassword!!)
            }
        }

        binding.loginRedirectText.setOnClickListener{
            val intent = Intent(this, AdminLogin::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signupDatabase(name: String, phoneNo: Int, email: String, password: String){
        val inseredRowId = databaseAdmin.insertAdmin(name, phoneNo, email, password)
        if (inseredRowId != -1L){
            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AdminLogin::class.java)
            startActivity(intent)
            finish()
        } else{
            // Insertion failed due to duplicate email
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
        }
    }

    // function which checks all the text fields
    // are filled or not by the user.
    // when user clicks on the PROCEED button
    // this function is triggered.
    private fun CheckAllFields(): Boolean {
        if (signupName.isNullOrEmpty()) {
            binding.signupName.error = "This field is required"
            return false
        }
        if (signupPhoneNo.isNullOrEmpty()) {
            binding.signupPhoneNo.error = "This field is required"
            return false
        }
        if (!isPhoneNumberValid(signupPhoneNo!!)) {
            binding.signupPhoneNo.error = "Invalid phone number. Must be 10 digit"
            return false
        }
        if (signupEmail.isNullOrEmpty()) {
            binding.signupEmail.error = "Email is required"
            return false
        }
        if (signupPassword.isNullOrEmpty()) {
            binding.signupPassword.error = "Password is required"
            return false
        } else if (signupPassword!!.length < 8) {
            binding.signupPassword.error = "Password must be a minimum of 8 characters"
            return false
        }
        if (confirmPassword != signupPassword){
            binding.signupConfirm.error = "Password is not match"
            return false
        }

        // after all validation return true.
        return true
    }

    // Function to check if the phone number is valid
    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        // Remove any non-digit characters from the phone number
        val digitsOnly = phoneNumber.replace("\\D".toRegex(), "")

        // Check if the phone number has a valid length (e.g., 10 digits for a standard phone number)
        return digitsOnly.length == 10
    }

}