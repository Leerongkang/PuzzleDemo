package com.puzzle.template

/**
 * 拼图质量实体类
 */
data class PuzzleQuality(
    var name: String = "拼图",
    var category: String = "metric",
    var label: PuzzleLabel,
    var metric: PuzzleMetric,
    var baggage: Baggage
)

/**
 * 机型环境上下文的信息。
 */
data class Baggage(
    var android_sdk_int: Int = 0,               //  Android API Level
    var runtime_max_memory: Int = 0,            //  JVM 最大可分配空间。单位MB。
    var cpu: String = "other",                  //  CPU型号信息
    var cpu_vendor: String = "other",           //  CPU厂商信息
    var processor_count: Int = 0,               //  CPU个数
    var ram: Int                                //  当前手机RAM值。单位 MB。
)

/**
 * 记录拼图业务的报表分析字段。新字段需与后台协商一致。
 */
data class PuzzleMetric(
    var input_suc: Int = -1,                //  0:导入失败；1:导入成功
    var process_suc: Int = -1,              //  0:拼图处理失败；1:拼图处理成功  默认值-1不上报本字段。
    var output_suc: Int = -1,               //  0:成图导出失败；1:成图导出成功  默认值-1不上报本字段。
    var pt: Long = -1,                      //  拼图process阶段的处理耗时  默认值-1不上报本字段。
    var pt_import: Long = -1,               //  拼图导入图片阶段的耗时  默认值-1不上报本字段。
    var input_max: Int = -1,                //  用户所选择的图片的宽高中的最大值。最长边。
    var output_width: Int = -1,             //  拼图成图的输出宽，单位像素。
    var output_height: Int = -1,            //  拼图成图的输出高，单位像素。
    var input_origin_sizes: String = "",    //  用户导入的原始图片的宽高。(2020.12.01新增) sample: [[3168,4752],[3456,4608],[3456,4608],[1620,1620],[3456,4608]]
    var input_sizes: String = ""            //  手机内存加载原始图片后的真实宽高(可能被压缩)。(2020.12.01新增) sample: [[480,720],[540,720],[540,720],[720,720],[540,720]]
) {
    companion object {
        const val OPERATE_SUCCESS = 1
        const val OPERATE_FAILED = 0
    }
}

/**
 *   记录拼图业务的一些字段。不能与报表分析。新字段的定义不影响后台分析。
 */
data class PuzzleLabel(
    var `function`: String = FUNCTION_IMPORT,   //  拼图-自由、拼图-模板、拼图-海报、拼图-拼接、拼图-导入
    var material_id: String = "",               //  当前所使用的拼图素材
    var image_format: String = "jpg",           //  导出的图片的编码。  一般为jpg
    var uhd_enable: Boolean = false,            //  是否开启：超高清。
    var puzzle_import_size: Int = -1,           //  后台下发(本地策略兜底) 的拼图导入最大边边长值，单位像素。当不在后台策略范围时，为了上报不为0，选取屏幕宽高的最大值。
    var user_import_count: Int = -1,            //  用户在选图页选择的图片数量，存在重复选择的情况。
    var original_wh_count: Int = -1,            //  '原图宽高的导入'的个数
    var every_wh: String = "",                  //  { 当前图片重复的个数 * 图片宽 * 图片高 } 当前图片重复的个数为1时，不显示。 如：用户导入一张100*200的图片两次，再加一张200*300的图片， 则值为 2*100*200, 200*300 。
    var import_all_MB: Int = -1,                //  所有的图片以ARGB_8888加载进来所消耗的内存值，单位MB
    var output_MB: Int = -1,                    //  拼图的成图以ARGB_8888加载进来所消耗的内存值，单位MB
    var end_avail_runtime_MB: Int = -1,         //  拼图处理结束后，JVM剩余可用内存，单位MB  处理结束不一定代表处理成功。
    var end_avail_ROM_MB: Int = -1,             //  拼图处理结束后，ROM剩余可用内存，单位MB  处理结束不一定代表处理成功。
) {
    companion object {
        const val FUNCTION_TEMPLATE = "拼图-模板"
        const val FUNCTION_IMPORT = "拼图-导入"
    }
}
