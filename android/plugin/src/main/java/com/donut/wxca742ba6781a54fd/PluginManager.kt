package com.donut.wxca742ba6781a54fd

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import cn.jpush.android.api.JPushInterface
import com.tencent.luggage.wxa.SaaA.plugin.NativePluginInterface
import com.tencent.luggage.wxa.SaaA.plugin.NativePluginBase
import com.tencent.luggage.wxa.SaaA.plugin.SyncJsApi
import com.tencent.luggage.wxa.SaaA.plugin.AsyncJsApi
import com.tencent.luggage.wxa.SaaA.plugin.NativePluginMainProcessTask
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import cn.jpush.android.ups.JPushUPSManager
import cn.jpush.android.ups.TokenResult
import cn.jpush.android.ups.UPSRegisterCallBack
import java.lang.reflect.Method

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class MsgReceiver(private val nativePlugin: TestNativePlugin) : BroadcastReceiver() {
    private val TAG = "MessageReceiverManager.MsgReceiver"

    val clickedJsonStrs = mutableListOf<String>()
    val showedJsonStrs = mutableListOf<String>()



    override fun onReceive(context: Context?, intent: Intent) {
        val event = intent.getStringExtra(Extras.INTENT_ACTION_EXTRA_EVENT)
        when(event) {
            Extras.INTENT_ACTION_EXTRA_EVENT_SHOWED -> {
                val jsonData = intent.getStringExtra(Extras.INTENT_ACTION_EXTRA_EVENT_DATA_JSON_STR)
                if (!jsonData.isNullOrEmpty()) {
                    // 创建 Gson 实例
                    val gson = Gson()

                    // 创建一个类型标记，表示我们想要转换的类型（HashMap<String, Any>）
                    val type: Type = object : TypeToken<HashMap<String, Any>>() {}.type

                    // 将 JSON 字符串转换为 HashMap
                    val hashMap: HashMap<String, Any> = gson.fromJson(jsonData, type)
                    val res: HashMap<String, Any> = HashMap()
                    res["data"] = hashMap
                    res["type"] = "onNotifyMessageArrived"
                    val androidRes: HashMap<String, Any> = HashMap()
                    androidRes["android"] = res
                    this.nativePlugin.sendMiniPluginEventOut(androidRes)
//                        if (isRuntimeRunning()) {
//                            MessageReceiverManager.showedEvent?.dispatch(jsonData)
//                        } else {
//                            showedJsonStrs.add(jsonData)
//                        }

                } else {
//                        MessageReceiverManager.showedEvent?.dispatch(-1, "no data")
                }

            }
            else -> {
                android.util.Log.e(TAG, "invalid action $event")
            }
        }


    }
}


class TestNativePlugin: NativePluginBase(), NativePluginInterface {
    private val TAG = "TestNativePlugin"

    var msgReceiver: MsgReceiver = MsgReceiver(this)

    fun  sendMiniPluginEventOut(msg:HashMap<String, Any>) {
        this.sendMiniPluginEvent(msg)
    }



    override fun getPluginID(): String {
        android.util.Log.e(TAG, "getPluginID ${BuildConfig.PLUGIN_ID}")
        return BuildConfig.PLUGIN_ID
    }

    @SyncJsApi(methodName = "mySyncFunc")
    fun test(data: JSONObject?): String {
        android.util.Log.i(TAG, data.toString())
        return "test"
    }

    @AsyncJsApi(methodName = "myAsyncFuncwithCallback")
    fun testAsync(data: JSONObject?, callback: (data: Any) -> Unit) {
        android.util.Log.i(TAG, data.toString())

        callback("async testAsync")
    }


    @AsyncJsApi(methodName = "registerPush")
    fun registerPush(data: JSONObject?, _callback: (data: Any) -> Unit, activity: Activity) {
        android.util.Log.i(TAG, data.toString())

        val callback: (data: Any) -> Unit = {data ->
            _callback(data)
//            JPushInterface.setBadgeNumber(activity,10);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
//            if (false) {
            android.util.Log.i(TAG, ">= 32");

            this.requestPermission(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            ) { permissions, grantResults ->
                if (grantResults != null && grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    android.util.Log.i(TAG, "PERMISSION_GRANTED, do invoke again");

                    val testTask = RegisterTokenTask()
                    testTask.setClientCallback(callback)
                    testTask.execAsync()
                } else {
                    android.util.Log.e(TAG, "reloadQRCode fail, SYS_PERM_DENIED");
                    callback("fail！")
                }

            }
        } else {
            android.util.Log.e(TAG, "<= 32, SYS_PERM_DENIED！");
            val testTask = RegisterTokenTask()
            testTask.setClientCallback(callback)
            testTask.execAsync()
        }

        try {
            //        this.msgReceiver = MsgReceiver(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(Extras.INTENT_ACTION)
//        val context = MMApplicationContext.getContext()
            val context = activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(msgReceiver, intentFilter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(msgReceiver, intentFilter)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }



    }

}

@Parcelize
class RegisterTokenTask(private var xgToken: String? = null, private var xgErrCode: Int? = null, private var xgErrMsg: String? = null) :
    NativePluginMainProcessTask() {

    private var clientCallback: ((Any) -> Unit)? = null


//    var xgToken: String? = null
//    var xgErrCode: Int? = null
//    var xgErrMsg: String? = null

    fun setClientCallback(callback: (data: Any) -> Unit) {
        this.clientCallback = callback
    }

    /**
     * 运行在主进程的逻辑，不建议在主进程进行耗时太长的操作
     */
    override fun runInMainProcess() {
        android.util.Log.e("MainProcess", "runInMainProcess, xgToken:${xgToken}, xgErrCode:${xgErrCode}")
        // 如果需要把主进程的数据回调到小程序进程，就赋值后调用 callback 函数
//        valToSync1 = "runInMainProcess"
//        this.callback() // callback函数会同步主进程的task数据，并在子进程调用runInClientProcess

        val clazz = Class.forName("android.app.ActivityThread")
        val method: Method = clazz.getMethod("currentApplication")
        val  mApplication = method.invoke(null) as Application

//        JPushInterface.setDebugMode(true);

        val appkey = this.getMetaDataValue("JPUSH_APPKEY")
        android.util.Log.i("TAG", "appkey:$appkey")
        JPushUPSManager.registerToken(mApplication, appkey, "", "", object : UPSRegisterCallBack {
            override fun onResult(tokenResult: TokenResult) {
                android.util.Log.d("TPush", "tokenResult：$tokenResult")
                //token在设备卸载重装的时候有可能会变
                val data = tokenResult.token
                val returnCode = tokenResult.returnCode
                android.util.Log.d("TPush", "注册成功，设备token为：$data")
                val res: MutableMap<String, Any> = HashMap()
//                res[Extras.XG_TOKEN] = data
//                env.callback(callbackId, makeReturnJson("ok", res))
//
//                MessageReceiverManager.showedEvent = EventOnTpushNotificationShowedResult(env)
//
//                val currentThread = Thread.currentThread()
//                if (MessageReceiverManager.showedEvent == null) {
//                    android.util.Log.e(MessageReceiver.LogTag, "set null？？")
//                }

                xgToken = "$data"
                xgErrCode = returnCode
                xgErrMsg = ""

                callback() // callback函数会同步主进程的task数据，并在子进程调用runInClientProcess
            }
        } )
    }

    /**
     * 运行在小程序进程的逻辑
     */
    override fun runInClientProcess() {
        android.util.Log.e("ClientProcess", "xgToken: ${xgToken}, xgErrCode:${xgErrCode}")
        this.clientCallback?.let { callback ->
            val xgToken: String = xgToken ?: ""
            val res: MutableMap<String, Any> = HashMap()
            res["token"] = xgToken
            res["success"] = true
            callback(res)
        }
    }

    override fun parseFromParcel(mainProcessData: Parcel?) {
        // 如果需要获得主进程数据，需要重写parseFromParcel，手动解析Parcel
//        this.valToSync1 = mainProcessData?.readString() ?: ""
//        this.valToSync2 = mainProcessData?.readString() ?: ""

        xgToken = mainProcessData?.readString()
        xgErrCode = mainProcessData?.readInt()
        xgErrMsg = mainProcessData?.readString()
    }

    private fun getMetaDataValue(key: String): String? {
        return try {
//            val packageManager = packageManager
//            val applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
//            applicationInfo.metaData?.getString(key)

            val clazz = Class.forName("android.app.ActivityThread")
            val method: Method = clazz.getMethod("currentApplication")
            val  mApplication = method.invoke(null) as Application

            val applicationInfo = mApplication.packageManager.getApplicationInfo(
                mApplication.packageName,
                PackageManager.GET_META_DATA
            )
            applicationInfo.metaData?.getString(key)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

}