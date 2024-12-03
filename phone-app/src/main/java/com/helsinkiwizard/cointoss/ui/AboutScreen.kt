package com.helsinkiwizard.cointoss.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.helsinkiwizard.cointoss.BuildConfig
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.navigation.NavRoute
import com.helsinkiwizard.cointoss.ui.composable.AppIconPainterResource
import com.helsinkiwizard.cointoss.ui.composable.PreviewSurface
import com.helsinkiwizard.cointoss.ui.composable.PrimaryButton
import com.helsinkiwizard.cointoss.ui.theme.BodyMediumSpan
import com.helsinkiwizard.cointoss.ui.theme.LinkText
import com.helsinkiwizard.cointoss.ui.theme.LocalNavController
import com.helsinkiwizard.core.CoreConstants.PACKAGE_NAME
import com.helsinkiwizard.core.theme.Forty
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.Sixty
import com.helsinkiwizard.core.theme.ThirtyTwo
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.theme.TwentyFour
import com.helsinkiwizard.core.utils.buildTextWithLink
import com.helsinkiwizard.core.utils.getEmailIntent
import com.helsinkiwizard.core.utils.onLinkClick
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private val AppIconSize = 120.dp

@Composable
fun AboutScreen(
    dateUpdated: LocalDate = getLastUpdatedDate(LocalContext.current)
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AppInfo(dateUpdated)
        Spacer(modifier = Modifier.height(Sixty))
        Contact()
        AddCoinDetailsButtons()
    }
}

@Composable
private fun AppInfo(dateUpdated: LocalDate) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = AppIconPainterResource(id = R.mipmap.ic_launcher_round),
            contentDescription = null,
            modifier = Modifier
                .size(AppIconSize)
                .clip(CircleShape)
                .padding(top = Forty, bottom = Twelve)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )

        val dateString = dateUpdated.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
        )
        Text(
            text = "${BuildConfig.VERSION_NAME} - $dateString",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        val appOwner = stringResource(id = R.string.app_owner)
        Text(
            text = stringResource(id = R.string.copyright, dateUpdated.year, appOwner),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Four)
        )
    }
}

@Composable
private fun Contact() {
    val context = LocalContext.current
    val emailAddress = stringResource(id = R.string.email_address)
    val annotatedString = buildTextWithLink(
        fullText = stringResource(id = R.string.contact),
        linkText = emailAddress,
        style = BodyMediumSpan,
        linkStyle = LinkText
    )
    ClickableText(
        text = annotatedString,
        modifier = Modifier.padding(horizontal = Twelve, vertical = ThirtyTwo),
        onClick = { offset ->
            annotatedString.onLinkClick(
                offset = offset,
                onClick = {
                    context.startActivity(getEmailIntent(emailAddress))
                }
            )
        }
    )
}

@Composable
private fun AddCoinDetailsButtons() {
    val navController = LocalNavController.current
    Column(
        verticalArrangement = Arrangement.spacedBy(Twelve),
        modifier = Modifier.padding(horizontal = TwentyFour)
    ) {
        val context = LocalContext.current

        PrimaryButton(
            text = stringResource(id = R.string.attributions),
            onClick = { navController.navigate(NavRoute.Attributions.name) }
        )
        PrimaryButton(
            text = stringResource(id = R.string.install_on_watch),
            onClick = { openGooglePlay(context) }
        )
        PrimaryButton(
            text = stringResource(id = R.string.rate_on_google_play),
            onClick = { openGooglePlay(context) }
        )
    }
}

private fun getLastUpdatedDate(context: Context): LocalDate {
    val time = context.packageManager.getPackageInfo(PACKAGE_NAME, 0).lastUpdateTime
    return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate()
}

private fun openGooglePlay(context: Context) {
    try {
        // Try to use the Google Play Store app to open the rating page
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$PACKAGE_NAME")
            )
        )
    } catch (e: android.content.ActivityNotFoundException) {
        // Fallback to opening the page in a web browser if the Google Play app is not available
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$PACKAGE_NAME")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {
    PreviewSurface {
        AboutScreen(
            dateUpdated = LocalDate.now()
        )
    }
}
