package com.puzzle.material

import com.google.gson.annotations.SerializedName

data class Response(
    val data: Data,
    val error: String = "",
    @SerializedName("error_code")
    val errorCode: Int = 0,
    val msg: String = "",
    val ret: Int = 0
)

/**
 * 素材数据包
 */
data class Data(
    val items: CategoryGroup,
    @SerializedName("next_cursor")
    val nextCursor: String = ""
)

/**
 * 素材一级分类
 */
data class CategoryGroup(
    val categories: List<Category> = emptyList(),
    val id: Int = 0,
    val name: String = ""
)

/**
 * 素材二级分类
 */
data class Category(
    @SerializedName("category_id")
    val categoryId: Int = 0,
    val name: String = "",
    @SerializedName("sub_categories")
    val subCategories: List<SubCategory> = emptyList(),
    @SerializedName("updated_at")
    val updatedAt: Long = 0L
)

/**
 * 素材三级分类
 */
data class SubCategory(
    @SerializedName("end_time")
    val endTime: Long = 0L,
    val items: List<Material> = emptyList(),
    @SerializedName("max_version")
    val maxVersion: String = "",
    @SerializedName("min_version")
    val minVersion: String = "",
    val name: String = "",
    val sort: Int = 0,
    @SerializedName("sub_category_id")
    val subCategoryId: Int = 0,
    val type: Int = 0,
    @SerializedName("updated_at")
    val updatedAt: Long = 0
)

/**
 * 素材额外信息
 */
data class ExtraInfo(
    @SerializedName("is_choose_color")
    val beChooseColor: Int = 0,
    @SerializedName("is_color_logo")
    val beColorLogo: Int = 0,
    @SerializedName("is_multy")
    val BeMulty: Int = 0,
    @SerializedName("is_with_filter")
    val beWithFilter: Int = 0
)

const val NETWORK_ERROR = -8523

fun errorResponse(msg: String) = Response(Data(CategoryGroup()), msg, errorCode = NETWORK_ERROR)