#include "pebble_device_plugin_defines.h"
#include "pebble_device_plugin.h"
#include "binary_profile.h"


/*!
 @brief データを一時的に保管するバッファ。
 */
static uint8_t *binary_data = NULL;

/*!
 @brief データを一時的に保管するバッファ。
 */
static bool under_receive = false ;

/*!
 @brief データのインデックス。
 この値の分だけパケットが送られてくる予定。
 */
static int binary_index = 0;

/*!
 @brief データのサイズ
 */
static int binary_length = 0;


/*!
 @brief ログ出力用の文字列を求める

 @param[in] index 現在の index
 @param[in] all_index index 総数
 @return 文字列
 */
static char* make_log_explain( int index, int all_index )
{
    static char buf[ 20 ] ;
    
    snprintf( buf, sizeof( buf), "%d(%d/%d)", index* BINARY_BUF_SIZE, index, all_index ) ;
    return buf ;
}

bool is_under_receive( void )
{
    return under_receive ;
}

void binary_cleanup() {
    DBG_LOG(APP_LOG_LEVEL_DEBUG, "binary_cleanup");

    if (binary_data != NULL) {
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "free binary_data");
        free(binary_data);
        binary_data = NULL;
    }
    binary_index = 0;
    binary_length = 0;
}

int in_received_binary_handler(DictionaryIterator *received, DictionaryIterator *iter) {
    Tuple *lenTuple = dict_find(received, KEY_PARAM_BINARY_LENGTH);
    if (lenTuple != NULL) {
        pebble_sniff_interval_normal() ;
        int len = (int) lenTuple->value->uint32;
        if (binary_data != NULL) {
            free(binary_data);
        }
        binary_data = (uint8_t *) malloc(len);
        binary_index = len / BINARY_BUF_SIZE;
        binary_length = len;
        DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_binary_handler start: %d %d", binary_index, binary_length);
        under_receive = true ;
        entry_log( "file send", "start" ) ;
    } else if (binary_data != NULL) {
        pebble_sniff_interval_normal() ;
        Tuple *indexTuple = dict_find(received, KEY_PARAM_BINARY_INDEX);
        Tuple *bodyTuple = dict_find(received, KEY_PARAM_BINARY_BODY);
        if (indexTuple != NULL && bodyTuple != NULL) {
            int index = (int) indexTuple->value->uint16;
            uint8_t *body = (uint8_t *) bodyTuple->value->data;
            if (index == binary_index) {
                // コピー終了
                DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_binary_handler end: %d", index);
                int l = binary_length - index * BINARY_BUF_SIZE;
                memcpy(&binary_data[index * BINARY_BUF_SIZE], body, l);

                // データを受け取った後の処理を行う。
                pebble_set_bitmap(binary_data, binary_length);
                under_receive = false ;

                // 使い終わった添付データは削除
                binary_cleanup();
                pebble_sniff_interval_reduced();
                entry_log( "file send", "end" ) ;
            } else {
                pebble_sniff_interval_normal() ;
                replace_last_log( "file send", make_log_explain(index, binary_index) ) ;
                //DBG_LOG(APP_LOG_LEVEL_DEBUG, "in_received_binary_handler: %d", index);
                memcpy(&binary_data[index * BINARY_BUF_SIZE], body, BINARY_BUF_SIZE);
            }
        }
    }
    return RETURN_ASYNC;
}

int get_pbi_image_width( uint8_t* data )
{
    unsigned int hi = data[ 9 ];
    unsigned int low = data[ 8 ] ;
    unsigned int ans ;
    hi &= 0xff ;
    low &= 0xff ;
    ans = ( hi << 8 ) | low ;
    return (int)ans ;
}
