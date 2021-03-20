package com.example.pacer.services

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class LatLng(val lat: Double, val lng: Double) : Parcelable

@Parcelize
data class SegmentStat(
    val grade: Double,
    val mps: Float,
    val distanceMeters: Float,
    val durationSeconds: Long,
    val startLatLng: LatLng,
    val endLatLng: LatLng,
    val startTime: Date,
    val endTime: Date
) : Parcelable

@Parcelize
data class StatsSummary(
    val avgGrade: Double,
    val avgMps: Double,
    val distanceMeters: Double,
    val durationSeconds: Double,
    val startTime: Date,
    val endTime: Date
) : Parcelable
