package com.example.mystudyproject1

sealed class Screens(val route: String) {
    object NoteListScreen: Screens("note_list_screen")
    object CreateNoteScreen: Screens("create_note_screen/{default_noteId}"){
        fun defaultRouteFun(noteId: Long) = "create_note_screen/$noteId"
    }

    object EditNoteScreen: Screens("edit_note_screen/{noteId}"){
        fun routeFun(noteId: Long) = "edit_note_screen/$noteId"
    }
    object AboutAppScreen: Screens("about_app_screen")

    object AddCategoryScreen: Screens("add_category_screen/{categoryId}"){
        fun categoryRoute(categoryId: Int) = "add_category_screen/$categoryId"
    }
    object ExtraFilterScreen: Screens("extra_filter_screen/{categoryId}/{categoryName}"){
        fun categoryRoute(categoryId: Int, categoryName: String) = "extra_filter_screen/$categoryId/$categoryName"

    }




}

