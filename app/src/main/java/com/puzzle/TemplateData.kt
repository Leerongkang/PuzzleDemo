package com.puzzle

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 用于获取模板数据的单例
 */
object TemplateData {
    const val template34 = 0
    const val template11 = 1
    const val template43 = 2
    const val template169 = 3
    const val templateFull = 4
    const val templateMore = 5

    private val gson = Gson()
    private val mapType = object : TypeToken<HashMap<Int, List<Template>>>() {}.type
    private val data = HashMap<Int, HashMap<Int, List<Template>>>()

    /**
     * @return 返回对应图片数量的所有拼图模板的缩略图路径
     */
    suspend fun allTemplateThumbnailPathWithNum(num: Int, context: Context): List<String> {
        val map = loadTemplatesFromJson(num, context)
        if (map.isEmpty()) {
            return emptyList()
        } else {
            return map.values.flatten().flatMap {
                listOf(it.templateThumbnail)
            }
        }
    }

    /**
     * @return 返回对应图片数量的所有拼图模板
     */
    suspend fun allTemplateWithNum(num: Int, context: Context): List<Template> {
        val map = loadTemplatesFromJson(num, context)
        if (map.isEmpty()) {
            return emptyList()
        } else {
            return map.values.flatten()
        }
    }

    /**
     * 生成模板分类序号对应该分类下第一个模板序号的map，用于拼图界面模板与类别的联动
     * @return key: 模板分类序号，value: 该分类下的第一个模板的序号
     */
    suspend fun templateCategoryFirst(num: Int, context: Context): Map<Int, Int> {
        val map = loadTemplatesFromJson(num, context)
        if (map.isEmpty()) return emptyMap()
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
     * 生成模板序号与模板分类对应的map，用于拼图界面模板与类别的联动
     * @return key: 模板序号，value: 模板分类序号
     */
    suspend fun templateInCategory(num: Int, context: Context): Map<Int, Int> {
        val map = loadTemplatesFromJson(num, context)
        if (map.isEmpty()) return emptyMap()
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

    /**
     * 解析json文件中的模板数据
     */
    private suspend fun loadTemplatesFromJson(num: Int, context: Context) =
        withContext(Dispatchers.IO) {
            val map = data[num]
            if (map.isNullOrEmpty()) {
                val json = context.assets.open("data/$num").buffered().reader().readText()
                val mapFroJson = gson.fromJson<HashMap<Int, List<Template>>>(json, mapType)
                data[num] = mapFroJson
                mapFroJson
            } else {
                map
            }
        }
}