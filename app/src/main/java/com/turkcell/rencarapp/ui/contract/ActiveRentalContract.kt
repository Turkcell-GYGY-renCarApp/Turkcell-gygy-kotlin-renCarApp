package com.turkcell.rencarapp.ui.contract

import androidx.compose.runtime.Stable
import com.turkcell.rencarapp.data.rental.ActiveRentalResponseDto
import com.turkcell.rencarapp.data.rental.FinishRentalResponseDto
import com.turkcell.rencarapp.data.rental.VehiclePoint

@Stable
data class ActiveRentalState(
    val rentalId: String = "",
    val activeRental: ActiveRentalResponseDto? = null,
    val carLocation: VehiclePoint? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEndingRental: Boolean = false,
    val endedRentalSummary: FinishRentalResponseDto? = null
)

sealed interface ActiveRentalIntent {
    data class LoadActiveRental(val rentalId: String) : ActiveRentalIntent
    object EndRental : ActiveRentalIntent
    object ClearError : ActiveRentalIntent
}

sealed interface ActiveRentalEffect {
    data class NavigateToPaymentSummary(val rentalId: String) : ActiveRentalEffect
    data class ShowError(val message: String) : ActiveRentalEffect
}
