package com.helsinkiwizard.cointoss.di

import android.content.Context
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.data.room.CoinTossDatabase
import com.helsinkiwizard.core.BaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideRepository(@ApplicationContext context: Context, database: CoinTossDatabase): Repository {
        return Repository(context, database)
    }

    @Provides
    fun provideBaseRepository(@ApplicationContext context: Context, database: CoinTossDatabase): BaseRepository {
        return Repository(context, database)
    }
}
