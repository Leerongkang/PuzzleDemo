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
            "3001901001",
            "3001901002",
            "3001901003",
            "3001901009"
        )
        templateMap1[template11] = listOf(
            "3001901004",
            "3001901005",
            "3001901006",
            "3001901007",
            "3001901010",
            "3001901011"
        )
        templateMap1[template43] = listOf(
            "3001901008",
            "3001901012"
        )
        templateMap1[template169] = listOf(
            "3001901013",
            "3001901014"
        )
        templateMap1[templateFull] = listOf(
            "3001901015",
            "3001901016",
            "3001901017"
        )
        templateMap1[templateMore] = listOf(
            "3001901018",
            "3001901019",
            "3001901020"
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