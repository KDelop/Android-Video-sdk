package com.developer.cardscansdkdemo.Service

import com.developer.cardscansdkdemo.Models.SessionResponse
import retrofit2.Call
import retrofit2.http.POST

interface Api {
    @POST("session")
    fun session():Call<SessionResponse>
}