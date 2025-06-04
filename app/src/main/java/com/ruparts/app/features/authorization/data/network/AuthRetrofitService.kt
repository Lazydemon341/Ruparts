package com.ruparts.app.features.authorization.data.network

import com.ruparts.app.features.authorization.data.network.model.AuthRequest
import com.ruparts.app.features.authorization.data.network.model.AuthResponse
import com.ruparts.app.features.menu.network.model.GetUserResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthRetrofitService {
    @Headers("X-App-Secret: 0c6420f4f5c20fb7b26adffbc59eff3b")
    @POST("user/login_by_code")
    suspend fun loginByCode(@Body request: AuthRequest): AuthResponse

    suspend fun getUser() : GetUserResponseDto
}
