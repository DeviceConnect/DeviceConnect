#include "pebble_device_plugin_defines.h"
#include "pebble_device_plugin.h"
#include "battery_profile.h"

/*! @define タイマーハンドラー呼び出しインターバル(msec) */
#define TIMER_HANDLER_INTERVAL 1000

/*! @define 最初の通信のタイマーハンドラー呼び出しインターバル(msec) */
#define TIMER_HANDLER_INTERVAL_FIRST 3000

/*!
 @brief 最初の通信かどうか
 */
static bool is_first_communication = true ;

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
 @brief onbatterychange/onchargingchange 両方の送信を行う必要性が発生した場合
        送信タイミングを2回に分ける必要性があり、その処理に使用するタイマー
 */
static AppTimer *app_timer;

/*!
 @brief 充電容量(%)を記憶する

 */
static int percent_for_onbatterychange_next ;


/*!
 @brief 充電状態 を記憶する

 */
static bool charging_for_onbatterychange_next ;

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
    DictionaryIterator *iter = NULL;
    app_message_outbox_begin(&iter);
    if (iter == NULL) {
        // 送信用イテレータの作成に失敗
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "error send_battery_percent");
        entry_log( "error", "send_battery_percent" ) ;
        return;
    }
    Tuplet actionTuple = TupletInteger(KEY_ACTION, ACTION_EVENT);
    dict_write_tuplet(iter, &actionTuple);

    Tuplet profileTuple = TupletInteger(KEY_PROFILE, PROFILE_BATTERY);
    dict_write_tuplet(iter, &profileTuple);

    Tuplet attributeTuple = TupletInteger(KEY_ATTRIBUTE, BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE ) ;
    dict_write_tuplet(iter, &attributeTuple);

    Tuplet levelTuple = TupletInteger(KEY_PARAM_BATTERY_LEVEL, percent);
    dict_write_tuplet(iter, &levelTuple);

    dict_write_end(iter);
    pebble_sniff_interval_normal();
    app_message_outbox_send();
    entry_log( "send", "send_battery_percent" ) ;
}

/*!
 @brief 充電状態を送信する。

 @param[in] charging 充電中は true
 */
static void send_battery_charging( bool charging )
{
    DictionaryIterator *iter = NULL;
    app_message_outbox_begin(&iter);
    if (iter == NULL) {
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "error send_battery_charging");
        entry_log( "error", "send_battery_charging" ) ;
        return;
    }

    Tuplet actionTuple = TupletInteger(KEY_ACTION, ACTION_EVENT);
    dict_write_tuplet(iter, &actionTuple);

    Tuplet profileTuple = TupletInteger(KEY_PROFILE, PROFILE_BATTERY);
    dict_write_tuplet(iter, &profileTuple);

    Tuplet attributeTuple = TupletInteger(KEY_ATTRIBUTE, BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE);
    dict_write_tuplet(iter, &attributeTuple);

    Tuplet chargingTuple = TupletInteger(KEY_PARAM_BATTERY_CHARGING, charging);
    dict_write_tuplet(iter, &chargingTuple);

    dict_write_end(iter);
    pebble_sniff_interval_normal();
    app_message_outbox_send();
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
 @brief pebble_sniff_interval_reduced()を行うタイマーハンドラー

 */
static void sniff_interval_timer_callback( void* data )
{
    DBG_LOG( APP_LOG_LEVEL_DEBUG, "sniff_interval_timer_callback" ) ;
    pebble_sniff_interval_reduced();
}

/*!
 @brief 充電容量を送信し、pebble_sniff_interval_reduced()を行うタイマーハンドラーを登録する。

 */
static void send_percent_callback( void* data )
{
    DBG_LOG( APP_LOG_LEVEL_DEBUG, "send_percent_callback" ) ;
    send_battery_percent( percent_for_onbatterychange_next ) ;
    app_timer = app_timer_register( TIMER_HANDLER_INTERVAL
                                         , sniff_interval_timer_callback, NULL);
}

/*!
 @brief 充電状態を送信し、pebble_sniff_interval_reduced()を行うタイマーハンドラーを登録する。

 */
static void send_charge_callback( void* data )
{
    DBG_LOG( APP_LOG_LEVEL_DEBUG, "send_charge_callback" ) ;
    send_battery_charging( charging_for_onbatterychange_next ) ;
    app_timer = app_timer_register( TIMER_HANDLER_INTERVAL
                                         , sniff_interval_timer_callback, NULL);
}

/*!
 @brief 通信インターバルを求める(msec)
        最初の通信は失敗する確率が高いので、最初の通信かどうかを判定して適切なインターバルを返す。

 @return 通信インターバル
 */
static int get_timer_interval( void )
{
    int interval ;
    if( is_first_communication ) {
        is_first_communication = false ;
        interval = TIMER_HANDLER_INTERVAL_FIRST ;
    }
    else {
        interval = TIMER_HANDLER_INTERVAL ;
    }
    return interval ;
}    

/*!
 @brief 充電容量を送信するタイマーハンドラーを登録する。

 @param[in] percent 充電容量(%)
 */
static void send_percent_next_time( int percent )
{
    percent_for_onbatterychange_next = percent ;
    app_timer = app_timer_register( get_timer_interval(), send_percent_callback, NULL);
}

/*!
 @brief 充電状態を送信するタイマーハンドラーを登録する。

 @param[in] percent 充電容量(%)
 */
static void send_charge_next_time( bool charging )
{
    charging_for_onbatterychange_next = charging ;
    app_timer = app_timer_register( get_timer_interval(), send_charge_callback, NULL);
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
        if( is_battery_changed && event_onbatterychange ) {
            // 1回のハンドラー呼び出しで、連続して app_message_outbox_send() は不可能な為、
            // onbatterychange のイベントは別のタイミングで送る必要がある。
            send_percent_next_time( (int) state.charge_percent ) ;
        }
        last_state = state ;
        return ;
    }
    if( is_battery_changed && event_onbatterychange ) {
        entry_log( "event","onbatterychange" ) ;
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "event_onbatterychange %d%%",state.charge_percent);
        send_battery_percent( state.charge_percent ) ;

        if( is_charging_changed && event_onchargingchange ) {
            // 1回のハンドラー呼び出しで、連続して app_message_outbox_send() は不可能な為、
            // onbatterychange のイベントは別のタイミングで送る必要がある。
            send_charge_next_time( state.is_plugged ) ;
        }
        last_state = state ;
    }
}

/*!
 @brief getメソッドのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_get_battery_handler(DictionaryIterator *received, DictionaryIterator *iter) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_get_battery_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    if (attributeTuple != NULL) {
        switch (attributeTuple->value->uint8) {
        case BATTERY_ATTRIBUTE_ALL: {
            entry_log( "get", "BATTERY_ATTRIBUTE_ALL" ) ;
            BatteryChargeState state = battery_state_service_peek();
            // チャージングフラグ
            int charging = state.is_plugged ? BATTERY_CHARGING_ON : BATTERY_CHARGING_OFF;
            Tuplet chargingTuple = TupletInteger(KEY_PARAM_BATTERY_CHARGING, charging);
            dict_write_tuplet(iter, &chargingTuple);
            // パーセンテージ
            // pebbleのバッテリーは、0-100になっている。
            // dconnectでは、0.0-1.0の浮動少数なので変換が必要。
            // ただし、C側で変換するのは手間なので、Java側で行うこととする。
            Tuplet levelTuple = TupletInteger(KEY_PARAM_BATTERY_LEVEL, (int) state.charge_percent);
            dict_write_tuplet(iter, &levelTuple);
        }   break;
        case BATTERY_ATTRIBUTE_CHARING: {
            entry_log( "get", "BATTERY_ATTRIBUTE_CHARING" ) ;
            BatteryChargeState state = battery_state_service_peek();
            int charging = state.is_plugged ? BATTERY_CHARGING_ON : BATTERY_CHARGING_OFF;
            // チャージングフラグ
            Tuplet tuple = TupletInteger(KEY_PARAM_BATTERY_CHARGING, charging);
            dict_write_tuplet(iter, &tuple);
        }    break;
        case BATTERY_ATTRIBUTE_LEVEL: {
            entry_log( "get", "BATTERY_ATTRIBUTE_LEVEL" ) ;
            BatteryChargeState state = battery_state_service_peek();
            // パーセンテージ
            Tuplet levelTuple = TupletInteger(KEY_PARAM_BATTERY_LEVEL, (int) state.charge_percent);
            dict_write_tuplet(iter, &levelTuple);
        }    break;
        default:
            entry_log( "get", "BATTERY error" ) ;
            // not support
            pebble_set_error_code(iter, ERROR_NOT_SUPPORT_ATTRIBUTE);
            break;
        }
    }
}

/*!
 @brief putメソッドのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_put_battery_handler(DictionaryIterator *received, DictionaryIterator *iter) {
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
        pebble_set_error_code(iter, ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}


/*!
 @brief deleteメソッドのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_delete_battery_handler(DictionaryIterator *received, DictionaryIterator *iter) {
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
        pebble_set_error_code(iter, ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

int in_received_battery_handler(DictionaryIterator *received, DictionaryIterator *iter) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_battery_handler");

    Tuple *actionTuple = dict_find(received, KEY_ACTION);
    switch (actionTuple->value->uint8) {
    case ACTION_GET:
        in_received_get_battery_handler(received, iter);
        break;
    case ACTION_PUT:
        in_received_put_battery_handler(received, iter);
        break;
    case ACTION_DELETE:
        in_received_delete_battery_handler(received, iter);
        break;
    case ACTION_POST:
    default:
        entry_log( "battery ", "NOT_SUPPORT" ) ;
        // not support.
        pebble_set_error_code(iter, ERROR_NOT_SUPPORT_ACTION);
        break;
    }
    return RETURN_SYNC;
}
