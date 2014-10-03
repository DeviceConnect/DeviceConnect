#include "pebble_device_plugin_defines.h"
#include "pebble_device_plugin.h"
#include "device_orientation_profile.h"

/*!
 @define 何回サンプリングをしたら、in_event_accel_handler(コールバック関数)を呼び出すか
 */
#define PER_UPDATE 5 // 10 にすると delete が取得できるが、3や4だとかなりの確率で取得できない(Pebble側で受信できていない)

/////////////////////////////////////////////////////////////////////
// Device Orientation
/////////////////////////////////////////////////////////////////////

/*!
 @brief ログ出力のオーバーヘッドが大きい場合に、ログ出力を間引く為のカウンタ
 */
static int counterForLog = 0 ;

/*!
 @brief app_message_outbox_begin() が失敗すると、連続で失敗する。一定時間の間、
        送信を止める処理に使用する。
 */
static int outbox_wait_period_counter = 0 ;

/*!
 @brief トータル送信回数

 */
static int total_send_counter = 0 ;

/*!
 @brief 加速度の値を取得するコールバック関数。

 @param[in] data 加速度データ
 @param[in] num_samples サンプル数

 */
static void in_event_accel_handler(AccelData *data, uint32_t num_samples) {
    //APP_LOG(APP_LOG_LEVEL_DEBUG, "in_event_accel_handler");

    if( outbox_wait_period_counter > 0 ) {
        outbox_wait_period_counter -- ;
        return ;
    }

#if 0  //以下はあまり効果がない
    //あまり連続して送信すると、android 側からの受信が出来なくなるので、少し休む処理
    total_send_counter ++ ;
    if( ( total_send_counter % 20 ) == 0 ) {
        entry_log2( "accel", "pause" ) ;
        outbox_wait_period_counter = 12 ;
    }
#endif        

    DictionaryIterator *iter = NULL;
    app_message_outbox_begin(&iter);
    if (iter == NULL) {
        outbox_wait_period_counter = 6 ;
        entry_log( "error", "in_event_accel_handler" ) ; // 送信用イテレータの作成に失敗
        return;
    }
    pebble_sniff_interval_normal();
    Tuplet actionTuple = TupletInteger(KEY_ACTION, ACTION_EVENT);
    dict_write_tuplet(iter, &actionTuple);

    Tuplet profileTuple = TupletInteger(KEY_PROFILE, PROFILE_DEVICE_ORIENTATION);
    dict_write_tuplet(iter, &profileTuple);

    Tuplet attributeTuple = TupletInteger(KEY_ATTRIBUTE, DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION);
    dict_write_tuplet(iter, &attributeTuple);

    Tuplet xTuple = TupletInteger(KEY_PARAM_DEVICE_ORIENTATION_X, data->x);
    dict_write_tuplet(iter, &xTuple);

    Tuplet yTuple = TupletInteger(KEY_PARAM_DEVICE_ORIENTATION_Y, data->y);
    dict_write_tuplet(iter, &yTuple);

    Tuplet zTuple = TupletInteger(KEY_PARAM_DEVICE_ORIENTATION_Z, data->z);
    dict_write_tuplet(iter, &zTuple);

    Tuplet intervalTuple = TupletInteger(KEY_PARAM_DEVICE_ORIENTATION_INTERVAL, 100);
    dict_write_tuplet(iter, &intervalTuple);

    counterForLog ++ ;
    if( 1 ) { //if( ( counterForLog % 4 ) == 0 ) {
        char title[ 20 ] ;
        snprintf( title, sizeof( title), "accel %d",counterForLog ) ;
        char buf[ 30 ] ;
        snprintf( buf, sizeof( buf), "X%d Y%d Z%d", data->x, data->y, data->z ) ;

        replace_last_log( title, buf) ;
    }


    dict_write_end(iter);
    app_message_outbox_send();
}

/*!
 @brief putメソッド,DeviceOrientationプロファイルのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_put_device_orientation_handler(DictionaryIterator *received, DictionaryIterator *iter) {
    //APP_LOG(APP_LOG_LEVEL_DEBUG, "in_received_put_device_orientation_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION:
        accel_data_service_subscribe(PER_UPDATE, &in_event_accel_handler);//イベントハンドラと呼び出す回数の設定
        accel_service_set_sampling_rate(ACCEL_SAMPLING_10HZ);//サンプリングレートの設定
        break;
    default:
        // not support
        pebble_set_error_code(iter, ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

/*!
 @brief deleteメソッド,DeviceOrientationプロファイルのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_delete_device_orientation_handler(DictionaryIterator *received, DictionaryIterator *iter) {

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION:
        accel_data_service_unsubscribe();
        pebble_sniff_interval_reduced();
        break;
    default:
        // not support
        entry_log( "not support", "orientation" ) ;
        pebble_set_error_code(iter, ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

void orientation_service_unsubscribe_force( void )
{
    accel_data_service_unsubscribe();
}


int in_received_device_orientation_handler(DictionaryIterator *received, DictionaryIterator *iter) {
    Tuple *actionTuple = dict_find(received, KEY_ACTION);
    switch (actionTuple->value->uint8) {
    case ACTION_PUT:
        entry_log2( "put", "orientation" ) ;
        counterForLog = 0 ;
        outbox_wait_period_counter = 0 ;
        in_received_put_device_orientation_handler(received, iter);
        break;
    case ACTION_DELETE:
        entry_log( "delete", "orientation" ) ;
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_delete_device_orientation_handler");
        in_received_delete_device_orientation_handler(received, iter);
        break;
    default:
        // not support.
        pebble_set_error_code(iter, ERROR_NOT_SUPPORT_ACTION);
        break;
    }
    return RETURN_SYNC;
}
