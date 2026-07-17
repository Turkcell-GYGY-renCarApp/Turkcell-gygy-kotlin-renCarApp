package com.turkcell.rencarapp.data.rental

import retrofit2.Response

interface RentalRepository {
    suspend fun getRentalDetails(id: String): Response<RentalResponseDto>
    suspend fun payRental(id: String, request: PayRentalDto): Response<PayRentalResponseDto>
}
