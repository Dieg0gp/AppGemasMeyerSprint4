package com.example.gemasmeyerapp_ver2.Data

import com.example.gemasmeyerapp_ver2.Models.Pedido
import com.example.gemasmeyerapp_ver2.Models.Peticion
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface solicitudesPedidos {
    @POST("UsuarioPedido/RegistrarPedido")
    fun registrarPedido(@Body pedido: Pedido): Call<Boolean>
    //Pedido joya personalizada
    @POST("UsuarioPeticion/RegistrarPeticionMovil")
    fun registrarPeticion(@Body peticion: Peticion): Call<Boolean>
}