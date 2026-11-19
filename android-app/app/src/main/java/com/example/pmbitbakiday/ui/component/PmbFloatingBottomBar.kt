package com.example.pmbitbakiday.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pmbitbakiday.AppScreen
import com.example.pmbitbakiday.ui.theme.PastelYellow
import com.example.pmbitbakiday.ui.theme.SurfacePrimary
import com.example.pmbitbakiday.ui.theme.TextMuted
import com.example.pmbitbakiday.ui.theme.TextOnBrightAccent

data class NavItemData(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun PmbFloatingBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItemData(AppScreen.LIST, "Beranda", Icons.Default.Home),
        NavItemData(AppScreen.FORM, "Pendaftaran", Icons.AutoMirrored.Filled.Assignment),
        NavItemData(AppScreen.STATUS, "Status PMB", Icons.Default.Info),
        NavItemData(AppScreen.AUTH, "Akun", Icons.Default.AccountCircle)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = SurfacePrimary,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    val iconBgColor = if (isSelected) PastelYellow else Color.Transparent
                    val iconTintColor = if (isSelected) TextOnBrightAccent else TextMuted

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(iconBgColor)
                            .clickable { onNavigate(item.screen) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconTintColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
