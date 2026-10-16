package com.example.mystudyproject1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@SuppressLint("SuspiciousIndentation")
@Composable
fun NoteElement(
    note: Note,
    onEdit: (Note) -> Unit,
    svScope: SharedTransitionScope,
    avScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onIntent: (NoteIntent) -> Unit,
    state: ScreenState,
    context: Context

) {
    with(svScope){
        ElevatedCard(
            onClick = {
                onEdit(note)
            },
            modifier = modifier
                .fillMaxWidth()
                .sharedElement(
                rememberSharedContentState(key = "${note.id}"),
                animatedVisibilityScope = avScope
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 6.dp
            ),

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Картинка если есть
                if(!note.pictureUris.isEmpty())
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(note.pictureUris) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                // Текст заметки
                Text(
                    text = note.text ?: "empty text",
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Контакт (если есть)
                if (note.contact != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.ContactPhone,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = note.contact.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Кнопки действий
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.categories.find {
                            it.categoryId == note.categoryId
                        } ?.categoryName ?: "",
                        style = MaterialTheme.typography.labelSmall
                    )
                    IconButton(
                        onClick = {
                            //onIntent(NoteIntent.ShareNote(note))
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, note.text)
                                type = "text/plain"
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)


                            }
                            context.startActivity(shareIntent)
                                  } ,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(18.dp)
                        )
                    }




                    IconButton(
                        onClick = {
                            onIntent(NoteIntent.DeleteNote(note))
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

}