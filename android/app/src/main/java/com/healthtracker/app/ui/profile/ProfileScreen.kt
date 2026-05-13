package com.healthtracker.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthtracker.app.R
import com.healthtracker.app.ui.theme.Mint
import com.healthtracker.app.ui.theme.Surface

@Composable
fun ProfileScreen(vm: ProfileViewModel) {
    val profile by vm.profileState.collectAsStateWithLifecycle()
    val streak = profile?.streakDays ?: 0

    val achievements = listOf(
        Triple(1, R.string.achievement_day_1, streak >= 1),
        Triple(7, R.string.achievement_day_7, streak >= 7),
        Triple(30, R.string.achievement_day_30, streak >= 30)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.profile_title), style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())

        Icon(Icons.Outlined.Person, contentDescription = null, tint = Mint, modifier = Modifier.size(96.dp))
        Text(profile?.displayName ?: stringResource(R.string.profile_not_signed_in), style = MaterialTheme.typography.headlineSmall)
        Text(profile?.email ?: "", style = MaterialTheme.typography.bodyMedium)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.profile_notifications))
            Switch(
                checked = profile?.notificationsEnabled ?: true,
                onCheckedChange = { vm.setNotifications(it) }
            )
        }

        Text(stringResource(R.string.profile_achievements), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            achievements.forEach { (days, titleRes, unlocked) ->
                Card(colors = CardDefaults.cardColors(containerColor = Surface), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.EmojiEvents,
                            contentDescription = null,
                            tint = if (unlocked) Mint else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        )
                        Column {
                            Text(stringResource(titleRes), style = MaterialTheme.typography.titleSmall)
                            Text(
                                if (unlocked) {
                                    stringResource(R.string.profile_achievement_unlocked)
                                } else {
                                    stringResource(R.string.profile_achievement_locked, days)
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Button(onClick = { vm.signOut() }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.profile_log_out))
        }
    }
}
