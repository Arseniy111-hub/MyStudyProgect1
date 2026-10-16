package com.example.mystudyproject1

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApi {
    @GET("search/photos")
    suspend fun getImages(
        @Query("query") query: String,
        @Query("client_id") clientId: String = "Pgoa2eIE2TSdn8PISD87j2v9-RY2-7WLJ3kft8YM4-Y"
    ): Response<UnspleshResponse>
}
