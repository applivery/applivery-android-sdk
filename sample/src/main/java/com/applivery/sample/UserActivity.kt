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
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        initViews()
        getUser()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        bindUserButton.setOnClickListener { bindUser() }
        unBindUserButton.setOnClickListener { Applivery.unbindUser() }
    }

    private fun bindUser() {

        if (emailEditText.text.isEmpty()) {
            emailEditText.error = getString(R.string.field_required)
            return
        }
        val email = emailEditText.text.toString()

        val firstName = if (firstNameEditText.text.isNotEmpty()) {
            firstNameEditText.text.toString()
        } else {
            null
        }

        val lastName = if (lastNameEditText.text.isNotEmpty()) {
            lastNameEditText.text.toString()
        } else {
            null
        }

        val tags = if (tagsEditText.text.isNotEmpty()) {
            tagsEditText.text.toString().split(",")
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
                emailEditText.setText(userProfile.email)
                firstNameEditText.setText(userProfile.firstName)
                lastNameEditText.setText(userProfile.lastName)
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
