#include "send_message.h"
#include "pebble_device_plugin.h"

static int retry_count = 0;

void send_message() {
	
	DBG_LOG(APP_LOG_LEVEL_DEBUG, "send_message");
	
	DictionaryIterator *iter = NULL;
	app_message_outbox_begin(&iter);
	if (iter == NULL) {
		// 送信用イテレータの作成に失敗
		//entry_log( "error", "outbox_begin" ) ;
		DBG_LOG(APP_LOG_LEVEL_DEBUG, "send_message_error: outbox_begin");

		return;
	}
	
	int k = 0, v = 0;
	bool more = mq_kv_get_first(&k, &v);
	
	while (more) {
		if (k == KEY_PARAM_SETTING_DATE) {
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
		} else {
			Tuplet tuple = TupletInteger(k, v);
			dict_write_tuplet(iter, &tuple);
		}
		
		more = mq_kv_get_next(&k, &v);
	}
	
	// データ終了
	dict_write_end(iter);
	// データ送信
	AppMessageResult res = app_message_outbox_send();
	DBG_LOG(APP_LOG_LEVEL_DEBUG, "res:%d", res);
}

void success_message() {
	retry_count = 0;
	if (mq_pop()) {
		DBG_LOG(APP_LOG_LEVEL_DEBUG, "pop!!!");
		send_message();
	}
}

void retry_message() {
	retry_count++;
	if (retry_count<3) {
		send_message();
	}
}
