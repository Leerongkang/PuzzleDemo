package com.puzzle.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkServiceCreator {

    private const val BASE_URL = "https://tool.xiuxiu.meitu.com/v1/tool/material/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}
