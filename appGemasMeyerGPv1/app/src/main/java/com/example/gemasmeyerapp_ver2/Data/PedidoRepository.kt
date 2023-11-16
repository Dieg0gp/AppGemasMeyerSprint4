package com.example.gemasmeyerapp_ver2.Data

import com.example.gemasmeyerapp_ver2.Models.Pedido
import com.example.gemasmeyerapp_ver2.Models.Peticion
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PedidoRepository : solicitudesPedidos {

    private val pedidoServicio: solicitudesPedidos

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constantes.URL_API) // Reemplaza con la URL base de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        pedidoServicio = retrofit.create(solicitudesPedidos::class.java)
    }

    override fun registrarPedido(pedido: Pedido): Call<Boolean> {
        return pedidoServicio.registrarPedido(pedido)
    }
    override fun registrarPeticion(peticion: Peticion): Call<Boolean> {
        return pedidoServicio.registrarPeticion(peticion)
    }
}