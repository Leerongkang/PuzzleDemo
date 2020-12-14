package com.lrk.puzzle.demo

object TemplateData {
    val data = HashMap<Int,HashMap<Int,List<String>>>().apply {
        val templateMap1 = HashMap<Int,List<String>>()
        templateMap1[0] = listOf("3001901001/thumbnail","3001901002/thumbnail","3001901003/thumbnail","3001901009/thumbnail")
        templateMap1[1] = listOf("3001901004/thumbnail","3001901005/thumbnail","3001901006/thumbnail","3001901007/thumbnail","3001901010/thumbnail","3001901011/thumbnail")
        templateMap1[2] = listOf("3001901008/thumbnail","3001901012/thumbnail")
        templateMap1[3] = listOf("3001901013/thumbnail","3001901014/thumbnail","3001901015/thumbnail")
        templateMap1[4] = listOf("3001901016/thumbnail","3001901017/thumbnail","3001901018/thumbnail","3001901019/thumbnail")
        templateMap1[5] = listOf("3001901020/thumbnail")
        put(1,templateMap1)
    }

    fun allTemplateWithPictureNum(num: Int) : List<String>{
        val map = data[num]
        if (map.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<String>()
        map.forEach{
            list.addAll(it.value)
        }
        return list
    }
}