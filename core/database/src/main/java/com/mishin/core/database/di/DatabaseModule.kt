package com.mishin.core.database.di

import android.content.Context
import androidx.room.Room
import com.mishin.core.database.MishinDatabase
import com.mishin.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing the Room database and all DAOs as singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MishinDatabase {
        return Room.databaseBuilder(
            context,
            MishinDatabase::class.java,
            MishinDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(dropAllTables = true)  // Only for development; use proper migrations in production
            .build()
    }

    @Provides fun provideInventoryDao(db: MishinDatabase): InventoryDao = db.inventoryDao()
    @Provides fun provideMenuDao(db: MishinDatabase): MenuDao = db.menuDao()
    @Provides fun provideOrderDao(db: MishinDatabase): OrderDao = db.orderDao()
    @Provides fun provideReservationDao(db: MishinDatabase): ReservationDao = db.reservationDao()
    @Provides fun provideGameDao(db: MishinDatabase): GameDao = db.gameDao()
    @Provides fun provideCatDao(db: MishinDatabase): CatDao = db.catDao()
    @Provides fun provideAlertDao(db: MishinDatabase): AlertDao = db.alertDao()
    @Provides fun provideSyncQueueDao(db: MishinDatabase): SyncQueueDao = db.syncQueueDao()
    @Provides fun provideSupplierDao(db: MishinDatabase): SupplierDao = db.supplierDao()
}
