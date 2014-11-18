#include "message_queue.h"
//#include <stdio.h>
//#include "pebble_device_plugin.h"

#define QUEUE_SIZE 5
#define KEY_SIZE 7

typedef struct key_value_data {
	int key;
	int value;
} key_value_data_t;

static key_value_data_t queue[QUEUE_SIZE][KEY_SIZE] = {};

static int front;
static int rear;
static int current;
static int set_index;
static int get_index;

//#pragma mark - KeyValue

// 値をクリア
void mq_kv_clear() {
	for (int i=0; i<KEY_SIZE; i++) {
		key_value_data_t kv = {0};
		queue[current][i] = kv;
	}
	set_index = 0;
}

// 値を設定
void mq_kv_set(int key, int value) {
	if (set_index < KEY_SIZE) {
		//printf("set:%d, %d\n", key, value);
		key_value_data_t kv;
		kv.key = key;
		kv.value = value;
		queue[current][set_index++] = kv;
	} else {
		//printf("*ERROR!!*\n");
		// assertion!!
	}
}

// 次の値を取得
bool mq_kv_get_next(int *key, int *value) {
	if (get_index < KEY_SIZE) {
		key_value_data_t kv = queue[front][get_index++];
		*key = kv.key;
		*value = kv.value;
	} else {
		return false;
	}
	return true;
}

// 最初の値を取得
bool mq_kv_get_first(int *key, int *value) {
	get_index = 0;
	return mq_kv_get_next(key, value);
}


//#pragma mark - Queue

// キューを初期化
void mq_init() {
	front = rear = current = 0;
	mq_kv_clear();
}

// 次の要素の添え字を求める
int mq_next(int index) {
	return (index + 1) % QUEUE_SIZE;
}

// キューをプッシュ
bool mq_push() {
	if (mq_next(rear) == front) {
		// キューが一杯
//		if (force) {
//			// 強制的に追加
//			// FIXME:ちゃんと優先順位を持たせたい
//			mq_kv_clear();
//			return true;
//		}
		//printf("full!!\n");
		return false;
	}
	current = rear;
	rear = mq_next(rear);
	mq_kv_clear();
//	DBG_LOG(APP_LOG_LEVEL_DEBUG, "mq_push:%d, %d", front, rear);
//	printf("mq_push:%d, %d\n", front, rear);
	return true;
}

// キューをポップ
bool mq_pop() {
//	printf("mq_pop:%d, %d\n", front, rear);
//	DBG_LOG(APP_LOG_LEVEL_DEBUG, "mq_pop:%d, %d", front, rear);
	if (mq_next(front) == rear) {
		// キューが空
		//printf("emp!!\n");
		front = rear;
		return false;
	}
	front = mq_next(front);
	return true;
}
