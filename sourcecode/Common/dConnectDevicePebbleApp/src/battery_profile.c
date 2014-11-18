#include "pebble_device_plugin_defines.h"
#include "pebble_device_plugin.h"
#include "battery_profile.h"
#include "device_orientation_profile.h"

/*!
 @brief バッテリーの最後の状態を保存する
 */
static BatteryChargeState last_state ;

/*!
 @brief onchargingchange イベントが有効かどうか
 */
static bool event_onchargingchange = false ;

/*!
 @brief onbatterychange イベントが有効かどうか
 */
static bool event_onbatterychange = false ;


/*!
 @brief event_onchargingchange, event_onbatterychange の両方が false の場合に、
        battery_state_service_unsubscribe() を呼び出す

 */
static void service_unsubscribe( void )
{
    if( ( event_onchargingchange == false ) && ( event_onbatterychange == false ) ) {
        battery_state_service_unsubscribe();
    }
}
void battery_service_unsubscribe_force( void )
{
    battery_state_service_unsubscribe();
    event_onchargingchange = false ;
    event_onbatterychange = false ;
}

/*!
 @brief 充電容量を送信する。

 @param[in] percent 充電容量(%)
 */
static void send_battery_percent( int percent )
{
    // 加速度センサーが動いている場合には、一瞬止める
    orientation_service_pause();

	if (!mq_push()) {
		entry_log( "error", "send_battery_percent" ) ;
		return;
	}
	
	mq_kv_set(KEY_ACTION, ACTION_EVENT);
	mq_kv_set(KEY_PROFILE, PROFILE_BATTERY);
	mq_kv_set(KEY_ATTRIBUTE, BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE);
	mq_kv_set(KEY_PARAM_BATTERY_LEVEL, percent);
	
	send_message();

	pebble_sniff_interval_normal();
    entry_log( "send", "send_battery_percent" ) ;
}

/*!
 @brief 充電状態を送信する。

 @param[in] charging 充電中は true
 */
static void send_battery_charging( bool charging )
{
	if (!mq_push()) {
		entry_log( "error", "send_battery_charging" ) ;
		return;
	}
	
	// 加速度センサーが動いている場合には、一瞬止める
	orientation_service_pause();
	
	mq_kv_set(KEY_ACTION, ACTION_EVENT);
	mq_kv_set(KEY_PROFILE, PROFILE_BATTERY);
	mq_kv_set(KEY_ATTRIBUTE, BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE);
	mq_kv_set(KEY_PARAM_BATTERY_CHARGING, charging);

	send_message();
	
    pebble_sniff_interval_normal();
    entry_log( "send", "battery_charging" ) ;
}

/*!
 @brief バッテリーの状態変化をテストする。

 @param[in] last 最後に取得したバッテリーの状態
 @param[in] now  現在のバッテリーの状態
 @param[out] is_charging_changed 充電状態が変化した時には true が入る
 @param[out] is_battery_changed 電池容量が変化した時には true が入る

 @return last と now を比較して、充電状態か電池容量のどちらかが変化した場合に true を返す

 */
static bool get_changed_status( BatteryChargeState* last, BatteryChargeState* now, bool* is_charging_changed, bool* is_battery_changed )
{
    if( last->is_plugged != now->is_plugged ) {
        *is_charging_changed = true ;
    }
    else {
        *is_charging_changed = false ;
    }
    if( last->charge_percent != now->charge_percent ) {
        *is_battery_changed = true ;
    }
    else {
        *is_battery_changed = false ;
    }
    if( *is_charging_changed ) {
        return true ;
    }
    if( *is_battery_changed ) {
        return true ;
    }
    return false ;
}


/*!
 @brief バッテリーの状態が変化した場合に呼び出されるコールバック関数

 @param[in] state バッテリーの状態

 */
static void in_event_battery_handler(BatteryChargeState state) {
    bool is_charging_changed, is_battery_changed ;
    if( get_changed_status( &last_state, &state, &is_charging_changed, &is_battery_changed ) == false ) {
        return ;
    }

    if( is_charging_changed && event_onchargingchange ) {
        entry_log( "event","onchargingchange" ) ;
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "event_onchargingchange %d",(int)state.is_plugged);
        send_battery_charging( state.is_plugged ) ;
    }
    if( is_battery_changed && event_onbatterychange ) {
        entry_log( "event","onbatterychange" ) ;
        send_battery_percent( state.charge_percent ) ;
    }
	last_state = state ;
	return ;
}

/*!
 @brief getメソッドのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_get_battery_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_get_battery_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    if (attributeTuple != NULL) {
        switch (attributeTuple->value->uint8) {
        case BATTERY_ATTRIBUTE_ALL: {
            // 加速度センサーが動いている場合には、一瞬止める
            orientation_service_pause();

            entry_log( "get", "BATTERY_ATTRIBUTE_ALL" ) ;
            BatteryChargeState state = battery_state_service_peek();
            // チャージングフラグ
            int charging = state.is_plugged ? BATTERY_CHARGING_ON : BATTERY_CHARGING_OFF;
			mq_kv_set(KEY_PARAM_BATTERY_CHARGING, charging);
            // パーセンテージ
            // pebbleのバッテリーは、0-100になっている。
            // dconnectでは、0.0-1.0の浮動少数なので変換が必要。
            // ただし、C側で変換するのは手間なので、JavaまたはiOS側で行うこととする。
			mq_kv_set(KEY_PARAM_BATTERY_LEVEL, state.charge_percent);

        }   break;
        case BATTERY_ATTRIBUTE_CHARING: {
            // 加速度センサーが動いている場合には、一瞬止める
            orientation_service_pause();

            entry_log( "get", "BATTERY_ATTRIBUTE_CHARING" ) ;
            BatteryChargeState state = battery_state_service_peek();
            int charging = state.is_plugged ? BATTERY_CHARGING_ON : BATTERY_CHARGING_OFF;
            // チャージングフラグ
			mq_kv_set(KEY_PARAM_BATTERY_CHARGING, charging);
        }    break;
        case BATTERY_ATTRIBUTE_LEVEL: {
            // 加速度センサーが動いている場合には、一瞬止める
            orientation_service_pause();

            entry_log( "get", "BATTERY_ATTRIBUTE_LEVEL" ) ;
            BatteryChargeState state = battery_state_service_peek();
            // パーセンテージ
			mq_kv_set(KEY_PARAM_BATTERY_LEVEL, state.charge_percent);
        }    break;
        default:
            entry_log( "get", "BATTERY error" ) ;
            // not support
            pebble_set_error_code(ERROR_NOT_SUPPORT_ATTRIBUTE);
            break;
        }
    }
}

/*!
 @brief putメソッドのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_put_battery_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_put_battery_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE:
        entry_log( "put battery", "ON_CHARGING_CHANGE" ) ;
        event_onchargingchange = true ;
        last_state = battery_state_service_peek();
        battery_state_service_subscribe(&in_event_battery_handler);
        break;
    case BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE:
        entry_log( "put battery", "ON_BATTERY_CHANGE" ) ;
        event_onbatterychange = true ;
        last_state = battery_state_service_peek();
        battery_state_service_subscribe(&in_event_battery_handler);
        break;
    default:
        entry_log( "put battery", "not support" ) ;
        pebble_set_error_code(ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}


/*!
 @brief deleteメソッドのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_delete_battery_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_delete_battery_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE:
        entry_log( "delete battery", "ON_CHARGING_CHANGE" ) ;
        event_onchargingchange = false ;
        service_unsubscribe() ;
        break;
    case BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE:
        entry_log( "delete battery", "ON_BATTERY_CHANGE" ) ;
        event_onbatterychange = false ;
        service_unsubscribe() ;
        break;
    default:
        // not support
        pebble_set_error_code(ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

int in_received_battery_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_battery_handler");

    Tuple *actionTuple = dict_find(received, KEY_ACTION);
    switch (actionTuple->value->uint8) {
    case ACTION_GET:
        in_received_get_battery_handler(received);
        break;
    case ACTION_PUT:
        in_received_put_battery_handler(received);
        break;
    case ACTION_DELETE:
        in_received_delete_battery_handler(received);
        break;
    case ACTION_POST:
    default:
        entry_log( "battery ", "NOT_SUPPORT" ) ;
        // not support.
        pebble_set_error_code(ERROR_NOT_SUPPORT_ACTION);
        break;
    }
    return RETURN_SYNC;
}
