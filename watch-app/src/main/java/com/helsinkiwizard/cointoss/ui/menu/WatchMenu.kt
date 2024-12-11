package com.helsinkiwizard.cointoss.ui.menu

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.navigation.NavRoute
import com.helsinkiwizard.cointoss.ui.theme.LocalNavController
import com.helsinkiwizard.core.theme.Forty
import com.helsinkiwizard.core.theme.OnPrimaryContainerDark
import com.helsinkiwizard.core.theme.PrimaryContainerDark
import com.helsinkiwizard.core.theme.Twelve
import kotlinx.coroutines.launch

private object MenuParams {
    val menuItems = listOf(
        MenuItem(
            NavRoute.CoinList,
            R.string.choose_a_coin,
            Icons.Outlined.MonetizationOn
        ),
        MenuItem(
            NavRoute.Settings,
            R.string.settings,
            Icons.Outlined.Settings
        ),
        MenuItem(
            NavRoute.About,
            R.string.about,
            Icons.Outlined.Info
        ),
    )
}

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
internal fun WatchMenu() {
    val listState = rememberScalingLazyListState()
    Scaffold(
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        val focusRequester = rememberActiveFocusRequester()
        val coroutineScope = rememberCoroutineScope()

        ScalingLazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = Forty, horizontal = Twelve),
            modifier = Modifier
                .fillMaxSize()
                .onRotaryScrollEvent {
                    // https://developer.android.com/training/wearables/compose/rotary-input
                    coroutineScope.launch {
                        listState.scrollBy(it.verticalScrollPixels)
                        listState.animateScrollBy(0f)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
        ) {
            items(MenuParams.menuItems) { menuItem ->
                MenuButton(menuItem)
            }
        }
    }
}

@Composable
private fun MenuButton(menuItem: MenuItem) {
    val navController = LocalNavController.current
    Button(
        onClick = { navController.navigate(menuItem.route.name) },
        colors = ButtonDefaults.primaryButtonColors(
            backgroundColor = PrimaryContainerDark,
            contentColor = OnPrimaryContainerDark
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = menuItem.icon,
                contentDescription = null,
                tint = OnPrimaryContainerDark,
                modifier = Modifier.padding(horizontal = Twelve)
            )
            Text(
                text = stringResource(id = menuItem.title),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
