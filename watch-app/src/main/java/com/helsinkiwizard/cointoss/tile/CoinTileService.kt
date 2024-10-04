package com.helsinkiwizard.cointoss.tile

import android.graphics.Bitmap
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import com.helsinkiwizard.core.utils.toBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@OptIn(ExperimentalHorologistApi::class)
@AndroidEntryPoint
class CoinTileService : SuspendingTileService() {

    companion object {
        private const val SELECTED_COIN = "selected_coin"
        private const val SCALED_BITMAP_SIZE = 500
        private const val ICON_SIZE = 50f
        private const val ICON_BOTTOM_PADDING = 8f
    }

    @Inject
    lateinit var repo: Repository

    private lateinit var coinTypeFlow: Flow<CoinType>
    private lateinit var resourceVersionFlow: Flow<Int>
    private lateinit var customCoinFlow: Flow<CustomCoinUiModel?>

    override fun onCreate() {
        super.onCreate()
        coinTypeFlow = repo.getCoinType
        resourceVersionFlow = repo.getResourceVersion
        customCoinFlow = repo.getCustomCoin
    }

    override suspend fun resourcesRequest(
        requestParams: ResourcesRequest
    ): ResourceBuilders.Resources {
        val headsImageResource = if (coinTypeFlow.get() == CoinType.CUSTOM) {
            val bitmap = customCoinFlow.get().headsUri.toBitmap(applicationContext)
            bitmapToImageResource(bitmap)
        } else {
            drawableResToImageResource(coinTypeFlow.get().heads)
        }

        val resourceVersion = resourceVersionFlow.get()
        return ResourceBuilders.Resources.Builder().setVersion(resourceVersion.toString())
            .addIdToImageMapping(
                SELECTED_COIN,
                headsImageResource
            )
            .build()
    }

    private fun bitmapToImageResource(bitmap: Bitmap?): ImageResource {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return ImageResource.Builder()
            .setInlineResource(
                ResourceBuilders.InlineImageResource.Builder()
                    .setData(byteArray)
                    .setWidthPx(SCALED_BITMAP_SIZE)
                    .setHeightPx(SCALED_BITMAP_SIZE)
                    .build()
            )
            .build()
    }

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        val dimens = requestParams.deviceConfiguration

        val singleTileTimeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(tileLayout(dimens))
                            .build()
                    )
                    .build()
            )
            .build()

        // Bump the version number to refresh the coin image
        var resourceVersion = resourceVersionFlow.get()
        resourceVersion++
        repo.setResourceVersion(resourceVersion)

        return Tile.Builder()
            .setResourcesVersion(resourceVersion.toString())
            .setTileTimeline(singleTileTimeline)
            .build()
    }

    private fun tileLayout(deviceParams: DeviceParametersBuilders.DeviceParameters): LayoutElementBuilders.LayoutElement {
        val clickable = launchActivityClickable("coin_button", openCoin())
        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .addContent(
                LayoutElementBuilders.Image.Builder()
                    .apply {
                        setResourceId(SELECTED_COIN)
                        setModifiers(
                            ModifiersBuilders.Modifiers.Builder().setClickable(clickable).build()
                        )
                        setHeight(dp(deviceParams.screenHeightDp.toFloat()))
                        setWidth(dp(deviceParams.screenWidthDp.toFloat()))
                    }.build()
            ).build()
    }

    private suspend fun <T> Flow<T>.get() = this.filterNotNull().first()
}
