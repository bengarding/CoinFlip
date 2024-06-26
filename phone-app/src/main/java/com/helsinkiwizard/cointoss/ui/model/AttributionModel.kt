package com.helsinkiwizard.cointoss.ui.model

import com.helsinkiwizard.core.coin.CoinType

private const val UCoin = "uCoin.net"
private const val Amazon = "Amazon.com"

class AttributionModel(
    val coin: CoinType,
    val name: String,
    val source: String
)

object AttributionParams {
    val attributions = listOf(
        AttributionModel(
            coin = CoinType.DIAPER,
            name = "Huwane Us",
            source = Amazon
        ),
        AttributionModel(
            coin = CoinType.SWEDEN,
            name = "Jonathan(swe)",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.SAUDI_ARABIA,
            name = "agpanich",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.MEXICO,
            name = "Фанис 67",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.RUSSIA,
            name = "Sirdare61",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.SPAIN,
            name = "Фанис 67",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.URUGUAY,
            name = "Фанис 67",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.POLAND,
            name = "Фанис 67",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.TURKEY,
            name = "Рожден в СССР",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.CHINA,
            name = "Andra",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.EGYPT,
            name = "Monetka",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.NEW_ZEALAND,
            name = "Resurs",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.IRAN,
            name = "Руслан Николаевич",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.CZECH_REPUBLIC,
            name = "agpanich",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.UNITED_KINGDOM_2,
            name = "Safan",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.BRAZIL,
            name = "moedamoeda",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.NORWAY,
            name = "Khufu",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.COLOMBIA,
            name = "Khufu",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.AZERBAIJAN,
            name = "Фанис 67",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.ISRAEL,
            name = "Фанис 67",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.SWITZERLAND,
            name = "Фанис 67",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.INDONESIA,
            name = "Andra",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.DENMARK,
            name = "Дмитрий Казаков",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.LEBANON,
            name = "kirina",
            source = UCoin
        ),
        AttributionModel(
            coin = CoinType.CZECHOSLOVAKIA,
            name = "Zetko",
            source = UCoin
        ),
    )
}
