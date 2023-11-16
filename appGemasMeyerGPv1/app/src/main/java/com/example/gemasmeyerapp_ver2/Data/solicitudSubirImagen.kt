package com.example.gemasmeyerapp_ver2.Data

import com.example.gemasmeyerapp_ver2.Models.SubirImagen
import com.example.gemasmeyerapp_ver2.Models.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface solicitudSubirImagen {
    @FormUrlEncoded
    @POST("upload")
    fun subirImagen(@Body imgSubir: SubirImagen): Call<Boolean>
}