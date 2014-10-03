Device Connect Device Plugin
----------------------------------------------------------------------------
Device Connect Device Plugin は Device Connectデバイスプラグインアプリケーション である。


How to Build
----------------------------------------------------------------------------

アプリケーションのビルド

    % ant debug


How to Maintain code quality 
----------------------------------------------------------------------------

Static Code Analysis
============================================================================
Eclipse Plugin に以下のソフトウェアをインストールし、Error, Warning を解消することでコード品質を保つ。

 * FindBugs     - http://findbugs.sourceforge.net
 * CheckStyle   - http://checkstyle.sourceforge.net
   * CheckStyle-CS - http://eclipse-cs.sourceforge.net 

Javaソースコードスタイルは以下のガイドラインに従う。
http://source.android.com/source/code-style.html


Logging
============================================================================
java.util.logging.Logger API を使用して entering, exiting を含めてログを出力する。
Android ADB に対するログハンドラーは AndroidHandler を利用する。

デバッグログは基本的に Logging#fine を用いてログを出力する。
マーキングとして一般ユーザから見て出力されて意味のあるログは Logging#info を用いてログ出力する。
Logging#warning, Logging#severe は警告、エラーの出力に対してログ出力する。

