package com.stockita.feature.backup

import android.content.Context
import android.net.Uri
import com.stockita.core.database.StockitaDatabase
import com.stockita.core.database.dao.TransactionDao
import kotlinx.coroutines.flow.firstOrNull
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupHelper @Inject constructor(
    private val transactionDao: TransactionDao
) {
    suspend fun exportTransactionsToCsv(context: Context, uri: Uri) {
        val transactions = transactionDao.getAllTransactions().firstOrNull() ?: emptyList()
        
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream).use { writer ->
                writer.write("ID,Tanggal,Total,Metode Pembayaran,Catatan\n")
                for (t in transactions) {
                    val noteStr = t.note?.replace(",", " ") ?: ""
                    writer.write("${t.id},${t.createdAt},${t.total},${t.paymentMethod},$noteStr\n")
                }
            }
        }
    }

    fun backupDatabase(context: Context, uri: Uri) {
        val dbFile = context.getDatabasePath("stockita_db")
        if (!dbFile.exists()) return

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            FileInputStream(dbFile).use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    fun restoreDatabase(context: Context, uri: Uri, db: StockitaDatabase) {
        // Need to close db before restore
        db.close()
        
        val dbFile = context.getDatabasePath("stockita_db")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(dbFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}
