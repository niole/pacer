package com.example.pacer.services

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.*
import android.util.Log
import java.lang.IllegalStateException
import java.util.*

class StatsTracker : Service() {
    private val LOG_TAG  = "StatsTracker"
    private val THRESHOLD_DISTANCE_METERS = 3

    private val locationProvider = LocationProviderService(this)
    private var unsubscribeLocationUpdates: () -> Unit = {}

    private var segments: MutableList<SegmentStat> = mutableListOf()
    private var inProgressState: Location? = null

    private val serviceMessenger = Messenger(IncomingHandler())
    private var clientMessenger: Messenger? = null

    private fun start(messenger: Messenger): Unit {
        if (clientMessenger == null) {
            clientMessenger = messenger

            try {
                locationProvider.start()
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Something when wrong when starting location provider {e}")

                when (e) {

                    is SecurityException -> clientMessenger?.send(Message().apply {
                        replyTo = serviceMessenger
                        what = MSG_SECURITY_EXN
                        data = Bundle().apply {
                            putString("message", e.message)
                        }
                    })

                }
            }

        } else {
            throw IllegalStateException("Cannot start StatsTracker for client. A client is already bound to StatsTracker")
        }
    }

    private fun pause(): Unit {
        Log.i(LOG_TAG, "Pausing tracker")

        locationProvider.stop()
    }

    private fun stop(): Unit {
        Log.i(LOG_TAG, "Stopping and resetting tracker")

        clientMessenger = null

        locationProvider.stop()

        segments.clear()
    }

    private fun listSegments(): Unit {
        val message = Message().apply {
            data = Bundle().apply { putParcelable("segments", segments as Parcelable) }
            what = MSG_LIST_SEGMENTS
        }

        try {
            clientMessenger?.send(message)
        } catch (exn: RemoteException) {
            Log.e(LOG_TAG, "Failed to reply with segments for client $clientMessenger: $exn")
        }
    }

    private fun updateInProgressSegment(update: Location): Unit {
        if (inProgressState == null) {
            // new segment
            inProgressState = update
        } else {
            // finishing a segment
            // TODO in the future provide alternative speed calculations if speed is not accurate
            // update.getSpeedAccuracyMetersPerSecond

            val startState = inProgressState!!
            val distance = update.distanceTo(startState)

            if (distance > THRESHOLD_DISTANCE_METERS) {
                // user has to have gone more than two meters in order for this segment to be real

                val segment = SegmentStat(
                    ((update.altitude - startState.altitude) / distance)*100,
                    (update.speed + startState.speed)/2,
                    distance,
                    (update.time - startState.time)/1000,
                    LatLng(startState.latitude, startState.longitude),
                    LatLng(update.latitude, update.longitude),
                    Date(startState.time),
                    Date(update.time)
                )

                segments.add(segment)

                inProgressState = null
            }

            reportSummary()
        }
    }

    private fun reportSummary(): Unit {
        fun roundToOneDec(n: Double): Double {
            return Math.round(n * 10.0)/10.0
        }

        val avgGrade = roundToOneDec(segments.sumByDouble { it.grade } / segments.size)
        val avgMps = roundToOneDec(segments.sumByDouble { it.mps.toDouble() } / segments.size)
        val distanceMeters = roundToOneDec(segments.sumByDouble { it.distanceMeters.toDouble() })
        val durationSeconds = Math.round(segments.sumByDouble { it.durationSeconds.toDouble() }).toDouble()
        val startTime = if (segments.size > 0) {
            segments.first().startTime
        } else {
            Date()
        }
        val endTime = if (segments.size > 0) {
            segments.last().endTime
        } else {
            Date()
        }

        clientMessenger?.send(Message().apply {
           replyTo = serviceMessenger
           what = MSG_SUMMARY
           data = Bundle().apply {
               putParcelable("summary", StatsSummary(
                   avgGrade,
                   avgMps,
                   distanceMeters,
                   durationSeconds,
                   startTime,
                   endTime
               ))
           }
        })

    }

    companion object {
        val MSG_START = 1
        val MSG_PAUSE = 2
        val MSG_STOP = 3
        val MSG_LIST_SEGMENTS = 4
        val MSG_SECURITY_EXN = 5
        val MSG_SUMMARY = 6
    }

    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message): Unit {
            Log.i(LOG_TAG, "Received message $msg")
            when (msg.what) {
                MSG_START -> start(msg.replyTo)
                MSG_PAUSE -> pause()
                MSG_STOP -> stop()
                MSG_LIST_SEGMENTS  -> listSegments()
                else -> Log.w(LOG_TAG, "unexpected message $msg received")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG, "Destroying service")

        unsubscribeLocationUpdates()

        Log.i(LOG_TAG, "Destroyed service")
    }

    override fun onCreate() {
        super.onCreate()

        Log.i(LOG_TAG, "Initializing service")

        unsubscribeLocationUpdates = locationProvider.subscribe("stats-tracker") {
            updateInProgressSegment(it)
        }

        Log.i(LOG_TAG, "Initialized service")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return serviceMessenger.binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }
}