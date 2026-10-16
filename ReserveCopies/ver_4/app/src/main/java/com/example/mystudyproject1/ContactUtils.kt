package com.example.mystudyproject1

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract

fun provider(uri: Uri?, contentResolver: ContentResolver, onContactUpdated: (Contact?) -> Unit) {

    if (uri != null) {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        )
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                contentResolver.query(
                    uri, projection, null, null, null
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                    val nameColumn =
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val uri = uri
                        onContactUpdated(Contact(id, name, uri))


                    }


                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

}