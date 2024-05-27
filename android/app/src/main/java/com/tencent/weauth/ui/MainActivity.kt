package com.tencent.weauth.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tencent.luggage.wxa.SaaA.api.SaaAApi
import com.tencent.luggage.wxaapi.LaunchWxaAppResult
import com.tencent.luggage.wxa.SaaA.api.LaunchAppModuleResultListener
import kotlin.reflect.full.functions
import com.tencent.weauth.BuildConfig
import com.tencent.weauth.R

class MainActivity : AppCompatActivity() {
    private companion object {
        private const val TAG = "Demo.MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.SPLASHSCREEN) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
            setTheme(R.style.Theme_Splashscreen)
            setContentView(R.layout.layout_splashscreen)
            window.decorView.post {
                launchAppModule()    
            }
        } else {
            setTheme(R.style.Theme_App_Global)
            setContentView(R.layout.layout)
            launchAppModule()
        }
        var appMenuEnable = BuildConfig.APP_MENU_ENABLE
        if (appMenuEnable) {
          applySaaAActionSheet()
        }
    }

    private fun launchAppModule() {
        // 启动示例
        try {
            val saaAApiImpl = SaaAApi.Factory.getApi()
            saaAApiImpl.launchAppModule(this, "wx03804765e7d2096d", "",
            LaunchAppModuleResultListener {  miniModuleId, timestamp, result ->
                android.util.Log.e("LaunchAppModuleResult", "$result")
                if (result == LaunchWxaAppResult.OK) {
                    runOnUiThread {
                        finish()
                    }
                }
            })
        }  catch (e: NullPointerException) {
            Log.e(TAG, e.toString());
        }
    }

    private fun applySaaAActionSheet() {
        val saaAApiImpl = SaaAApi.Factory.getApi()
        val method = saaAApiImpl::class.functions.find { it.name == "applySaaAActionSheet" }
        method?.call(saaAApiImpl, this)
    }
}
