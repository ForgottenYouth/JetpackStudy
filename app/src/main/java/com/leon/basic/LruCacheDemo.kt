/**
 * FileName: LruCacheDemo
 * Author: shiwenliang
 * Date: 2021/11/18 16:56
 * Description:
 */
package com.leon.basic

import android.util.LruCache

class LruCacheDemo {

    var cached:LruCache<String,String> = LruCache<String,String>(20)

    fun addCached(){
        cached.put("A","AA00")

        System.out.println(cached.toString())

//        cached.remove("A")
    }



}
fun main(args: Array<String>){
    LruCacheDemo().addCached()
}