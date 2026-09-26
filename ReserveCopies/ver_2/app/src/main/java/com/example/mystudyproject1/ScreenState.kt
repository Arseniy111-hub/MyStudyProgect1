package com.example.mystudyproject1

import android.net.Uri

data class ScreenState (
    val filteredNotes: List<Note> = emptyList(),
    val notes: List<Note> = emptyList(),
    val text: String = "",
    val contact: Contact? = null,
    val pictureUris: List<Uri> = emptyList(),
    val isAddingNote: Boolean = false,
    val sortType: SortType = SortType.ALL,
    val isEditing: Boolean = false,
    val isSearching: Boolean = false,
    val isActiveRowTab: Boolean = false,
    val searchText: String = "",
    val filterType: FilterNotesTypes = FilterNotesTypes.ALL,
    val timerIsRunning: Boolean = false,
    val dialogState: Note? = null,
    val contactIsPin: Boolean = false,
    val pictureIsPin: Boolean = false,
    val contactUri: Uri? = null,
    val isCharging: Boolean = false,
    val fromOtherApp: Boolean = false,
    val editingNoteId: Long? = null,
    val categories: List<NoteCategory> = emptyList(),
    val categoryName: String = "",
    val isAddingCategory: Boolean = false,
    val categoryIsPinned: Boolean = false,
    val currentCategoryId: Int = 0

)

