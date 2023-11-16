package com.example.gemasmeyerapp_ver2.Data

import com.google.gson.JsonArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PublicacionesRepository : solicitudesPublicaciones {
    private val publicacionServicio: solicitudesPublicaciones

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constantes.URL_API) // Reemplaza con la URL base de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        publicacionServicio = retrofit.create(solicitudesPublicaciones::class.java)
    }
    override suspend fun obtenerPublicaciones(): JsonArray {
        return publicacionServicio.obtenerPublicaciones()
    }

}