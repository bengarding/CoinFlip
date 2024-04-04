package com.helsinkiwizard.cointoss.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCoinDao {
    @Insert
    suspend fun insert(customCoin: CustomCoin)

    @Delete
    fun delete(customCoin: CustomCoin)

    @Query("SELECT * FROM custom_coin WHERE selected = 0 ORDER BY id DESC")
    fun getAllFlow(): Flow<List<CustomCoin>>

    @Query("SELECT * FROM custom_coin")
    fun getAll(): List<CustomCoin>

    @Query("SELECT * FROM custom_coin WHERE selected = 1 LIMIT 1")
    fun getSelectedCoin(): Flow<CustomCoin?>

    @Query("UPDATE custom_coin SET selected = 0")
    suspend fun deselectAllCoins()

    @Transaction
    suspend fun deselectThenInsert(customCoin: CustomCoin) {
        deselectAllCoins()
        insert(customCoin)
    }
}