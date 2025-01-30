package com.applivery.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.getInstance
import com.applivery.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPreferences = AppliveryApplication.appPreferences
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        binding.chronometer.start()
        binding.checkForUpdatesBackgroundSwitch.isChecked =
            Applivery.getInstance().getCheckForUpdatesBackground()
        binding.checkForUpdatesBackgroundSwitch.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.checkForUpdatesBackground = isChecked
            Applivery.getInstance().setCheckForUpdatesBackground(isChecked)
        }

        Applivery.getInstance().disableShakeFeedback()

        binding.feedbackSwitch.isChecked = false
        binding.feedbackSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Applivery.getInstance().enableShakeFeedback()
            } else {
                Applivery.getInstance().disableShakeFeedback()
                Applivery.getInstance().disableScreenshotFeedback()
            }
        }

        binding.screenshotSwitch.isChecked = false
        binding.screenshotSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Applivery.getInstance().enableScreenshotFeedback()
            } else {
                Applivery.getInstance().disableScreenshotFeedback()
            }
        }

        binding.checkForUpdatesButton.setOnClickListener {
            Applivery.getInstance().checkForUpdates()
        }

        binding.downloadLastBuildButton.setOnClickListener {
            Applivery.getInstance().update()
        }
    }

    override fun onResume() {
        super.onResume()
        Applivery.getInstance().isUpToDate { isUpToDate ->
            if (isUpToDate) {
                binding.isUpToDateTextView.text = getString(R.string.is_up_to_date_text_updated)
            } else {
                binding.isUpToDateTextView.text = getString(R.string.is_up_to_date_text_no_updated)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_user -> {
                UserActivity.open(this)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
