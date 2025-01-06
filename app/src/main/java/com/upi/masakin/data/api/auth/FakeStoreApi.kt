package com.upi.masakin.data.api.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FakeStoreApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}