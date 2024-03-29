package com.helsinkiwizard.cointoss.di

import android.content.Context
import com.helsinkiwizard.cointoss.data.Repository
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
    fun provideRepository(@ApplicationContext context: Context): Repository {
        return Repository(context)
    }

    @Provides
    fun provideBaseRepository(@ApplicationContext context: Context): BaseRepository {
        return Repository(context)
    }
}
