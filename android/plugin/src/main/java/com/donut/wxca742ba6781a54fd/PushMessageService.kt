package com.donut.wxca742ba6781a54fd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.jpush.android.api.CmdMessage
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.JPushMessage
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageService
import org.json.JSONObject

class PushMessageService : JPushMessageService() {
    companion object {
        private const val TAG = "PushMessageService"
        const val TEST_ACTION = Extras.INTENT_ACTION
    }



    override fun onMessage(context: Context, customMessage: CustomMessage) {
        Log.e(TAG, "[onMessage] $customMessage")
        val intent = Intent("com.jiguang.demo.message")
        intent.putExtra("msg", customMessage.message)
        context.sendBroadcast(intent)
    }

    override fun onNotifyMessageOpened(context: Context, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageOpened] $message")
    }

    override fun onMultiActionClicked(context: Context, intent: Intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮")
        val nActionExtra = intent.extras!!.getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA)

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            Log.d(TAG, "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null")
            return
        }
        if (nActionExtra == "my_extra1") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一")
        } else if (nActionExtra == "my_extra2") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二")
        } else if (nActionExtra == "my_extra3") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三")
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义")
        }
    }

    override fun onNotifyMessageArrived(context: Context, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageArrived] $message")

        val testIntent = Intent(TEST_ACTION)
        testIntent.putExtra(Extras.INTENT_ACTION_EXTRA_EVENT, Extras.INTENT_ACTION_EXTRA_EVENT_SHOWED)

        // 创建一个空的 JSON 对象
        val jsonObject = JSONObject()

        jsonObject.put("msgId", message.msgId)
        jsonObject.put("notificationContent", message.notificationContent)
        jsonObject.put("notifactionId", message.notificationId)
        jsonObject.put("notificationTitle", message.notificationTitle)
        jsonObject.put("notificationChannelId", message.notificationChannelId)
        jsonObject.put("notificationType", message.notificationType)

        val jsonStr = jsonObject.toString()
        testIntent.putExtra(Extras.INTENT_ACTION_EXTRA_EVENT_DATA_JSON_STR, jsonStr)

        Log.i(TAG, "onNotificationClickedResult: $message")
        android.util.Log.e(TAG, "onNotificationClickedResult: $message")
        context.sendBroadcast(testIntent)
    }

    override fun onNotifyMessageDismiss(context: Context, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageDismiss] $message")
    }

    override fun onRegister(context: Context, registrationId: String) {
        Log.e(TAG, "[onRegister] $registrationId")
        val intent = Intent("com.jiguang.demo.register")
        context.sendBroadcast(intent)
    }

    override fun onConnected(context: Context, isConnected: Boolean) {
        Log.e(TAG, "[onConnected] $isConnected")
    }

    override fun onCommandResult(context: Context, cmdMessage: CmdMessage) {
        Log.e(TAG, "[onCommandResult] $cmdMessage")
    }

    override fun onTagOperatorResult(context: Context, jPushMessage: JPushMessage) {
//        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage)
    }

    override fun onCheckTagOperatorResult(context: Context, jPushMessage: JPushMessage) {
//        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage)
    }

    override fun onAliasOperatorResult(context: Context, jPushMessage: JPushMessage) {
//        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage)
    }

    override fun onMobileNumberOperatorResult(context: Context, jPushMessage: JPushMessage) {
//        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage)
    }

    override fun onNotificationSettingsCheck(context: Context, isOn: Boolean, source: Int) {
        super.onNotificationSettingsCheck(context, isOn, source)
        Log.e(TAG, "[onNotificationSettingsCheck] isOn:$isOn,source:$source")
    }

    override fun onInAppMessageClick(context: Context, message: NotificationMessage) {
        // 应用内消息被用户点击，会在此方法中回调结果。
        Log.e(TAG, "[onInAppMessageClick] $message")
    }


}