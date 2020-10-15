package com.applivery.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.applivery.applvsdklib.Applivery
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appPreferences = AppliveryApplication.appPreferences
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        chronometer.start()
        checkForUpdatesBackgroundSwitch.isChecked = Applivery.getCheckForUpdatesBackground()
        checkForUpdatesBackgroundSwitch.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.checkForUpdatesBackground = isChecked
            Applivery.setCheckForUpdatesBackground(isChecked)
        }

        Applivery.disableShakeFeedback()
        Applivery.disableScreenshotFeedback()

        feedbackSwitch.isChecked = false
        feedbackSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Applivery.enableShakeFeedback()
            } else {
                Applivery.disableShakeFeedback()
                Applivery.disableScreenshotFeedback()
            }
        }

        screenshotSwitch.isChecked = false
        screenshotSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Applivery.enableScreenshotFeedback()
            } else {
                Applivery.disableScreenshotFeedback()
            }
        }

        checkForUpdatesButton.setOnClickListener {
            Applivery.checkForUpdates()
        }

        downloadLastBuildButton.setOnClickListener {
            Applivery.downloadLastBuild()
        }
    }

    override fun onResume() {
        super.onResume()
        Applivery.isUpToDate { isUpToDate ->
            if (isUpToDate) {
                isUpToDateTextView.text = getString(R.string.is_up_to_date_text_updated)
            } else {
                isUpToDateTextView.text = getString(R.string.is_up_to_date_text_no_updated)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.show_user -> {
                UserActivity.open(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
