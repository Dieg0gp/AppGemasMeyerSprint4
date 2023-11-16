package com.example.gemasmeyerapp_ver2.Data

import com.example.gemasmeyerapp_ver2.Models.Pedido
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DeepIARepository : solicitudesDeepAI {
    private lateinit var deepAIAPI: solicitudesDeepAI

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constantes.URL_DEEP_AI_API) // Reemplaza con la URL base de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        deepAIAPI = retrofit.create(solicitudesDeepAI::class.java)
    }
    override fun generateImage(apiKey: String, request: DeepAIRequest): Call<DeepAIResponse> {
        return deepAIAPI.generateImage(apiKey,request)
    }
}