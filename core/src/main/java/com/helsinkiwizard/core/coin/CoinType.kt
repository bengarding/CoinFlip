package com.helsinkiwizard.core.coin

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.helsinkiwizard.core.R

enum class CoinType(
    val value: Int,
    @DrawableRes val heads: Int,
    @DrawableRes val tails: Int,
    @StringRes val nameRes: Int
) {
    CUSTOM(23, R.drawable.bitcoin_heads, R.drawable.bitcoin_tails, R.string.custom_coin),
    BITCOIN(1, R.drawable.bitcoin_heads, R.drawable.bitcoin_tails, R.string.bitcoin),
    CANADA(2, R.drawable.canada_heads, R.drawable.canada_tails, R.string.canada),
    CHINA(3, R.drawable.china_heads, R.drawable.china_tails, R.string.china),
    EURO(4, R.drawable.euro_heads, R.drawable.euro_tails, R.string.euro),
    INDIA(5, R.drawable.india_heads, R.drawable.india_tails, R.string.india),
    JAPAN(6, R.drawable.japan_heads, R.drawable.japan_tails, R.string.japan),
    THAILAND(7, R.drawable.thailand_heads, R.drawable.thailand_tails, R.string.thailand),
    TURKEY(8, R.drawable.turkey_heads, R.drawable.turkey_tails, R.string.turkey),
    UKRAINE(9, R.drawable.ukraine_heads, R.drawable.ukraine_tails, R.string.ukraine),
    UNITED_KINGDOM(10, R.drawable.uk_heads, R.drawable.uk_tails, R.string.united_kingdom),
    UNITED_STATES(11, R.drawable.usa_heads, R.drawable.usa_tails, R.string.united_states),
    UZBEKISTAN(12, R.drawable.uzbekistan_heads, R.drawable.uzbekistan_tails, R.string.uzbekistan),
    DIAPER(13, R.drawable.diaper_heads, R.drawable.diaper_tails, R.string.diaper_duty),
    KUWAIT(14, R.drawable.kuwait_heads, R.drawable.kuwait_tails, R.string.kuwait),
    SWEDEN(15, R.drawable.sweden_heads, R.drawable.sweden_tails, R.string.sweden),
    RUSSIA(16, R.drawable.russia_heads, R.drawable.russia_tails, R.string.russia),
    UAE(17, R.drawable.uae_heads, R.drawable.uae_tails, R.string.uae),
    SAUDI_ARABIA(18, R.drawable.saudi_arabia_heads, R.drawable.saudi_arabia_tails, R.string.saudi_arabia),
    JORDAN(19, R.drawable.jordan_heads, R.drawable.jordan_tails, R.string.jordan),
    AUSTRALIA(20, R.drawable.australia_heads, R.drawable.australia_tails, R.string.australia),
    MEXICO(21, R.drawable.mexico_heads, R.drawable.mexico_tails, R.string.mexico),
    SPAIN(22, R.drawable.spain_heads, R.drawable.spain_tails, R.string.spain),
    EGYPT(24, R.drawable.egypt_heads, R.drawable.egypt_tails, R.string.egypt),
    POLAND(25, R.drawable.poland_heads, R.drawable.poland_tails, R.string.poland),
    URUGUAY(26, R.drawable.uruguay_heads, R.drawable.uruguay_tails, R.string.uruguay),
    CZECH_REPUBLIC(27, R.drawable.czech_republic_heads, R.drawable.czech_republic_tails, R.string.czech_republic),
    IRAN(28, R.drawable.iran_heads, R.drawable.iran_tails, R.string.iran),
    NEW_ZEALAND(29, R.drawable.new_zealand_heads, R.drawable.new_zealand_tails, R.string.new_zealand),
    PIKE_PLACE(30, R.drawable.pike_place_heads, R.drawable.pike_place_tails, R.string.pike_place_market),
    EURO_2(31, R.drawable.euro_2_heads, R.drawable.euro_2_tails, R.string.euro),
    UNITED_KINGDOM_2(32, R.drawable.uk_2_heads, R.drawable.uk_2_tails, R.string.united_kingdom),
    AZERBAIJAN(33, R.drawable.azerbaijan_heads, R.drawable.azerbaijan_tails, R.string.azerbaijan),
    BRAZIL(34, R.drawable.brazil_heads, R.drawable.brazil_tails, R.string.brazil),
    CZECHOSLOVAKIA(35, R.drawable.czechoslovakia_heads, R.drawable.czechoslovakia_tails, R.string.czechoslovakia),
    DENMARK(36, R.drawable.denmark_heads, R.drawable.denmark_tails, R.string.denmark),
    GERMANY(37, R.drawable.germany_heads, R.drawable.germany_tails, R.string.germany),
    INDONESIA(38, R.drawable.indonesia_heads, R.drawable.indonesia_tails, R.string.indonesia),
    ISRAEL(39, R.drawable.israel_heads, R.drawable.israel_tails, R.string.israel),
    LEBANON(40, R.drawable.lebanon_heads, R.drawable.lebanon_tails, R.string.lebanon),
    NORWAY(41, R.drawable.norway_heads, R.drawable.norway_tails, R.string.norway),
    SWITZERLAND(42, R.drawable.switzerland_heads, R.drawable.switzerland_tails, R.string.switzerland),
    COLOMBIA(43, R.drawable.colombia_heads, R.drawable.colombia_tails, R.string.colombia),
    ;

    companion object {
        fun parse(value: Int): CoinType {
            return CoinType.entries.firstOrNull { it.value == value } ?: BITCOIN
        }
    }
}
