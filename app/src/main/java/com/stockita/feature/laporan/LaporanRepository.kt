package com.stockita.feature.laporan

import com.stockita.core.database.dao.ReportDao
import com.stockita.core.database.model.LabaRugiResult
import com.stockita.core.database.model.TopProductResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LaporanRepository @Inject constructor(
    private val reportDao: ReportDao
) {
    fun getLabaRugiSummary(startDate: Long, endDate: Long): Flow<LabaRugiResult> {
        return reportDao.getLabaRugiSummary(startDate, endDate)
    }

    fun getTopProducts(startDate: Long, endDate: Long): Flow<List<TopProductResult>> {
        return reportDao.getTopProducts(startDate, endDate)
    }
}
