package com.mishin.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mishin.app.R
import com.mishin.core.ui.theme.MishinColors

/**
 * App navigation drawer: brand header, top-level destinations and logout.
 * Matches the sidebar mockup: cream background, flat full-width selection
 * band, warm gold dividers and a close button in the header.
 */
@Composable
fun MishinDrawer(
    currentRoute: String?,
    onDestinationClick: (TopLevelDestination) -> Unit,
    onClose: () -> Unit,
    /** Placeholder until a real auth flow exists: closes the drawer. */
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = colors.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 32.dp, end = 16.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colors.onBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = colors.background,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Text(
                    text = stringResource(R.string.drawer_role),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(colors.onBackground)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.drawer_close),
                    tint = colors.background,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        HorizontalDivider(color = MishinColors.Gold.copy(alpha = 0.35f))

        TopLevelDestination.drawerItems.forEach { destination ->
            DrawerItem(
                destination = destination,
                selected = currentRoute == destination.route,
                onClick = { onDestinationClick(destination) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(color = MishinColors.Gold.copy(alpha = 0.35f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLogout)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                tint = colors.onBackground,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.drawer_logout),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onBackground
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * Single drawer row: icon + label on the cream background, with a flat
 * full-width band highlight when selected (mockup style, no pill).
 */
@Composable
private fun DrawerItem(
    destination: TopLevelDestination,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) colors.onBackground.copy(alpha = 0.08f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = null,
            tint = colors.onBackground,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = destination.title,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onBackground
        )
    }
}
