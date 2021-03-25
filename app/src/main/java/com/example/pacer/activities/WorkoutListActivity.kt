package com.example.pacer.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.pacer.activities.workoutlist.Page

class WorkoutListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Page(
                workouts = listOf("cat", "dog"),
                handleCreateNewWorkout = { startCreateNewWorkoutActivity() },
                handleEditWorkout = { w -> startEditWorkoutActivity(w) },
                handleDeleteWorkout = { w -> println("delete $w")}
            )
        }

    }

    private fun startCreateNewWorkoutActivity(): Unit {
        startActivity(Intent(this, CreateEditWorkoutActivity::class.java))
    }

    private fun startEditWorkoutActivity(workoutName: String): Unit {
        startActivity(
            Intent(this, CreateEditWorkoutActivity::class.java).apply {
                putExtra("name", workoutName)
            }
        )
    }
}