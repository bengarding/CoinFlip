package com.helsinkiwizard.cointoss.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.helsinkiwizard.cointoss.ui.viewmodel.RemoveAdsViewModel
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener

@Composable
fun RemoveAdsScreen(
    viewModel: RemoveAdsViewModel = hiltViewModel()
) {
    PaywallDialog(
        PaywallDialogOptions.Builder()
            .setListener(
                object : PaywallListener {
                    override fun onPurchaseCompleted(customerInfo: CustomerInfo, storeTransaction: StoreTransaction) {
                        super.onPurchaseCompleted(customerInfo, storeTransaction)
                        viewModel.onPurchaseCompleted()
                    }

                    override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                        super.onRestoreCompleted(customerInfo)
                        viewModel.onPurchaseCompleted()
                    }
                }
            )
            .build()
    )
}
