package com.puzzle.material

import com.puzzle.dao.MaterialDB
import com.puzzle.network.MaterialNetworkService
import com.puzzle.network.NetworkServiceCreator

/**
 * 素材仓库，用于获取本地和网络素材
 */
object MaterialRepository {

    private val materialNetworkService = NetworkServiceCreator.create<MaterialNetworkService>()

    // 内置素材图片路径前缀
    private const val assetFilePath = "file:///android_asset/material"

    val materialDao = MaterialDB.getDatabase().materialDao()

    /**
     * 通过网络获取海报分类下的素材
     * @param imageCount 输入图片数量
     */
    suspend fun getNetWorkPosterMaterials(imageCount: Int): List<Material> {
        val posterResponse = materialNetworkService.getPosterMaterials()
        val posterMaterials = getResponseMaterials(posterResponse)
        // 内置素材，海报分类下内置素材因输入图片数量而不同
        val innerMaterial = Material(
                                thumbnailUrl = "$assetFilePath/poster/$imageCount/thumbnail",
                                materialId = 3010L + imageCount,
                                extraInfo = ExtraInfo(),
                                beDownload = DOWNLOAD_STATE_DOWNLOADED
                            )
        posterMaterials.add(0, innerMaterial)
        posterMaterials.forEach {
            it.categoryType = MATERIAL_TYPE_POSTER
        }
        return posterMaterials
    }

    /**
     * 通过网络获取自由分类下的素材
     */
    suspend fun getNetWorkFreeMaterials(): List<Material> {
        val freeResponse = materialNetworkService.getFreeMaterials()
        val freeMaterials = getResponseMaterials(freeResponse)
        val innerMaterial = Material(
                                thumbnailUrl = "$assetFilePath/free/thumbnail",
                                materialId = 3020L,
                                extraInfo = ExtraInfo(),
                                beDownload = DOWNLOAD_STATE_DOWNLOADED
                            )
        freeMaterials.add(0, innerMaterial)
        freeMaterials.forEach {
            it.categoryType = MATERIAL_TYPE_FREE
        }
        return freeMaterials
    }

    /**
     * 通过网络获取拼接分类下的素材
     */
    suspend fun getNetWorkSpliceMaterials(): List<Material> {
        val spliceResponse = materialNetworkService.getSpliceMaterials()
        val spliceMaterials = getResponseMaterials(spliceResponse)
        val innerMaterial = Material(
                                thumbnailUrl = "$assetFilePath/splice/thumbnail",
                                materialId = 3030L,
                                extraInfo = ExtraInfo(),
                                beDownload = DOWNLOAD_STATE_DOWNLOADED
                            )
        spliceMaterials.add(0, innerMaterial)
        spliceMaterials.forEach {
            it.categoryType = MATERIAL_TYPE_SPLICE
        }
        return spliceMaterials
    }

    /**
     * 获取 [materialResponse] 中的全部素材
     * @param [materialResponse] 网络请求返回的请求体
     * @return 本次网络请求中返回的全部素材
     */
    private fun getResponseMaterials(materialResponse: Response): MutableList<Material> {
        val materials = mutableListOf<Material>()
        if (materialResponse.ret == 0) {
            val networkSubCategories = materialResponse.data.items.categories.flatMap {
                it.subCategories
            }
            val networkMaterials = networkSubCategories.flatMap {
                it.items
            }
            materials.addAll(networkMaterials)
        }
        return materials
    }

    suspend fun saveMaterials(materials: List<Material>) {
        materialDao.insertMaterials(*materials.toTypedArray())
    }

    suspend fun updateMaterials(materials: List<Material>) {

    }
}