package com.puzzle.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.puzzle.dao.MaterialDB
import com.puzzle.material.*
import com.puzzle.network.MaterialNetworkService
import com.puzzle.network.NetworkServiceCreator
import com.puzzle.showAppToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class MainActivityViewModel : ViewModel() {

    private val _posterMaterials = MutableLiveData<List<Material>>()

    val posterMaterials : LiveData<List<Material>>
        get() = _posterMaterials

    private val _freeMaterials = MutableLiveData<List<Material>>()

    val freeMaterials : MutableLiveData<List<Material>>
        get() = _freeMaterials

    private val _spliceMaterials = MutableLiveData<List<Material>>()

    val spliceMaterials : MutableLiveData<List<Material>>
        get() = _spliceMaterials

    private val materialNetwork = NetworkServiceCreator.create<MaterialNetworkService>()

    // 内置素材图片路径前缀
    private  val assetFilePath = "file:///android_asset/material"

    val materialDao = MaterialDB.db.materialDao()

    suspend fun pickMaterials(imageCount: Int) = withContext(Dispatchers.IO) {

        // 获取本地素材
        val posterLocal = async { materialDao.queryMaterial(MATERIAL_TYPE_POSTER) }
        val freeLocal = async { materialDao.queryMaterial(MATERIAL_TYPE_FREE) }
        val spliceLocal = async { materialDao.queryMaterial(MATERIAL_TYPE_SPLICE) }

        // 获取在线素材
        val posterNet = async { getNetWorkPosterMaterials(imageCount) }
        val spliceNet = async { getNetWorkSpliceMaterials() }
        val freeNet = async { getNetWorkFreeMaterials() }

        //
        val posterListLocal =  posterLocal.await()
        _posterMaterials.postValue(posterListLocal)

        val freeListLocal = freeLocal.await()
        _freeMaterials.postValue(freeListLocal)

        val spliceListLocal = spliceLocal.await()
        _spliceMaterials.postValue(spliceListLocal)

        val posterListNet = posterNet.await()
        _posterMaterials.postValue(posterListNet)

        val freeListNet = freeNet.await()
        _freeMaterials.postValue(freeListNet)

        val spliceListNet = spliceNet.await()
        _spliceMaterials.postValue(spliceListNet)
    }


    /**
     * 通过网络获取海报分类下的素材
     * @param imageCount 输入图片数量
     */
    suspend fun getNetWorkPosterMaterials(imageCount: Int): List<Material> {
        val response = try {
            materialNetwork.getPosterMaterials()
        } catch (e: Exception) {
            showAppToast(e.toString())
            errorResponse(e.toString())
        }
        val posterMaterials = getResponseMaterials(response)
        posterMaterials.forEach {
            it.categoryType = MATERIAL_TYPE_POSTER
        }
        // 素材入库
        val materials = insert2db(MATERIAL_TYPE_POSTER, posterMaterials)

        // 内置素材，海报分类下内置素材因输入图片数量而不同
        val innerMaterial = Material(
            thumbnailUrl = "$assetFilePath/poster/$imageCount/thumbnail",
            materialId = 3010L + imageCount,
            extraInfo = ExtraInfo(),
            beDownload = DOWNLOAD_STATE_DOWNLOADED,
            beNew = MATERIAL_OLD
        )
        materials.add(0, innerMaterial)
        return materials
    }

    /**
     * 通过网络获取自由分类下的素材
     */
    private suspend fun getNetWorkFreeMaterials(): List<Material> {
        val response = try {
            materialNetwork.getFreeMaterials()
        } catch (e: Exception) {
            showAppToast(e.toString())
            errorResponse(e.toString())
        }
        val freeMaterials = getResponseMaterials(response)
        freeMaterials.forEach {
            it.categoryType = MATERIAL_TYPE_FREE
        }
        // 素材入库
        val materials = insert2db(MATERIAL_TYPE_FREE, freeMaterials)

        // 内置素材
        val innerMaterial = Material(
            thumbnailUrl = "$assetFilePath/free/thumbnail",
            materialId = 3020L,
            extraInfo = ExtraInfo(),
            beDownload = DOWNLOAD_STATE_DOWNLOADED,
            beNew = MATERIAL_OLD
        )
        materials.add(0, innerMaterial)
        return materials
    }

    /**
     * 通过网络获取拼接分类下的素材
     */
    private suspend fun getNetWorkSpliceMaterials(): List<Material> {
        val response = try {
            materialNetwork.getSpliceMaterials()
        } catch (e: Exception) {
            showAppToast(e.toString())
            errorResponse(e.toString())
        }
        val spliceMaterials = getResponseMaterials(response)
        spliceMaterials.forEach {
            it.categoryType = MATERIAL_TYPE_SPLICE
        }
        // 素材入库
        val materials = insert2db(MATERIAL_TYPE_SPLICE, spliceMaterials)
        // 内置素材
        val innerMaterial = Material(
            thumbnailUrl = "$assetFilePath/splice/thumbnail",
            materialId = 3030L,
            extraInfo = ExtraInfo(),
            beDownload = DOWNLOAD_STATE_DOWNLOADED,
            beNew = MATERIAL_OLD
        )
        materials.add(0, innerMaterial)
        return materials
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

    /**
     * 素材分类入库：
     * 第一部分："完全新上架的素材"，标记新素材，直接入库。
     * 第二部分："已存在的素材"，取消新素材标记。
     * 第三部分："已下架的素材"
     *          a. "未下载素材" 或 "(不论下载与否的)限时素材"，
     *             操作：删除 Material
     *          b. "已下载的普通素材"
     *             操作：保留 Material
     *
     * @param type 素材类别 - 海报 ，自由，拼接
     * @param materialNet 在线素材
     */
    private suspend fun insert2db(type: Int, materialNet: List<Material>): MutableList<Material> {

        val materialLocal = materialDao.queryMaterial(type)
        if (materialNet.isEmpty()) {
            return materialLocal.filter(this::filerMaterial).toMutableList()
        }
        val localIDs = materialLocal.map { it.materialId }
        val netIDs = materialNet.map { it.materialId }
        // 1. "完全新上架的素材"
        val materialNew = materialNet.filter {
            it.materialId !in localIDs
        }
        materialNew.forEach {
            it.beNew = MATERIAL_NEW
        }
        if (materialNew.isNotEmpty()) {
            materialDao.insertMaterials(*materialNew.toTypedArray())
        }

        // 2. "已存在的素材"
        val materialExits = materialLocal.filter {
            it.materialId in netIDs
        }
//        materialExits.forEach {
//            it.beNew = 0
//        }
        if (materialExits.isNotEmpty()) {
            materialDao.updateMaterials(*materialExits.toTypedArray())
        }

        // 3. "已下架的素材"
        val materialDisable = materialLocal.filter {
            it.materialId !in netIDs
        }
        val materialDelete = materialDisable.filter(this::filerMaterial)

        if (materialDelete.isNotEmpty()) {
            materialDao.deleteMaterial(*materialDelete.toTypedArray())
        }
        // 有效素材
        val materialEnable = (materialNew + materialExits + materialDisable - materialDelete)
        return materialEnable.materialSort().toMutableList()
    }

    /**
     * 对素材进行过滤
     */
    private fun filerMaterial(material: Material): Boolean {
        // 未下载素材，直接删除
        if (material.beDownload == DOWNLOAD_STATE_NOT_DOWNLOAD) {
            return true
        }
        // 已下载素材，但为但仍在有效期内的素材，保留
        val now = System.currentTimeMillis() / 1_000   // 单位为秒
        if (now in material.startTime..material.endTime) {
            return false
        }
        // 已下载，但不在有效期内的素材，删除
        return true
    }

    suspend fun updateMaterial(vararg material: Material) = withContext(Dispatchers.IO){
        materialDao.updateMaterials(*material)
    }
}