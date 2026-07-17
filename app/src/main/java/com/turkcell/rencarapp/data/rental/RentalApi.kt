package com.turkcell.rencarapp.data.rental

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RentalApi {

    @GET("rentals/{id}")
    suspend fun getRentalDetails(
        @Path("id") id: String
    ): Response<RentalResponseDto>

    @POST("rentals/{id}/pay")
    suspend fun payRental(
        @Path("id") id: String,
        @Body request: PayRentalDto
    ): Response<PayRentalResponseDto>
}
