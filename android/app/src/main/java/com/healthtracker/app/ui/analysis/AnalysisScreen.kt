package com.healthtracker.app.ui.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.healthtracker.app.R
import com.healthtracker.app.data.local.model.LocalAnalyticsSummary
import com.healthtracker.app.data.local.model.LocalHabitLogRef
import com.healthtracker.app.ui.theme.Mint
import com.healthtracker.app.ui.theme.RelapseRed
import com.healthtracker.app.ui.theme.Surface
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max

@Composable
fun AnalysisScreen(summary: LocalAnalyticsSummary?, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.analysis_title), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(R.string.analysis_subtitle), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
        Button(onClick = onRefresh) { Text(stringResource(R.string.community_refresh)) }

        if (summary == null) {
            Text(stringResource(R.string.analysis_empty))
            return
        }

        val money = String.format("%.2f", summary.moneySavedUah)
        Text(
            text = stringResource(R.string.analysis_money_saved, money),
            style = MaterialTheme.typography.headlineSmall,
            color = Mint
        )

        Text("XP: ${summary.totalXp} · Success ratio: ${(summary.successfulDaysRatio * 100).toInt()}%", style = MaterialTheme.typography.bodyLarge)

        WeekStrip(relapses = summary.relapseHistory)

        Spacer(Modifier.height(8.dp))
        Text("Relapse timeline (count)", style = MaterialTheme.typography.titleMedium)
        RelapseSparkline(relapses = summary.relapseHistory)

        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.analysis_insights), style = MaterialTheme.typography.titleMedium)
        if (summary.insights.isEmpty()) {
            Text(stringResource(R.string.analysis_no_insights))
        } else {
            summary.insights.take(5).forEach { insight ->
                Card(colors = CardDefaults.cardColors(containerColor = Surface), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(insight.title, style = MaterialTheme.typography.titleSmall, color = Mint)
                        Text(insight.detail, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(relapses: List<LocalHabitLogRef>) {
    val zone = ZoneId.systemDefault()
    val relapseDays = remember(relapses) {
        relapses.map { Instant.ofEpochMilli(it.occurredAtEpochMs).atZone(zone).toLocalDate() }.toSet()
    }
    val today = LocalDate.now(zone)
    val start = today.minusDays(6)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Last 7 days", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            for (i in 0..6L) {
                val day = start.plusDays(i)
                val isRelapse = relapseDays.contains(day)
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isRelapse) RelapseRed else Mint.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(day.dayOfMonth.toString(), color = Color.White)
                    }
                    Text(day.dayOfWeek.name.take(1), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun RelapseSparkline(relapses: List<LocalHabitLogRef>) {
    val zone = ZoneId.systemDefault()
    val countsByMonth = remember(relapses) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        relapses
            .map { Instant.ofEpochMilli(it.occurredAtEpochMs).atZone(zone).toLocalDate() }
            .groupingBy { it.withDayOfMonth(1).format(formatter) }
            .eachCount()
            .toList()
            .sortedBy { it.first }
            .map { it.second.toFloat() }
    }

    val values = if (countsByMonth.isEmpty()) listOf(0f, 0f) else countsByMonth
    val maxY = max(values.maxOrNull() ?: 1f, 1f)

    Card(colors = CardDefaults.cardColors(containerColor = Surface), modifier = Modifier.fillMaxWidth().height(140.dp)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val w = size.width
            val h = size.height
            if (values.size < 2) {
                drawLine(Color.LightGray, Offset(0f, h / 2), Offset(w, h / 2), strokeWidth = 3f)
                return@Canvas
            }
            val step = w / (values.size - 1)
            for (i in 0 until values.size - 1) {
                val x1 = step * i
                val x2 = step * (i + 1)
                val y1 = h - (values[i] / maxY) * h
                val y2 = h - (values[i + 1] / maxY) * h
                drawLine(Mint, Offset(x1, y1), Offset(x2, y2), strokeWidth = 4f)
            }
        }
    }
}
