package com.puzzle.network

import Response
import retrofit2.http.GET

interface MaterialNetworkService {

    @GET("detail.json?id=307&category_id=3003&ar_sdk_version=2.10.0.1&client_timestamp=1611911581198&client_operator=%E4%B8%AD%E5%9B%BD%E7%A7%BB%E5%8A%A8&client_timezone=GMT+8&is_gdpr=0&gid=2177445072&client_channel_id=setup&client_model=EVR-AL00&client_brand=HUAWEI&resolution=1080*2068&client_language=zh_CN&client_id=1089867602&runtimeMaxMemory=512&ad_sdk_version=4.26.0&client_os=10&is_test=0&lang=1&is_privacy=0&client_network=wifi&user_agent=mtxx-9110-HUAWEI-EVR-AL00-android-10-f146e553&oaid=0e55b6fc-c83b-4e1f-a51b-2227ba76e50f&ram=5634&appAreaType=3&version=9.1.1.0&personality_not_recommend=0&community_version=2.0.0&country_code=CN&app_hot_start_times=1&android_sdk_int=29&is64Bit=1&is_device_support_64=1&android_id=883d3e9309176930&client_is_root=1&sig=b589c427799109801c961433e99f78e3&sigTime=1611911581213&sigVersion=1.3")
    suspend fun getFreeMaterials(): Response

    @GET("detail.json?id=308&category_id=3004&ar_sdk_version=2.10.0.1&client_timestamp=1611911581199&client_operator=%E4%B8%AD%E5%9B%BD%E7%A7%BB%E5%8A%A8&client_timezone=GMT+8&is_gdpr=0&gid=2177445072&client_channel_id=setup&client_model=EVR-AL00&client_brand=HUAWEI&resolution=1080*2068&client_language=zh_CN&client_id=1089867602&runtimeMaxMemory=512&ad_sdk_version=4.26.0&client_os=10&is_test=0&lang=1&is_privacy=0&client_network=wifi&user_agent=mtxx-9110-HUAWEI-EVR-AL00-android-10-f146e553&oaid=0e55b6fc-c83b-4e1f-a51b-2227ba76e50f&ram=5634&appAreaType=3&version=9.1.1.0&personality_not_recommend=0&community_version=2.0.0&country_code=CN&app_hot_start_times=1&android_sdk_int=29&is64Bit=1&is_device_support_64=1&android_id=883d3e9309176930&client_is_root=1&sig=0fbd675c62205f60fc40975f9324e115&sigTime=1611911581218&sigVersion=1.3")
    suspend fun getSpliceMaterials(): Response

    @GET("detail.json?id=306&category_id=3012&ar_sdk_version=2.10.0.1&client_timestamp=1611911581195&client_operator=%E4%B8%AD%E5%9B%BD%E7%A7%BB%E5%8A%A8&client_timezone=GMT+8&is_gdpr=0&gid=2177445072&client_channel_id=setup&client_model=EVR-AL00&client_brand=HUAWEI&resolution=1080*2068&client_language=zh_CN&client_id=1089867602&runtimeMaxMemory=512&ad_sdk_version=4.26.0&client_os=10&is_test=0&lang=1&is_privacy=0&client_network=wifi&user_agent=mtxx-9110-HUAWEI-EVR-AL00-android-10-f146e553&oaid=0e55b6fc-c83b-4e1f-a51b-2227ba76e50f&ram=5634&appAreaType=3&version=9.1.1.0&personality_not_recommend=0&community_version=2.0.0&country_code=CN&app_hot_start_times=1&android_sdk_int=29&is64Bit=1&is_device_support_64=1&android_id=883d3e9309176930&client_is_root=1&sig=9904a7f0da7966b6af0668a7e1165383&sigTime=1611911581210&sigVersion=1.3")
    suspend fun getPosterMaterials(): Response

}