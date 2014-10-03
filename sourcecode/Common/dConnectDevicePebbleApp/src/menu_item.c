#include "pebble.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdarg.h>

#include "pebble_device_plugin.h"
#include "menu_item.h"

/*!
 @brief メニューを管理するモジュール。使用できるメモリが少ないので、メニューに表示できる画像は1種類だけとする。

 */

#define FIRST_MENU_TITLE  "start App"
#define FIRST_MENU_TEXT   "ver 2014/09/19 11:00"
#define MENU_ITEM_MAX 20

static int16_t how_many_item = 0 ;
static MenuItem menu[ MENU_ITEM_MAX ] ;
static bool is_first = true ;

static void delete_menu_item( int index );
static void zero_menu_item( void );

/*!
 @brief 文字列のコピーを作成する。戻り値は、free() すること。pebble の library には、strdup が存在しない。その代わりの関数である。

 @param[in] str
 @retval コピーした文字列。
 */
static char* _strdup( char* str )
{
    char* buffer = malloc( strlen( str ) + 1 ) ;
    strcpy( buffer, str ) ;
    return buffer ;
}

void init_menu_item( void )
{
    how_many_item = 0 ;
    zero_menu_item() ;

    is_first = true ;
    menu[0].is_gbitmap = false ;
    menu[0].title = FIRST_MENU_TITLE ;
    menu[0].bitmap_or_text = FIRST_MENU_TEXT ;
    how_many_item = 1 ;
}

void entry_menu_item( char* titleStr, void* contents, bool is_gbitmap )
{
    if( is_first ) {
        //メニュー追加が初めての場合には、最初に作成したメニューを置き換えるだけ
        is_first = false ;
        menu[ 0 ].title = _strdup( titleStr ) ;
        if( is_gbitmap ) {
            menu[ 0 ].bitmap_or_text = contents ;
        }
        else {
            menu[ 0 ].bitmap_or_text = _strdup( contents ) ;
        }
        menu[0].is_gbitmap = is_gbitmap ;
        return ;
    }
    how_many_item ++ ;
    if( how_many_item > MENU_ITEM_MAX ) {
        delete_menu_item( MENU_ITEM_MAX - 1 ) ;//メニュー個数が限界を超えた場合には、古いメニューを削除
    }

    for( int i = ( MENU_ITEM_MAX - 1 ) ; i > 0; i -- ) {
        menu[ i ] = menu[ i - 1 ] ;
    }
    menu[0].is_gbitmap = is_gbitmap ;
    menu[0].title = _strdup( titleStr );
    if( is_gbitmap ) {
        menu[0].bitmap_or_text = contents ;
    }
    else {
        menu[0].bitmap_or_text = _strdup( contents ) ;
    }
}
void replace_menu_item( char* titleStr, void* contents, bool is_gbitmap )
{
    if( is_first ) {
        //メニュー追加が初めての場合には、最初に作成したメニューを置き換えるだけ
        is_first = false ;
        menu[ 0 ].title = _strdup( titleStr ) ;
        if( is_gbitmap ) {
            menu[ 0 ].bitmap_or_text = contents ;
        }
        else {
            menu[ 0 ].bitmap_or_text = _strdup( contents ) ;
        }
        menu[0].is_gbitmap = is_gbitmap ;
        return ;
    }
    if( menu[ 0 ].is_gbitmap == false ) {
        if( menu[ 0 ].bitmap_or_text != NULL ) {
            free( menu[ 0 ].bitmap_or_text ) ;
        }
        if( menu[ 0 ].title != NULL ) {
            free( menu[ 0 ].title ) ;
        }
    }

    menu[0].is_gbitmap = is_gbitmap ;
    menu[0].title = _strdup( titleStr );
    if( is_gbitmap ) {
        menu[0].bitmap_or_text = contents ;
    }
    else {
        menu[0].bitmap_or_text = _strdup( contents ) ;
    }
}

/*!
 @brief メニューの内容の後始末を行う

 @param[in] index 削除するメニュー番号
 */
static void clean_menu_item( int index )
{
    if( menu[ index ].is_gbitmap ) {
        menu[ index ].bitmap_or_text = NULL ;
    }
    else {
        if( menu[ index ].bitmap_or_text != NULL ) {
            free( menu[ index ].bitmap_or_text ) ;
            menu[ index ].bitmap_or_text = NULL ;
        }
    }
    if( menu[ index ].title != NULL ) {
        free( menu[ index ].title ) ;
        menu[ index ].title = NULL ;
    }
}
/*!
 @brief メニューを削除する

 @param[in] index 削除するメニュー番号
 */
static void delete_menu_item( int index )
{
    clean_menu_item( index ) ;
    for( int i = index ; i < ( MENU_ITEM_MAX - 1 ) ; i ++ ) {
        menu[ i ] = menu[ i + 1 ] ;
    }
    how_many_item -- ;
    zero_menu_item() ;
}

void menu_cleanup( void )
{
    if( is_first ) {
        return ;
    }
    for( int i = 0 ; i < MENU_ITEM_MAX ; i ++ ) {
        clean_menu_item( i );
    }
}

int how_many_menu_item( void )
{
    return how_many_item ;
}

MenuItem* get_menu_item( int index )
{
    return &( menu[ index ] ) ;
}


/*!
 @brief 使われていないメニューを初期化する

 */
static void zero_menu_item( void )
{
    for( int i = how_many_item ; i < MENU_ITEM_MAX ; i ++ ) {
        memset( &( menu[ i ] ), sizeof( menu[0] ), 0 ) ;
    }
}
    
