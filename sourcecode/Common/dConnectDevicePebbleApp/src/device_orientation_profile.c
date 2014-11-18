#include "pebble_device_plugin_defines.h"
#include "pebble_device_plugin.h"
#include "device_orientation_profile.h"
#include "message_queue.h"

/*!
 @define 何回サンプリングをしたら、in_event_accel_handler(コールバック関数)を呼び出すか
 */
#define PER_UPDATE 3 // 10 にすると delete が取得できるが、3や4だとかなりの確率で取得できない(Pebble側で受信できていない)

/////////////////////////////////////////////////////////////////////
// Device Orientation
/////////////////////////////////////////////////////////////////////

/*!
 @brief ログ出力のオーバーヘッドが大きい場合に、ログ出力を間引く為のカウンタ
 */
static int counter_for_log = 0 ;

/*!
 @breif 最後に加速度を送った時間を保持する.
 */
static int32_t last_event_time = 0;

/*!
 @brief app_message_outbox_begin() が失敗すると、連続で失敗する。一定時間の間、
        送信を止める処理に使用する。
 */
static int outbox_wait_flag = 0 ;

/*!
 @brief 現在時間(ミリ秒)を取得する。
 @return 現在時刻(ミリ秒)
 */
static int32_t get_current_time() {
    time_t sec;
    uint16_t ms;
    time_ms(&sec, &ms);
    return (int32_t) 1000 * (int32_t) sec + (int32_t) ms;
}


/*!
 @brief 加速度の値を取得するコールバック関数。

 @param[in] data 加速度データ
 @param[in] num_samples サンプル数
 */
static void in_event_accel_handler(AccelData *data, uint32_t num_samples) {

    if (outbox_wait_flag != 0) {
        int32_t t = get_current_time();
        if (t - last_event_time > 1500) {
            outbox_wait_flag = 0;
        } else {
            return ;
        }
    }

	if (!mq_push()) {
		entry_log( "error", "in_event_accel_handler" );
		return;
	}
	
    int32_t t = get_current_time();
    int32_t interval = t - last_event_time;
    last_event_time = t;

    pebble_sniff_interval_normal();
	
	mq_kv_set(KEY_ACTION, ACTION_EVENT);
	mq_kv_set(KEY_PROFILE, PROFILE_DEVICE_ORIENTATION);
	mq_kv_set(KEY_ATTRIBUTE, DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION);
	mq_kv_set(KEY_PARAM_DEVICE_ORIENTATION_X, data->x);
	mq_kv_set(KEY_PARAM_DEVICE_ORIENTATION_Y, data->y);
	mq_kv_set(KEY_PARAM_DEVICE_ORIENTATION_Z, data->z);
	mq_kv_set(KEY_PARAM_DEVICE_ORIENTATION_INTERVAL, interval);

    counter_for_log ++ ;
    {
        char title[ 20 ] ;
        snprintf( title, sizeof( title), "accel %d",counter_for_log ) ;
        char buf[ 30 ] ;
        snprintf( buf, sizeof( buf), "X%d Y%d Z%d", data->x, data->y, data->z ) ;
        replace_last_log( title, buf) ;
    }

	send_message();
}

/*!
 @brief putメソッド,DeviceOrientationプロファイルのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_put_device_orientation_handler(DictionaryIterator *received) {
    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION:
        accel_data_service_subscribe(PER_UPDATE, &in_event_accel_handler);//イベントハンドラと呼び出す回数の設定
        accel_service_set_sampling_rate(ACCEL_SAMPLING_10HZ);//サンプリングレートの設定
        break;
    default:
        // not support
        pebble_set_error_code(ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

/*!
 @brief deleteメソッド,DeviceOrientationプロファイルのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_delete_device_orientation_handler(DictionaryIterator *received) {

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION:
        accel_data_service_unsubscribe();
        pebble_sniff_interval_reduced();
        break;
    default:
        // not support
        entry_log( "not support", "orientation" ) ;
        pebble_set_error_code(ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

void orientation_service_pause(void) {
    outbox_wait_flag = 1;
    last_event_time = get_current_time();
}

void orientation_service_unsubscribe_force( void ) {
    accel_data_service_unsubscribe();
}

int in_received_device_orientation_handler(DictionaryIterator *received) {
    Tuple *actionTuple = dict_find(received, KEY_ACTION);
    switch (actionTuple->value->uint8) {
    case ACTION_PUT:
        entry_log2( "put", "orientation" ) ;
        counter_for_log = 0 ;
        last_event_time = get_current_time();
        outbox_wait_flag = 0 ;
        in_received_put_device_orientation_handler(received);
        break;
    case ACTION_DELETE:
        entry_log( "delete", "orientation" ) ;
        in_received_delete_device_orientation_handler(received);
        break;
    default:
        // not support.
        pebble_set_error_code(ERROR_NOT_SUPPORT_ACTION);
        break;
    }
    return RETURN_SYNC;
}
