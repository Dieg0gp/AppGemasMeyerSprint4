package com.example.gemasmeyerapp_ver2.Data

import com.google.gson.JsonArray
import retrofit2.http.GET

interface solicitudesPublicaciones {
    @GET("UsuarioPublicacion/ObtenerPublicaciones")
    suspend fun obtenerPublicaciones(): JsonArray
}