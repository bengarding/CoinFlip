package com.helsinkiwizard.cointoss.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CustomCoin::class], version = 1)
abstract class CoinTossDatabase : RoomDatabase() {
    abstract fun customCoinDao(): CustomCoinDao
}
