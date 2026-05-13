package com.healthtracker.app.ui.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthtracker.app.R
import com.healthtracker.app.data.local.entity.PostEntity
import com.healthtracker.app.ui.theme.Surface

@Composable
fun CommunityScreen(vm: CommunityViewModel) {
    val posts by vm.posts.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.community_title), style = MaterialTheme.typography.titleLarge)
        Text(text = stringResource(R.string.community_subtitle), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(posts, key = { it.id }) { post ->
                PostCard(post = post, onSupport = { vm.upvote(post.id) })
            }
        }
    }
}

@Composable
private fun PostCard(post: PostEntity, onSupport: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = post.authorDisplayName ?: "Member",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = "${post.upvotes} ♥", style = MaterialTheme.typography.labelLarge)
            }
            Text(post.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(post.body, style = MaterialTheme.typography.bodyLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onSupport) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = stringResource(R.string.community_support))
                }
                Text(stringResource(R.string.community_support), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
