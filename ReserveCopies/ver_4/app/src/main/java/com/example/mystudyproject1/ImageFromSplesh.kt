package com.example.mystudyproject1

import com.google.gson.annotations.SerializedName

data class ImageFromSplesh(
    val id: String,
    val width: Int? = null,
    val height: Int? = null,
    val color: String? = null,
    @SerializedName("blur_hash") val blurHash: String? = null,
    val description: String? = null,
    @SerializedName("alt_description") val altDescription: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val urls: Urls,
    val user: User? = null
)
