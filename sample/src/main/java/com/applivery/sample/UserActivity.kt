package com.applivery.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applivery.applvsdklib.Applivery
import com.applivery.applvsdklib.BindUserCallback
import com.applivery.applvsdklib.GetUserCallback
import com.applivery.base.domain.model.UserProfile
import com.applivery.sample.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        getUser()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.bindUserButton.setOnClickListener { bindUser() }
        binding.unBindUserButton.setOnClickListener { Applivery.unbindUser() }
    }

    private fun bindUser() {

        if (binding.emailEditText.text.isEmpty()) {
            binding.emailEditText.error = getString(R.string.field_required)
            return
        }
        val email = binding.emailEditText.text.toString()

        val firstName = if (binding.firstNameEditText.text.isNotEmpty()) {
            binding.firstNameEditText.text.toString()
        } else {
            null
        }

        val lastName = if (binding.lastNameEditText.text.isNotEmpty()) {
            binding.lastNameEditText.text.toString()
        } else {
            null
        }

        val tags = if (binding.tagsEditText.text.isNotEmpty()) {
            binding.tagsEditText.text.toString().split(",")
        } else {
            null
        }

        Applivery.bindUser(email, firstName, lastName, tags, object : BindUserCallback {
            override fun onSuccess() {
                Toast.makeText(this@UserActivity, "Success", Toast.LENGTH_SHORT).show()
            }

            override fun onError(message: String) {
                Toast.makeText(this@UserActivity, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUser() {

        Applivery.getUser(object : GetUserCallback {

            override fun onSuccess(userProfile: UserProfile) {
                binding.emailEditText.setText(userProfile.email)
                binding.firstNameEditText.setText(userProfile.firstName)
                binding.lastNameEditText.setText(userProfile.lastName)
            }

            override fun onError(message: String) = Unit
        })
    }

    companion object Navigator {

        fun open(context: Context) {
            val intent = Intent(context, UserActivity::class.java)
            context.startActivity(intent)
        }
    }
}
