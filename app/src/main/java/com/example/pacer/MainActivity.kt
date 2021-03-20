package com.example.pacer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pacer.services.StatsTracker
import androidx.core.app.ActivityCompat
import com.example.pacer.services.StatsSummary
import java.util.*

class MainActivity : AppCompatActivity() {
    private val LOG_TAG = "MainActivity"
    private var statsTracker: Messenger? = null
    private val receiver = Messenger(IncomingHandler())
    private var timer: Timer? = null
    private var durationSeconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestLocationPermissions()
        bindStatsTrackerService()

        findViewById<Button>(R.id.stop_button).setOnClickListener {
            statsTracker?.send(Message().apply {
                what = StatsTracker.MSG_STOP
                replyTo = receiver
            })

            timer?.cancel()
        }

        findViewById<Button>(R.id.start_button).setOnClickListener {
            statsTracker?.send(Message().apply {
                what = StatsTracker.MSG_START
                replyTo = receiver
            })

            durationSeconds = 0
            timer = Timer()
            timer?.schedule(object : TimerTask() {
                override fun run() {
                    durationSeconds += 1
                    runOnUiThread {
                        findViewById<TextView>(R.id.duration).text = "${durationSeconds} seconds"
                    }
                }
            }, 0L, 1000L)
        }
    }

    private fun bindStatsTrackerService(): Unit {
        val service = Intent(this, StatsTracker::class.java)

        if (!bindService(service, serviceConnection, BIND_AUTO_CREATE)) {
            Log.e(LOG_TAG, "Couldn't bind to stats tracker")
        }
    }

    private fun updateSummaryView(summary: StatsSummary): Unit {
        findViewById<TextView>(R.id.grade).text = "${summary.avgGrade}%"
        findViewById<TextView>(R.id.pace).text = "${summary.avgMps} m/s"
        findViewById<TextView>(R.id.distance).text = "${summary.distanceMeters} meters"
    }


    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message): Unit {
            when (msg.what) {
                StatsTracker.MSG_LIST_SEGMENTS -> println(msg.data)
                StatsTracker.MSG_SECURITY_EXN -> requestLocationPermissions()
                StatsTracker.MSG_SUMMARY -> {
                    val summary = msg.data.getParcelable<StatsSummary>("summary")
                    if (summary != null) {
                        updateSummaryView(summary)
                    } else {
                        Log.e(LOG_TAG, "Expected a summary but got null")
                    }
                }
                else -> Log.w(LOG_TAG, "unexpected message $msg received")
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            if (className.className == "com.example.pacer.services.StatsTracker" && statsTracker == null) {
                Log.i(LOG_TAG, "Connected to StatsTracker")

                statsTracker = Messenger(service)
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "com.example.pacer.services.StatsTracker") {
                statsTracker = null
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (!grantResults.all { it == PackageManager.PERMISSION_GRANTED}) {
                    Toast.makeText(
                        this,
                        "You must enable location permissions for this app.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun requestLocationPermissions(): Unit {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

}