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
    @ColumnInfo(name = "selected") val selected: Boolean,
) {
    fun toUiModel(): CustomCoinUiModel {
        return CustomCoinUiModel(id, Uri.parse(heads), Uri.parse(tails), name, selected)
    }
}