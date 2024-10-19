package com.ithink.dailytodo.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.ithink.dailytodo.BaseActivity
import com.ithink.dailytodo.R
import com.ithink.dailytodo.databinding.ActivityDonateBinding

class DonateActivity : BaseActivity(), PurchasesUpdatedListener {

    private lateinit var binding: ActivityDonateBinding

    private lateinit var billingClient: BillingClient
    private var productDetailsList: List<ProductDetails> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityDonateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billingClient = BillingClient.newBuilder(this@DonateActivity)
            .setListener(this).enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()).build()

        startBillingConnection()

        // 버튼 클릭 이벤트 설정
        binding.donate1000.setOnClickListener {
            launchBillingFlow("d_1000")
        }

        binding.donate2000.setOnClickListener {
            launchBillingFlow("d_2000")
        }

        binding.donate5000.setOnClickListener {
            launchBillingFlow("d_5000")
        }

        binding.donate10000.setOnClickListener {
            launchBillingFlow("d_10000")
        }

        binding.donateBackBtn.setOnClickListener{
            finish()
        }
    }

    // BillingClient 연결
    private fun startBillingConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // 연결 성공 시 SKU 정보 쿼리
                    queryAvailableProducts()
                }
            }
            override fun onBillingServiceDisconnected() {
                // 결제 서비스 연결 끊김 처리
            }
        })
    }

    // 결제 창 띄우기
    private fun launchBillingFlow(productId: String) {
        val productDetails = productDetailsList.find { it.productId == productId }
        productDetails?.let {
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(it)
                            .build()
                    )
                )
                .build()
            billingClient.launchBillingFlow(this, billingFlowParams)
        }
    }

    // 사용 가능한 인앱 상품을 가져오기
    fun queryAvailableProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("d_1000")
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("d_2000")
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("d_5000")
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("d_10000")
                .setProductType(BillingClient.ProductType.INAPP)
                .build())

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient.queryProductDetailsAsync(params) { billingResult, mutableList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList = mutableList
            }
        }
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        if (p0.responseCode == BillingClient.BillingResponseCode.OK && p1 != null) {
            for (purchase in p1) {
                Toast.makeText(this@DonateActivity, getString(R.string.thank_you), Toast.LENGTH_SHORT).show()
            }
        }
    }

}