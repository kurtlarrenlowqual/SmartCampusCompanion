package com.example.smartcampuscompanion.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.data.local.AnnouncementEntity
import java.text.SimpleDateFormat
import java.util.*

// ── Drawer menu item used in every screen ─────────────────────────────────────
@Composable
fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label   = { Text(label, fontWeight = FontWeight.Medium) },
        icon    = { Icon(icon, null) },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        shape   = RoundedCornerShape(16.dp),
        colors  = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            selectedIconColor      = MaterialTheme.colorScheme.primary,
            selectedTextColor      = MaterialTheme.colorScheme.primary
        )
    )
}

// ── Empty state shown when announcements list is empty ────────────────────────
@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.AutoAwesomeMotion,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint     = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Nothing new yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

// ── Single announcement card ──────────────────────────────────────────────────
@Composable
fun AestheticAnnouncementCard(
    item: AnnouncementEntity,
    onToggleRead: () -> Unit
) {
    val fmt = remember { SimpleDateFormat("MMM dd • hh:mm a", Locale.getDefault()) }

    val backgroundColor by animateColorAsState(
        targetValue = if (item.isRead)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleRead() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp) // 🔥 remove 3D feel
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = item.body,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fmt.format(Date(item.postedAtMillis)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                if (item.isRead) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Read",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}