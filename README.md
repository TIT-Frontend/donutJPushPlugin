# 第三方消息推送插件使用指南

作为多端框架的开发者，我们除了可对接腾讯云的消息推送服务之外，亦可通过下方 2 种方式对接第三方的消息推送服务，本文以极光推送服务为例子。

1、自行开发[多端插件](https://dev.weixin.qq.com/docs/framework/dev/plugin/plugin_guidelines.html)进行对接；相关的插件 demo 代码已已开源，可下载参考。[去下载]()

2、直接引用我已开发好的多端插件（插件 id 为 wxca742ba6781a54fd ），详细的使用指引可查看下方介绍。

#### 前置条件

- 需前往极光平台，开通[极光消息推送服务](https://www.jiguang.cn/push)并按照文档开通和配置相关信息

#### changlog

**iOS**
1.0.1
1. `A` registerPush 返回 registrationID

## 一、iOS 对接指南

### 1. 配置插件

- 在微信开发者工具，前往`project.miniapp.json`，点击右上角切换到 json 模式，然后按照将下方内容添加`project.miniapp.json`

```json
"mini-plugin": {
  "ios": [
    {
      "open": true,
      "pluginId": "wxca742ba6781a54fd",// 这是插件id，不可修改
      "pluginVersion": "1.0.0",
      "loadWhenStart": true, // 需设为true；表示为App启动即加载插件
      "resourcePath": "pluginResources/wxca742ba6781a54fd.json"// 开发者需修改此处，即，需要把极光推送的key放入到json中，让插件读取注册极光SDK。路径为相对项目根路径的文件相对路径
    },
  ]
```

- 在小程序项目下创建pluginResources/wxca742ba6781a54fd.json，内容为如下。需要去极光推送创建。

```json
{
	"appKey": "8c6eexxxxxxxxxxxbee1",//此处需开发者修改为真正的key
	"channel": "App Store",
	"apsForProduction": false
}
```

- 补充说明，以上的配置最终会用在极光的`JPUSHService`和`setupWithOption`接口.具体的值可以查看极光的[接口文档](https://docs.jiguang.cn/jpush/quickstart/iOS_quick)。

```objective-c
[JPUSHService setupWithOption:self.launchOptions appKey:appKey
                      channel:channel
             apsForProduction:[apsForProduction boolValue]
        advertisingIdentifier:nil];
```

- 更多补充：**离线无法收到推送**可查看[https://go48pg.yuque.com/go48pg/pa41sm/wfgms8](https://go48pg.yuque.com/go48pg/pa41sm/wfgms8)

### 2. iOS appex 的使用

如果有接入[Notification Service Extension](https://docs.jiguang.cn/jpush/client/iOS/ios_api) 的需求，需要添加如下的配置。

1. 需要去苹果的后台申请一个新的 Bundle Id，要以主 Bundle Id 为前缀。
2. 申请对应的开发版和分发版的 profile。然后分别配置在对应的 profilePath 和 distributeProfilePath 中
3. profile 的证书需要都使用主包 profile 的证书。
**4. 1.0.0版本 appexProfiles 的 key 为 JPushNSE，1.0.1版本 appexProfiles 的 key 为 NSE。**

```json
"mini-plugin": {
  "ios": [
    {
      "open": true,
      "pluginId": "wxca742ba6781a54fd",
      "pluginVersion": "1.0.0",
      "loadWhenStart": true,
      "resourcePath": "pluginResources/wxca742ba6781a54fd.json",
      "appexProfiles": {
          "JPushNSE": {
            "enable": true,
            "bundleID": "com.xx.xx.service",
            "profilePath": "xxx/xxxxservice.mobileprovision",// 开发版的 profile
            "distributeProfilePath": "xxx/xxx/xxx.mobileprovision"// 分发版的profile
          }
        },
    },
  ],
  "android": [
    ...
  ]
}
```

### 3. js 调用

在添加插件以后，并编译到真机，就可以开始用js调用了

**wx.miniapp.loadNativePlugin**
加载插件，获取插件的js对象。

**plugin.getCachedMsg**
获取loadNativePlugin调用之前的消息。因为配置loadWhenStart 所以插件native侧其实早已初始化完毕。所以在监听回调之前的消息都被放到了缓存中。
这里只有调用了getCachedMsg，后续的消息才会往js侧发送。这里一定要先调用再监听消息。

**plugin.onMiniPluginEvent**
监听消息。回调的对象结构如下。
```
{
  ios:{
    type: '', 
    data: {},
    msg: ''
  }
}
```

type 的类型如下，具体表示的情况可以在[极光文档](https://docs.jiguang.cn/jpush/client/iOS/ios_api#%E5%8A%9F%E8%83%BD%E8%AF%B4%E6%98%8E)中搜索
openSettingsForNotification
willPresentNotification
didReceiveNotificationResponse
didReceiveRemoteNotification
didFinishLaunchingWithOptions

data的数据接口为 userInfo 具体的内容可以在[极光文档](https://docs.jiguang.cn/jpush/client/iOS/ios_api#%E5%8F%82%E6%95%B0%E8%AF%B4%E6%98%8E)中搜索userInfo

NSDictionary * userInfo = notification.request.content.userInfo;

**myPlugin.registerPush**

开启极光推送。调用以后就会弹出系统弹窗“xxx想要给你发送通知“。
回调返回对象。

```json
{
  "success": true,
  "deviceToken": "xxx",
  "registrationID": "xxx",
  "resCodeOfRegistrationID": 0, // 0为成功，其他为失败
  "msg": "xxx",
  "token": "xxxx", // 1.0.1 开始废弃
}
```

**myPlugin.setBadge**

设置下标数字

**具体调用可以参考如下**

```javascript
loadJIGUANGPlugin() {
  if (this.hasLoad) return
  this.hasLoad = true
  wx.miniapp.loadNativePlugin({
    pluginId: "wxca742ba6781a54fd",
    success: (plugin) => {
      const msg = plugin.getCachedMsg();

      console.log('getCachedMsg', msg)
      plugin.onMiniPluginEvent(this.onJiguangMsg)
      console.log('load plugin success', plugin)
      this.setData({
        myPlugin: plugin
      })
    },
    fail: (e) => {
      console.log('load plugin fail', e)
    }
  })
},

onJiguangMsg(msg) {
  console.log(msg)
},

onUsePlugin() {
  const { myPlugin } = this.data
  if (!myPlugin) {
    console.log('plugin is undefined')
    return
  }

  myPlugin.registerPush({}, (ret) => {
    console.log('registerPush ret:', ret)
  })
},

setBadge() {
  const { myPlugin } = this.data
  myPlugin.setBadge({
    number: 10
  })
},

clearBadge() {
  const { myPlugin } = this.data
  myPlugin.setBadge({
    number: 0
  })
},
```

## 二、Android 对接指南

### 1. 配置插件

- 在微信开发者工具，前往`project.miniapp.json`，点击右上角切换到 json 模式，然后按照将下方内容添加`project.miniapp.json`

```json
"mini-plugin": {
  "android": [
    {
      "open": true,
      "pluginId": "wxca742ba6781a54fd",// 这是插件id，不可修改
      "pluginVersion": "1.0.0"
    }
  ]
}
```

### 2. 配置manifestPlaceholders

- 在`mini-android`新增一个 key`manifestPlaceholders`，参考以下填写
- 请注意，插件目前集成了以下厂商：小米，oppo，vivo，荣耀，参数请参考极光的[厂商通道参数申请指南](https://docs.jiguang.cn/jpush/client/Android/android_3rd_param)
- 如果不需要厂商推送，则下面的对应厂商 key 保持默认的即可（请勿删除 key）
```json
  "mini-android": {
    "manifestPlaceholders": {
      "JPUSH_PKGNAME": "你的包名",
      "JPUSH_APPKEY": "你的 appkey",
      "JPUSH_CHANNEL": "developer-default",
      "XIAOMI_APPKEY" : "MI-您的应用对应的小米的APPKEY",
      "XIAOMI_APPID" : "MI-您的应用对应的小米的APPID",
      "OPPO_APPKEY" : "OP-您的应用对应的OPPO的APPKEY",
      "OPPO_APPID" : "OP-您的应用对应的OPPO的APPID",
      "OPPO_APPSECRET": "OP-您的应用对应的OPPO的APPSECRET",
      "VIVO_APPKEY" : "您的应用对应的VIVO的APPKEY",
      "VIVO_APPID" : "您的应用对应的VIVO的APPID",
      "HONOR_APPID" : "您的应用对应的Honor的APP ID"
    }
  }
```

### 3. js调用

在添加插件以后，并编译到真机，就可以开始用js调用了

**wx.miniapp.loadNativePlugin**
加载插件，获取插件的js对象。

**plugin.onMiniPluginEvent**
监听消息。回调的对象结构如下。
```
{
  android:{
    type: '', 
    data: {},
    msg: ''
  }
}
```
目前只有安卓type 只有`onNotifyMessageArrived`（消息到达时）

**myPlugin.registerPush**

开启极光推送。 targetsdkversion 33 及以上会需要系统授权。
回调返回对象。

```json
{
  "success": true,
  "token": "xxxx",
  "msg": "xxx"
}
```

**具体调用可以参考如下**
```javascript
  loadJIGUANGPlugin() {
    const listener1 = (param) => {
      console.log('onMiniPluginEvent listener1', param)
    }

    // 注意，实际使用的时候，如果用户曾经同意过需要在 app.js/onLaunch 的时候注册
    wx.miniapp.loadNativePlugin({
      pluginId: miniAppPluginId,
      success: (plugin) => {
        console.log('load plugin success', plugin)
        const myPlugin = plugin
        this.setData({
          myPlugin: plugin
        })
        myPlugin.onMiniPluginEvent(listener1)
        // 注册
        myPlugin.registerPush({}, (ret) => {
          console.log('myAsyncFuncwithCallback ret:', ret)
        })
      },
      fail: (e) => {
        console.log('load plugin fail', e)
      }
    })
  },

```

### 4. 监听用户点击消息
- 极光控制台发送消息时，配置 Android点击通知打开选择`DeepLink`，也就是`scheme`

<img src="https://testchu-7gy8occc8dcc14c3-1304825656.tcloudbaseapp.com/img%2Fmelody%2F%E4%BC%81%E4%B8%9A%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_1fadc823-df8e-4996-afc5-347d15250424.png" />


- 在 js 侧使用[wx.miniapp.registOpenURL](https://dev.weixin.qq.com/docs/framework/dev/jsapi/miniapp/registOpenURL.html)监听
