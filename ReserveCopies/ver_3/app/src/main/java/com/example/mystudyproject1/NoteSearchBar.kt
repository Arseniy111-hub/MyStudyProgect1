package com.example.mystudyproject1

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSearchBar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    onIntent: (NoteIntent) -> Unit,
    state: ScreenState,
    stScope: SharedTransitionScope,
    avScope: AnimatedVisibilityScope,
    onEdit: (Note) -> Unit,
    context: Context


){


    var expanded by remember { mutableStateOf(false) }
    var eraseIconIsClicked by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current


    LaunchedEffect(expanded){
        if(expanded){
            onIntent(NoteIntent.DisplaySearch)
        }
        else{
            onIntent(NoteIntent.HideSearch)
            textFieldState.edit {
                replace(0, length, "")
            }
            onIntent(NoteIntent.UpdateSearchText(""))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true },



    ){

        SearchBar(
            modifier = Modifier
                .semantics { traversalIndex = 0f }
                .align(Alignment.TopCenter),

            inputField = {
                SearchBarDefaults.InputField(
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            "search_icon"
                        )
                    },
                    trailingIcon = {
                                        Icon(
                                            Icons.Default.Close,
                                            "close_icon",
                                            Modifier.clickable(
                                                onClick = {
                                                    eraseIconIsClicked = true

                                                    textFieldState.edit {
                                                        replace(0, length, "")
                                                    }
                                                    onIntent(NoteIntent.UpdateSearchText(""))
                                                    eraseIconIsClicked = !eraseIconIsClicked

                                                }
                                            )

                                        )
                                   },
                    query = if(expanded) textFieldState.text.toString()
                        else "",
                    onQueryChange = {
                        textFieldState.edit {
                            replace(0, length, it)
                             onIntent(NoteIntent.UpdateSearchText(it))
                                            }
                                    },
                    onSearch = {
                        focusManager.clearFocus()
                       // onSearch(textFieldState.text.toString())

                        //expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text(text = "Search") }


                )
            },

            expanded = expanded,
            onExpandedChange = { expanded = it }

        ){
            if(expanded){
                MyTabRow(
                    modifier = Modifier.padding(12.dp),
                    onIntent = onIntent,
                    state = state
                )
            }
            AnimatedVisibility(
                visible = state.searchText.isNotEmpty() && state.notes.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),

                ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "search",
                        Modifier.size(256.dp)
                    )
                    Text(
                        text = "Nothing not find"
                    )
                }
            }

            if (state.notes.isNotEmpty())
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(150.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),

                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {



                    items(
                        state.notes,
                        key = { it.id }
                    ) { item ->

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
}
