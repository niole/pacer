package com.example.pacer.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pacer.services.WorkoutService
import com.example.pacer.activities.createeditworkout.Page
import com.example.pacer.activities.createeditworkout.WorkoutViewModel
import com.example.pacer.services.Workout

class CreateEditWorkoutActivity : AppCompatActivity() {

    val workoutViewModel by viewModels<WorkoutViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val workoutName = intent.getStringExtra("name")

        setContent {
            if (workoutName != null) {
                workoutViewModel.setWorkout(WorkoutService.getWorkout(workoutName))
            }

            Page(workoutViewModel)
        }

    }

}