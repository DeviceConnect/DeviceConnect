#include "pebble_device_plugin_defines.h"
#include "pebble_device_plugin.h"
#include "system_profile.h"

#include "battery_profile.h"
#include "device_orientation_profile.h"

/////////////////////////////////////////////////////////////////////
// Settings
/////////////////////////////////////////////////////////////////////


/*!
 @brief deleteメソッド,eventsプロファイルのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_delete_event_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_delete_event_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case SYSTEM_ATTRIBUTE_EVENTS:
        entry_log( "delete", "system/events" ) ;
        battery_service_unsubscribe_force() ;
        orientation_service_unsubscribe_force();
        pebble_sniff_interval_reduced();
        return ;
    default:
        entry_log( "error", "system/events?");
        pebble_set_error_code(ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}
int in_received_system_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_system_handler");

    Tuple *actionTuple = dict_find(received, KEY_ACTION);
    switch (actionTuple->value->uint8) {
    case ACTION_DELETE:
        in_received_delete_event_handler(received);
        break;
    case ACTION_GET:
    case ACTION_POST:
    case ACTION_PUT:
    default:
        entry_log( "error", "system/events");
        // not support.
        pebble_set_error_code(ERROR_NOT_SUPPORT_ACTION);
        break;
    }
    return RETURN_SYNC;
}
