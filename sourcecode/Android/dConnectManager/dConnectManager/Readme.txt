■ dConnect Manager
dConnect Managerとは、イベントプラグイン、デバイスプラグインをつなげるためのコンポーネントです。
イベントプラグイン、デバイスプラグインとdConnect Managerの間は、dConnect抽象化プロトコルで通信を行います。

■ Build方法
local.properties に Android SDK と Android NDK へのパスを登録します。
sdk.dirにはAndroid SDKへのパスを指定し、ndk.dirにはAndroid NDKへのパスを指定します。
SDK、NDKへのパスはインストールされた箇所になりますので、各PCの環境に合わせてください。

例) MacOSの場合
------------------------------
sdk.dir=/Applications/android-sdk-macosx
ndk.dir=/Applications/android-ndk-r9c-darwin-x86
------------------------------

local.propertiesの修正ができたら、Antを実行することでビルドすることができます。

> ant release

■ dConnect Managerのデバッグアプリの使い方
1.dConnectManagerのapkをインストールする。
2.アプリが起動すると同時にデバッグ用の画面が起動する。
3.HttpMethod(GET,POST,PUT,DELETE,マルチパート)が選択できるので、呼び出すメソッドに合わせたMethodを選ぶ。
   マルチパート時は、自動的にassetsフォルダのtest.pngを送るようになっている。
   このときのHTTPMethodはPOST。test.pngは自動的に付加されるが、deviceidなどの属性は付加されないため、
   次に説明するテキスト入力欄でGETのときのようにパラメータを指定すること。
4.実行したいRESTAPIのパスを、「/method/profile?deviceid=xxxx」と出ているテキスト入力欄に、
「http://ホスト名:ポート番号/」以下のパスのみ入力する。
   GETでもPOSTでもマルチパートでも、パラメータの指定の方式はKeyValueの形式で行うこと。
   デバッグアプリの方で内部的にそれぞれのHttpMethodの形式にあった形に変換している。
5.ここまでの準備が整ったら、「Send」ボタンをタップする。
6.その下に、HTTPのリクエストとレスポンスが表示される。
   RESTAPIによっては、レスポンスの代わりに画像が表示されるものもある。
   例)
      /file/receive

■ dConnectManagerの起動と停止
1.デバッグ画面でメニューの「Settings」をタップする。
2.dConnectManagerの設定画面が表示される。今のところは、ServerのStart/Shutdownしかない。
3.起動時は「Server On」の状態になっている。この状態でボタンをタップすると「Server Off」になる。
4.再び起動させる場合は、もう一度ボタンをタップして「Server On」にする。
5.前の画面に戻る場合は、左上のタイトル部分かバックキーをタップする。

■ dConnectManagerの使い方
dConnectManagerを別アプリから使用する場合は、dConnectManagerの設定画面で「Server On」になっていることを確認してから、
以下のようなURLにHttpリクエストを投げること。

   例)
      http://localhost:8080/{profile}/{interfaces}/{attribute}?deviceid=xxxxx

{profile}/{interfaces}/{attribute}?deviceid=xxxxxについては、RESTfulAPI仕様書を確認のこと。
もしくは、dConnectSDKのReadmeを参照。