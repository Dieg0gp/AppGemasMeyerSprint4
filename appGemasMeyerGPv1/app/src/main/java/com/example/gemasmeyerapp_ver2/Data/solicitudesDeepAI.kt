package com.example.gemasmeyerapp_ver2.Data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
data class DeepAIRequest(val text: String)

data class DeepAIResponse(val output: String)
interface solicitudesDeepAI {
    @POST("api/text2img")
    fun generateImage(@Header("api-key") apiKey: String, @Body request: DeepAIRequest): Call<DeepAIResponse>
}