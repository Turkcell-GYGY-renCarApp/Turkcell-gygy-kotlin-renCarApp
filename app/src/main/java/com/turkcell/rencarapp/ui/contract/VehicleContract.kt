package com.turkcell.rencarapp.ui.contract

import androidx.compose.runtime.Stable
import com.turkcell.rencarapp.data.vehicle.VehicleResponseDto

@Stable
data class VehicleState(
    val vehicles: List<VehicleResponseDto> = emptyList(),
    val selectedVehicle: VehicleResponseDto? = null,
    val isLoading: Boolean = false,
    val isDetailLoading: Boolean = false,
    val error: String? = null
)

sealed interface VehicleIntent {
    data class FetchVehicles(val segment: String? = null) : VehicleIntent
    data class SelectVehicle(val vehicleId: String) : VehicleIntent
    object ClearSelectedVehicle : VehicleIntent
    object ClearError : VehicleIntent
}

sealed interface VehicleEffect {
    data class ShowError(val message: String) : VehicleEffect
}
