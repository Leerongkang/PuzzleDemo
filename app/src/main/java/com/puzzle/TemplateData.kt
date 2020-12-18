package com.puzzle

object TemplateData {
    private const val template34 = 0
    private const val template11 = 1
    private const val template43 = 2
    private const val template169 = 3
    private const val templateFull = 4
    private const val templateMore = 5
    private val data = HashMap<Int, HashMap<Int, List<String>>>().apply {
        val templateMap1 = HashMap<Int, List<String>>()
        templateMap1[template34] = listOf(
            "1001",
            "1002",
            "1003",
            "1009"
        )
        templateMap1[template11] = listOf(
            "1004",
            "1005",
            "1006",
            "1007",
            "1010",
            "1011"
        )
        templateMap1[template43] = listOf(
            "1008",
            "1012"
        )
        templateMap1[template169] = listOf(
            "1013",
            "1014"
        )
        templateMap1[templateFull] = listOf(
            "1015",
            "1016",
            "1017"
        )
        templateMap1[templateMore] = listOf(
            "1018",
            "1019",
            "1020"
        )
        put(1, templateMap1)
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