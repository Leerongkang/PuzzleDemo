package com.puzzle.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 素材下载前缀
const val MATERIAL_DOWNLOAD_BASE_URL = "https://xx"

/**
 * 素材下载 Retrofit 创建单例
 */
object DownloadServiceCreator {

    private val retrofit = Retrofit.Builder()
                                   .addConverterFactory(GsonConverterFactory.create())
                                   .baseUrl(MATERIAL_DOWNLOAD_BASE_URL)
                                   .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    // 使用 inline + reified 可以动态使用泛型类型，简化使用步骤
    inline fun <reified T> create(): T = create(T::class.java)
}