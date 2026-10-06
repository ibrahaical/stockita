package com.stockita.feature.laporan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.model.LabaRugiResult
import com.stockita.core.database.model.TopProductResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

data class LaporanUiState(
    val isLoading: Boolean = false,
    val labaRugi: LabaRugiResult? = null,
    val topProducts: List<TopProductResult> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LaporanViewModel @Inject constructor(
    private val laporanRepository: LaporanRepository
) : ViewModel() {

    // Default to this month
    private val _dateRange = MutableStateFlow(getCurrentMonthRange())
    val dateRange: StateFlow<Pair<Long, Long>> = _dateRange

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<LaporanUiState> = _dateRange.flatMapLatest { (start, end) ->
        combine(
            laporanRepository.getLabaRugiSummary(start, end),
            laporanRepository.getTopProducts(start, end)
        ) { labaRugi, topProducts ->
            LaporanUiState(
                isLoading = false,
                labaRugi = labaRugi,
                topProducts = topProducts
            )
        }.catch { emit(LaporanUiState(error = it.message)) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LaporanUiState(isLoading = true)
    )

    fun setDateRange(start: Long, end: Long) {
        _dateRange.value = Pair(start, end)
    }

    private fun getCurrentMonthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        val calEnd = Calendar.getInstance()
        calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH))
        calEnd.set(Calendar.HOUR_OF_DAY, 23)
        calEnd.set(Calendar.MINUTE, 59)
        calEnd.set(Calendar.SECOND, 59)
        val end = calEnd.timeInMillis

        return Pair(start, end)
    }
}
