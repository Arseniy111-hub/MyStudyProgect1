package com.example.mystudyproject1

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun MyTabRow(
    modifier: Modifier = Modifier,
    onIntent: (NoteIntent) -> Unit,
    state: ScreenState

) {
    val selectedFilter = state.filterType
    val selectedIndex = FilterNotesTypes.entries.indexOf(selectedFilter)

    PrimaryTabRow(
        modifier = modifier.fillMaxWidth(),
        selectedTabIndex = selectedIndex,

    ) {
        FilterNotesTypes.entries.forEachIndexed { index, filter ->
            Tab(
                selected = filter == selectedFilter,
                onClick = {
                    onIntent(NoteIntent.ChangeFilter(filter))
                },
                text = {
                    Text(
                      text = filter.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                icon = {
                    Icon(
                        imageVector = if(index == selectedIndex) filter.selectedIcon
                        else filter.unselectedIcon,
                         contentDescription = filter.contentDescription
                    )
                }
            )
        }
    }


}





