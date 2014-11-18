#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include "pebble_device_plugin_defines.h"
#include "pebble_device_plugin.h"
#include "battery_profile.h"
#include "binary_profile.h"
#include "device_orientation_profile.h"
#include "settings_profile.h"
#include "vibration_profile.h"
#include "system_profile.h"
#include "menu_item.h"
#include "message_queue.h"

void send_message();

/*!
 @brief ウィンドウ。
 */
static Window *window;

/*!
 @brief 表示画像(Bitmap)。
 */
static GBitmap *bitmap = NULL;

/*!
 @brief 表示画像(Bitmap)の横幅。
 */
static short pbi_image_width ;


/*!
 @brief メニューを表示するためのレイヤー。
 */
static MenuLayer *menu_layer;

void pebble_set_error_code(int error_code) {
	mq_kv_set(KEY_PARAM_RESULT, RESULT_ERROR);
	mq_kv_set(KEY_PARAM_ERROR_CODE, error_code);

	char buf[ 20 ] ;
	snprintf( buf, sizeof( buf), "code=%d", error_code ) ;
	entry_log( "error", buf ) ;
}

void pebble_sniff_interval_normal( void )
{
    app_comm_set_sniff_interval(SNIFF_INTERVAL_REDUCED);//この行を削除してはいけない
    app_comm_set_sniff_interval(SNIFF_INTERVAL_NORMAL);
}
void pebble_sniff_interval_reduced( void )
{
    app_comm_set_sniff_interval(SNIFF_INTERVAL_NORMAL);//この行を削除してはいけない
    app_comm_set_sniff_interval(SNIFF_INTERVAL_REDUCED);
}

void entry_log( char* title, char* contents )
{
    entry_menu_item( title, contents, false ) ;
    menu_layer_reload_data( menu_layer) ;
}
void entry_log2( char* title, char* contents )
{
    for( int i = 0 ; i < 2 ; i ++ ) {
        entry_menu_item( title, contents, false ) ;
    }
    menu_layer_reload_data( menu_layer) ;
}
void entry_gbitmap_log( char* title, void* contents )
{
    entry_menu_item( title, contents, true ) ;
    menu_layer_reload_data( menu_layer) ;
}
void replace_last_log( char* title, char* contents )
{
    replace_menu_item( title, contents, false ) ;
    menu_layer_reload_data( menu_layer) ;
}

void pebble_set_bitmap(uint8_t* data, int32_t size) {
    if (data == NULL) {
        return;
    }
    pbi_image_width = get_pbi_image_width( data ) ;
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "width=%d",pbi_image_width);

    // 前に表示していたbitmapを削除
    if (bitmap != NULL) {
        gbitmap_destroy(bitmap);
    }

    bitmap = gbitmap_create_with_data((const uint8_t *) data);
    entry_gbitmap_log( "bitmap", bitmap ) ;
}

/*!
 @brief 端末からのメッセージをPebbleで受信成功。

 @param[in] received 受信データ
 @param[in] context コンテキスト
 */
static void in_received_handler(DictionaryIterator *received, void *context) {
    Tuple *profileTuple = dict_find(received, KEY_PROFILE);
    if (profileTuple != NULL) {
        if (profileTuple->value->uint8 == PROFILE_BINARY) {
            in_received_binary_handler(received, NULL);
            return;
        }
    } else {
        // error
        return;
    }

    Tuple *requestCodeTuple = dict_find(received, KEY_PARAM_REQUEST_CODE);
    if (requestCodeTuple == NULL) {
        return;
    }

	if (!mq_push()) {
		entry_log( "error", "in_received_handler" ) ;
		return;
	}

	// リクエストコード追加
	mq_kv_set(KEY_PARAM_REQUEST_CODE, requestCodeTuple->value->uint32);

    int ret = RETURN_SYNC;

    // 各プロファイル
    switch (profileTuple->value->uint8) {
    case PROFILE_BATTERY:
        ret = in_received_battery_handler(received);
        break;
    case PROFILE_DEVICE_ORIENTATION:
        ret = in_received_device_orientation_handler(received);
        break;
    case PROFILE_SETTING:
        ret = in_received_setting_handler(received);
        break;
    case PROFILE_VIBRATION:
        ret = in_received_vibration_handler(received);
        break;
    case PROFILE_SYSTEM:
        ret = in_received_system_handler(received);
        break;
    default:
        {
            char buf[ 20 ] ;
            snprintf( buf, sizeof( buf), "profile=%d error", profileTuple->value->uint8) ;
            entry_log( "profile error", buf ) ;
        }

        pebble_set_error_code(ERROR_NOT_SUPPORT_PROFILE);
        break;
    }

    // 非同期でレスポンスすることがあるのか？
    if (ret == RETURN_SYNC) {
		send_message();
    }
}

/*!
 @brief 端末からのメッセージをPebbleで受信失敗。

 @param[in] reasion 受信失敗理由
 @param[in] context コンテキスト
 */
static void in_dropped_handler(AppMessageResult reason, void *context) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in dropped handler");
    char buf[ 20 ] ;
    snprintf( buf, sizeof( buf), "err=0x%x", reason ) ;
    entry_log( "receive error", buf ) ;
}

/*!
 @brief Pebbleから端末への送信成功。

 @param[in] sent 送信したデータ
 @param[in] context コンテキスト
 */
static void out_sent_handler(DictionaryIterator *sent, void *context) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "out sent handler");
	
	success_message();
}

/*!
 @brief Pebbleから端末への送信失敗。

 @param[in] failed 送信失敗したデータ
 @param[in] reason 送信失敗理由
 @param[in] context コンテキスト
 */
static void out_failed_handler(DictionaryIterator *failed, AppMessageResult reason, void *context) {
	DBG_LOG(APP_LOG_LEVEL_DEBUG, "out failed handler");
	
    if( reason != APP_MSG_OK ) {
        char buf[ 20 ] ;
        snprintf( buf, sizeof( buf), "err=0x%x", reason ) ;
        entry_log( "out_failed_handler", buf ) ;
    }

    switch (reason) {
    case APP_MSG_OK:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_OK");
        break;
    case APP_MSG_SEND_TIMEOUT:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_SEND_TIMEOUT");
        break;
    case APP_MSG_SEND_REJECTED:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_SEND_REJECTED");
        break;
    case APP_MSG_NOT_CONNECTED:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_NOT_CONNECTED");
        break;
    case APP_MSG_APP_NOT_RUNNING:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_APP_NOT_RUNNING");
        break;
    case APP_MSG_INVALID_ARGS:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_INVALID_ARGS");
        break;
    case APP_MSG_BUSY:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_BUSY");
        break;
    case APP_MSG_BUFFER_OVERFLOW:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_BUFFER_OVERFLOW");
        break;
    case APP_MSG_ALREADY_RELEASED:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_ALREADY_RELEASED");
        break;
    case APP_MSG_CALLBACK_ALREADY_REGISTERED:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_CALLBACK_ALREADY_REGISTERED");
        break;
    case APP_MSG_CALLBACK_NOT_REGISTERED:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_CALLBACK_NOT_REGISTERED");
        break;
    case APP_MSG_OUT_OF_MEMORY:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_OUT_OF_MEMORY");
        break;
    case APP_MSG_CLOSED:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_CLOSED");
        break;
    case APP_MSG_INTERNAL_ERROR:
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "APP_MSG_INTERNAL_ERROR");
        break;
    }
	
	//
	retry_message();
}


/*!
 @brief メニューのセクション個数を返す。OS から呼び出されるコールバック関数である。

 @param[in] menu_layer メニューのレイヤー
 @param[in] data データ
 @param[out] このアプリは、セクションは1つしか使わないので、常に1を返す
 */
static uint16_t menu_get_num_sections_callback(MenuLayer *menu_layer, void *data) {
    return 1;
}

/*!
 @brief メニューの個数を返す。OS から呼び出されるコールバック関数である。

 @param[in] menu_layer メニューのレイヤー
 @param[in] section_index セクション番号
 @param[in] data データ
 @param[out] このアプリは、セクションは1つしか使わないので、常にセクション0に対応するメニューの個数を返す
 */
static uint16_t menu_get_num_rows_callback(MenuLayer *menu_layer, uint16_t section_index, void *data) {
    return how_many_menu_item() ;
}

/*!
 @brief ヘッダー文字列を表示する高さを返す。OS から呼び出されるコールバック関数である。

 @param[in] menu_layer メニューのレイヤー
 @param[in] section_index セクション番号
 @param[in] data データ
 @param[out] ヘッダー文字列の高さ。
 */
static int16_t menu_get_header_height_callback(MenuLayer *menu_layer, uint16_t section_index, void *data) {
    return 2 ;//セクションは表示しても意味が無いので、最小の値にしておく
}

/*!
 @brief  メニューを表示する高さを返す。OS から呼び出されるコールバック関数である。

 @param[in] menu_layer メニューのレイヤー
 @param[in] cell_index セクション番号とメニュー番号情報が入っている構造体
 @param[in] callback_context コールバック関数
 @param[out] メニューを表示する高さ
 */
int16_t menu_get_cell_height(struct MenuLayer *menu_layer, MenuIndex *cell_index, void *callback_context)
{
    MenuItem* menu = get_menu_item( cell_index->row ) ;
    if( menu->is_gbitmap ) {
        return 120 ;// GBitmap の場合
    }
    return 36 ;//文字列の場合
}

/*!
 @brief  メニューのセクションを表示する。OS から呼び出されるコールバック関数である。

 @param[in] ctx context
 @param[in] cell_layer メニューのレイヤー
 @param[in] section_index セクション番号
 @param[in] data data
 */
static void menu_draw_header_callback(GContext* ctx, const Layer *cell_layer, uint16_t section_index, void *data) {
    menu_cell_basic_header_draw(ctx, cell_layer, " ");//メニューセクションは1つだけ
}

/*!
 @brief  メニューを表示する。OS から呼び出されるコールバック関数である。

 @param[in] ctx context
 @param[in] cell_layer メニューのレイヤー
 @param[in] cell_index セクション番号とメニュー番号情報が入っている構造体
 @param[in] data data
 */
static void menu_draw_row_callback(GContext* ctx, const Layer *cell_layer, MenuIndex *cell_index, void *data) {

    MenuItem* menu = get_menu_item( cell_index->row ) ;
    if( menu->is_gbitmap ) {
        if( is_under_receive() ) {
            menu_cell_basic_draw(ctx, cell_layer, "recieving...", "image", NULL );
        }
        else {//バイナリデータ受信中でなければ描画する
            GSize draw_size = layer_get_frame((Layer*) cell_layer).size ;
            if( pbi_image_width < draw_size.w ) {
                draw_size.w = pbi_image_width ;
            }
            DBG_LOG(APP_LOG_LEVEL_DEBUG, "draw width=%d",draw_size.w);

            graphics_draw_bitmap_in_rect(ctx, menu->bitmap_or_text
                                     , (GRect){ .origin = GPointZero, .size = draw_size });
        }
        return ;
    }
    menu_cell_basic_draw(ctx, cell_layer, menu->title, menu->bitmap_or_text, NULL );
}

/*!
 @brief  メニューが選択された場合に、OS から呼び出されるコールバック関数である。

 @param[in] menu_layer メニューのレイヤー
 @param[in] cell_index セクション番号とメニュー番号情報が入っている構造体
 @param[in] data data
 */
static void menu_select_callback(MenuLayer *menu_layer, MenuIndex *cell_index, void *data) {
    return ;//何もしない
}

/*!
 @brief ウィンドウの初期化。
 */
static void window_load(Window *window) {
    Layer *window_layer = window_get_root_layer(window);

    // menu layerの作成
    GRect bounds = layer_get_frame(window_layer);
    menu_layer = menu_layer_create(bounds);

    // menu layer のコールバック関数等の初期化
    menu_layer_set_callbacks(menu_layer, NULL, (MenuLayerCallbacks){
            .get_num_sections = menu_get_num_sections_callback,
                .get_num_rows = menu_get_num_rows_callback,
                .get_cell_height = menu_get_cell_height,
                .get_header_height = menu_get_header_height_callback,
                .draw_header = menu_draw_header_callback,
                .draw_row = menu_draw_row_callback,
                .select_click = menu_select_callback,
                });

    // menu layer にプッシュボタンの操作を割り付ける
    menu_layer_set_click_config_onto_window(menu_layer, window);

    // Add it to the window for display
    layer_add_child(window_layer, menu_layer_get_layer(menu_layer));

    init_menu_item();

}

/*!
 @brief ウィンドウの後始末。
 */
static void window_unload(Window *window) {
}

/*!
 @brief アプリの初期化。
 */
static void init() {
    window = window_create();
    //window_set_background_color(window, GColorBlack);
    window_set_background_color(window, GColorWhite);
    window_set_fullscreen(window, true);
    window_set_window_handlers(window, (WindowHandlers) {
        .load = window_load,
        .unload = window_unload
    });


    const int inbound_size = 128;
    const int outbound_size = 128;
    app_message_open(inbound_size, outbound_size);

    const bool animated = true;
    window_stack_push(window, animated);

    // 以下の通信ハンドラの設定は、window_stack_push()の後で行う。
    // バイナリー受信完了時に呼び出されるコールバック関数と、もともとの in_received_handler
    // を指定して通信初期化
    app_message_register_outbox_sent(out_sent_handler);
    app_message_register_outbox_failed(out_failed_handler);
    app_message_register_inbox_received(in_received_handler);
    app_message_register_inbox_dropped(in_dropped_handler);
	
	mq_init();
}

/*!
 @brief アプリの後始末。
 */
static void deinit() {
    // 加速度情報更新終了
    accel_data_service_unsubscribe();
    // バッテリー情報更新処理終了
    battery_state_service_unsubscribe();
    // メニュー使用後の後処理
    menu_cleanup() ;
    // バイナリの一時データを初期化
    binary_cleanup();
    // 画像の後始末
    if (bitmap != NULL) {
        gbitmap_destroy(bitmap);
    }
    menu_layer_destroy(menu_layer);
    window_destroy(window);
}

/*!
  @brief エントリポイント。
 */
int main(void) {
    init();
    app_event_loop();
    deinit();
}
