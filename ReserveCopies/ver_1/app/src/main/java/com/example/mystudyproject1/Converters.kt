package com.example.mystudyproject1

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, JsonSerializer<Uri> { src, _, _ ->
            JsonPrimitive(src.toString())
        })
        .registerTypeAdapter(Uri::class.java, JsonDeserializer<Uri> { json, _, _ ->
            if (json.isJsonObject) {
                val obj = json.asJsonObject
                if (obj.has("uriString")) {
                    Uri.parse(obj.get("uriString").asString)
                } else {
                    Uri.parse("")
                }
            } else {
                Uri.parse(json.asString)
            }
        })
        .create()

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun fromUriList(uris: List<Uri>?): String? {
        return gson.toJson(uris?.map { it.toString() })
    }

    @TypeConverter
    fun toUriList(urisString: String?): List<Uri>? {
        val type = object : TypeToken<List<String>>() {}.type
        val list: List<String>? = gson.fromJson(urisString, type)
        return list?.map { Uri.parse(it) }
    }

    @TypeConverter
    fun fromContact(contact: Contact?): String? {
        return gson.toJson(contact)
    }

    @TypeConverter
    fun toContact(contactString: String?): Contact? {
        return gson.fromJson(contactString, Contact::class.java)
    }
}
