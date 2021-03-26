package com.example.pacer.activities.createeditworkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pacer.services.Workout
import com.example.pacer.services.WorkoutStage
import com.example.pacer.services.WorkoutSubStage

class WorkoutViewModel : ViewModel() {

    val workout = MutableLiveData<Workout>(Workout("untitled", listOf()))

    fun getWorkout(): LiveData<Workout> {
        return workout
    }

    fun setWorkout(newWorkout: Workout): Unit {
        workout.value = newWorkout
    }

    fun setWorkoutName(name: String): Unit {
        workout.value = Workout(name, workout.value!!.stages)
    }

    fun addEmptyStage(): Unit {
        val w = workout.value!!
        val newSubStages = listOf(
            WorkoutSubStage("untitled", null, null)
        )
        workout.value = Workout(
            w.name,
            w.stages + WorkoutStage(1, newSubStages)
        )
    }
}