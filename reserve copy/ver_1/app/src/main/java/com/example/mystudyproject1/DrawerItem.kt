package com.example.mystudyproject1


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector


enum class DrawerItem(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val contentDescription: String = "empty description",
){
    ABOUT_APP(
        id = "about app",
        title = "About app",
        contentDescription = "about_app_item",
        icon = Icons.Default.Info,
    ),
    CLEAR_FILTERS(
        id = "clear filters",
        title = "Clear filters",
        contentDescription = "clear_filters",
        icon = Icons.Default.ClearAll

    )
}


