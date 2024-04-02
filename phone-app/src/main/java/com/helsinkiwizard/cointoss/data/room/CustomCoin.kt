package com.helsinkiwizard.cointoss.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_coin")
class CustomCoin(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "heads") val heads: String,
    @ColumnInfo(name = "tails") val tails: String,
    @ColumnInfo(name = "name") val name: String,
)