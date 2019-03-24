package com.applivery.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.applivery.applvsdklib.Applivery
import kotlinx.android.synthetic.main.activity_main.checkForUpdatesBackgroundSwitch
import kotlinx.android.synthetic.main.activity_main.chronometer
import kotlinx.android.synthetic.main.activity_main.feedbackSwitch
import kotlinx.android.synthetic.main.activity_main.toolbar

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)


    initViews()
  }

  private fun initViews() {
    setSupportActionBar(toolbar)
    chronometer.start()
    checkForUpdatesBackgroundSwitch.isChecked = Applivery.getCheckForUpdatesBackground()
    checkForUpdatesBackgroundSwitch.setOnCheckedChangeListener { _, isChecked ->
      Applivery.setCheckForUpdatesBackground(isChecked)
    }

    Applivery.disableShakeFeedback()
    Applivery.disableScreenshotFeedback()
    feedbackSwitch.isChecked = false
    feedbackSwitch.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        Applivery.enableShakeFeedback()
        Applivery.enableScreenshotFeedback()
      } else {
        Applivery.disableShakeFeedback()
        Applivery.disableScreenshotFeedback()
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
