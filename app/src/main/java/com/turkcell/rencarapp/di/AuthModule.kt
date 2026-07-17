package com.turkcell.rencarapp.di

import com.turkcell.rencarapp.data.auth.AuthRepository
import com.turkcell.rencarapp.data.auth.AuthRepositoryImpl
import com.turkcell.rencarapp.data.vehicle.VehicleRepository
import com.turkcell.rencarapp.data.vehicle.VehicleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLicenseRepository(
        licenseRepositoryImpl: com.turkcell.rencarapp.data.license.LicenseRepositoryImpl
    ): com.turkcell.rencarapp.data.license.LicenseRepository

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(
        vehicleRepositoryImpl: VehicleRepositoryImpl
    ): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindRentalRepository(
        rentalRepositoryImpl: com.turkcell.rencarapp.data.rental.RentalRepositoryImpl
    ): com.turkcell.rencarapp.data.rental.RentalRepository

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        cardRepositoryImpl: com.turkcell.rencarapp.data.card.CardRepositoryImpl
    ): com.turkcell.rencarapp.data.card.CardRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        walletRepositoryImpl: com.turkcell.rencarapp.data.wallet.WalletRepositoryImpl
    ): com.turkcell.rencarapp.data.wallet.WalletRepository
}
