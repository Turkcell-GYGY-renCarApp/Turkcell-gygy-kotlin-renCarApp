package com.turkcell.rencarapp.data.vehicle

interface VehicleRepository {
    suspend fun getVehicles(
        type: String? = null,
        segment: String? = null,
        includeBusy: String? = null
    ): Result<List<VehicleResponseDto>>

    suspend fun getVehicle(id: String): Result<VehicleResponseDto>
}
