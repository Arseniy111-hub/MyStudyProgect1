package com.example.mystudyproject1

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

@SuppressLint("MutableCollectionMutableState")
class NoteViewModel(
    private val dao: NoteDao,
    application: MyApplication
): AndroidViewModel(application) {
    val TAG = "ViewModel"

    private val _sortType = MutableStateFlow(SortType.ALL)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _notes = _sortType
        .flatMapLatest { sortType ->
            when(sortType){
                SortType.TEXT -> {
                    dao.orderByText()
                }
                SortType.CONTACTS -> {
                    dao.orderByContacts()
                }

                SortType.ALL -> {
                    dao.orderByAll()
                }

                else -> { dao.orderByAll() }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = emptyList())
    private val _state = MutableStateFlow(ScreenState())
        val state = combine(_state, _notes, _sortType) { state, notes, sortType ->
            val filteredNotes = notes.filter { note ->
                val matchesSearch = state.searchText.isBlank() ||
                        note.text?.contains(state.searchText, ignoreCase = true) == true
                val matchesFilterType = when (state.filterType) {
                    FilterNotesTypes.ALL -> true
                    FilterNotesTypes.TEXT -> note.contact == null && note.pictureUris.isEmpty()
                    FilterNotesTypes.CONTACTS -> note.contact != null
                    FilterNotesTypes.PICTURES -> note.pictureUris.isNotEmpty()
                }
                matchesFilterType && matchesSearch
            }
            state.copy(
                sortType = sortType,
                notes = if (state.isSearching) filteredNotes else notes,
                filteredNotes = filteredNotes
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ScreenState()
        )

    fun onIntent(intent: NoteIntent){
        when(intent){
            is NoteIntent.DeleteNote -> {
                viewModelScope.launch {
                    dao.delete(intent.note)
                }
            }
            NoteIntent.DisplayAddNote -> {
                if(!_state.value.fromOtherApp)
                {

                    _state.update {
                        it.copy(
                           text = "",
                            contact = null,
                            pictureUris = emptyList(),
                            isAddingNote = true,
                            isEditing = false,
                            isSearching = false,
                            editingNoteId = null
                        )
                    }

                }
                Log.d(TAG + "AfterDisplayAddNote", "DATA: ${_state.value.text}")
            }
            NoteIntent.DisplayRedNote -> {
                _state.update {
                    it.copy(isEditing = true)
                }
            }
            NoteIntent.DisplaySearch -> {
                _state.update {
                    it.copy(isSearching = true)

                }
            }
            NoteIntent.HideAddNote -> {
                _state.update {
                    it.copy(
                        text = "",
                        contact = null,
                        pictureUris = emptyList(),
                        isAddingNote = false,
                        isEditing = false,
                        isSearching = false,
                        editingNoteId = null
                    )
                }
            }
            NoteIntent.HideEditNote -> {
                _state.update {
                    it.copy(
                        text = "",
                        contact = null,
                        pictureUris = emptyList(),
                        isAddingNote = false,
                        isEditing = false,
                        isSearching = false,
                        editingNoteId = null
                    )
                }
            }
            NoteIntent.HideSearch -> {
                _state.update {
                    it.copy(isSearching = false)
                }
            }
            NoteIntent.SaveNote -> {
                val id = _state.value.editingNoteId ?: Random.nextLong()
                val text = _state.value.text
                val contact = _state.value.contact
                val images = _state.value.pictureUris
                val newNote = Note(
                    id =  id,
                    text = text,
                    contact = contact,
                    pictureUris = images
                )
                viewModelScope.launch {
                    dao.upsert(newNote)
                }
                _state.update {
                    it.copy(
                        text = "",
                        contact = null,
                        pictureUris = emptyList(),
                        isAddingNote = false,
                        isEditing = false,
                        isSearching = false,
                        editingNoteId = null
                    )
                }
            }
            is NoteIntent.SetContact -> {
                _state.update {
                    it.copy(
                        contact = intent.contact,
                        contactIsPin = intent.contact != null
                    )
                }
            }
            is NoteIntent.SetImages -> {
                _state.update {
                    it.copy(
                        pictureUris = intent.images,
                        pictureIsPin = intent.images.isNotEmpty()
                    )
                }
            }
            is NoteIntent.SetSortType -> {
                _sortType.value = intent.sortType
            }
            is NoteIntent.SetText -> {
                _state.update {
                    it.copy(text = intent.text)
                }
            }
            is NoteIntent.ShareNote -> {
                //Todo("Скорее всего потом удалить полностью, пока затычка")

            }
            is NoteIntent.UpdateSearchText -> {
                _state.update {
                    it.copy(searchText = intent.searchText)
                }
            }

            is NoteIntent.ChangeFilter -> {
                _state.update {
                    it.copy(
                        filterType = intent.filter
                    )
                }
            }

            is NoteIntent.StartTimer -> {
                Intent(
                    application,
                    TimerService::class.java
                ).apply {
                    action = TimerService.Actions.START.toString()
                    application.startService(this)
                    _state.update{
                        it.copy(timerIsRunning = true)
                    }
                }
            }
            is NoteIntent.StopTimer -> {
                Intent(
                    application,
                    TimerService::class.java
                ).apply {
                    action = TimerService.Actions.STOP.toString()
                    application.startService(this)
                    _state.update{
                        it.copy(timerIsRunning = false)
                    }
                }
            }

            is NoteIntent.UpdateDialogState -> {
                _state.update {
                    it.copy(dialogState = intent.dialogState)
                }
            }
            is NoteIntent.SetContactPin -> {
                _state.update {
                    it.copy(contactIsPin = intent.isPinned)
                }
            }
            is NoteIntent.SetPicturePin -> {
                _state.update {
                    it.copy(pictureIsPin = intent.isPinned)
                }
            }
            is NoteIntent.SetContactUri -> {
                _state.update {
                    it.copy(contactUri = intent.uri)
                }
            }

            is NoteIntent.LoadNote -> {
                viewModelScope.launch {
                    val note = dao.getNoteById(intent.noteId)
                    if (!_state.value.fromOtherApp) {
                        _state.update {
                            it.copy(
                                text = note?.text ?: "",
                                contact = note?.contact,
                                pictureUris = note?.pictureUris ?: emptyList(),
                                contactUri = note?.contact?.uri,
                                contactIsPin = note?.contact != null,
                                pictureIsPin = note?.pictureUris?.isNotEmpty() == true,
                                isEditing = note != null,
                                isAddingNote = note == null,
                                editingNoteId = note?.id
                            )
                        }
                    } else {
                        // Если пришли из другого приложения, просто запоминаем ID (хотя он будет рандомным)
                         _state.update { it.copy(editingNoteId = intent.noteId) }
                    }
                }
            }
            is NoteIntent.UpdateChargingState -> {
                _state.update {
                    it.copy(isCharging = intent.isCharging)
                }
            }
            is NoteIntent.HandleIncomingText -> {
                _state.update {
                    it.copy(
                        text = intent.text ?: "",
                        fromOtherApp = true,
                        isAddingNote = true


                    )
                }
                Log.d(TAG, "DATA: ${_state.value.text}")
            }
            NoteIntent.ResetFromOtherAppFlag -> {
                if(!_state.value.isAddingNote){
                    _state.update {

                        it.copy(fromOtherApp = false)
                    }
                }

            }


        }
    }
}