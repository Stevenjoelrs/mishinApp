package com.mishin.core.data.di

import com.mishin.core.data.repository.*
import com.mishin.core.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds repository interfaces to their implementations.
 * This is the only place where the data layer's concrete classes are exposed.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        impl: InventoryRepositoryImpl
    ): InventoryRepository

    @Binds
    @Singleton
    abstract fun bindMenuRepository(
        impl: MenuRepositoryImpl
    ): MenuRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindReservationRepository(
        impl: ReservationRepositoryImpl
    ): ReservationRepository

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        impl: GameRepositoryImpl
    ): GameRepository

    @Binds
    @Singleton
    abstract fun bindCatRepository(
        impl: CatRepositoryImpl
    ): CatRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(
        impl: AlertRepositoryImpl
    ): AlertRepository

    @Binds
    @Singleton
    abstract fun bindSupplierRepository(
        impl: SupplierRepositoryImpl
    ): SupplierRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        impl: SyncRepositoryImpl
    ): SyncRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
