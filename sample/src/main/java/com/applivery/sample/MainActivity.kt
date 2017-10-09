package com.applivery.sample

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Chronometer
import com.applivery.applvsdklib.Applivery

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar = findViewById(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)

    val disable = findViewById(R.id.fab) as FloatingActionButton
    val enable = findViewById(R.id.fab2) as FloatingActionButton

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

    startChrono()
  }

  override fun onResume() {
    super.onResume()

    Applivery.checkForUpdates()
  }

  private fun startChrono() {
    (findViewById(R.id.chronometer) as Chronometer).start()
  }
}
