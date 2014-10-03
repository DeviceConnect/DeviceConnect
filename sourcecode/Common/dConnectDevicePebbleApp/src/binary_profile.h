#ifndef binary_profile_h
#define binary_profile_h

/*!
 @brief バイナリープロファイルの処理を行う.

 @param received 受信したメッセージデータ
 @param iter レスポンスを格納するイテレータ

 @retval RETURN_SYNC 同期
 @retval RETURN_ASYNC 非同期
 */
int in_received_binary_handler(DictionaryIterator *received, DictionaryIterator *iter);

/*!
 @brief pbi image の横幅を求める

 @param data pbi image へのポインタ.

 @retval image の横幅
 */
int get_pbi_image_width( uint8_t* data ) ;


/*!
 @brief バイナリ送信で使用したバッファを初期化する.
 */
void binary_cleanup();

/*!
 @brief 受信中であるかどうかを判定する
 */
bool is_under_receive( void );


#endif	/* binary_profile_h */
