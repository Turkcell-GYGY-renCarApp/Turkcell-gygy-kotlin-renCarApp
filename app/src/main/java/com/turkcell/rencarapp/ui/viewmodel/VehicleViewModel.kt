package com.turkcell.rencarapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencarapp.data.vehicle.VehicleRepository
import com.turkcell.rencarapp.data.vehicle.VehicleResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _vehicles = MutableStateFlow<List<VehicleResponseDto>>(emptyList())
    val vehicles: StateFlow<List<VehicleResponseDto>> = _vehicles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchVehicles(segment: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            Log.d("VehicleViewModel", "Fetching vehicles for segment: $segment")
            val result = vehicleRepository.getVehicles(segment = segment, includeBusy = "true")
            result.onSuccess { list ->
                Log.d("VehicleViewModel", "Successfully fetched ${list.size} vehicles")
                _vehicles.value = list
            }.onFailure { exception ->
                Log.e("VehicleViewModel", "Error fetching vehicles: ${exception.message}", exception)
                _error.value = exception.message ?: "Araçlar yüklenirken bir hata oluştu"
                
                // Kullanıcı rolü PENDING olduğu için 403 alınması veya servis kesintisi durumlarında
                // kullanıcı deneyimini bozmamak adına haritayı mockup dummy araçlarla besliyoruz.
                val fallbackList = getDummyVehiclesForSegment(segment)
                Log.d("VehicleViewModel", "Falling back to ${fallbackList.size} dummy vehicles")
                _vehicles.value = fallbackList
            }
            _isLoading.value = false
        }
    }

    private fun getDummyVehiclesForSegment(segment: String?): List<VehicleResponseDto> {
        val allDummies = listOf(
            VehicleResponseDto(
                id = "dummy1", plate = "34 ABC 123", brand = "Opel", model = "Corsa",
                type = "HATCHBACK", pricePerDay = 1200.0, pricePerMinute = 28.0, pricePerHour = 150.0,
                fuelPercent = 85.0, rangeKm = 350.0, transmission = "AUTOMATIC", seats = 5,
                segment = "ECONOMY", status = "AVAILABLE", latitude = 40.9945, longitude = 29.0125,
                createdAt = "", updatedAt = ""
            ),
            VehicleResponseDto(
                id = "dummy2", plate = "34 XYZ 789", brand = "BMW", model = "320i",
                type = "SEDAN", pricePerDay = 2500.0, pricePerMinute = 38.0, pricePerHour = 300.0,
                fuelPercent = 60.0, rangeKm = 240.0, transmission = "AUTOMATIC", seats = 5,
                segment = "COMFORT", status = "AVAILABLE", latitude = 40.9985, longitude = 29.0295,
                createdAt = "", updatedAt = ""
            ),
            VehicleResponseDto(
                id = "dummy3", plate = "34 DEF 456", brand = "Renault", model = "Clio",
                type = "HATCHBACK", pricePerDay = 1100.0, pricePerMinute = 32.0, pricePerHour = 140.0,
                fuelPercent = 90.0, rangeKm = 400.0, transmission = "MANUAL", seats = 5,
                segment = "ECONOMY", status = "AVAILABLE", latitude = 40.9855, longitude = 29.0255,
                createdAt = "", updatedAt = ""
            ),
            VehicleResponseDto(
                id = "dummy4", plate = "34 MNO 321", brand = "Hyundai", model = "Tucson",
                type = "SUV", pricePerDay = 2200.0, pricePerMinute = 26.0, pricePerHour = 260.0,
                fuelPercent = 45.0, rangeKm = 180.0, transmission = "AUTOMATIC", seats = 5,
                segment = "SUV", status = "AVAILABLE", latitude = 40.9815, longitude = 29.0145,
                createdAt = "", updatedAt = ""
            )
        )
        return if (segment == null) {
            allDummies
        } else {
            allDummies.filter { it.segment == segment }
        }
    }
}
