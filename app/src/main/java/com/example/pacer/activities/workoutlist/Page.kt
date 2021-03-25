package com.example.pacer.activities.workoutlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Page(
    workouts: List<String>,
    handleCreateNewWorkout: () -> Unit,
    handleEditWorkout: (String) -> Unit,
    handleDeleteWorkout: (String) -> Unit
) {
    Column {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = handleCreateNewWorkout) {
                    Text(text = "Create New Workout")
                }
            }
        }

        LazyColumn {
            items(workouts) { workout ->
                Text(workout)
                Button(onClick = { handleEditWorkout(workout) }) {
                    Text(text = "Edit")
                }
                Button(onClick = { handleDeleteWorkout(workout) }) {
                    Text(text = "Delete")
                }
            }
        }
    }
}
