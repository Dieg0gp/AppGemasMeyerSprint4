package com.example.gemasmeyerapp_ver2.Models

import retrofit2.http.Field

data class SubirImagen(
    @Field("key") val apiKey: String,
    @Field("image") val imageUrl: String
)