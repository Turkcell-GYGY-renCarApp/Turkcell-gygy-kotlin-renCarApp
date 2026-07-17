package com.turkcell.rencarapp.data.rental

import retrofit2.Response
import javax.inject.Inject

class RentalRepositoryImpl @Inject constructor(
    private val rentalApi: RentalApi
) : RentalRepository {

    override suspend fun getRentalDetails(id: String): Response<RentalResponseDto> {
        return rentalApi.getRentalDetails(id)
    }

    override suspend fun payRental(id: String, request: PayRentalDto): Response<PayRentalResponseDto> {
        return rentalApi.payRental(id, request)
    }
}
