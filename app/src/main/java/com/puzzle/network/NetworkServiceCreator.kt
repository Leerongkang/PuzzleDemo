package com.puzzle.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 在线素材获取 Retrofit 单例
 */
object NetworkServiceCreator {

    private const val BASE_URL = "https://tool.xiuxiu.meitu.com/v1/tool/material/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    // 使用 inline + reified 可以动态使用泛型类型，简化使用步骤
    inline fun <reified T> create(): T = create(T::class.java)
}
