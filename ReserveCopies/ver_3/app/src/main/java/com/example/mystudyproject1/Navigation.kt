package com.example.mystudyproject1

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlin.random.Random

@Composable
fun Navigation(
    viewModel: NoteViewModel,
) {
    val navController = rememberNavController()
    val flowState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(flowState.fromOtherApp) {
        if(flowState.fromOtherApp){
                navController.navigate(Screens.CreateNoteScreen.defaultRouteFun(noteId = Random.nextLong()))
            viewModel.onIntent(NoteIntent.ResetFromOtherAppFlag)
        }
    }


    SharedTransitionLayout{
        NavHost(navController = navController, startDestination = Screens.NoteListScreen.route){

            composable(
                route = Screens.NoteListScreen.route,
            ){
                NoteListScreen(
                    onExtraFilterScreen = { noteCategory ->
                        navController.navigate(route = Screens.ExtraFilterScreen.categoryRoute(
                            noteCategory.categoryId,
                            noteCategory.categoryName
                        )
                        )

                    },
                    stScope = this@SharedTransitionLayout,
                    avScope = this@composable,
                    onEdit = { note ->
                        navController
                            .navigate(
                                Screens.EditNoteScreen.routeFun(note.id)
                            )
                    },
                    onAboutAppScreen = { navController.navigate(Screens.AboutAppScreen.route) },
                    onIntent = {
                        viewModel.onIntent(it)
                    },
                    state = flowState,
                    onCreateNote = {
                        navController
                            .navigate(
                                Screens.CreateNoteScreen.defaultRouteFun(-1L)
                            )
                    }
                )
            }

            composable(
                route = Screens.CreateNoteScreen.route,
                arguments = listOf(
                    navArgument("default_noteId"){
                        type = NavType.LongType
                    }
                )
            ) {backStackEntry ->
                val defaultNoteId = backStackEntry.arguments?.getLong("default_noteId") ?: -1L
                CreateNoteScreen(
                    onBack = {
                        navController.popBackStack()

                    },
                    svScope = this@SharedTransitionLayout,
                    avScope = this@composable,
                    noteId = defaultNoteId,
                    context = LocalContext.current,
                    onIntent = {
                        viewModel.onIntent(it)
                    },
                    state = flowState,
                    onCategory = {
                        navController.navigate(
                            Screens.
                            AddCategoryScreen.
                            categoryRoute(viewModel.state.value.currentCategoryId)
                        )

                    }
                )
            }
            composable(
                route = Screens.EditNoteScreen.route,
                arguments = listOf(
                    navArgument("noteId"){
                        type = NavType.LongType
                    }
                )
            ){ backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L



                CreateNoteScreen(
                    noteId = noteId,
                    onBack = {
                        navController.popBackStack()

                    },
                    svScope = this@SharedTransitionLayout,
                    avScope = this@composable,
                    context = LocalContext.current,
                    onIntent = {
                        viewModel.onIntent(it)
                    },
                    state = flowState,
                    onCategory = {
                        navController.navigate(
                            Screens.
                            AddCategoryScreen.
                            categoryRoute(viewModel.state.value.currentCategoryId)
                        )
                    }
                )
            }
            composable(
                route = Screens.AboutAppScreen.route,
                enterTransition = { slideInHorizontally { it } + fadeIn() },
                exitTransition = { slideOutHorizontally { -it } + fadeOut() }
            ) {
                AboutAppScreen(
                    stScope = this@SharedTransitionLayout,
                    avScope = this@composable,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screens.AddCategoryScreen.route,
                enterTransition = { slideInHorizontally { it } + fadeIn() },
                exitTransition = { slideOutHorizontally { -it } + fadeOut() },
                arguments = listOf(
                    navArgument("categoryId"){
                        type = NavType.IntType
                    }
                )
            ) {
                AddCategoryScreen(
                    stScope = this@SharedTransitionLayout,
                    avScope = this@composable,
                    onBack = { navController.popBackStack() },
                    onIntent = {
                        viewModel.onIntent(it)
                    },
                    state = flowState,
                )
            }
            composable(
                route = Screens.ExtraFilterScreen.route,
                enterTransition = { slideInHorizontally { it } + fadeIn() },
                exitTransition = { slideOutHorizontally { -it } + fadeOut() },
                arguments = listOf(
                    navArgument("categoryId"){
                        type = NavType.IntType
                    },
                    navArgument("categoryName"){
                        type = NavType.StringType
                    }

                )
            ){
                val categoryId = it.arguments?.getInt("categoryId") ?: -1
                val categoryName = it.arguments?.getString("categoryName") ?: ""


                ExtraFilterScreen(
                    stScope = this@SharedTransitionLayout,
                    avScope = this@composable,
                    onBack = { navController.popBackStack() },
                    onIntent = { noteIntent ->
                        viewModel.onIntent(noteIntent)
                    },
                    state = flowState,
                    categoryId = categoryId,
                    onEdit = { note ->
                        navController
                            .navigate(
                                Screens.EditNoteScreen.routeFun(note.id)
                            )
                    },
                    context = LocalContext.current,
                    categoryName = categoryName
                    )

            }

        }
    }

}