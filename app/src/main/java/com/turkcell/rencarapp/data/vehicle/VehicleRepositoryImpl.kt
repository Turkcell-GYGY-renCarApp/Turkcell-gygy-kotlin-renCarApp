package com.turkcell.rencarapp.data.vehicle

import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleApi: VehicleApi
) : VehicleRepository {
    override suspend fun getVehicles(
        type: String?,
        segment: String?,
        includeBusy: String?
    ): Result<List<VehicleResponseDto>> {
        return try {
            val response = vehicleApi.getVehicles(type = type, segment = segment, includeBusy = includeBusy)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
