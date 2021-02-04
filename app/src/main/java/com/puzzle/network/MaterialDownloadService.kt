package com.puzzle.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * 素材下载接口
 */
interface MaterialDownloadService {

    // 素材下载
    @GET
    @Streaming
    suspend fun downloadMaterialZip(@Url url: String): ResponseBody
}