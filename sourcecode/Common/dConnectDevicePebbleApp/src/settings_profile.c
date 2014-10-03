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
static void in_received_get_setting_handler(DictionaryIterator *received, DictionaryIterator *iter) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_get_setting_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case SETTING_ATTRIBUTE_DATE: {
        time_t timer = time(NULL);
        struct tm *local = localtime(&timer);
        char str[64];
        // ポインタにしないとTupletCStringがエラーを出す
        char *p = str;
        int year = local->tm_year + 1900;
        int month = local->tm_mon + 1;
        int day = local->tm_mday;
        int hour = local->tm_hour;
        int min = local->tm_min;
        int sec = local->tm_sec;
        // RFC 3339に合わせて変換を行う
        snprintf(str, sizeof(str), "%4d-%02d-%02dT%02d:%02d:%02d", year, month, day, hour, min, sec);
        entry_log( "get setting/date", str ) ;
        Tuplet dateTuple = TupletCString(KEY_PARAM_SETTING_DATE, p);
        dict_write_tuplet(iter, &dateTuple);
    }   break;
    default:
        // not support
        pebble_set_error_code(iter, ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

int in_received_setting_handler(DictionaryIterator *received, DictionaryIterator *iter) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_setting_handler");

    Tuple *actionTuple = dict_find(received, KEY_ACTION);
    switch (actionTuple->value->uint8) {
    case ACTION_GET:
        in_received_get_setting_handler(received, iter);
        break;
    case ACTION_POST:
    case ACTION_PUT:
    case ACTION_DELETE:
    default:
        // not support.
        pebble_set_error_code(iter, ERROR_NOT_SUPPORT_ACTION);
        break;
    }
    return RETURN_SYNC;
}
