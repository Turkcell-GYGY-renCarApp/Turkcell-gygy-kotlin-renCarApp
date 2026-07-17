package com.turkcell.rencarapp.data.reservation

interface ReservationRepository {
    suspend fun createReservation(vehicleId: String): Result<ReservationResponseDto>
}
