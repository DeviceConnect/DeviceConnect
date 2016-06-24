# DeviceConnectレポジトリの移行
DeviceConnectのレポジトリを以下のように移行しました。


DeviceConnect-Docs:　ドキュメントとバイナリー
https://github.com/DeviceConnect/DeviceConnect-Docs

DeviceConnect-Android: Androidプラットフォームのソースコード
https://github.com/DeviceConnect/DeviceConnect-Android

DeviceConnect-JS: JavaScriptプラットフォームのソースコード
https://github.com/DeviceConnect/DeviceConnect-JS

DeviceConnect-iOS: iOSプラットフォームのソースコード
https://github.com/DeviceConnect/DeviceConnect-iOS

DeviceConnect-Common: デバイスアプリのソースコード
https://github.com/DeviceConnect/DeviceConnect-Common

# DeviceConnect WebAPI について


Device Connect WebAPIはスマートフォン上で仮想サーバとして動作するWebAPIで、様々なウェアラブルデバイスやIoTデバイスをWebブラウザやアプリから統一的な記述で簡単に利用することができます。

* 動作環境として、Android、iOSに対応しています。WebブラウザとしてはChrome、Safari(擬似的な仕組み)、Firefoxで動作を確認しています。  
  _※それぞれの動作環境で利用できる対応デバイスは異なります。_
* 仮想サーバによるREST/WebSocketのWebAPIにより、任意の開発環境がご利用いただけます。
* コンテンツ開発を容易にするために、Javascript用SDK、Android用SDK、iOS用SDKを用意しています。
* 機能拡張のためのプラグイン開発用SDKを用意しております。任意のWebAPI機能の追加が可能です。
* 同じローカルネットワーク上にあるDeviceConnect WebAPIがセットアップされたAndroid端末も設定変更で利用できます（セキュリティ上のリスクについてご留意いただく必要があります）。



# サンプルでの動作確認（Android用）
* https://github.com/DeviceConnect/DeviceConnect-Docs/blob/master/Bin/demoWebSite.zip

## Android端末でのChromeブラウザからの動作確認

1. Androidの内部ストレージにデモコンテンツをフォルダを作成し（[例]"dConnect"）、  
  そこにサンプルのdemoWebSiteフォルダ以下のすべてのファイルをコピーしてください。
```
Android root
   └── mnt
       └── sdcard
           └── dConnect #ここにフォルダを作成
                └── demoWebSite #ここにjavaScriptのサンプルをコピー
```

2. Chromeブラウザのアドレス欄に内部ストレージのURIを記入し、  
  内部ストレージにあるWebコンテンツにアクセスしてください。
```
    [例] file:///storage/emulated/0/dConnect/demoWebSite/index.html
```

3. "Download APK"のリンクから、Device Connect本体の"DeviceConnectManager"、  
  デバイスプラグインの"Android Host"をダウンロードし、インストールしてください。

  _※設定のセキュリティから、提供元不明のアプリのインストールの許可が必要です。_

4. トップページに戻ってください。

5. "Launch UI-App"のリンクからDeviceConnectWebAPIの動作をご確認ください。
  * CheckボタンでDeviceConnectWebAPIの動作状況の確認が行えます。
  * 未起動の場合、"Device Connect was not found."と表示されます。
  * DeviceConnectManagerがインストールされていれば、設定画面が表示されます。
  * DeviceConnectManagerをONにしてWeb画面に戻り、もう一度Checkボタンを押すとDevice Connect API version:1.0と表示されます。
  * accessTokenボタンでコンテンツからのデバイス機能アクセスを許可します。
  * Search Deviceボタンを押すとインストールされているプラグインのリストが表示されます。
  * HOSTを選ぶと利用できる機能の一覧（Profile List）が表示されます。
  * そこから、vibrationを選び、Vibrateを選ぶと端末が振動します（最初の利用時にプラグイン利用の許可が必要です）。



##外部からのアクセスについて

* demoWebSiteのURLにIPアドレスのパラメータを付加することで、ローカルネットワーク上の他の端末で動作するDeviceConnect WebAPIの操作も可能になります。ただし、操作される側の端末に以下の設定が必要です。

 _※遠隔で意図しない端末の操作およびデータ参照をされるリスクが伴います。信頼が出来ないローカルネットワーク環境では利用しないでください。_

1. 上記の動作確認と同様の手順で、操作対象の端末にDeviceConnect WebAPIをセットアップしてください。

2. DeviceConnectManagerをAndroidのランチャーから起動し、DeviceConnectManagerを一旦OFFにしてください。

3. Allow External IPのチェックを有効化し、DeviceConnectManagerをONにしてください。

4. 操作する側（PC等）のdemoWebSiteからHTMLを開き、操作対象のIPアドレスのパラメータを付加してください。
```
    [例] file:///C:/demoWebSite/demo/index.html?ip=192.168.13.3
```
   _※遠隔で意図しない端末の操作およびデータ参照をされるリスクが伴います。信頼が出来ないローカルネットワーク環境に接続される可能性がある場合はDeviceConnectManagerのAllow External IPのチェックを無効化してください_



# 対応デバイス

<table>
  <tr>
    <td>メーカー</td>
    <td>製品名</td>
    <td>機器種別</td>
    <td>対応プラグイン</td>
    <td>Android</td>
    <td>iOS</td>
    <td>備考</td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>SmartWatchMN2</td>
    <td>腕時計型デバイス</td>
    <td>MN2/SW2 プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td>通知以外の機能を利用するには、スマートウォッチ側でミニアプリの起動が必要</td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>SmartWatchSW2</td>
    <td>腕時計型デバイス</td>
    <td>MN2/SW2 プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td>通知以外の機能を利用するには、スマートウォッチ側でミニアプリの起動が必要</td>
  </tr>
  <tr>
    <td>Orbotix</td>
    <td>Sphero 2.0</td>
    <td>ボール型スマートトイ</td>
    <td>Sphero プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>Game Technologies</td>
    <td>DICE+</td>
    <td>サイコロ型スマートトイ</td>
    <td>DICE+ プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td>ソースコード開示なし、開発者向けファームウェアへのアップデートが必要</td>
  </tr>
  <tr>
    <td>Philips</td>
    <td>hue</td>
    <td>スマートライト</td>
    <td>hue プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>Philips</td>
    <td>Bloom Lamp</td>
    <td>スマートライト</td>
    <td>hue プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>Philips</td>
    <td>LightStrips</td>
    <td>スマートライト</td>
    <td>hue プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>IRKit</td>
    <td>IRKit</td>
    <td>赤外線リモコン</td>
    <td>IRKit プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td>学習した赤外線機器を紐付け</td>
  </tr>
  <tr>
    <td>Epson</td>
    <td>Moverio BT-200</td>
    <td>メガネ型デバイス</td>
    <td>AndroidHost プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td>個別の拡張機能にも対応予定</td>
  </tr>
  <tr>
    <td>Vuzix</td>
    <td>M100 Smart Glass</td>
    <td>メガネ型デバイス</td>
    <td>AndroidHost プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td>個別の拡張機能にも対応予定</td>
  </tr>
  <tr>
    <td>ウエストユニティス</td>
    <td>Inforod</td>
    <td>メガネ型デバイス</td>
    <td>AndroidHost プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td>個別の拡張機能にも対応予定</td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>DSC-QX100</td>
    <td>レンズスタイルカメラ</td>
    <td>SonyCamera プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>DSC-QX10</td>
    <td>レンズスタイルカメラ</td>
    <td>SonyCamera プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>ActionCam</td>
    <td>レンズスタイルカメラ</td>
    <td>SonyCamera プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>Pebble</td>
    <td>Pebble</td>
    <td>腕時計型デバイス</td>
    <td>Pebble プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>-</td>
    <td>Android端末 Ver4.2以降</td>
    <td>Android端末</td>
    <td>AndroidHost プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>LG</td>
    <td>G Watch</td>
    <td>Android  Wear端末</td>
    <td>Wear プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td>仮対応のため、ADBでのAPKのインストールが必要</td>
  </tr>
  <tr>
    <td>Samsung</td>
    <td>Gear Live</td>
    <td>Android  Wear端末</td>
    <td>Wear プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td>仮対応のため、ADBでのAPKのインストールが必要</td>
  </tr>
  <tr>
    <td>Google</td>
    <td>ChromeCast</td>
    <td>HDMIドングル</td>
    <td>ChromeCast プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td>Google Cast SDK Developer ConsoleのページでデバイスとReceiverアプリの登録が必要</td>
  </tr>
  <tr>
    <td>Google</td>
    <td>NexusPlayer</td>
    <td>AndroidTV</td>
    <td>ChromeCast プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td>Google Cast SDK Developer ConsoleのページでデバイスとReceiverアプリの登録が必要</td>
  </tr>

  <tr>
    <td>Polar</td>
    <td>H7</td>
    <td>心拍計</td>
    <td>BLE HeartRate プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>Mio Global</td>
    <td>Mio Alpha</td>
    <td>心拍計</td>
    <td>BLE HeartRate プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>Mio Global</td>
    <td>Mio Fuse</td>
    <td>心拍計</td>
    <td>BLE HeartRate プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
    <tr>
    <td>EPSON</td>
    <td>Pulsense PS-500</td>
    <td>心拍計</td>
    <td>BLE HeartRate プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
    <tr>
    <td>EPSON</td>
    <td>Pulsense PS-100</td>
    <td>心拍計</td>
    <td>BLE HeartRate プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>NTTドコモ</td>
    <td>Hitoe/td>
    <td>心拍計、他</td>
    <td>Hitoe プラグイン</td>
    <td>○</td>
    <td>○</td>
    <td>開発中</td>
  </tr>
  <tr>
    <td>A&D</td>
    <td>UT-201BLE</td>
    <td>体温計</td>
    <td></td>
    <td>○</td>
    <td>x</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON</td>
    <td>HVC-C</td>
    <td>Human Vision Components</td>
    <td>HVC プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON</td>
    <td>HVC-C2W</td>
    <td>Human Vision Components</td>
    <td>HVC2W プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON</td>
    <td>HVC-P</td>
    <td>Human Vision Components</td>
    <td>HVCP プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>FUJITSU</td>
    <td>F-PLUG</td>
    <td>スマートメーター</td>
    <td>F-PLUG プラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>RICOH</td>
    <td>THETA m15</td>
    <td>Spherical Camera</td>
    <td>THETAデバイスプラグイン</td>
    <td>○</td>
    <td>○</td>
    <td>RICOH THETA Developersにて開発者登録、およびSDKのダウンロードが必要。</td>
  </tr>
  <tr>
    <td>LIFX</td>
    <td>White 800</td>
    <td>ライト</td>
    <td>AllJoynプラグイン</td>
    <td>○</td>
    <td>○</td>
    <td>White 800の色は変えられない。</td>
  </tr>
  <tr>
    <td>EchonetLite</td>
    <td></td>
    <td>家電製品制御</td>
    <td>EchoneLiteプラグイン</td>
    <td>○</td>
    <td>×</td>
    <td>準拠機器全般</td>
  </tr>
  <tr>
    <td>Linking</td>
    <td></td>
    <td>BLE連携デバイス</td>
    <td>Linkingプラグイン</td>
    <td>○</td>
    <td>○</td>
    <td>準拠機器全般。開発中</td>
  </tr>
  <tr>
    <td></td>
    <td>UVC(USB Video Class)</td>
    <td>Webカメラ</td>
    <td>UVCプラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td></td>
    <td>テレビ会議</td>
    <td>WebRTCプラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>インフィニテグラ</td>
    <td>OWLIFT</td>
    <td>サーマルカメラ</td>
    <td></td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>FaBo</td>
    <td>FaBo</td>
    <td>IoTHWプロトタイプキット</td>
    <td>FaBoプラグイン</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>

</table>

* 本プロジェクトはＮＴＴドコモとしての正式なサービス提供ではなく、実験的なソースコード開示です。仕様やセキュリティの検討を目的としております。
* 各メーカーが動作を保障するものではありません。
* 利用できる機能は各機器で異なります。
* 利用する開発用ライブラリの都合により、プラグインのソースコードは一部機器についての開示となります。
* 各種ドキュメントの整備、対応デバイスの拡大についても順次進めていく予定です。
* 利便性の改善やセキュリティ向上のため、仕様が変更となる場合があります。予めご了承ください。
