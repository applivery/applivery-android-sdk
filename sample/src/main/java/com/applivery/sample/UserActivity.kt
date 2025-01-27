package com.applivery.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.user.BindUserCallback
import com.applivery.android.sdk.user.GetUserCallback
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
        binding.unBindUserButton.setOnClickListener { Applivery.getInstance().unbindUser() }
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

        Applivery.getInstance().bindUser(email, firstName, lastName, tags.orEmpty(), object : BindUserCallback {
            override fun onSuccess() {
                Toast.makeText(this@UserActivity, "Success", Toast.LENGTH_SHORT).show()
            }

            override fun onError(message: String) {
                Toast.makeText(this@UserActivity, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUser() {

        Applivery.getInstance().getUser(object : GetUserCallback {

            override fun onSuccess(user: User) {
                binding.emailEditText.setText(user.email)
                binding.firstNameEditText.setText(user.firstName)
                binding.lastNameEditText.setText(user.lastName)
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
