package com.example.gemasmeyerapp_ver2.Data

import com.example.gemasmeyerapp_ver2.Models.Usuario
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface solicitudesUsuarios {
    @GET("Usuario/ObtenerUsuarios")
    suspend fun obtenerUsuarios() : JsonArray
    @POST("Usuario/RegistrarUsuario")
    fun registrarUsuario(@Body usuario: Usuario): Call<Boolean>
}