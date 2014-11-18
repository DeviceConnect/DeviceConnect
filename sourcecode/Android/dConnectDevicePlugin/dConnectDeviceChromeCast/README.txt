ChromeCast Device Plugin
----------------------------------------------------------------------------
本プロジェクトをビルドするためには、下記の準備が必要です。

1. android-support-v7-appcompat
2. android-support-v7-mediarouter
3. google-play-services_lib 
4. dConnectSDKAndroid
5. dConnectDevicePluginSDK

Project → Properties → Android → Library に参照を追加します。

android-support-v7-mediarouter
  - android-support-v7-appcompat
  
dConnectDevicePluginSDK
  - dConnectSDKAndroid

dConnectDeviceChromeCast
  - android-support-v7-mediarouter
  - dConnectDevicePluginSDK
  - google-play-services_lib

以上