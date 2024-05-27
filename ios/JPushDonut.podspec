# IM_TPNS.podspec

Pod::Spec.new do |spec|
  spec.name         = 'JPushDonut'
  spec.version      = '1.0.0'
  spec.summary      = 'Summary of JPushDonut'
  spec.homepage     = 'https://docs.jiguang.cn/jpush'
  spec.author       = { 'Your Name' => 'your@email.com' }
  spec.source       = { :git => 'https://github.com/your/repo.git', :tag => "#{spec.version}" }
  spec.license      = { :type => "MIT", :file => "LICENSE" }

  # Set your deployment target
  spec.ios.deployment_target = '11.0'

  spec.source_files = [
    'MyPlugin/**/*.{h,c,m,mm,cpp,H}',
  ]
    
  spec.vendored_frameworks = [
    'MyPlugin/WeAppNativePlugin.framework'
  ]

  spec.resources = "MyPlugin/Resources/MiniPlugin.bundle"
end
