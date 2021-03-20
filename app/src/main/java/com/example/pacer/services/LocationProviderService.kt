package com.example.pacer.services

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.lang.IllegalStateException

data class Subscriber(val name: String, val handler: (Location) -> Unit)

// TODO thread safety
class LocationProviderService(private val context: Context) {
    private val LOG_TAG = "LocationProviderService"
    private val LOCATION_UPDATE_INTERVAL_MS = 10000L

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var subscribers = mutableListOf<Subscriber>()

    fun start(): Unit {
        doIfAuthorized {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            fusedLocationClient.lastLocation
                .addOnFailureListener { Log.w(LOG_TAG, "Failed to get last location of device on start: {it}") }
                .addOnSuccessListener {
                    if (it != null) {
                        reportLocation(it)
                    } else {
                        Log.w(LOG_TAG, "Got last location of device, but it was null")
                    }
                }

            val locationRequest = LocationRequest().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = LOCATION_UPDATE_INTERVAL_MS
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun stop(): Unit {
        Log.i(LOG_TAG, "Stopping location updates")

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun subscribe(name: String, handler: (Location) -> Unit): () -> Unit {

        if (subscribers.find { it.name == name } == null) {
            subscribers.add(Subscriber(name, handler))
        }

        return {
            subscribers = subscribers.filter { it.name != name } as MutableList<Subscriber>
        }
    }

    private fun reportLocation(location: Location): Unit {
        subscribers.forEach { it.handler(location) }
    }

    private fun doIfAuthorized(block: () -> Unit): Unit {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            block()
        } else {
            throw SecurityException("Access fine location permissions must be enabled to use this feature")
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            reportLocation(p0.lastLocation)
        }
    }

}