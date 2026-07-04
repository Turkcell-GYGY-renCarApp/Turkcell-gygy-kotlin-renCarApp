package com.turkcell.rencarapp.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencarapp.data.license.LicenseRepository
import com.turkcell.rencarapp.ui.contract.LicenseEffect
import com.turkcell.rencarapp.ui.contract.LicenseIntent
import com.turkcell.rencarapp.ui.contract.LicenseState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenseViewModel @Inject constructor(
    private val licenseRepository: LicenseRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(LicenseState())
    val state: StateFlow<LicenseState> = _state.asStateFlow()

    private val _effect = Channel<LicenseEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LicenseIntent) {
        when (intent) {
            is LicenseIntent.GetStatus -> {
                getLicenseStatus()
            }
            is LicenseIntent.FrontImageChanged -> {
                val bitmap = decodeUriToBitmap(intent.uri)
                _state.value = _state.value.copy(
                    frontImageUri = intent.uri,
                    frontBitmap = bitmap
                )
            }
            is LicenseIntent.BackImageChanged -> {
                val bitmap = decodeUriToBitmap(intent.uri)
                _state.value = _state.value.copy(
                    backImageUri = intent.uri,
                    backBitmap = bitmap
                )
            }
            is LicenseIntent.UploadLicense -> {
                uploadLicense()
            }
            is LicenseIntent.ClearError -> {
                _state.value = _state.value.copy(
                    statusError = null,
                    uploadError = null
                )
            }
        }
    }

    private fun getLicenseStatus() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isStatusLoading = true, statusError = null)
            try {
                val response = licenseRepository.getLicenseStatus()
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isStatusLoading = false,
                        statusResponse = response.body()!!
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Ehliyet durumu alınamadı"
                    _state.value = _state.value.copy(
                        isStatusLoading = false,
                        statusError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isStatusLoading = false,
                    statusError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun uploadLicense() {
        val frontUri = _state.value.frontImageUri
        val backUri = _state.value.backImageUri

        if (frontUri == null || backUri == null) {
            _state.value = _state.value.copy(uploadError = "Lütfen hem ön hem de arka yüz fotoğraflarını seçin")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isUploading = true, uploadError = null, uploadSuccess = false)
            try {
                val frontBytes = getBytesFromUri(frontUri)
                val backBytes = getBytesFromUri(backUri)

                if (frontBytes == null || backBytes == null) {
                    _state.value = _state.value.copy(isUploading = false, uploadError = "Dosyalar okunurken bir hata oluştu")
                    return@launch
                }

                val frontMimeType = context.contentResolver.getType(frontUri) ?: "image/jpeg"
                val backMimeType = context.contentResolver.getType(backUri) ?: "image/jpeg"

                val response = licenseRepository.uploadLicense(
                    frontBytes = frontBytes,
                    frontFileName = "front.jpg",
                    frontMimeType = frontMimeType,
                    backBytes = backBytes,
                    backFileName = "back.jpg",
                    backMimeType = backMimeType
                )

                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isUploading = false,
                        uploadSuccess = true
                    )
                    _effect.send(LicenseEffect.NavigateToSelfie)
                    getLicenseStatus() // Refresh status
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Ehliyet yüklenemedi"
                    _state.value = _state.value.copy(
                        isUploading = false,
                        uploadError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isUploading = false,
                    uploadError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun decodeUriToBitmap(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getBytesFromUri(uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseError(jsonError: String): String {
        return try {
            val jsonObject = org.json.JSONObject(jsonError)
            jsonObject.optString("message", "Bir hata oluştu")
        } catch (e: Exception) {
            "Bir hata oluştu"
        }
    }
}
