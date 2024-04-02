package com.helsinkiwizard.cointoss.data.room

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.helsinkiwizard.cointoss.ui.model.CustomCoinUiModel

@Entity(tableName = "custom_coin")
class CustomCoin(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "heads") val heads: String,
    @ColumnInfo(name = "tails") val tails: String,
    @ColumnInfo(name = "name") val name: String,
) {
    fun toUiModel(): CustomCoinUiModel {
        return CustomCoinUiModel(
            id = id,
            headsUri = Uri.parse(heads),
            tailsUri = Uri.parse(tails),
            name = name
        )
    }
}