import com.google.gson.annotations.SerializedName

data class Response(
    val data: Data,
    val error: String = "",
    @SerializedName("error_code")
    val errorCode: Int = 0,
    val msg: String = "",
    val ret: Int = 0
)

data class Data(
    val items: CategoryGroup,
    @SerializedName("next_cursor")
    val nextCursor: String = ""
)

data class CategoryGroup(
    val categories: List<Category> = emptyList(),
    val id: Int = 0,
    val name: String = ""
)

data class Category(
    @SerializedName("category_id")
    val categoryId: Int = 0,
    val name: String = "",
    @SerializedName("sub_categories")
    val subCategories: List<SubCategory> = emptyList(),
    @SerializedName("updated_at")
    val updatedAt: Long = 0L
)

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

data class Material(
    @SerializedName("background_img")
    val backgroundImg: String = "",
    @SerializedName("bgColor")
    val bgColor: String = "",
    val color: String = "",
    @SerializedName("created_at")
    val createdAt: Long = 0,
    @SerializedName("end_time")
    val endTime: Long = 0,
    @SerializedName("extra_info")
    val extraInfo: ExtraInfo,
    @SerializedName("has_music")
    val hasMusic: Int = 0,
    val height: Int = 0,
    @SerializedName("hot_sort")
    val hotSort: Int = 0,
    @SerializedName("is_dynamic")
    val beDynamic: Int = 0,
    @SerializedName("is_top")
    val beTop: Int = 0,
    @SerializedName("jump_buy_addr")
    val jumpBuyAddr: String = "",
    @SerializedName("jump_buy_icon")
    val jumpBuyIcon: String = "",
    @SerializedName("material_feature")
    val materialFeature: Int = 0,
    @SerializedName("material_id")
    val materialId: Long,
    @SerializedName("max_version")
    val maxVersion: String = "",
    @SerializedName("min_version")
    val minVersion: String = "",
    @SerializedName("music_id")
    val musicId: Int = 0,
    @SerializedName("music_start_at")
    val musicStartAt: Long = 0,
    val name: String = "",
    val preview: String = "",
    val price: Int = 0,
    @SerializedName("region_type")
    val regionType: Int = 0,
    val sort: Int = 0,
    @SerializedName("start_time")
    val startTime: Long = 0,
    @SerializedName("support_scope")
    val supportScope: Int = 0,
    val threshold: Int = 0,
    @SerializedName("threshold_new")
    val thresholdNew: Int = 0,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String = "",
    val toast: Int = 0,
    val topic: String = "",
    val type: Int = 0,
    val width: Int = 0,
    @SerializedName("zip_url")
    val zipUrl: String = "",
    @SerializedName("zip_ver")
    val zipVer: Int = 0
)

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