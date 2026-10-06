package com.stockita.feature.stok

import com.stockita.core.database.dao.MaterialDao
import com.stockita.core.database.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaterialRepository @Inject constructor(
    private val materialDao: MaterialDao
) {
    fun getAllMaterials(): Flow<List<MaterialEntity>> = materialDao.getAllMaterials()

    suspend fun insertMaterial(material: MaterialEntity): Long {
        return materialDao.insertMaterial(material)
    }

    suspend fun updateMaterial(material: MaterialEntity) {
        materialDao.updateMaterial(material)
    }
}
