package com.example.mystudyproject1

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    onCreateNote: () -> Unit,
    onEdit: (Note) -> Unit,
    onAboutAppScreen: () -> Unit,
    stScope: SharedTransitionScope,
    avScope: AnimatedVisibilityScope,
    onIntent: (NoteIntent) -> Unit,
    state: ScreenState
) {
    val context = LocalContext.current
    with(stScope) {

        val textFieldState = rememberTextFieldState()
        val drawerState = rememberDrawerState(
            initialValue = DrawerValue.Closed
        )
        val scope = rememberCoroutineScope()


        val drawerItems = listOf(
            DrawerItem.ABOUT_APP,

        )




        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerContentTitle()
                    DrawerContentBody(drawerItems) { drawerItem ->
                        when (drawerItem.id) {
                            "about app" -> {
                                onAboutAppScreen()
                                scope.launch { drawerState.close() }
                            }
                        }

                    }
                }
            }
        ) {


            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            scrolledContainerColor = MaterialTheme.colorScheme.primary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            subtitleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),

                        title = {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(start = 5.dp),
                                    onClick = {
                                        scope.launch {
                                            drawerState.apply {
                                                if (isClosed) open() else close()
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "burger_menu"
                                    )
                                }
                            }
                        }
                        )
                },

                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            onCreateNote()
                            onIntent(NoteIntent.DisplayAddNote)
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .sharedElement(
                                rememberSharedContentState(
                                    "floating_action_button"
                                ),
                                animatedVisibilityScope = avScope

                            ),

                        ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.add)
                        )
                    }
                },
                bottomBar = {
                    BottomAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.primary,


                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                colors = ButtonColors(
                                    MaterialTheme.colorScheme.onSecondary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = {
                                    if (state.timerIsRunning) {
                                        onIntent(NoteIntent.StopTimer)
                                    } else {
                                        onIntent(NoteIntent.StartTimer)
                                    }
                                }

                            ) {
                                Text(
                                    text = stringResource(R.string.on_off_tracker)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,


                    ) {
                    NoteSearchBar(
                        modifier = Modifier,
                        textFieldState = textFieldState,
                        onIntent = onIntent,
                        state = state,
                        stScope = stScope,
                        avScope = avScope,
                        onEdit = onEdit,
                        context = context
                    )
                    AnimatedVisibility(
                        visible = state.notes.isEmpty() && state.searchText.isEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(

                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.empty_note_list),

                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp

                        )
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


                            items(state.notes, key = { it.id }) { item ->

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
                    state.dialogState?.let { note ->
                        AlertDialog(
                            onDismissRequest = { onIntent(NoteIntent.UpdateDialogState(null)) },
                            title = { Text(stringResource(R.string.do_you_want_delete_it)) },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onIntent(NoteIntent.DeleteNote(note))
                                        onIntent(NoteIntent.UpdateDialogState(null))
                                    }
                                ) {
                                    Text(
                                        text = "Да"
                                    )
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = {
                                        onIntent(NoteIntent.UpdateDialogState(null))
                                    }
                                ) {
                                    Text(
                                        text = "Нет"
                                    )
                                }
                            }
                        )
                    }
                    }
                }
            }

        }

    }
