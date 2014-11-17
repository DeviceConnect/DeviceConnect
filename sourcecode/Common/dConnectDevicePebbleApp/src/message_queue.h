#ifndef message_queue_h
#define message_queue_h

#include <stdbool.h>

void mq_kv_clear();
bool mq_kv_get_next(int *key, int *value);
bool mq_kv_get_first(int *key, int *value);
int mq_next(int index);
void mq_init();
bool mq_push();
bool mq_pop();
void mq_kv_set(int key, int value);

#endif	/* message_queue_h */
