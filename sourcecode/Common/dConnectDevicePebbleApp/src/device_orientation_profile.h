#ifndef device_orientation_profile_h
#define device_orientation_profile_h

/*!
 @brief DeviceOrientationプロファイルのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 @retval RETURN_SYNC 同期
 @retval RETURN_ASYNC 非同期
 */
int in_received_device_orientation_handler(DictionaryIterator *received);

/*!
 @brief DeviceOrientationのイベントを一時的に送信をやめる。
 一定時間経過後に送信を再開する。
 DeviceOrientationのイベントが開始されていない場合には、特に何も起こりません。
 */
void orientation_service_pause(void);

/*!
 @brief DeviceOrientation のイベントを強制終了
*/
void orientation_service_unsubscribe_force( void );

#endif	/* device_orientation_profile_h */
