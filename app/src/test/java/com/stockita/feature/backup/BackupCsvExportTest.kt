package com.stockita.feature.backup

import com.stockita.core.database.entity.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.StringWriter

class BackupCsvExportTest {

    private fun generateTransactionsCsv(transactions: List<TransactionEntity>): String {
        val writer = StringWriter()
        writer.write("ID,Tanggal,Total,Metode Pembayaran,Catatan\n")
        for (t in transactions) {
            val noteStr = t.note?.replace(",", " ") ?: ""
            writer.write("${t.id},${t.createdAt},${t.total},${t.paymentMethod},$noteStr\n")
        }
        return writer.toString()
    }

    @Test
    fun `generateCsv with empty transactions produces valid header only`() {
        val csv = generateTransactionsCsv(emptyList())
        val lines = csv.lines().filter { it.isNotBlank() }
        assertEquals(1, lines.size)
        assertEquals("ID,Tanggal,Total,Metode Pembayaran,Catatan", lines[0])
    }

    @Test
    fun `generateCsv formats transaction rows correctly`() {
        val transactions = listOf(
            TransactionEntity(id = 1L, createdAt = 1700000000L, subtotal = 50000L, discount = 0L, total = 50000L, paymentMethod = "CASH", note = "Lunas"),
            TransactionEntity(id = 2L, createdAt = 1700003600L, subtotal = 25000L, discount = 0L, total = 25000L, paymentMethod = "QRIS", note = null)
        )

        val csv = generateTransactionsCsv(transactions)
        val lines = csv.lines().filter { it.isNotBlank() }

        assertEquals(3, lines.size)
        assertEquals("ID,Tanggal,Total,Metode Pembayaran,Catatan", lines[0])
        assertEquals("1,1700000000,50000,CASH,Lunas", lines[1])
        assertEquals("2,1700003600,25000,QRIS,", lines[2])
    }

    @Test
    fun `generateCsv sanitizes commas in note to prevent CSV column corruption`() {
        val transactions = listOf(
            TransactionEntity(
                id = 10L,
                createdAt = 1700010000L,
                subtotal = 35000L,
                discount = 0L,
                total = 35000L,
                paymentMethod = "CASH",
                note = "Meja 3, pedas, tanpa gula"
            )
        )

        val csv = generateTransactionsCsv(transactions)
        val lines = csv.lines().filter { it.isNotBlank() }

        assertEquals(2, lines.size)
        val row = lines[1]
        // Should not have raw unescaped commas breaking into more than 5 columns
        val columns = row.split(",")
        assertEquals(5, columns.size)
        assertEquals("Meja 3  pedas  tanpa gula", columns[4])
    }
}
