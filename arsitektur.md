# Stockita — Arsitektur Aplikasi

> Aplikasi **offline-first** untuk UMKM: kasir, stok, resep & HPP, pengeluaran, laporan, dan to-do.
> Prinsip: **simpel, ringan, cepat, mudah dikerjakan solo developer**.

---

## 1. Keputusan Utama

| Hal | Keputusan | Alasan |
|---|---|---|
| Arsitektur | **MVVM + Repository** (Clean ringan) | Cukup rapi, tidak over-engineered |
| Modul Gradle | **1 modul** (`:app`), dipisah per package | Build cepat, setup mudah |
| UI | Jetpack Compose + Material 3 | Modern, ringan, satu bahasa |
| Database | **Room** (SQLite) — satu-satunya sumber data | Offline, cepat, reaktif via Flow |
| Setting | DataStore (Preferences) | Pengganti SharedPreferences |
| DI | Hilt (alternatif lebih ringan: Koin) | Standar industri, bagus untuk portfolio |
| Async | Coroutines + Flow | Reaktif, tanpa library tambahan |
| Use Case | **Hanya untuk logika kompleks** (checkout, HPP, laporan) | CRUD biasa cukup ViewModel → Repository |
| Export | CSV + PDF (`PdfDocument` bawaan Android) | Tanpa library berat |
| Backup | JSON (kotlinx.serialization) via SAF | Portabel, tanpa izin storage |
| Excel `.xlsx` | **Tidak di MVP** (Apache POI membengkakkan APK) | CSV bisa dibuka Excel |

**Aturan emas:** UI tidak pernah menyentuh database. Logika stok & uang tidak boleh ada di Composable.

---

## 2. Gambaran Arsitektur

```text
┌──────────────────────────────────────────────┐
│  UI Layer (Compose)                          │
│  Screen  ◄── UiState (StateFlow) ── ViewModel │
│     └── event/aksi user ──────────────►      │
└──────────────────────┬───────────────────────┘
                       │
┌──────────────────────▼───────────────────────┐
│  Domain (hanya jika logika kompleks)         │
│  CheckoutUseCase · HitungHppUseCase          │
│  LaporanUseCase                              │
└──────────────────────┬───────────────────────┘
                       │
┌──────────────────────▼───────────────────────┐
│  Data Layer                                  │
│  Repository ──► Room DAO   (data utama)      │
│             ──► DataStore  (setting, tema)   │
│             ──► SAF/File   (PDF, CSV, backup)│
│             ──► WorkManager (reminder)       │
└──────────────────────────────────────────────┘
```

Alur satu arah: **Event ↓ — State ↑**.

---

## 3. Struktur Package

```text
com.stockita
├── StockitaApp.kt              # @HiltAndroidApp
├── MainActivity.kt
│
├── core/
│   ├── database/               # StockitaDatabase, entity, DAO, converter
│   ├── designsystem/           # Color.kt, Theme.kt, Type.kt, komponen umum
│   ├── navigation/             # NavHost, rute, bottom bar
│   ├── export/                 # CsvExporter, PdfExporter
│   ├── backup/                 # BackupManager (JSON)
│   ├── datastore/              # SettingsDataStore
│   ├── notification/           # ReminderWorker, notifikasi
│   ├── di/                     # Hilt modules
│   └── util/                   # Rupiah formatter, tanggal, Result
│
└── feature/
    ├── dashboard/
    ├── kasir/
    ├── produk/
    ├── stok/                   # bahan + mutasi stok
    ├── resep/                  # resep + HPP
    ├── pengeluaran/
    ├── tugas/                  # To-Do
    ├── laporan/
    └── pengaturan/
```

Isi tiap feature (flat, tanpa sub-layer berlapis):

```text
feature/kasir/
├── KasirScreen.kt
├── KasirViewModel.kt
├── KasirUiState.kt
├── KasirRepository.kt          # class biasa, interface hanya bila perlu di-fake saat test
├── CheckoutUseCase.kt          # hanya jika logikanya kompleks
└── component/
    ├── ProdukItem.kt
    └── CartItem.kt
```

> Kalau nanti membesar, baru pecah menjadi modul Gradle (`:feature:kasir`, `:core:database`, dst.).

---

## 4. Skema Database

Aturan data:
- **Uang → `Long`** (rupiah utuh). Jangan `Double`/`Float`.
- **Waktu → `Long`** (epoch millis).
- **Qty bahan → `Double`** (mis. 0.25 kg).
- Semua query yang sering dipakai punya **index**.

| Tabel | Kolom penting |
|---|---|
| `categories` | id, name, type (PRODUK / BAHAN) |
| `products` | id, name, categoryId, price, **stockMode** (`NONE` / `DIRECT` / `RECIPE`), stock, isActive |
| `materials` | id, name, unit, stock, minStock, lastCost, categoryId |
| `recipe_items` | id, productId, materialId, qty |
| `transactions` | id, createdAt, subtotal, discount, total, paymentMethod, note |
| `transaction_items` | id, transactionId, productId, **nameSnapshot**, **priceSnapshot**, **hppSnapshot**, qty |
| `stock_movements` | id, targetType, targetId, qty (+/−), reason (`PURCHASE`/`SALE`/`ADJUST`/`WASTE`), refId, createdAt |
| `expenses` | id, title, amount, category, createdAt, note |
| `tasks` | id, title, note, dueAt, isDone, priority, **refType**, **refId**, createdAt |

Penjelasan `stockMode` (menyederhanakan resep vs stok langsung):
- `NONE` → jasa / tidak melacak stok
- `DIRECT` → barang dagang (stok produk berkurang langsung)
- `RECIPE` → makanan/minuman (stok **bahan** yang berkurang sesuai resep)

Poin penting:
1. **Snapshot** nama, harga, dan HPP disimpan di `transaction_items`, agar laporan lama tidak berubah saat harga naik.
2. **Resep tidak butuh tabel terpisah** — cukup `recipe_items` per produk.
3. Setiap perubahan stok **wajib** lewat `stock_movements` (audit trail).
4. Setting toko, tema, dan preferensi → **DataStore**, bukan tabel.

Index yang disarankan:
```kotlin
// transactions
@Index("createdAt")
// transaction_items
@Index("transactionId"), @Index("productId")
// stock_movements
@Index("targetType", "targetId"), @Index("createdAt")
// tasks
@Index("isDone", "dueAt")
// expenses
@Index("createdAt")
```

---

## 5. Fitur To-Do (Tugas)

**Desain:** satu tabel `tasks`, mandiri tapi **bisa opsional terhubung** ke data usaha lewat `refType` + `refId` (nullable). Ini murah dibangun tapi terasa pintar.

```kotlin
@Entity(tableName = "tasks", indices = [Index("isDone", "dueAt")])
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String? = null,
    val dueAt: Long? = null,          // null = tanpa tenggat
    val isDone: Boolean = false,
    val priority: Int = 0,            // 0 normal, 1 penting
    val refType: String? = null,      // "MATERIAL" | "PRODUCT" | "EXPENSE" | null
    val refId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
```

| Tahap | Fitur |
|---|---|
| **MVP** | Tambah / centang / hapus tugas, tenggat, prioritas, filter (Hari ini · Mendatang · Selesai) |
| **v1.1** | Reminder notifikasi via WorkManager |
| **v1.2** | Tombol "Buat tugas restock" dari bahan yang stoknya ≤ `minStock` (isi `refType = MATERIAL`) |
| **Nanti** | Tugas berulang (bayar listrik tiap bulan) |

Catatan teknis:
- Reminder cukup **WorkManager OneTimeWork** dengan delay — hemat baterai, tidak perlu izin alarm eksak.
- Android 13+ butuh izin `POST_NOTIFICATIONS`; minta saat user pertama kali mengaktifkan reminder, bukan saat app dibuka.

---

## 6. Alur Checkout (Inti Aplikasi)

```text
Pilih produk → Cart (state di ViewModel, di memori)
        ↓
   Klik Bayar
        ↓
 CheckoutUseCase  →  KasirRepository
        ↓
 Room @Transaction (atomik, semua berhasil atau batal):
   1. Insert transactions
   2. Insert transaction_items (+ snapshot harga & HPP)
   3. Kurangi stok:
        DIRECT → products.stock
        RECIPE → materials.stock sesuai recipe_items × qty
   4. Insert stock_movements (reason = SALE, refId = transactionId)
        ↓
 Return Result → tampilkan struk / sukses
```

Kerangka kode:

```kotlin
@Dao
abstract class CheckoutDao {
    @Transaction
    open suspend fun checkout(trx: TransactionEntity, items: List<TransactionItemEntity>, movements: List<StockMovementEntity>) {
        val id = insertTransaction(trx)
        insertItems(items.map { it.copy(transactionId = id) })
        movements.forEach { applyMovement(it.copy(refId = id)) }   // update stok + insert movement
    }
    // ...
}
```

Validasi stok (cukup/tidak) dilakukan **di dalam transaksi yang sama** agar tidak terjadi stok minus akibat race.

---

## 7. Navigasi

Bottom bar maksimal 5 tab (rule of thumb Material):

| Tab | Isi |
|---|---|
| 🏠 Beranda | Dashboard ringkas: omzet hari ini, laba, stok menipis, tugas hari ini |
| 🧾 Kasir | Pilih produk, cart, bayar |
| 📦 Stok | Bahan, produk, resep, mutasi |
| ✅ Tugas | To-Do |
| ⋯ Lainnya | Pengeluaran, Laporan, Backup, Pengaturan |

Navigation Compose dengan rute bertipe (type-safe routes) dan satu `NavHost` di `core/navigation`.

---

## 8. Tema & Warna (Light, Putih + Oranye)

Warna utama: **`#FF6900`**. Mode: **light only** untuk MVP (dark mode opsional di rilis berikutnya).

| Token | Hex | Pemakaian |
|---|---|---|
| `primary` | `#FF6900` | Tombol utama, FAB, tab aktif, aksen |
| `onPrimary` | `#FFFFFF` | Teks/ikon di atas primary |
| `primaryContainer` | `#FFEDE0` | Chip aktif, highlight lembut, kartu aksen |
| `onPrimaryContainer` | `#7A3200` | Teks di atas primaryContainer |
| `background` | `#FFFFFF` | Latar halaman |
| `surface` | `#FFFFFF` | Kartu, sheet, dialog |
| `surfaceVariant` | `#F7F7F8` | Latar input, kartu sekunder |
| `outline` | `#E5E7EB` | Garis pemisah, border |
| `onSurface` | `#1F2937` | Teks utama |
| `onSurfaceVariant` | `#6B7280` | Teks sekunder, hint |
| `success` | `#16A34A` | Laba, stok aman, tugas selesai |
| `warning` | `#F59E0B` | Stok menipis |
| `error` | `#DC2626` | Rugi, gagal, stok habis |

```kotlin
// core/designsystem/Color.kt
val Orange        = Color(0xFFFF6900)
val OrangeSoft    = Color(0xFFFFEDE0)
val OrangeDeep    = Color(0xFF7A3200)
val Ink           = Color(0xFF1F2937)
val InkSoft       = Color(0xFF6B7280)
val Line          = Color(0xFFE5E7EB)
val Canvas        = Color(0xFFF7F7F8)

// core/designsystem/Theme.kt
private val LightColors = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = OrangeSoft,
    onPrimaryContainer = OrangeDeep,
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Canvas,
    onSurfaceVariant = InkSoft,
    outline = Line,
    error = Color(0xFFDC2626),
)
```

Panduan UI:
- **Satu aksen saja** (oranye). Sisanya putih/abu netral → tampilan bersih dan cepat dipahami.
- Putih di atas `#FF6900` kontrasnya sedang (~2.9:1): pakai untuk **teks tebal/besar** (tombol, label 16sp+). Untuk teks kecil di atas putih, gunakan `OrangeDeep` atau `Ink`, bukan oranye.
- Sudut membulat 12–16dp, elevasi minimal (pakai border tipis `outline` daripada shadow berat).
- Tombol & area sentuh minimal 48dp (kasir sering dipakai cepat/satu tangan).

---

## 9. Performa & Ringan

**Aplikasi**
- `minSdk 24`, `targetSdk` terbaru.
- Aktifkan **R8/minify + shrinkResources** di build release.
- Hindari library berat (Apache POI, iText). Pakai `PdfDocument` + CSV.
- Tambahkan **Baseline Profile** (opsional) untuk startup lebih cepat.

**Database**
- Semua query lewat `Flow` + `Dispatchers.IO` (Room sudah menangani).
- Index sesuai tabel di atas; agregasi laporan (SUM/GROUP BY) dilakukan **di SQL**, bukan di Kotlin.
- Riwayat transaksi panjang → **Paging 3** (atau `LIMIT/OFFSET` sederhana).

**Compose**
- `LazyColumn` selalu pakai `key = { it.id }`.
- State di ViewModel berupa **satu `UiState` immutable** via `StateFlow`; kumpulkan dengan `collectAsStateWithLifecycle()`.
- Gunakan `remember` / `derivedStateOf` untuk perhitungan turunan (total cart).
- Pisahkan Composable kecil agar recomposition terbatas.

**Kualitas**
- Satu `Result`/`UiState` konsisten: `Loading · Success · Error · Empty`.
- Semua teks ke `strings.xml` (siap multi-bahasa).
- Format rupiah lewat satu util: `Rp 12.500`.

---

## 10. Backup, Export, Import

| Fitur | Cara |
|---|---|
| **Backup** | Serialisasi seluruh tabel → JSON (kotlinx.serialization) → simpan via **Storage Access Framework** (user pilih lokasi, termasuk Google Drive) |
| **Restore** | Pilih file JSON → validasi versi skema → insert dalam satu `@Transaction` |
| **CSV** | Tulis stream langsung ke `Uri` dari SAF |
| **PDF** | `PdfDocument` bawaan Android untuk laporan & struk |
| **Auto-backup** | WorkManager periodik (v1.2), simpan ke folder pilihan user |

> Jangan copy file `.db` mentah sebagai backup utama — rawan beda versi skema. JSON memberi kontrol penuh dan portabel.
> Sertakan field `schemaVersion` di file backup.

---

## 11. Roadmap Build (Urutan Pengerjaan)

| Fase | Isi | Hasil |
|---|---|---|
| **0** | Setup project, Hilt, Navigation, theme (warna di atas) | Kerangka + bottom bar kosong |
| **1** | Room: entity, DAO, database, repository dasar | Data tersimpan |
| **2** | Master **Produk** & **Bahan** (CRUD, kategori) | Data master siap |
| **3** | **Stok** + `stock_movements` (masuk, koreksi, buang) | Stok akurat |
| **4** | **Resep + HPP** | Biaya pokok otomatis |
| **5** | **Kasir + Checkout** + struk | Transaksi nyata |
| **6** | **Pengeluaran** | Pencatatan biaya |
| **7** | **Tugas (To-Do)** MVP | Pengingat harian |
| **8** | **Dashboard** | Ringkasan harian |
| **9** | **Laporan** (penjualan, laba/rugi, stok) | Insight usaha |
| **10** | **Export** CSV/PDF + **Backup/Restore** | Data aman & portabel |
| **11** | Reminder tugas, auto-restock task, polish, tes | Siap rilis |
| **Nanti** | Auth, multi-user, sync online (Supabase/Firebase), `.xlsx`, dark mode | Opsional |

**Definisi MVP:** Fase 0–7 + Dashboard sederhana + Backup manual. Itu sudah cukup untuk dipakai UMKM nyata dan jadi portfolio yang kuat.

---

## 12. Tech Stack Ringkas

```text
Bahasa       : Kotlin
UI           : Jetpack Compose + Material 3
Arsitektur   : MVVM + Repository (+ UseCase untuk logika kompleks)
Navigasi     : Navigation Compose
DI           : Hilt
Database     : Room
Setting      : DataStore Preferences
Async        : Coroutines + Flow
Background   : WorkManager (reminder, auto-backup)
Serialisasi  : kotlinx.serialization (backup JSON)
File         : Storage Access Framework
PDF          : android.graphics.pdf.PdfDocument
Testing      : JUnit, Turbine (Flow), Room in-memory DB
```

---

## 13. Checklist Kualitas (untuk Portfolio)

- [ ] Checkout atomik (`@Transaction`) dan teruji
- [ ] HPP snapshot di `transaction_items`
- [ ] Semua perubahan stok tercatat di `stock_movements`
- [ ] Unit test untuk `CheckoutUseCase` & `HitungHppUseCase`
- [ ] Empty state, loading state, dan error state di tiap layar
- [ ] Backup → hapus data → restore berhasil
- [ ] APK release < ~15 MB, cold start cepat
- [ ] README: screenshot, fitur, arsitektur, cara build
