package com.example.pacer.services

object WorkoutService {

    fun getWorkout(name: String): Workout {
       return Workout(
           name = name,
           stages = listOf(
               WorkoutStage(2, listOf(WorkoutSubStage("warmup", null, Distance(1, DistanceUnit.MILES))))
           )
       )
    }
}