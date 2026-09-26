package com.example.mystudyproject1

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
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
    private val _categories = dao.getALlCategories()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyList()
        )

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
        val state = combine(_state, _notes, _sortType, _categories) { state, notes, sortType, categories ->
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
                filteredNotes = filteredNotes,
                categories = categories
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
                val categoryId = _state.value.currentCategoryId
                val newNote = Note(
                    id =  id,
                    text = text,
                    contact = contact,
                    pictureUris = images,
                    categoryId = categoryId
                )
                viewModelScope.launch {
                    dao.upsertTheNote(newNote)
                }
                _state.update {
                    it.copy(
                        text = "",
                        contact = null,
                        pictureUris = emptyList(),
                        isAddingNote = false,
                        isEditing = false,
                        isSearching = false,
                        editingNoteId = null,
                        categoryName = "",
                        currentCategoryId = 0
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
                // Загружаем из базы ТОЛЬКО если это существующая заметка (id != -1L)
                if (intent.noteId != -1L) {
                    viewModelScope.launch {
                        val note = dao.getNoteById(intent.noteId)
                        if (note != null && !_state.value.fromOtherApp) {
                            _state.update {
                                it.copy(
                                    text = note.text ?: "",
                                    contact = note.contact,
                                    pictureUris = note.pictureUris,
                                    contactUri = note.contact?.uri,
                                    contactIsPin = note.contact != null,
                                    pictureIsPin = note.pictureUris.isNotEmpty(),
                                    isEditing = true,
                                    isAddingNote = false,
                                    editingNoteId = note.id,
                                    currentCategoryId = note.categoryId,



                                )
                            }
                        }
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

            is NoteIntent.DeleteCategory -> {
                viewModelScope.launch {
                    dao.deleteCategory(category = intent.category)
                }
            }
            NoteIntent.SaveCategory -> {
                val currentId = _state.value.currentCategoryId
                val categoryName = _state.value.categoryName
                val category = if (categoryName.isNotBlank()){
                     NoteCategory(
                        categoryName = categoryName,
                        categoryId =currentId
                    )
                } else {
                    Log.d(TAG, "Blank category name")
                }

                viewModelScope.launch {
                    val newId = dao.insertCategory(category as NoteCategory)

                    _state.update {
                        it.copy(
                            currentCategoryId = if(currentId == 0) newId.toInt() else currentId,
                            categoryName = "",


                        )
                    }
                }

                //TODO: Если не будет сбрасываться флаг "isAddingCategory"
                // после добавления категории или же будут иные проблемы,
                // то тут обновить состояние вручную

            }
            is NoteIntent.SetCategoryName -> {
                _state.update {
                    it.copy(categoryName = intent.categoryName)
                }


            }

            NoteIntent.DisplayCategoryScreen -> {
                _state.update {
                    it.copy(isAddingCategory = true)
                }
            }
            NoteIntent.HideCategoryScreen -> {
                _state.update {
                    it.copy(isAddingCategory = false)
                }
            }

            is NoteIntent.SetCurrentCategoryId -> {
                _state.update {
                    it.copy(
                        currentCategoryId = intent.currentCategoryId,

                    )
                }
            }

            NoteIntent.SetCategoryIsPinned -> {
                _state.update {
                    it.copy(categoryIsPinned = true)
                }
            }
        }
    }
}