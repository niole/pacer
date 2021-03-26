package com.example.pacer.activities.createeditworkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Page(workoutViewModel: WorkoutViewModel) {

    val workout by workoutViewModel.workout.observeAsState()

    Column {
        TextField(
            value = workout?.name ?: "untitled", // TODO create global default for workout name
            onValueChange = { workoutViewModel.setWorkoutName(it) }
        )

        Button(onClick = { workoutViewModel.addEmptyStage() }) {
            Text("Add New Stage")
        }

        LazyColumn {
            items(workout?.stages ?: listOf()) { stage ->
                Row() {
                    Column {
                        stage.subStages.forEach { subStage ->
                            Row(modifier = Modifier.padding(5.dp)) {
                                Text(text = subStage.name, modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp))
                                Text(text = subStage.durationSeconds.toString(), modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp))
                                Text(text = subStage.distance.toString(), modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp))
                            }
                        }
                    }
                }

            }
        }
    }
}