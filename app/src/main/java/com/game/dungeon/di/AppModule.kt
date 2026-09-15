package com.game.dungeon.di

import android.content.Context
import androidx.room.Room
import com.game.dungeon.analytics.AnalyticsManager
import com.game.dungeon.analytics.FirebaseAnalyticsManager
import com.game.dungeon.data.db.GameDatabase
import com.game.dungeon.data.repository.AuthRepository
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.data.repository.LeaderboardRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGameDatabase(@ApplicationContext context: Context): GameDatabase {
        return Room.databaseBuilder(
            context,
            GameDatabase::class.java,
            "game_database"
        )
        .addMigrations(
            GameDatabase.MIGRATION_9_10,
            GameDatabase.MIGRATION_10_11,
            GameDatabase.MIGRATION_11_12,
            GameDatabase.MIGRATION_12_13,
            GameDatabase.MIGRATION_13_14,
            GameDatabase.MIGRATION_14_15,
            GameDatabase.MIGRATION_15_16,
            GameDatabase.MIGRATION_16_17,
            GameDatabase.MIGRATION_17_18,
            GameDatabase.MIGRATION_18_19
        )
        .fallbackToDestructiveMigration(true) // Keep as safety, but explicit migrations prioritized
        .build()
    }

    @Provides
    @Singleton
    fun provideGameRepository(database: GameDatabase, leaderboardRepository: LeaderboardRepository): GameRepository {
        return GameRepository(database, leaderboardRepository)
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAnalyticsManager(firebaseAnalytics: FirebaseAnalytics): AnalyticsManager {
        return FirebaseAnalyticsManager(firebaseAnalytics)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = 
        FirebaseDatabase.getInstance("https://final-dungeon-8e349-default-rtdb.europe-west1.firebasedatabase.app/")

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepository(auth)

    @Provides
    @Singleton
    fun provideLeaderboardRepository(database: FirebaseDatabase): LeaderboardRepository = LeaderboardRepository(database)
}
