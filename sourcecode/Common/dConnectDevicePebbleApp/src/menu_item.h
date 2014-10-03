#ifndef menu_item_h
#define menu_item_h


typedef struct __attribute__((__packed__)) {
    bool is_gbitmap ; // Gbitmap(image), text
    char* title ;      // title
    void* bitmap_or_text ;
} MenuItem ;

/*!
 @brief メニューの初期化

 @param[in] data GBitmapのデータ
 @param[in] size データサイズ
 */
void init_menu_item( void ) ;

/*!
 @brief メニューを追加する

 @param[in] titleStr メニューのタイトル
 @param[in] contents 文字列または gbitmap
 @param[in] is_gbitmap ;
 */
void entry_menu_item( char* titleStr, void* contents, bool is_gbitmap );

/*!
 @brief 最新のメニューをリプレースする

 @param[in] titleStr メニューのタイトル
 @param[in] contents 文字列または gbitmap
 @param[in] is_gbitmap ;
 */
void replace_menu_item( char* title, void* contents, bool is_gbitmap );

/*!
 @brief メニューを追加する

 @param[in] index メニューの番号
 @return[out] inex に対応する、MenuItem構造体
 */
MenuItem* get_menu_item( int index );

/*!
 @brief メニューを追加する

 @return[out] メニューの総数を返す
 */
int how_many_menu_item( void );


/*!
 @brief メニュー使用後の処理を行う。

 */
void menu_cleanup( void );


#endif	/* menu_item_h */
