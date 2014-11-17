#ifndef battery_profile_h
#define battery_profile_h

/*!
 @brief バッテリープロファイルの処理を行う.

 @param received 受信したメッセージデータ
 @param iter レスポンスを格納するイテレータ

 @retval RETURN_SYNC 同期
 @retval RETURN_ASYNC 非同期
 */
int in_received_battery_handler(DictionaryIterator *received);


/*!
 @brief イベントを強制的に中止する
 */
void battery_service_unsubscribe_force( void );

#endif	/* battery_profile_h */
