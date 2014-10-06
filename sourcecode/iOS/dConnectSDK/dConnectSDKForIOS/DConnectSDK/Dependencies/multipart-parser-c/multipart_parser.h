/* Based on node-formidable by Felix Geisendörfer 
 * Igor Afonov - afonov@gmail.com - 2012
 * MIT License - http://www.opensource.org/licenses/mit-license.php
 */
#ifndef _multipart_parser_h
#define _multipart_parser_h

#ifdef __cplusplus
extern "C"
{
#endif

#include <stdlib.h>
#include <ctype.h>

typedef struct multipart_parser multipart_parser;
typedef struct multipart_parser_settings multipart_parser_settings;
typedef struct multipart_parser_state multipart_parser_state;

  /// コールバック関数型（データ引数あり）
typedef int (*multipart_data_cb) (multipart_parser*, const char *at, size_t length);
  /// コールバック関数型（データ引数なし）
typedef int (*multipart_notify_cb) (multipart_parser*);

  /**
   * マルチパートパーサの設定
   * マルチパートにおける特定トークン（boundaryやContent-Typeヘッダなど）の開始・終了を検知した際に
   * 呼び出されるコールバック関数をここで指定する。
   */
struct multipart_parser_settings {
  /// ヘッダー項目名を検出した際に呼ばれるコールバック。
  multipart_data_cb on_header_field;
  /// ヘッダー項目の値を検出した際に呼ばれるコールバック。
  multipart_data_cb on_header_value;
  /// マルチパートのコンテンツ部分を検出した際に呼ばれるコールバック。
  multipart_data_cb on_part_data;

  multipart_notify_cb on_part_data_begin;
  /// ヘッダー部分が終了した際に呼ばれるコールバック
  multipart_notify_cb on_headers_complete;
  multipart_notify_cb on_part_data_end;
  multipart_notify_cb on_body_end;
};

  /**
   * 初期化
   * @param boundary マルチパートContent-Typeに指定されたboundaryパラメータ値
   * @param settings マルチパートパーサの設定
   * @return 初期化されたマルチパートパーサ
   */
multipart_parser* multipart_parser_init
    (const char *boundary, const multipart_parser_settings* settings);

  /**
   * マルチパートパーサを解放する。
   * @param p マルチパートパーサ
   */
void multipart_parser_free(multipart_parser* p);

  /**
   * マルチパート解析を実行する。
   * <code>multipart_parser_init()</code>で指定した設定に従い、コールバック関数が呼ばれる。
   * @param p マルチパートパーサ
   * @param buf マルチパート解析を行う文字列
   * @param len <code>buf</code>の長さ
   * @return ?<code>buf</code>中のエラーが発生した位置?
   */
size_t multipart_parser_execute(multipart_parser* p, const char *buf, size_t len);

void multipart_parser_set_data(multipart_parser* p, void* data);
void * multipart_parser_get_data(multipart_parser* p);

#ifdef __cplusplus
} /* extern "C" */
#endif

#endif
