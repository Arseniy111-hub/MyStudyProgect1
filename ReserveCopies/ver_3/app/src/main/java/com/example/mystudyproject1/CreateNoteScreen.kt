package com.example.mystudyproject1

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    onBack: () -> Unit,
    onCategory: () -> Unit,
    noteId: Long,
    context: Context,
    svScope: SharedTransitionScope,
    avScope: AnimatedVisibilityScope,
    onIntent: (NoteIntent) -> Unit,
    state: ScreenState

) {
val TAG = "CreateScreen"

LaunchedEffect(noteId) {
    onIntent(NoteIntent.LoadNote(noteId))
    Log.d(TAG, "DATA: ${state.text}")
}


    

    val contactLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { result ->
        onIntent(NoteIntent.SetContactUri(result))
        provider(result, context.contentResolver) { contact ->
            onIntent(NoteIntent.SetContact(contact))
        }
        if (result != null) {

            onIntent(NoteIntent.SetContactPin(true))
        }
    }
    val pictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { result ->
        if(result.isNotEmpty()){
            result.forEach { uri ->
                try{
                    context.contentResolver.takePersistableUriPermission (
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                catch (e: SecurityException){
                    Log.d(TAG, e.message.toString())
                }

            }
        }
        onIntent(NoteIntent.SetImages(result))
    }




with(svScope){
    Scaffold(
        modifier = Modifier.fillMaxSize().let{ modifier ->
            if(state.isAddingNote){
                modifier.sharedElement(
                    rememberSharedContentState(key = "$noteId"),
                    animatedVisibilityScope = avScope
                )
            }
            else{
                modifier.sharedElement(
                    rememberSharedContentState(key ="floating_action_button"),
                    avScope
                )
            }
        },
        topBar = {

            TopAppBar(

                modifier = Modifier.fillMaxWidth(),


                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    subtitleContentColor = MaterialTheme.colorScheme.onPrimary
                ), navigationIcon = {
                    Row(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        IconButton(
                            {
                                onBack()
                            }, modifier = Modifier.size(48.dp)

                        ) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack,
                                modifier = Modifier.size(20.dp),
                                contentDescription = "Back button",


                                )

                        }
                    }

                },


                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.create_note),
                            Modifier.padding(20.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,

                            )
                    }

                }


            )

        }) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)


        ) {
            Log.d(TAG + "BeforeTextField", "DATA: ${state.text}")
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = state.text,
                onValueChange = { text -> onIntent(NoteIntent.SetText(text)) }

            )
            Log.d(TAG + "AfterTextField", "DATA: ${state.text}")
            ConstraintLayout {
                val (apply, contact, pictures, category) = createRefs()


                Button(
                    onClick = {
                        onIntent(NoteIntent.SaveNote)
                        onBack()
                    },
                    modifier = Modifier.constrainAs(apply) {
                        end.linkTo(contact.start, margin = 5.dp)

                    }, enabled = state.text.isNotEmpty()
                ) {
                    Text(
                        text = stringResource(R.string.apply)
                    )


                }
                if (!state.contactIsPin) {
                    OutlinedButton(onClick = {
                        contactLauncher.launch(null)

                    }, modifier = Modifier.constrainAs(contact) {
                        end.linkTo(pictures.start, margin = 5.dp)
                    }) {
                        Row {
                            Icon(
                                Icons.Default.ContactPhone, contentDescription = "Merge contact"
                            )

                        }

                    }


                } else {
                    Button(onClick = {
                        contactLauncher.launch(null)
                    }, modifier = Modifier.constrainAs(contact) {
                        end.linkTo(pictures.start, margin = 5.dp)
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ContactPhone, contentDescription = "Merge contact"
                            )

                        }
                    }
                }
                state.apply {
                    if (pictureIsPin && pictureUris.isNotEmpty()) Button(
                        onClick = {
                        pictureLauncher.launch(PickVisualMediaRequest())
                        onIntent(NoteIntent.SetPicturePin(!pictureIsPin))
                    }, modifier = Modifier.constrainAs(pictures) {
                        end.linkTo(category.start)
                    }

                    ) {
                        Icon(
                            Icons.Default.Image, contentDescription = "picture is pin "
                        )
                    }
                    else OutlinedButton(onClick = {
                        pictureLauncher.launch(PickVisualMediaRequest())
                        onIntent(NoteIntent.SetPicturePin(!pictureIsPin))

                    }, modifier = Modifier.constrainAs(pictures) {
                        end.linkTo(category.start)
                    }) {
                        Icon(
                            Icons.Default.ImageSearch, contentDescription = "picture is not pin "
                        )
                    }
                }
                if(state.currentCategoryId != 0){
                    Button(
                        onClick = {
                            onCategory()

                        },
                        modifier = Modifier.constrainAs(category){
                            start.linkTo(parent.end)
                        }
                    ) {
                        Icon(
                            Icons.Filled.Category,
                            "Filled category icon"
                        )
                    }
                }
                else{
                    OutlinedIconButton(
                        onClick = {
                            onCategory()

                        },
                        modifier = Modifier.constrainAs(category){
                            start.linkTo(parent.end)
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Category,
                            "Outlined category icon"
                        )
                    }
                }

            }


        }
    }
}

}