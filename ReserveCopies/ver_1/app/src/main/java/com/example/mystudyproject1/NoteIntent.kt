package com.example.mystudyproject1

import android.net.Uri

sealed interface NoteIntent {
    object SaveNote: NoteIntent
    data class DeleteNote(val note: Note): NoteIntent
    data class SetText(val text: String): NoteIntent
    data class SetContact(val contact: Contact?): NoteIntent
    data class SetImages(val images: List<Uri>): NoteIntent
    data class SetSortType(val sortType: SortType): NoteIntent
    object DisplayAddNote: NoteIntent
    object HideAddNote: NoteIntent
    object DisplayRedNote: NoteIntent
    object HideEditNote: NoteIntent
    data class ShareNote(val note: Note): NoteIntent
    object DisplaySearch: NoteIntent
    object HideSearch: NoteIntent
    data class UpdateSearchText(val searchText: String): NoteIntent
    data class ChangeFilter(val filter: FilterNotesTypes): NoteIntent
    object StartTimer: NoteIntent
    object StopTimer: NoteIntent
    data class UpdateDialogState(val dialogState: Note?): NoteIntent
    data class SetContactPin(val isPinned: Boolean): NoteIntent
    data class SetPicturePin(val isPinned: Boolean): NoteIntent
    data class SetContactUri(val uri: Uri?): NoteIntent
    data class LoadNote(val noteId: Long): NoteIntent
    data class UpdateChargingState(val isCharging: Boolean): NoteIntent
    data class HandleIncomingText(val text: String?): NoteIntent
    object ResetFromOtherAppFlag: NoteIntent
    //data class FilterNotes(val notes: List<Note>): NoteIntent

}