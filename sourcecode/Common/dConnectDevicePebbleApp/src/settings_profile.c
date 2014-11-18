#include "pebble_device_plugin_defines.h"
#include "pebble_device_plugin.h"
#include "settings_profile.h"

/////////////////////////////////////////////////////////////////////
// Settings
/////////////////////////////////////////////////////////////////////


/*!
 @brief getメソッド,settingプロファイルのメッセージを処理する.

 @param[in] received 受信したメッセージ
 @param[in] iter レスポンスを格納するイテレータ

 */
static void in_received_get_setting_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_get_setting_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case SETTING_ATTRIBUTE_DATE: {
		// 細かい動きはsend_message()内で行う
		mq_kv_set(KEY_PARAM_SETTING_DATE, 0);
    }   break;
    default:
        // not support
        pebble_set_error_code(ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

int in_received_setting_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_setting_handler");

    Tuple *actionTuple = dict_find(received, KEY_ACTION);
    switch (actionTuple->value->uint8) {
    case ACTION_GET:
        in_received_get_setting_handler(received);
        break;
    case ACTION_POST:
    case ACTION_PUT:
    case ACTION_DELETE:
    default:
        // not support.
        pebble_set_error_code(ERROR_NOT_SUPPORT_ACTION);
        break;
    }
    return RETURN_SYNC;
}
