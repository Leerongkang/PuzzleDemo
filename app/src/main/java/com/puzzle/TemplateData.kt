package com.puzzle

object TemplateData {
    private const val template34 = 0
    private const val template11 = 1
    private const val template43 = 2
    private const val template169 = 3
    private const val templateFull = 4
    private const val templateMore = 5
    private val data = HashMap<Int, HashMap<Int, List<Template>>>().apply {

        val b34n2PV = Template(
            2, 400, 300, "1001",
            listOf(
                TemplateInfo(
                    0, 0, 300, 200
                ), TemplateInfo(
                    0, 200, 300, 400
                )
            )
        )
        val b34n2PH = Template(
            2, 400, 300, "1002",
            listOf(
                TemplateInfo(
                    0, 0, 150, 400
                ), TemplateInfo(
                    150, 0, 300, 400
                )
            )
        )
        val b34n4PVPH = Template(
            4, 400, 300, "1003",
            listOf(
                TemplateInfo(
                    0, 0, 150, 200
                ),
                TemplateInfo(
                    150, 0, 300, 200
                ),
                TemplateInfo(
                    0, 200, 150, 400
                ),
                TemplateInfo(
                    150, 200, 300, 400
                )
            )
        )
        val b34n3PV = Template(
            3, 120, 90, "1009",
            listOf(
                TemplateInfo(
                    0, 0, 90, 40
                ),
                TemplateInfo(
                    0, 40, 90, 80
                ),
                TemplateInfo(
                    0, 80, 90, 120
                )
            )
        )
        val b11n2PH = Template(
            2, 100, 100, "1004",
            listOf(
                TemplateInfo(
                    0, 0, 50, 100
                ),
                TemplateInfo(
                    50, 0, 100, 100
                )
            )
        )
        val b11n2PV = Template(
            2, 100, 100, "1005",
            listOf(
                TemplateInfo(
                    0, 0, 100, 50
                ),
                TemplateInfo(
                    0, 50, 100, 100
                )
            )
        )
        val b11n4PVPH = Template(
            4, 100, 100, "1006",
            listOf(
                TemplateInfo(
                    0, 0, 50, 50
                ),
                TemplateInfo(
                    50, 0, 100, 50
                ),
                TemplateInfo(
                    0, 50, 50, 100
                ),
                TemplateInfo(
                    50, 50, 100, 100
                )
            )
        )
        val b11n9PVPH = Template(
            9, 90, 90, "1007",
            listOf(
                TemplateInfo(
                    0, 0, 30, 30
                ),
                TemplateInfo(
                    30, 0, 60, 30
                ),
                TemplateInfo(
                    60, 0, 90, 30
                ),
                TemplateInfo(
                    0, 30, 30, 60
                ),
                TemplateInfo(
                    30, 30, 60, 60
                ),
                TemplateInfo(
                    60, 30, 90, 60
                ),
                TemplateInfo(
                    0, 60, 30, 90
                ),
                TemplateInfo(
                    30, 60, 60, 90
                ),
                TemplateInfo(
                    60, 60, 90, 90
                )
            )
        )
        val b11n3PV = Template(
            3, 90, 90, "1010",
            listOf(
                TemplateInfo(
                    0, 0, 90, 30
                ),
                TemplateInfo(
                    0, 30, 90, 60
                ),
                TemplateInfo(
                    0, 60, 90, 90
                )
            )
        )
        val b11n3PH = Template(
            3, 90, 90, "1011",
            listOf(
                TemplateInfo(
                    0, 0, 30, 90
                ),
                TemplateInfo(
                    30, 0, 60, 90
                ),
                TemplateInfo(
                    60, 0, 90, 90
                )
            )
        )
        val b43n2PH = Template(
            2, 30, 40, "1008",
            listOf(
                TemplateInfo(
                    0, 0, 20, 30
                ),
                TemplateInfo(
                    20, 0, 40, 30
                )
            )
        )
        val b43n2PV = Template(
            2, 30, 40, "2004",
            listOf(
                TemplateInfo(
                    0, 0, 40, 15
                ),
                TemplateInfo(
                    0, 15, 40, 30
                )
            )
        )
        val b43n3PH = Template(
            3, 90, 120, "1012",
            listOf(
                TemplateInfo(
                    0, 0, 40, 90
                ),
                TemplateInfo(
                    40, 0, 80, 90
                ),
                TemplateInfo(
                    80, 0, 120, 90
                )
            )
        )
        val b169n2PH = Template(
            3, 27, 48, "2007",
            listOf(
                TemplateInfo(
                    0, 0, 24, 27
                ),
                TemplateInfo(
                    24, 0, 48, 27
                )
            )
        )
        val b169n3PH = Template(
            3, 27, 48, "1013",
            listOf(
                TemplateInfo(
                    0, 0, 16, 27
                ),
                TemplateInfo(
                    16, 0, 32, 27
                ),
                TemplateInfo(
                    32, 0, 48, 27
                )
            )
        )
        val b169n4PH = Template(
            4, 27, 48, "1014",
            listOf(
                TemplateInfo(
                    0, 0, 12, 27
                ),
                TemplateInfo(
                    12, 0, 24, 27
                ),
                TemplateInfo(
                    24, 0, 36, 27
                ),
                TemplateInfo(
                    36, 0, 48, 27
                )
            )
        )
        val bFulln3PV = Template(
            3, 100, 50, "1015",
            listOf(
                TemplateInfo(
                    0, 0, 50, 33
                ),
                TemplateInfo(
                    0, 33, 50, 66
                ),
                TemplateInfo(
                    0, 66, 50, 100
                )
            )
        )
        val bFulln2NP = Template(
            2, 100, 50, "1016",
            listOf(
                TemplateInfo(
                    0, 0, 50, 70
                ),
                TemplateInfo(
                    0, 70, 50, 100
                )
            )
        )
        val bFulln2PV = Template(
            2, 100, 50, "1017",
            listOf(
                TemplateInfo(
                    0, 0, 50, 50
                ),
                TemplateInfo(
                    0, 50, 50, 100
                )
            )
        )
        val bMoren2PH = Template(
            2, 50, 100, "1018",
            listOf(
                TemplateInfo(
                    0, 0, 50, 50
                ),
                TemplateInfo(
                    50, 0, 100, 50
                )
            )
        )
        val bMoren2PV = Template(
            2, 100, 50, "2009",
            listOf(
                TemplateInfo(
                    0, 0, 50, 50
                ),
                TemplateInfo(
                    0, 50, 50, 100
                )
            )
        )
        val bMoren3PH = Template(
            3, 30, 90, "1019",
            listOf(
                TemplateInfo(
                    0, 0, 30, 30
                ),
                TemplateInfo(
                    30, 0, 60, 30
                ),
                TemplateInfo(
                    60, 0, 90, 30
                )
            )
        )
        val bMoren4PH = Template(
            4, 30, 120, "1020",
            listOf(
                TemplateInfo(
                    0, 0, 30, 30
                ),
                TemplateInfo(
                    30, 0, 60, 30
                ),
                TemplateInfo(
                    60, 0, 90, 30
                ),
                TemplateInfo(
                    90, 0, 120, 30
                )
            )
        )
        val templateMap1 = HashMap<Int, List<Template>>()
        templateMap1[template34] = listOf(
            b34n2PV,
            b34n2PH,
            b34n4PVPH,
            b34n3PV
        )
        templateMap1[template11] = listOf(
            b11n2PH,
            b11n2PV,
            b11n4PVPH,
            b11n9PVPH,
            b11n3PV,
            b11n3PH
        )
        templateMap1[template43] = listOf(
            b43n2PH,
            b43n3PH
        )
        templateMap1[template169] = listOf(
            b169n3PH,
            b169n4PH
        )
        templateMap1[templateFull] = listOf(
            bFulln3PV,
            bFulln2NP,
            bFulln2PV
        )
        templateMap1[templateMore] = listOf(
            bMoren2PH,
            bMoren3PH,
            bMoren4PH
        )
        put(1, templateMap1)

        val templateMap2 = HashMap<Int, List<Template>>()
        templateMap1[template34] = listOf(
            b34n2PV,
            b34n2PH
        )
        templateMap2[template11] = listOf(
            b11n2PH,
            b11n2PV
        )
        templateMap2[template43] = listOf(
            b43n2PH,
            b43n2PV
        )
        templateMap2[template169] = listOf(
            b169n2PH
        )
        templateMap2[templateFull] = listOf(
            bFulln2PV
        )
        templateMap2[templateMore] = listOf(
            bMoren2PV,
            bMoren2PH
        )

        put(2, templateMap2)

        val templateMap3 = HashMap<Int, List<Template>>()
        templateMap3[template34] = listOf(
            b34n3PV,
            Template(
                3, 40, 30, "3001",
                listOf(
                    TemplateInfo(0, 0, 30, 25),
                    TemplateInfo(0, 25, 15, 40),
                    TemplateInfo(15, 25, 30, 40)
                )
            )
        )
        templateMap3[template11] = listOf(
            b11n3PH,
            b11n3PV
        )
        templateMap3[template43] = listOf(
            b43n3PH
        )
        templateMap3[template169] = listOf(
            b169n3PH
        )
        templateMap3[templateFull] = listOf(
            bFulln3PV
        )
        templateMap3[templateMore] = listOf(
            bMoren3PH
        )
        put(3, templateMap3)

        val templateMap4 = HashMap<Int, List<Template>>()
        templateMap4[template34] = listOf(
            b34n4PVPH,
            Template(
                4, 120, 90, "4002",
                listOf(
                    TemplateInfo(0, 0, 45, 120),
                    TemplateInfo(45, 0, 90, 40),
                    TemplateInfo(45, 40, 90, 80),
                    TemplateInfo(45, 80, 90, 120)
                )
            )
        )
        templateMap4[template11] = listOf(
            b11n4PVPH
        )
        templateMap4[template43] = listOf(

        )
        templateMap4[template169] = listOf(
            b169n4PH
        )
        templateMap4[templateFull] = listOf(
        )
        templateMap4[templateMore] = listOf(
            bMoren4PH
        )
        put(4, templateMap4)
    }

    fun allTemplateWithPictureNum(num: Int): List<String> {
        val map = data[num]
        if (map.isNullOrEmpty()) {
            return emptyList()
        } else {
            return map.values.flatten().flatMap {
                listOf(it.templateThumbnail)
            }
        }
    }

    fun allTemplateWithNum(num: Int): List<Template> {
        val map = data[num]
        if (map.isNullOrEmpty()) {
            return emptyList()
        } else {
            return map.values.flatten()
        }
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