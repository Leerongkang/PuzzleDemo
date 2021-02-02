package com.puzzle.material

import Material
import Response
import com.puzzle.network.MaterialNetworkService
import com.puzzle.network.NetworkServiceCreator

object MaterialRepository {

    private val materialNetworkService = NetworkServiceCreator.create<MaterialNetworkService>()

    suspend fun getPosterMaterials(): List<Material> {
        val posterResponse = materialNetworkService.getPosterMaterials()
        return getResponseMaterials(posterResponse)
    }

    suspend fun getSpliceMaterials(): List<Material> {
        val spliceResponse = materialNetworkService.getSpliceMaterials()
        return getResponseMaterials(spliceResponse)
    }

    suspend fun getFreeMaterials(): List<Material> {
        val freeResponse = materialNetworkService.getFreeMaterials()
        return getResponseMaterials(freeResponse)
    }

    private fun getResponseMaterials(materialResponse: Response): List<Material> {
        val materials = mutableListOf<Material>()
        if (materialResponse.ret == 0) {
            val networkFreeSubCategories = materialResponse.data.items.categories.flatMap {
                it.subCategories
            }
            val networkFreeMaterials = networkFreeSubCategories.flatMap {
                it.items
            }
            materials.addAll(networkFreeMaterials)
        }
        return materials
    }
}