#ifndef pebble_device_plugin_h
#define pebble_device_plugin_h

#include "pebble.h"
#include "pebble_device_plugin_defines.h"
#include "message_queue.h"
#include "send_message.h"


//デバッグログ出力に関する定義
//#define DEBUG_MODE //この行をコメントにすると、DBG_MSG の出力は抑制される
#ifdef DEBUG_MODE
  #define DBG_LOG(args...) APP_LOG(args)
#else  //DEBUG_MODE
  #define DBG_LOG(args...)
#endif //DEBUG_MODE


/*!
 @brief エラーコードを設定する。

 @param[in] iter エラーコードを設定するイテレータ
 @param[in] error_code エラーコード
 */
//void pebble_set_error_code(DictionaryIterator *iter, int error_code);
void pebble_set_error_code(int error_code);

/*!
 @brief 画面に指定された画像を表示する。

 @param[in] data GBitmapのデータ
 @param[in] size データサイズ
 */
void pebble_set_bitmap(uint8_t* data, int32_t size);

/*!
 @brief Bluetooth の通信速度を上げる(消費電力は高くなる)。
 */
void pebble_sniff_interval_normal( void );

/*!
 @brief Bluetooth の通信速度を下げる(消費電力は低くなる)。
 */
void pebble_sniff_interval_reduced( void );

/*!
 @brief 画面上にログを追加する

 @param[in] title 文字列
 @param[in] contents 文字列
 */
void entry_log( char* titleStr, char* contents ) ;

/*!
 @brief 画面上にログを2重に追加する。これは、replace_last_log() との兼ね合いで使用する。

 @param[in] title 文字列
 @param[in] contents 文字列
 */
void entry_log2( char* title, char* contents ) ;

/*!
 @brief 画面上にログを追加する

 @param[in] title 文字列
 @param[in] gbitmap
 */
void entry_gbitmap_log( char* titleStr, void* contents ) ;

/*!
 @brief 最新のログと置き換える

 @param[in] title 文字列
 @param[in] contents 文字列
 */
void replace_last_log( char* titleStr, char* contents ) ;

#endif	/* pebble_device_plugin_h */
