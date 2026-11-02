package com.example.myscreenshare

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var projectionManager: MediaProjectionManager
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
        }
        status = TextView(this).apply {
            text = "Screen sharing is OFF. No screen is being sent."
            textSize = 18f
        }
        val start = Button(this).apply {
            text = "Start screen sharing"
            setOnClickListener {
                startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_CAPTURE)
            }
        }
        val stop = Button(this).apply {
            text = "Stop screen sharing"
            setOnClickListener {
                stopService(Intent(this@MainActivity, CaptureService::class.java))
                status.text = "Screen sharing stopped."
            }
        }
        layout.addView(status)
        layout.addView(start)
        layout.addView(stop)
        setContentView(layout)
    }

    @Deprecated("Android activity result callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAPTURE) {
            if (resultCode == RESULT_OK && data != null) {
                val serviceIntent = Intent(this, CaptureService::class.java).apply {
                    putExtra(CaptureService.EXTRA_RESULT_CODE, resultCode)
                    putExtra(CaptureService.EXTRA_DATA, data)
                }
                startForegroundService(serviceIntent)
                status.text = "Permission granted. Local capture service active; remote streaming is not configured."
            } else {
                status.text = "Permission denied. Capture did not start."
            }
        }
    }

    companion object { private const val REQUEST_CAPTURE = 501 }
}
