package com.helsinkiwizard.cointoss.ui.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.navigation.NavRoute
import com.helsinkiwizard.core.theme.Sixteen
import com.helsinkiwizard.core.theme.Sixty

object DrawerParams {
    val drawerButtons = listOf(
        DrawerModel(
            NavRoute.Home,
            R.string.home,
            Icons.Outlined.Home
        ),
        DrawerModel(
            NavRoute.CoinList,
            R.string.choose_a_coin,
            Icons.Outlined.MonetizationOn
        ),
        DrawerModel(
            NavRoute.CreateCoin,
            R.string.create_a_coin,
            Icons.Outlined.AddCircleOutline
        ),
        DrawerModel(
            NavRoute.Settings,
            R.string.settings,
            Icons.Outlined.Settings
        ),
        DrawerModel(
            NavRoute.About,
            R.string.about,
            Icons.Outlined.Info
        )
    )
}

@Composable
fun DrawerContent(
    onClick: (NavRoute) -> Unit
) {
    val menuItems: List<DrawerModel> = remember { DrawerParams.drawerButtons }

    ModalDrawerSheet(
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        Column {
            menuItems.forEach { item ->
                DrawerItem(item, onClick)
            }
        }
    }
}

@Composable
private fun DrawerItem(item: DrawerModel, onClick: (options: NavRoute) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .fillMaxWidth()
            .clickable { onClick(item.drawerOption) }
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            modifier = Modifier
                .size(Sixty)
                .padding(horizontal = Sixteen)
        )
        Text(
            text = stringResource(id = item.title),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
