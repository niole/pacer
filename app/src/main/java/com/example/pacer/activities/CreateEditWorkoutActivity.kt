package com.example.pacer.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.pacer.services.WorkoutService
import com.example.pacer.activities.createeditworkout.Page
import com.example.pacer.services.Workout

class CreateEditWorkoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val workoutName = intent.getStringExtra("name")

        println("CreateEditWorkoutActivity $workoutName")

        setContent {
            var workout: Workout = Workout("untitle", listOf())
            if (workoutName != null) {
                workout = WorkoutService.getWorkout(workoutName)
            }

            Page(workout = workout, { updateWorkout(it) })
        }

    }

    private fun updateWorkout(newWorkout: Workout): Unit {
        println(newWorkout)
    }

}