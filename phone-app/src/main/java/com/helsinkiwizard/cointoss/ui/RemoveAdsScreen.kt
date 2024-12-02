package com.helsinkiwizard.cointoss.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.ui.composable.ErrorScreen
import com.helsinkiwizard.cointoss.ui.theme.LocalNavController
import com.helsinkiwizard.cointoss.ui.viewmodel.RemoveAdsContent
import com.helsinkiwizard.cointoss.ui.viewmodel.RemoveAdsViewModel
import com.helsinkiwizard.core.viewmodel.UiState
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener

@Composable
fun RemoveAdsScreen(
    viewModel: RemoveAdsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val navController = LocalNavController.current

    when (val state = viewModel.uiState.collectAsState().value) {
        is UiState.ShowContent -> {
            when (state.type as RemoveAdsContent) {
                RemoveAdsContent.ShowDialog -> {
                    RemoveAdsDialog(
                        onPurchaseCompleted = { viewModel.onPurchaseCompleted() },
                        onPurchaseCancelled = { viewModel.onPurchaseCancelled() },
                        onPurchaseError = { viewModel.onPurchaseError() }
                    )
                }

                RemoveAdsContent.PurchaseComplete -> {
                    Toast.makeText(context, R.string.purchase_success, Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }

                RemoveAdsContent.PurchaseCancelled -> {
                    navController.popBackStack()
                }
            }
        }

        is UiState.Error -> {
            ErrorScreen(
                message = stringResource(id = R.string.purchase_error),
                onCancelClicked = { viewModel.showDialog() }
            )
        }

        else -> {}
    }
}

@Composable
fun RemoveAdsDialog(
    onPurchaseCompleted: () -> Unit,
    onPurchaseError: () -> Unit,
    onPurchaseCancelled: () -> Unit,
) {
    val navController = LocalNavController.current
    PaywallDialog(
        PaywallDialogOptions.Builder()
            .setDismissRequest {
                navController.popBackStack()
            }
            .setListener(
                object : PaywallListener {
                    override fun onPurchaseCompleted(customerInfo: CustomerInfo, storeTransaction: StoreTransaction) {
                        super.onPurchaseCompleted(customerInfo, storeTransaction)
                        onPurchaseCompleted()
                    }

                    override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                        super.onRestoreCompleted(customerInfo)
                        onPurchaseCompleted()
                    }

                    override fun onPurchaseError(error: PurchasesError) {
                        super.onPurchaseError(error)
                        onPurchaseError()
                    }

                    override fun onRestoreError(error: PurchasesError) {
                        super.onRestoreError(error)
                        onPurchaseError()
                    }

                    override fun onPurchaseCancelled() {
                        super.onPurchaseCancelled()
                        onPurchaseCancelled()
                    }
                }
            )
            .build()
    )
}
