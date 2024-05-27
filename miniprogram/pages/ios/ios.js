// pages/ios/ios.js
const { miniAppPluginId } = require('../../constant');

Page({
	hasLoad: false,
  /**
   * 页面的初始数据
   */
  data: {
    myPlugin: undefined,
    quickStartContents: [
      '在「设置」->「安全设置」中手动开启多端插件服务端口',
      '在「工具栏」->「运行设备」中选择 iOS 点击「运行」，快速准备运行环境',
      '在打开的 Xcode 中点击「播放」运行原生工程',
      '保持开发者工具开启，修改小程序代码和原生代码仅需在 Xcode 中点击「播放」查看效果',
    ]
	},
	
	onMsg(msg) {
		console.log('onMsg', msg)
	},

  onLoadPlugin() {
		if (this.hasLoad) return
		this.hasLoad = true
    wx.miniapp.loadNativePlugin({
      pluginId: miniAppPluginId,
      success: (plugin) => {
				const msg = plugin.getCachedMsg();
				console.log('getCachedMsg', msg)
				plugin.onMiniPluginEvent(this.onMsg)
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
    // const ret = myPlugin.mySyncFunc({ a: 'hello', b: [1,2] })

    myPlugin.registerPush({}, (ret) => {
      console.log('myAsyncFuncwithCallback ret:', ret)
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

  copyLink() {
    wx.setClipboardData({
      data: 'https://dev.weixin.qq.com/docs/framework/dev/plugin/iosPlugin.html',
    })
  }
})