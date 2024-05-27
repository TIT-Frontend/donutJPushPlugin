package com.donut.wxca742ba6781a54fd;


public interface Extras {
    String TAGS = "tags";
    //参数
    String CONTENT = "content";  //
    String RESULT_CODE = "resultCode";
    String MESSAGE = "message";
    String TITLE = "title";
    String CUSTOM_MESSAGE = "customMessage";
    String PUSH_CHANNEL = "pushChannel";
    String NOTIFACTION_ID = "notifactionId";
    String NOTIFACTION_ACTION_TYPE = "notifactionActionType";
    String MSG_ID = "msgId";
    String ACTIVITY = "activity";
    String ACTIVITY_NAME = "activityName";
    String ACTION_TYPE = "actionType";
    String XG_PUSH_DID_SET_BADGE = "xgPushDidSetBadge";
    String XG_TOKEN = "xgToken";
    String XG_UPLOAD_FILE = "xgUploadFile";


    String INTENT_ACTION = "com.tencent.luggage.wxa.SaaA.jpush.TEST_ACTION";
    String INTENT_ACTION_EXTRA_EVENT = "event";
    String INTENT_ACTION_EXTRA_EVENT_CLICKED = "clicked";
    String INTENT_ACTION_EXTRA_EVENT_SHOWED = "showed";
    String ERR_CODE = "errCode";
    String ERR_STR = "errStr";
    String INTENT_ACTION_EXTRA_EVENT_DATA_JSON_STR = "dataJsonStr";



    //调用Flutter的函数名称
    String ON_REGISTERED_DEVICE_TOKEN = "onRegisteredDeviceToken"; //获取设备token回调（在注册成功里面获取的）
    String ON_REGISTERED_DONE = "onRegisteredDone";    //注册成功回调
    String UN_REGISTERED = "unRegistered";     //反注册回调
    String ON_RECEIVE_NOTIFICATION_RESPONSE = "onReceiveNotificationResponse";   //收到通知
    String ON_RECEIVE_MESSAGE = "onReceiveMessage";       //收到透传通知
    String XG_PUSH_DID_BIND_WITH_IDENENTIFIER = "xgPushDidBindWithIdentifier";   // 绑定账号跟标签
    String XG_PUSH_DID_UNBIND_WITH_IDENENTIFIER = "xgPushDidUnbindWithIdentifier"; //解绑账号跟标签
    String XG_PUSH_DID_CLEAR_WITH_IDENENTIFIER = "xgPushDidClearAllIdentifiers";  //清除账号跟标签
    String XG_PUSH_CLICK_ACTION = "xgPushClickAction";   //通知点击事件
}

