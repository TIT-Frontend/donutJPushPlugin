package com.tencent.weauth 

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.tencent.luggage.wxa.SaaA.api.SaaAApi
import com.tencent.luggage.wxa.SaaA.api.SaaAApiConfig

class DemoApplication : Application() {

    companion object {
        private const val TAG = "DemoApplication"
        lateinit var applicationContext: Context
            private set
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        DemoApplication.applicationContext = this
        val sdkKeyVal = BuildConfig.SDK_KEY
        val sdkKeySecretVal = BuildConfig.SDK_KEY_SECRET
        var privacyPopupVal = BuildConfig.PRIVACY_POPUP
        var enableVConsole: Boolean? = null
        if (BuildConfig.ENABLE_VCONSOLE === "open") {
            enableVConsole = true
        } else if (BuildConfig.ENABLE_VCONSOLE === "close") {
            enableVConsole = false
        }

        if (sdkKeyVal.contains("sdkKey") || sdkKeySecretVal.contains("sdkKeySecret")) {
            Toast.makeText(this, "SaaAApi.Factory.createApi should get the sdkKey and sdkSecret", Toast.LENGTH_LONG).show();
            return
        }

        SaaAApi.Factory.createApi(this,
            SaaAApiConfig().apply {
                codePath = mapOf(
                    "wx03804765e7d2096d" to "SaaA_embed"
                )
                sdkKeySecret = sdkKeySecretVal
                sdkKey = sdkKeyVal
                enableDebug = enableVConsole
                if (BuildConfig.SPLASHSCREEN) {
                    splashscreenImage = R.drawable.splashscreen
                }
                privacyPopup = privacyPopupVal
                setOpenTransition(0, 0)
                appMenuEnable = BuildConfig.APP_MENU_ENABLE
                scheme = BuildConfig.SCHEME
            }

        )
    }
}