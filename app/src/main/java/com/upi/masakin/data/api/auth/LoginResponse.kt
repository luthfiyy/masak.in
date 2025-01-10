package com.upi.masakin.data.api.auth

data class LoginResponse(
    val token: String,
    val username: String? = null
)
