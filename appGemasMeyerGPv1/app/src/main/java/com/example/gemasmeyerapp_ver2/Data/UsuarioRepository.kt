package com.example.gemasmeyerapp_ver2.Data

import com.example.gemasmeyerapp_ver2.Models.Usuario
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class UsuarioRepository : solicitudesUsuarios {
    private val usuariosApi: solicitudesUsuarios

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constantes.URL_API) // Reemplaza con la URL base de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        usuariosApi = retrofit.create(solicitudesUsuarios::class.java)
    }

    override suspend fun obtenerUsuarios(): JsonArray {
        return usuariosApi.obtenerUsuarios()
    }

    override fun registrarUsuario(usuario: Usuario): Call<Boolean> {
        return usuariosApi.registrarUsuario(usuario)
    }

}