package com.stockita.feature.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.net.Uri
import com.stockita.core.database.StockitaDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupHelper: BackupHelper,
    private val database: StockitaDatabase
) : ViewModel() {

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    fun exportCsv(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                backupHelper.exportTransactionsToCsv(context, uri)
                _statusMessage.value = "Berhasil export CSV"
            } catch (e: Exception) {
                _statusMessage.value = "Gagal export CSV: ${e.message}"
            }
        }
    }

    fun backupDb(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                backupHelper.backupDatabase(context, uri)
                _statusMessage.value = "Berhasil backup database"
            } catch (e: Exception) {
                _statusMessage.value = "Gagal backup database: ${e.message}"
            }
        }
    }

    fun restoreDb(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                backupHelper.restoreDatabase(context, uri, database)
                _statusMessage.value = "Berhasil restore database. Silakan restart aplikasi."
            } catch (e: Exception) {
                _statusMessage.value = "Gagal restore database: ${e.message}"
            }
        }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }
}
