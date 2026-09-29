package com.example.mystudyproject1

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.room.util.TableInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    modifier: Modifier = Modifier,
    avScope: AnimatedVisibilityScope,
    stScope: SharedTransitionScope,
    onBack: () -> Unit,
    onIntent : (NoteIntent) -> Unit,
    state: ScreenState
) {
    LaunchedEffect(Unit) {
        if(state.currentCategoryId != 0){
            val currentCategoryName = state.categories.find {
                it.categoryId == state.currentCategoryId
            }?.categoryName ?: ""
            onIntent(NoteIntent.SetCategoryName(currentCategoryName))
        }
        else { onIntent(NoteIntent.SetCategoryName("")) }

    }
    BasicAlertDialog(
        onDismissRequest = onBack,
        modifier = modifier,
        properties = DialogProperties(),
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.addcategory),
                                fontSize = 24.sp
                            )

                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    TextField(
                        value = state.categoryName,
                        onValueChange = { onIntent(NoteIntent.SetCategoryName(it)) },
                        placeholder = {Text("Category name")}
                    )
                    IconButton(
                        onClick = {
                            onIntent(NoteIntent.SaveCategory)
                            onBack()
                        }
                    ) {
                        Icon(
                            Icons.Default.Save,
                            "Save Category"
                        )
                    }
                }
            }
        })



}