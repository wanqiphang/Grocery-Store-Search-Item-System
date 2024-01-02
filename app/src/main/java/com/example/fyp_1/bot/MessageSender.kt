package com.example.fyp_1.bot

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageSender {
    @POST("webhook")
    fun messageSender(@Body userMessage:MessageClass): Call<ArrayList<BotResponse>>
}