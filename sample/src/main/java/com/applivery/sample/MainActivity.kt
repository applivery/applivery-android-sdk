package com.applivery.sample

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Chronometer
import com.applivery.applvsdklib.Applivery
import kotlinx.android.synthetic.main.activity_main.checkForUpdatesBackgroundSwitch

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)

    val disable = findViewById<FloatingActionButton>(R.id.fab)
    val enable = findViewById<FloatingActionButton>(R.id.fab2)

    enable.setOnClickListener { view ->
      Applivery.enableShakeFeedback()
      Applivery.enableScreenshotFeedback()
      Snackbar.make(view, "Feedback Enabled", Snackbar.LENGTH_LONG).show()
    }

    disable.setOnClickListener { view ->
      Applivery.disableShakeFeedback()
      Applivery.disableScreenshotFeedback()
      Snackbar.make(view, "Feedback disabled", Snackbar.LENGTH_LONG).show()
    }

    initViews()
  }

  private fun initViews() {
    startChrono()
    checkForUpdatesBackgroundSwitch.isChecked = Applivery.getCheckForUpdatesBackground()
    checkForUpdatesBackgroundSwitch.setOnCheckedChangeListener { _, isChecked ->
      Applivery.setCheckForUpdatesBackground(isChecked)
    }
  }

  private fun startChrono() {
    (findViewById<Chronometer>(R.id.chronometer)).start()
  }
}
