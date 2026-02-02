
package com.guerramath.safetyapp.auth.data.dto

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("success")
    val success: Boolean = true
)
