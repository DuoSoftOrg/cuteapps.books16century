package ru.cuteapps.books16century.shop

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.android.billingclient.api.*

import com.android.vending.billing.IInAppBillingService
import org.jetbrains.anko.doAsync

import ru.cuteapps.books16century.App
import org.json.JSONObject

import java.util.ArrayList
import java.util.Arrays

/**
 * Created by Admin on 05.04.17.
 */

object Shop {

    var isAdFree : Boolean = true

    fun init () {
        doAsync {
            checkAdFree()
        }
    }

    var billingClient: BillingClient? = null
        get() {
            if (field == null) {
                field = BillingClient.newBuilder(App.getContext()).setListener { responseCode, purchases ->
                    if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
                        for (purchase in purchases) {
                            if (purchase.sku == "adfree") {
                                App.restart()
                            }
                        }
                    } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
                        // Handle an error caused by a user cancelling the purchase flow.
                    } else {
                        // Handle any other error codes.
                    }
                }.build()
            }
            return field
        }

    private fun checkAdFree () {
        if (billingClient?.isReady!!) {
            return
        }
        billingClient!!.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(responseCode: Int) {
                try {
                    var adFree = false
                    if (responseCode == BillingClient.BillingResponse.OK) {
                        val p = billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
                        if (p != null && p.purchasesList != null) {
                            p.purchasesList.forEach {
                                if (it.sku == "adfree") {
                                    adFree = true
                                    return@forEach
                                }
                            }
                        }

                    }
                    isAdFree = adFree
                } catch (e : Exception) {
                    isAdFree = false
                }

            }

            override fun onBillingServiceDisconnected() {

            }

        })
    }

    fun buyAdFree(activity : Activity) {
        try {
            var sku : SkuDetails? = null
            val skuList = ArrayList<String>()
            skuList.add("adfree")
            val p = SkuDetailsParams.newBuilder()
            p.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
            if (billingClient == null)
                return
            billingClient!!.querySkuDetailsAsync(p.build()) { respCode, skuDetailsList ->
                if (skuDetailsList == null)
                    return@querySkuDetailsAsync
                skuDetailsList.forEach {
                    if (it.sku == "adfree") {
                        sku = it
                    }
                }
                val params = BillingFlowParams.newBuilder()
                params.setSkuDetails(sku)
                billingClient!!.launchBillingFlow(activity, params.build())
            }
        } catch (e : Exception) {

        }

    }

}
