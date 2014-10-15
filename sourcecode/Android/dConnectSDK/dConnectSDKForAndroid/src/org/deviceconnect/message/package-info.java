/*
 org.deviceconnect.message
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
/**
 * Device Connect のメッセージ及び、メッセージの操作に必要な全てのクラスを提供する.
 *
 * <p>
 * メッセージは
 * {@link org.deviceconnect.message.basic.message.DConnectRequestMessage},
 * {@link org.deviceconnect.message.basic.message.DConnectResponseMessage} で定義され、
 * これらのデータを送受信することでスマートデバイス間のメッセージ通信を実現する。
 *
 * <p>
 * メッセージの送受信プロトコルはスマートデバイス、および、
 * Device Connect API対応アプリケーションに依存するため、必要に応じて適切なプロトコルを利用する必要がある。
 *
 * <h3>サンプルコード</h3>
 * <h4>HTTPプロトコル</h4>
 * <pre>
 * {@code
 * DConnectClient client = new HttpDConnectClient();
 *
 * DConnectRequestMessage request = new DConnectRequestMessage(
 *         DConnectRequestMessage.METHOD_GET,
 *         new URI("https://localhost:4035/gotapi/battery/level"));
 * DConnectResponseMessage response = client.execute(request);
 *
 * if (response.getResult() > 0) {
 *   // 異常系ルート
 * } else {
 *   // 正常系ルート
 * }
 * }
 * </pre>
 */
package org.deviceconnect.message;
