#ifndef system_profile_h
#define system_profile_h

/*!
 @brief Systemプロファイルのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 @retval RETURN_SYNC 同期
 @retval RETURN_ASYNC 非同期
 */
int in_received_system_handler(DictionaryIterator *received);

#endif	/* system_profile_h */
