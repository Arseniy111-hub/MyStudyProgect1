package com.example.mystudyproject1

import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraFilterScreen(
    modifier: Modifier = Modifier,
    onIntent: (NoteIntent) -> Unit,
    state: ScreenState,
    stScope: SharedTransitionScope,
    avScope: AnimatedVisibilityScope,
    onBack: () -> Unit,
    categoryId: Int,
    onEdit: (Note) -> Unit,
    context: Context,
    categoryName: String
) {

    val filteredList: List<Note> = state.notes.filter { note ->
        val getCurrentNotesOnId = note.categoryId == categoryId
        getCurrentNotesOnId
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(categoryName)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack()
                        }
                    ){
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            ""
                        )
                    }
                },
            )
        }
    ) { padding ->
        if (state.notes.isNotEmpty())
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(150.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),

                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)

            ) {


                items(filteredList, key = { it.id }) { item ->

                    NoteElement(
                        modifier = Modifier.animateItem(),
                        note = item,
                        avScope = avScope,
                        svScope = stScope,
                        onEdit = onEdit,
                        onIntent = onIntent,
                        state = state,
                        context = context
                    )
                    Spacer(Modifier.padding(5.dp))

                }

            }
    }
}