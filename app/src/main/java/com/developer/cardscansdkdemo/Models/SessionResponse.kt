package com.developer.cardscansdkdemo.Models

import com.google.gson.annotations.SerializedName

data class  SessionResponse(
    val session: String?,
    val session_id: String?,
    val message: String?,
    val type: String?,
    val code: Int?
    )