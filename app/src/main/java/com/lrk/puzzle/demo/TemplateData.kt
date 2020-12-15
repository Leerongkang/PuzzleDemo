package com.lrk.puzzle.demo

object TemplateData {
    private val data = HashMap<Int, HashMap<Int, List<String>>>().apply {
        val templateMap1 = HashMap<Int, List<String>>()
        templateMap1[0] = listOf(
            "3001901001/thumbnail",
            "3001901002/thumbnail",
            "3001901003/thumbnail",
            "3001901009/thumbnail"
        )
        templateMap1[1] = listOf(
            "3001901004/thumbnail",
            "3001901005/thumbnail",
            "3001901006/thumbnail",
            "3001901007/thumbnail",
            "3001901010/thumbnail",
            "3001901011/thumbnail"
        )
        templateMap1[2] = listOf("3001901008/thumbnail", "3001901012/thumbnail")
        templateMap1[3] =
            listOf("3001901013/thumbnail", "3001901014/thumbnail")
        templateMap1[4] = listOf(
            "3001901015/thumbnail",
            "3001901016/thumbnail",
            "3001901017/thumbnail"
        )
        templateMap1[5] = listOf(
            "3001901018/thumbnail",
            "3001901019/thumbnail",
            "3001901020/thumbnail")
        put(1, templateMap1)

        val templateMap2 = HashMap<Int,List<String>>()
        templateMap2[0] = listOf(
            "3001901001/thumbnail",
            "3001901002/thumbnail",
            "3001901003/thumbnail",
            "3001901009/thumbnail"
        )
        templateMap2[1] = listOf(
            "3001901004/thumbnail",
            "3001901005/thumbnail",
            "3001901006/thumbnail",
            "3001901007/thumbnail",
            "3001901010/thumbnail",
            "3001901011/thumbnail"
        )
        templateMap2[2] = listOf("3001901008/thumbnail", "3001901012/thumbnail")
        templateMap2[3] =
            listOf("3001901013/thumbnail", "3001901014/thumbnail")
        templateMap2[4] = listOf(
            "3001901015/thumbnail",
            "3001901016/thumbnail",
            "3001901017/thumbnail"
        )
        templateMap2[5] = listOf(
            "3001901018/thumbnail",
            "3001901019/thumbnail",
            "3001901020/thumbnail")
        put(2, templateMap2)
    }

    fun allTemplateWithPictureNum(num: Int): List<String> {
        val map = data[num]
        if (map.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<String>()
        map.forEach {
            list.addAll(it.value)
        }
        return list
    }

    /**
     *
     */
    fun templateCategoryFirst(num: Int): Map<Int, Int> {
        val map = data[num]
        if (map.isNullOrEmpty()) return emptyMap()
        val rangeMap = HashMap<Int, Int>()
        var index = 0
        map.forEach {
            if (!it.value.isNullOrEmpty()) {
                rangeMap[it.key] = index
                index += it.value.size
            }
        }
        return rangeMap
    }

    /**
     *
     * @return key: 模板序号，value: 模板分类序号
     */
    fun templateInCategory(num: Int): Map<Int, Int> {
        val map = data[num]
        if (map.isNullOrEmpty()) return emptyMap()
        val templateCategoryMap = HashMap<Int, Int>()
        var templateIndex = 0
        map.forEach {
            val list = it.value
            if (!list.isNullOrEmpty()) {
                list.forEach { _ ->
                    templateCategoryMap[templateIndex++] = it.key
                }
            }
        }
        return templateCategoryMap
    }
}