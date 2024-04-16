package com.helsinkiwizard.cointoss.di

import android.content.Context
import androidx.room.Room
import com.helsinkiwizard.cointoss.Constants.DATABASE_NAME
import com.helsinkiwizard.cointoss.data.room.CoinTossDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): CoinTossDatabase {
        return Room.databaseBuilder(context, CoinTossDatabase::class.java, DATABASE_NAME)
            .build()
    }
}