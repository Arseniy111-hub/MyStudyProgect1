package com.example.mystudyproject1

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.ui.graphics.vector.ImageVector


    enum class FilterNotesTypes(
        val label: String,
        val unselectedIcon: ImageVector,
        val selectedIcon: ImageVector,
        val contentDescription: String,
    ){
        ALL(
            "all search",
            Icons.Default.Search,
            Icons.Default.Search,
            "all_search"
        ),
        TEXT(
            "Text search",
            Icons.Default.TextFields,
            Icons.Default.TextFields,
            "text_search"
        ),
        CONTACTS (
            "contacts search",
            Icons.Default.Contacts,
            Icons.Default.Contacts,
            "contacts_search"
        ),
        PICTURES(
            "pictures search",
            Icons.Default.ImageSearch,
            Icons.Default.ImageSearch,
            "pictures_search"
        )


    }
