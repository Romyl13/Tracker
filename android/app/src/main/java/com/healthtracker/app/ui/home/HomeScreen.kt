package com.healthtracker.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SmokeFree
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthtracker.app.R
import com.healthtracker.app.ui.theme.Mint
import com.healthtracker.app.ui.theme.RelapseRed
import com.healthtracker.app.ui.theme.Surface
import java.time.LocalTime

@Composable
fun HomeScreen(vm: HomeViewModel) {
    val profile by vm.profileState.collectAsStateWithLifecycle()
    val streak = profile?.streakDays ?: 0
    var showRelapse by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .border(width = 10.dp, color = Mint, shape = CircleShape)
                .background(Surface),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.SmokeFree, contentDescription = null, tint = Mint, modifier = Modifier.size(40.dp))
                Text(
                    text = streak.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.home_streak_label),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { vm.checkIn(null) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = MaterialTheme.colorScheme.background)
        ) {
            Text(stringResource(R.string.home_check_in))
        }

        Button(
            onClick = { showRelapse = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Surface, contentColor = RelapseRed)
        ) {
            Text(stringResource(R.string.home_relapse))
        }
    }

    if (showRelapse) {
        RelapseDialog(
            onDismiss = { showRelapse = false },
            onConfirm = { bucket, stress, reason ->
                vm.relapse(bucket, stress, reason)
                showRelapse = false
            }
        )
    }
}

@Composable
private fun RelapseDialog(
    onDismiss: () -> Unit,
    onConfirm: (bucket: String, stress: Int, reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var stress by remember { mutableFloatStateOf(6f) }
    val bucket = remember {
        val h = LocalTime.now().hour
        when (h) {
            in 5..11 -> "morning"
            in 12..16 -> "afternoon"
            in 17..20 -> "evening"
            else -> "night"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (reason.isNotBlank()) {
                        onConfirm(bucket, stress.toInt().coerceIn(1, 10), reason)
                    }
                },
                enabled = reason.isNotBlank()
            ) {
                Text(stringResource(R.string.home_relapse))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
        title = { Text(stringResource(R.string.home_relapse)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Time bucket: $bucket (auto)")
                Text("Stress level: ${stress.toInt()}")
                Slider(value = stress, onValueChange = { stress = it }, valueRange = 1f..10f, steps = 8)
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text(stringResource(R.string.relapse_reason_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }
    )
}
