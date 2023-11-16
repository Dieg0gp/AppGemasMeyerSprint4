package com.example.gemasmeyerapp_ver2.Data

import com.example.gemasmeyerapp_ver2.Models.SubirImagen
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SubirImagenRepository: solicitudSubirImagen {

    private val subirImagenApi: solicitudSubirImagen

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constantes.URL_IMG_BB) // Reemplaza con la URL base de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        subirImagenApi = retrofit.create(solicitudSubirImagen::class.java)
    }

    override fun subirImagen(imgSubir: SubirImagen): Call<Boolean> {
        return subirImagenApi.subirImagen(imgSubir)
    }

}