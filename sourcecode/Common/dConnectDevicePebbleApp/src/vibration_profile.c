#include "pebble_device_plugin_defines.h"
#include "pebble_device_plugin.h"
#include "vibration_profile.h"

/////////////////////////////////////////////////////////////////////
// Vibration
/////////////////////////////////////////////////////////////////////


#define VIB_PATTERN_OK 1
#define VIB_PATTERN_ERROR 0

/*!
 @brief Vibrationのパターン。
 */
static uint32_t vib_data[VIBRATION_PATTERN_SIZE];

///*!
// @brief Vibrationのパターン数。
// */
//static int vib_len = 0;

/*!
 @brief 指定されたデータのオフセットから4byteをintとして読み込む。
 @param[in] data データ
 @param[in] offset オフセット
 @retval int
 */
static int parse_int(char *data, int offset) {
    uint32_t res = 0;
    res |= (data[offset + 0] << 8);
    res |=  data[offset + 1];
    return res;
}

/*!
 @brief バイブレーションのパターンを解析する。
 @param[in] data データ
 @param[in] len パターンの個数
 */
static int vibration_parse_pattern(char *data, int len) {
    // 最大の配列のサイズはVIBRATION_PATTERN_SIZEで指定されている
    // これ以上のサイズの配列が送られてきた場合にはエラー
    if (len >= VIBRATION_PATTERN_SIZE) {
        return VIB_PATTERN_ERROR;
    }
    for (int i = 0; i < len; i++) {
        vib_data[i] = parse_int(data, 2 * i);
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "vib_data[%d]: %d ", i, (int) vib_data[i]);
        // pebbleの仕様では、最長で10000までしか設定できない
        // それ以上のパラメータが送られてきた場合にはエラー
        if (vib_data[i] > 10000) {
            return VIB_PATTERN_ERROR;
        }
    }
    return VIB_PATTERN_OK;
}

static void in_received_put_vibration_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_put_vibration_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case VIBRATION_ATTRIBUTE_VIBRATE: {
        Tuple *lenTuple = dict_find(received, KEY_PARAM_VIBRATION_LEN);
        Tuple *patternTuple = dict_find(received, KEY_PARAM_VIBRATION_PATTERN);
        if (lenTuple == NULL && patternTuple == NULL) {
            vibes_short_pulse();
        } else {
            int length = (int) lenTuple->value->uint16;
            if (length == 0) {
                entry_log( "put", "vibration" ) ;
                vibes_short_pulse();
            } else {
                char *pattern = (char *) patternTuple->value->data;
                if (vibration_parse_pattern(pattern, length) == VIB_PATTERN_ERROR) {
                    pebble_set_error_code(ERROR_ILLEGAL_PARAMETER);
                } else {
                    VibePattern pat = {
                        .durations = vib_data,
                        .num_segments = length,
                    };
                    entry_log( "put", "vibration" ) ;
                    vibes_enqueue_custom_pattern(pat);
                }
            }
        }
    }   break;
    default:
        // not support
        pebble_set_error_code(ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

static void in_received_delete_vibration_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_delete_vibration_handler");

    Tuple *attributeTuple = dict_find(received, KEY_ATTRIBUTE);
    switch (attributeTuple->value->uint8) {
    case VIBRATION_ATTRIBUTE_VIBRATE: {
        vibes_cancel();
    }   break;
    default:
        // not support
        pebble_set_error_code(ERROR_NOT_SUPPORT_ATTRIBUTE);
        break;
    }
}

int in_received_vibration_handler(DictionaryIterator *received) {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_vibration_handler");

    Tuple *actionTuple = dict_find(received, KEY_ACTION);
    switch (actionTuple->value->uint8) {
    case ACTION_PUT:
        in_received_put_vibration_handler(received);
        break;
    case ACTION_DELETE:
        in_received_delete_vibration_handler(received);
        break;
    case ACTION_GET:
    case ACTION_POST:
    default:
        // not support.
        pebble_set_error_code(ERROR_NOT_SUPPORT_ACTION);
        break;
    }
    return RETURN_SYNC;
}
