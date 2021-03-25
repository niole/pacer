package com.example.pacer.activities.createeditworkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
import com.example.pacer.services.Workout

@Composable
fun Page(workout: Workout, handleUpdateWorkout: (Workout) -> Unit) {

    val workoutName = remember {
        mutableStateOf(TextFieldValue(workout.name))
    }

    Column {
        TextField(
            value = workoutName.value,
            onValueChange = { workoutName.value = it }
        )

        LazyColumn {
            items(workout.stages) { stage ->
                Row() {
                    Column {
                        stage.subStages.forEach { subStage ->
                            Row() {
                                Text(text = subStage.name)
                                Text(text = subStage.durationSeconds.toString())
                                Text(text = subStage.distance.toString())
                            }
                        }
                    }
                }

            }
        }
    }
}