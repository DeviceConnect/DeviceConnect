#ifndef vibration_profile_h
#define vibration_profile_h

/*!
 @brief Vibrationプロファイルのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 @retval RETURN_SYNC 同期
 @retval RETURN_ASYNC 非同期
 */
int in_received_vibration_handler(DictionaryIterator *received);

#endif	/* vibration_profile_h */
