package com.turkcell.rencarapp.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencarapp.data.reservation.ReservationRepository
import com.turkcell.rencarapp.data.vehicle.VehicleRepository
import com.turkcell.rencarapp.ui.contract.ReservationEffect
import com.turkcell.rencarapp.ui.contract.ReservationIntent
import com.turkcell.rencarapp.ui.contract.ReservationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationState())
    val state: StateFlow<ReservationState> = _state.asStateFlow()

    private val _effect = Channel<ReservationEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ReservationIntent) {
        when (intent) {
            is ReservationIntent.LoadVehicle -> {
                loadVehicle(intent.vehicleId)
            }
            is ReservationIntent.PhotoSelected -> {
                val updatedPhotos = _state.value.photos.toMutableMap()
                updatedPhotos[intent.direction] = intent.uri
                _state.value = _state.value.copy(photos = updatedPhotos)
            }
            is ReservationIntent.PlanSelected -> {
                _state.value = _state.value.copy(selectedPlan = intent.plan)
            }
            is ReservationIntent.AgreedToTermsChanged -> {
                _state.value = _state.value.copy(isAgreedToTerms = intent.isChecked)
            }
            is ReservationIntent.SubmitReservation -> {
                submitReservation()
            }
            is ReservationIntent.ClearError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    private fun loadVehicle(vehicleId: String) {
        if (_state.value.vehicleId == vehicleId && _state.value.vehicle != null) return
        
        viewModelScope.launch {
            _state.value = _state.value.copy(vehicleId = vehicleId, isLoadingVehicle = true, error = null)
            val result = vehicleRepository.getVehicle(vehicleId)
            result.onSuccess { vehicle ->
                _state.value = _state.value.copy(vehicle = vehicle, isLoadingVehicle = false)
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    error = exception.message ?: "Araç bilgileri yüklenemedi",
                    isLoadingVehicle = false
                )
            }
        }
    }

    private fun submitReservation() {
        val vehicleId = _state.value.vehicleId
        if (vehicleId.isBlank()) {
            _state.value = _state.value.copy(error = "Geçersiz araç bilgisi")
            return
        }
        if (!_state.value.isAgreedToTerms) {
            _state.value = _state.value.copy(error = "Lütfen kullanım şartlarını kabul edin")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            val result = reservationRepository.createReservation(vehicleId)
            result.onSuccess { reservation ->
                _state.value = _state.value.copy(successReservation = reservation, isSubmitting = false)
                _effect.send(ReservationEffect.NavigateToSuccess)
            }.onFailure { exception ->
                val errMsg = exception.message ?: "Rezervasyon tamamlanamadı"
                _state.value = _state.value.copy(error = errMsg, isSubmitting = false)
                _effect.send(ReservationEffect.ShowError(errMsg))
            }
        }
    }
}
