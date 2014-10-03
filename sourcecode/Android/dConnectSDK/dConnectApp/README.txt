d-Connect App Sample for Android
----------------------------------------------------------------------------
d-Connect App Sample for Android は d-Connectアプリケーション の Androidサンプルアプリケーション である

How to Build on Command Line
----------------------------------------------------------------------------
コマンドプロンプト上でのサンプルアプリケーションプロジェクトのビルドとインストール

サンプルアプリケーションは d-Connect SDK for Android に依存しているため、同プロジェクトのビルドが行えるよう事前に準備しておくこと


コマンドラインビルド

    % cd dConnectApp
    % android update project -p . -t [Android 4.4 Target]
    % ant debug
    % ant installd


How to Build on Eclipse
----------------------------------------------------------------------------
Eclipse上でのサンプルアプリケーションプロジェクトのビルドとインストール

1. Android アプリケーションビルド環境の整った Eclipse を起動する
2. [File] - [Import...] - [Android] - [Existing Android Code into Workspace] から以下の２つのプロジェクトをインストールする
    * android
    * example/android/dConnectApp
3. Package Explorer に登録された dConnectApp を選択して [Project] - [Build Project] でプロジェクトをビルドする
4. [Run] - [Run As...] - [Android Application] でアプリケーションをインストール、実行する

コマンドラインビルドとEclipseビルドは併用できないため、一方でビルドした後に他方でビルドする場合はクリーンビルドを実行すること


