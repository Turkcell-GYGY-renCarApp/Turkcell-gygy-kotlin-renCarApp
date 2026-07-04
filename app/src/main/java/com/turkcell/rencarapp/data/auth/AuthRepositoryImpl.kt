package com.turkcell.rencarapp.data.auth

import com.turkcell.rencarapp.data.preferences.AuthPreferencesRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val authPreferencesRepository: AuthPreferencesRepository
) : AuthRepository {

    override val accessToken: Flow<String?> = authPreferencesRepository.accessToken
    override val refreshToken: Flow<String?> = authPreferencesRepository.refreshToken
    override val userRole: Flow<String?> = authPreferencesRepository.userRole
    override val userPhone: Flow<String?> = authPreferencesRepository.userPhone
    override val userFullName: Flow<String?> = authPreferencesRepository.userFullName

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): Response<AuthResponse> {
        return authApi.register(RegisterRequest(email, password, fullName, phone))
    }

    override suspend fun login(phone: String): Response<OtpResponse> {
        return authApi.login(OtpRequest(phone))
    }

    override suspend fun verifyOtp(phone: String, code: String): Response<AuthResponse> {
        return authApi.verifyOtp(VerifyOtpRequest(phone, code))
    }

    override suspend fun logout(): Response<LogoutResponse> {
        return authApi.logout()
    }

    override suspend fun getMe(): Response<UserDto> {
        return authApi.getMe()
    }

    override suspend fun saveAuthData(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        phone: String?,
        fullName: String,
        role: String
    ) {
        authPreferencesRepository.saveAuthData(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            email = email,
            phone = phone ?: "",
            fullName = fullName,
            role = role
        )
    }

    override suspend fun clearAuthData() {
        authPreferencesRepository.clearAuthData()
    }

    override suspend fun updateRole(role: String) {
        authPreferencesRepository.updateRole(role)
    }
}
