// pages/android/android.js
const { miniAppPluginId } = require('../../constant');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    myPlugin: undefined,
    quickStartContents: [
      '在「设置」->「安全设置」中手动开启多端插件服务端口',
      '在「工具栏」->「运行设备」中选择 Android 点击「运行」，快速准备运行环境',
      '在打开的 Android Stuido 中点击「播放」运行原生工程',
      '保持开发者工具开启，修改小程序代码和原生代码仅需在 Android Stuido 中点击「播放」查看效果',
    ]
  },

  loadJPush() {
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

  onLoadPlugin() {
    wx.miniapp.loadNativePlugin({
      pluginId: miniAppPluginId,
      success: (plugin) => {
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

  onUsePlugin() {
    const { myPlugin } = this.data
    if (!myPlugin) {
      console.log('plugin is undefined')
      return
    }
    const ret = myPlugin.mySyncFunc({ a: 'hello', b: [1,2] })
    console.log('mySyncFunc ret:', ret)

    myPlugin.myAsyncFuncwithCallback({ a: 'hello', b: [1,2] }, (ret) => {
      console.log('myAsyncFuncwithCallback ret:', ret)
    })
  },

  copyLink() {
    wx.setClipboardData({
      data: 'https://dev.weixin.qq.com/docs/framework/dev/plugin/androidPlugin.html',
    })
  }
})