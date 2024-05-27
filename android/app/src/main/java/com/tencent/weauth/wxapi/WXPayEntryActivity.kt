package com.tencent.weauth.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tencent.luggage.wxa.SaaA.api.SaaAApi
import java.lang.Exception

class WXPayEntryActivity : Activity() {
    private companion object {
        private const val TAG = "RuntimeSDKDemo.WXPayEntryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent ?: return finish()

        handleWxaAPI(intent!!)
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?: return finish()

        handleWxaAPI(intent)
        finish()
    }

    private fun handleWxaAPI(intent: Intent) {
        try {
            SaaAApi.Factory.getApi().handleWXAPIIntent(this, intent)
        } catch (e: Exception) {
            Log.e(TAG, "handleWxaAPI e=${e}")
        }
    }
}