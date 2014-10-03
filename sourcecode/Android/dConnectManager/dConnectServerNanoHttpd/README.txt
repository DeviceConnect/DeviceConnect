d-Connect Server NanoHTTPD
----------------------------------------------------------------------------
d-Connect Server NanoHTTPD は d-Connectサーバー のNanoHTTPD版である。


注意事項
----------------------------------------------------------------------------
d-Connect Server NanoHTTPD は git submodule に依存しているのでサブモジュールを事前に初期化すること

    % git submodule init
    % git submodule update

d-Connect Server NanoHTTPD は NanoHTTPD の websocket ブランチと ssl ブランチを独自にマージし利用している。
また、d-Connect Manager にリクエストボディを渡すために HTTPSession#getSpliByte() と HTTPSession#getRlen() 
を追加している。 NanoHTTPD.javaを入れ替える場合は同等の機能を用意すること。

WebSocketを常時接続させるため、タイムアウトを制限無しに変更している。