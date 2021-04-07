package com.puzzle.material

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * 素材下载状态
 */
const val DOWNLOAD_STATE_NOT_DOWNLOAD  = 0      // 未下载
const val DOWNLOAD_STATE_DOWNLOADING = -1       // 下载中
const val DOWNLOAD_STATE_DOWNLOADED = -2        // 下载完成

/**
 * 素材分类
 */
const val MATERIAL_TYPE_POSTER = 0      // 海报
const val MATERIAL_TYPE_FREE = 1        // 自由
const val MATERIAL_TYPE_SPLICE = 2      // 拼接


/**
 * 新素材标志位
 */
const val MATERIAL_OLD = 0
const val MATERIAL_NEW = 1

/**
 * 素材
 */
@Entity
@Keep
data class Material(
    @PrimaryKey
    @SerializedName("material_id")
    var materialId: Long = 0,
    @SerializedName("background_img")
    var backgroundImg: String = "",
    @SerializedName("bgColor")
    var bgColor: String = "",
    var color: String = "",
    @SerializedName("created_at")
    var createdAt: Long = 0,
    @SerializedName("end_time")
    var endTime: Long = 0,
    @SerializedName("has_music")
    var hasMusic: Int = 0,
    var height: Int = 0,
    @SerializedName("hot_sort")
    var hotSort: Int = 0,
    @SerializedName("is_dynamic")
    var beDynamic: Int = 0,
    @SerializedName("is_top")
    var beTop: Int = 0,
    @SerializedName("jump_buy_addr")
    var jumpBuyAddr: String = "",
    @SerializedName("jump_buy_icon")
    var jumpBuyIcon: String = "",
    @SerializedName("material_feature")
    var materialFeature: Int = 0,
    @SerializedName("max_version")
    var maxVersion: String = "",
    @SerializedName("min_version")
    var minVersion: String = "",
    @SerializedName("music_id")
    var musicId: Int = 0,
    @SerializedName("music_start_at")
    var musicStartAt: Long = 0,
    var name: String = "",
    var preview: String = "",
    var price: Int = 0,
    @SerializedName("region_type")
    var regionType: Int = 0,
    var sort: Int = 0,
    @SerializedName("start_time")
    var startTime: Long = 0,
    @SerializedName("support_scope")
    var supportScope: Int = 0,
    var threshold: Int = 0,
    @SerializedName("threshold_new")
    var thresholdNew: Int = 0,
    @SerializedName("thumbnail_url")
    var thumbnailUrl: String = "",
    var toast: Int = 0,
    var topic: String = "",
    var type: Int = 0,
    var width: Int = 0,
    @SerializedName("zip_url")
    var zipUrl: String = "",
    @SerializedName("zip_ver")
    var zipVer: Int = 0,
    @Embedded
    @SerializedName("extra_info")
    var extraInfo: ExtraInfo? = null,
//  素材下载进度（新增）
    var downloadProgress: Int = 0,
//  素材下载状态（新增）
    var beDownload: Int = DOWNLOAD_STATE_NOT_DOWNLOAD,
//  素材分类 - 海报，自由，拼接 (新增)
    var categoryType: Int = MATERIAL_TYPE_POSTER,
//  是否为新上架素材（新增）0:旧素材； 1:新素材
    var beNew: Int = MATERIAL_OLD
)

fun List<Material>.materialSort(): List<Material> {
    return sortedWith(
        compareByDescending<Material> {
            it.beTop
        }.thenByDescending {
            it.sort
        }
    )
}
