package com.helsinkiwizard.cointoss.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CustomCoinDao {
    @Insert
    fun insert(customCoin: CustomCoin)

    @Delete
    fun delete(customCoin: CustomCoin)

    @Query("SELECT * FROM custom_coin")
    fun getAll() : List<CustomCoin>
}