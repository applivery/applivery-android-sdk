package com.applivery.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.applivery.applvsdklib.Applivery
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
        binding.checkForUpdatesBackgroundSwitch.isChecked = Applivery.getCheckForUpdatesBackground()
        binding.checkForUpdatesBackgroundSwitch.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.checkForUpdatesBackground = isChecked
            Applivery.setCheckForUpdatesBackground(isChecked)
        }

        Applivery.disableShakeFeedback()
        Applivery.disableScreenshotFeedback()

        binding.feedbackSwitch.isChecked = false
        binding.feedbackSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Applivery.enableShakeFeedback()
            } else {
                Applivery.disableShakeFeedback()
                Applivery.disableScreenshotFeedback()
            }
        }

        binding.screenshotSwitch.isChecked = false
        binding.screenshotSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Applivery.enableScreenshotFeedback()
            } else {
                Applivery.disableScreenshotFeedback()
            }
        }

        binding.checkForUpdatesButton.setOnClickListener {
            Applivery.checkForUpdates()
        }

        binding.downloadLastBuildButton.setOnClickListener {
            Applivery.update()
        }
    }

    override fun onResume() {
        super.onResume()
        Applivery.isUpToDate { isUpToDate ->
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
