-keepclassmembers class * {
    @com.tencent.luggage.wxa.SaaA.plugin.SyncJsApi *;
    @com.tencent.luggage.wxa.SaaA.plugin.AsyncJsApi *;
}

# 小米厂商推送
-dontwarn com.xiaomi.push.**
-keep class com.xiaomi.push.** { *; }