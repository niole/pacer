package com.example.pacer.services

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

enum class DistanceUnit {
    METERS, MILES
}

@Parcelize
data class Distance(val value: Int, val unit: DistanceUnit) : Parcelable

@Parcelize
data class WorkoutSubStage(val name: String, val durationSeconds: Long?, val distance: Distance?) : Parcelable

@Parcelize
data class WorkoutStage(val reps: Int, val subStages: List<WorkoutSubStage>) : Parcelable

@Parcelize
data class Workout(val name: String, val stages: List<WorkoutStage>) : Parcelable

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
    val distanceMeters: Double, // TODO update to use dt with units
    val durationSeconds: Double,
    val startTime: Date,
    val endTime: Date
) : Parcelable
