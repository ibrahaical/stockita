# DESIGN.md — Design System Aplikasi Penjualan (Mobile)

> Dokumen acuan UI/UX. Semua layar, komponen, chart, modal, dan invoice WAJIB mengikuti token dan aturan di bawah.
> Jangan membuat nilai warna, ukuran, radius, atau shadow di luar token. Jika butuh yang baru, tambahkan dulu ke dokumen ini.

---

## 1. Karakter & Prinsip

**Kata kunci:** soft, ringan, rapi, tiny, ramah, bersih.

Gaya visual berasal dari referensi: ungu lavender, kartu putih berradius besar, gradasi pastel lembut, elemen kecil dan rapat tapi tetap lega.

| # | Prinsip | Artinya dalam praktik |
|---|---------|----------------------|
| 1 | **Soft, bukan blocky** | Semua sudut membulat. Tidak ada kotak tajam, border tebal, atau blok warna pekat besar. |
| 2 | **Tiny & rapi** | Teks kecil (11–14 px), ikon 16–20 px, komponen ringkas. Kesan ringan, bukan sempit. |
| 3 | **Satu fokus per layar** | Hanya satu tombol primary (ungu penuh) per layar. Sisanya secondary atau ghost. |
| 4 | **Warna = fungsi** | Warna dipakai untuk makna (aksi, sukses, bahaya, kategori), bukan dekorasi acak. |
| 5 | **Lega di luar, rapat di dalam** | Jarak antar section besar, jarak dalam komponen kecil dan konsisten. |
| 6 | **Satu tangan** | Aksi utama di bawah layar (zona jempol). Target sentuh minimal 44×44 px. |
| 7 | **Konsisten** | Komponen yang sama selalu tampil sama di semua modul. |

---

## 2. Warna

### 2.1 Palet Primary (Violet)

| Token | Hex | Fungsi |
|-------|-----|--------|
| `primary-50` | `#F4F1FF` | Latar tombol secondary, hover ringan, chip aktif lembut |
| `primary-100` | `#ECE7FF` | Latar ikon bulat, chip, badge ringan |
| `primary-200` | `#DDD5FF` | Border aktif lembut, track progress |
| `primary-300` | `#C4B7FA` | Dekorasi, ilustrasi |
| `primary-400` | `#A292F0` | Ikon sekunder di atas latar ungu, state disabled gelap |
| `primary-500` | `#8068E0` | Label kategori, link ringan, ikon aktif sekunder |
| **`primary-600`** | **`#6C4FD3`** | **Warna utama: tombol primary, ikon aktif, link, fokus** |
| `primary-700` | `#5A3FB8` | Pressed state tombol primary, teks di atas `primary-50/100` |
| `primary-800` | `#47318F` | Teks judul di atas latar ungu muda |
| `primary-900` | `#342468` | Bayangan tinted, teks gelap aksen |

**Gradient hero (header & kartu ringkasan):** `linear-gradient(160deg, #7B5FE0 0%, #5E43C4 100%)`

### 2.2 Neutral (agak keunguan, bukan abu murni)

| Token | Hex | Fungsi |
|-------|-----|--------|
| `bg-app` | `#F5F3FF` | Latar halaman |
| `bg-canvas` | `#EAE6FF` | Latar luar/pembungkus (desktop preview, area di belakang sheet) |
| `surface` | `#FFFFFF` | Kartu, sheet, modal, input |
| `surface-muted` | `#F8F7FD` | Latar tombol secondary netral, bottom nav, input disabled |
| `border` | `#ECE9F7` | Garis pemisah, border kartu dan input (1 px) |
| `border-strong` | `#DDD9EE` | Border input fokus-awal, handle sheet, divider tegas |
| `text-primary` | `#1F1B33` | Judul, angka penting, isi utama |
| `text-secondary` | `#5B5675` | Teks isi, label |
| `text-tertiary` | `#8E89A6` | Caption, placeholder, hint, label sumbu chart |
| `text-disabled` | `#BDB9CF` | Teks nonaktif |
| `text-on-primary` | `#FFFFFF` | Teks di atas gradient/primary |
| `overlay` | `rgba(31,27,51,0.40)` | Backdrop modal & bottom sheet |

> Dilarang memakai hitam murni `#000` atau abu netral `#888`/`#CCC`. Pakai token neutral di atas.

### 2.3 Semantic (status & keuangan)

| Token | Solid | Background lembut | Fungsi |
|-------|-------|-------------------|--------|
| `success` | `#2FB583` | `#E4F7EF` | Lunas, selesai, pemasukan, stok aman |
| `warning` | `#F2A23A` | `#FFF3E0` | Belum lunas, diproses, stok menipis, mendekati deadline |
| `danger` | `#E5566D` | `#FDE9ED` | Pengeluaran, batal, hapus, error, stok habis |
| `info` | `#4C9BEB` | `#E6F1FD` | Pesanan baru, informasi netral |

Teks di atas background lembut memakai versi tua: success `#1E8A63`, warning `#B7701A`, danger `#C13A52`, info `#2D77C4`.

**Aturan keuangan (wajib konsisten di seluruh app):**
- Pemasukan = `success`, selalu diawali tanda `+` dan ikon panah turun-masuk.
- Pengeluaran = `danger`, selalu diawali tanda `−` dan ikon panah keluar.
- Laba / saldo / omzet = `primary-600` atau `text-primary`.
- Jangan hanya mengandalkan warna. Selalu sertakan tanda +/− atau ikon (aksesibilitas).

### 2.4 Warna Aksen Pastel (kategori, kartu tugas, ikon bulat)

Dipakai sebagai gradient lembut `aksen-bg → #FFFFFF` (arah 90°, kiri ke kanan) dan label kecil berwarna.

| Nama | Latar pastel | Warna label/ikon | Contoh pemakaian |
|------|-------------|------------------|------------------|
| Lavender | `#E9E2FF` | `#7B5FE0` | Tugas umum, pengenalan |
| Biru | `#DDEEFF` | `#3D8FE0` | Pesanan, informasi |
| Pink | `#FFE3F1` | `#D857A5` | Pelanggan, promo |
| Peach | `#FFEBD9` | `#E08A3C` | Stok, pelatihan |
| Mint | `#D9F5EA` | `#2FB583` | Keuangan, selesai |

### 2.5 Warna Chart

Urutan seri (jangan diacak):

| Seri | Hex | Catatan |
|------|-----|---------|
| 1 | `#6C4FD3` | Seri utama / satu metrik tunggal |
| 2 | `#4C9BEB` | |
| 3 | `#F08BC3` | |
| 4 | `#F2A23A` | |
| 5 | `#2FB583` | |
| 6 | `#A292F0` | Maksimal 6 seri; lebih dari itu gabungkan jadi "Lainnya" (`#BDB9CF`) |

Khusus grafik **pemasukan vs pengeluaran**: pemasukan `#2FB583`, pengeluaran `#E5566D`.

---

## 3. Tipografi

**Font:** `Plus Jakarta Sans` (fallback: `Poppins`, `Inter`, `system-ui`, `sans-serif`).
Angka uang dan tabel memakai `font-variant-numeric: tabular-nums`.

| Token | Ukuran / Line-height | Weight | Fungsi |
|-------|---------------------|--------|--------|
| `display` | 28 / 36 | 700 | Angka hero (omzet hari ini, total invoice) |
| `title-lg` | 20 / 28 | 700 | Judul halaman, sapaan nama |
| `title` | 16 / 24 | 600 | Judul section, judul modal |
| `subtitle` | 14 / 20 | 600 | Judul kartu, nama produk, tombol |
| `body` | 13 / 20 | 400–500 | Isi teks, item list |
| `body-sm` | 12 / 16 | 400–500 | Teks pendukung, label form |
| `caption` | 11 / 14 | 400–500 | Meta (tanggal, nama pembuat), helper, badge |
| `micro` | 10 / 12 | 500 | Label sumbu chart, label bottom nav (ukuran minimum absolut) |

Aturan:
- Hanya weight 400, 500, 600, 700. Tidak ada 800/900.
- Tidak ada teks di bawah 10 px. Tidak ada teks di atas 28 px.
- Letter-spacing 0, kecuali label kategori huruf besar kecil (`caption`, +0.04em).
- Judul halaman tidak boleh ALL CAPS. Huruf besar hanya untuk label kategori mikro.
- Warna teks: judul `text-primary`, isi `text-secondary`, meta `text-tertiary`.

---

## 4. Spacing, Padding, Margin

**Basis 4 px.** Skala yang diizinkan:

| Token | px | Pemakaian umum |
|-------|----|----------------|
| `space-0.5` | 2 | Jarak sangat rapat (judul–subjudul dalam item) |
| `space-1` | 4 | Jarak ikon–teks kecil |
| `space-2` | 8 | Jarak antar elemen kecil, gap chip |
| `space-3` | 12 | Gap antar kartu/list item, padding kartu compact |
| `space-4` | 16 | Padding kartu standar, gap antar field form |
| `space-5` | 20 | **Padding horizontal layar**, padding sheet/modal |
| `space-6` | 24 | Jarak antar section |
| `space-8` | 32 | Jarak besar (sebelum CTA, antar blok besar) |
| `space-10` | 40 | Ruang atas empty state |

### Aturan penempatan
| Konteks | Nilai |
|---------|-------|
| Padding horizontal layar | 20 |
| Padding atas konten setelah header | 16 |
| Padding bawah konten | 24 + tinggi bottom nav + safe area |
| Padding kartu standar | 16 |
| Padding kartu compact (list item) | 12 horizontal 14 |
| Gap antar kartu dalam list | 12 |
| Jarak antar section | 24 |
| Jarak judul section ke konten | 12 |
| Gap antar field form | 16 |
| Jarak label ke input | 6 |
| Gap ikon ke teks | 8 |
| Gap antar chip | 8 |
| Padding sheet/modal | 20 (atas 12 untuk handle) |
| Bar CTA bawah | padding 16 20 + safe area |

---

## 5. Radius, Border, Shadow

### Radius
| Token | px | Pemakaian |
|-------|----|-----------|
| `r-xs` | 6 | Bar chart (ujung atas), tag kecil |
| `r-sm` | 10 | Thumbnail produk, tooltip kecil |
| `r-md` | 14 | Input multiline, toast, item list |
| `r-lg` | 18 | **Kartu standar** |
| `r-xl` | 24 | Kartu hero, dialog, kartu besar |
| `r-2xl` | 28 | Bottom sheet (sudut atas), area header bawah membulat |
| `r-full` | 999 | **Tombol, input, chip, badge, avatar, FAB, ikon bulat** |

### Border
- Standar 1 px `border`. Jangan lebih dari 1 px.
- Aksen kiri pada kartu tugas: 2 px, warna label kategori, radius mengikuti kartu.
- Banyak kartu tidak butuh border. Cukup `surface` + shadow `sm` di atas `bg-app`.

### Shadow (tinted ungu, sangat halus)
| Token | Nilai | Pemakaian |
|-------|-------|-----------|
| `shadow-sm` | `0 2px 8px rgba(108,79,211,0.06)` | Kartu, list item |
| `shadow-md` | `0 6px 20px rgba(108,79,211,0.10)` | Kartu hero, dropdown, bar CTA |
| `shadow-lg` | `0 12px 32px rgba(52,36,104,0.14)` | Modal, bottom sheet, FAB |
| `focus-ring` | `0 0 0 3px rgba(108,79,211,0.25)` | Fokus input/tombol |

---

## 6. Ikon & Ilustrasi

- Gaya **line icon**, stroke 1.5 px, ujung bulat (`round` cap & join). Set yang disarankan: Lucide / Phosphor (Regular).
- Ukuran: 16 (inline/kecil), **20 (default)**, 24 (bottom nav/header).
- Ikon di dalam lingkaran: wadah 36 atau 40 px, `r-full`, latar warna aksen pastel, ikon berwarna label aksen.
- Warna ikon: default `text-secondary`, aktif `primary-600`, di atas gradient putih.
- Ilustrasi (empty state/onboarding): bentuk bulat/organik, palet ungu pastel, tanpa outline hitam. Area ilustrasi 120–160 px dalam lingkaran `primary-100`.

---

## 7. Layout & Struktur Layar

### 7.1 Viewport
- Baseline desain: **390 × 844**. Minimum 360 px. Lebar konten maksimal 480 px (dipusatkan di layar lebih besar, latar `bg-canvas`).
- Selalu hormati safe area (notch dan home indicator).

### 7.2 Pola Layar A — Layar dengan Header Gradient (Beranda, Dashboard)
```
[ Header zone: gradient primary, sudut bawah r-2xl, padding 20 ]
   Avatar 36  •  Sapaan "Halo," (body-sm, putih 80%)  •  Chip poin/saldo (pill putih)
   Nama/Toko (title-lg, putih)
   Chip filter horizontal (pill, semi-transparan putih 16%)
[ Kartu hero overlap (naik -24px): surface, r-xl, shadow-md ]
[ Sheet konten: surface, sudut atas r-2xl, handle 36×4 ]
   Section: Tugas hari ini / Pesanan terbaru / Ringkasan
[ Bottom nav ]
```

### 7.3 Pola Layar B — Layar Standar (list, form, detail)
```
[ App bar: tinggi 56, tombol back bulat 36 (surface + border), judul title (center/left), aksi kanan ]
[ Konten scroll, padding horizontal 20 ]
[ Bar CTA sticky bawah (jika ada aksi utama) ]
```

### 7.4 App Bar
- Tinggi 56, latar transparan di atas `bg-app` (atau `surface` jika konten di-scroll, lengkap dengan shadow-sm).
- Tombol back/aksi: lingkaran 36, `surface`, border 1 px `border`, ikon 20.
- Judul `title` (16/600). Subjudul/konteks `caption` `primary-500` di atas judul (contoh "Website Project" biru kecil di atas "UX Fundamental Sharing").

### 7.5 Bottom Navigation
- Wadah mengambang: margin 12 dari tepi, tinggi 56, `surface-muted`/`surface`, `r-2xl`, shadow-md.
- 5 slot: **Beranda · Pesanan · (+) · Produk · Laporan**.
- Tombol tengah (+) = FAB 48 px, `primary-600`, ikon putih, `shadow-lg`, menonjol 8 px ke atas. Membuka bottom sheet "Aksi Cepat" (Buat Pesanan, Tambah Produk, Catat Pemasukan, Catat Pengeluaran, Tambah Tugas).
- Item aktif: ikon `primary-600` + garis indikator 16×3 px `r-full` di bawah ikon. Nonaktif: ikon `text-tertiary`. Label `micro`.
- Menu lain (Tugas, Keuangan, Pelanggan, Backup, Pengaturan) lewat avatar di header → layar "Lainnya", atau shortcut di Beranda.

---

## 8. Komponen

### 8.1 Tombol
| Varian | Latar | Teks | Tinggi | Radius | Pemakaian |
|--------|-------|------|--------|--------|-----------|
| **Primary** | `primary-600` (pressed `primary-700`) | putih, 14/600 | 48 | full | Aksi utama (maks. 1 per layar): Simpan, Buat Pesanan, Bagikan |
| **Secondary** | `primary-50` (atau `surface-muted`) | `primary-700` / `text-primary`, 14/500 | 48 | full | Aksi alternatif (Batal, Hanya yang ini) |
| **Ghost** | transparan | `primary-600`, 13/600 | 36 | full | Lewati, Lihat semua |
| **Destructive** | `danger-bg` | `danger` tua, 14/600 | 48 | full | Hapus, Restore (menimpa data) |
| **Small** | mengikuti varian | 13/600 | 36 | full | Aksi di dalam kartu |
| **Icon button** | `surface` + border | ikon 20 | 36–44 | full | Back, filter, share |

- Lebar penuh (`100%`) untuk CTA di bawah form/sheet. Dua tombol bersebelahan: rasio 1:1, gap 12.
- Disabled: latar `border`, teks `text-disabled`, tanpa shadow.
- Pressed: skala 0.98, durasi 120 ms.
- Ikon dalam tombol 18 px, gap 8.

### 8.2 Input & Form
- Tinggi 44, `r-full` untuk input satu baris (search, nama, angka). `r-md` (14) untuk textarea/multiline.
- Latar `surface`, border 1 px `border`, padding horizontal 16, teks `body` (13), placeholder `text-tertiary`.
- Fokus: border `primary-500` + `focus-ring`. Error: border `danger`, pesan `caption` `danger` di bawah (margin atas 4).
- Label di atas input: `body-sm` 500 `text-secondary`, jarak 6. Helper `caption` `text-tertiary`.
- Input rupiah: prefix "Rp" `text-tertiary`, nilai rata kiri, `tabular-nums`, format `1.250.000`.
- Tombol tambah di dalam input (contoh "+ ungu bulat" di kanan): 32 px bulat `primary-600`, ikon putih.
- **Qty stepper:** pill tinggi 36, tombol − dan + bulat 28 (`primary-50`, ikon `primary-700`), angka tengah 13/600.
- **Search bar:** input pill dengan ikon cari 18 di kiri, `surface-muted`, tanpa border.
- **Toggle:** 40×24, `r-full`, aktif `primary-600`, nonaktif `border-strong`, knob putih 18 px.
- **Checkbox/To-do:** bulat 22 px. Kosong: border 1.5 px `border-strong`. Tercentang: latar `primary-600` + centang putih. Task selesai: judul di-coret dan `text-tertiary`.
- **Select/Dropdown:** tampil seperti input pill + ikon chevron; pilihan dibuka dalam bottom sheet (bukan dropdown native kecil).

### 8.3 Chip & Badge
| Komponen | Spesifikasi |
|----------|-------------|
| **Chip filter** | Tinggi 32, padding 0 12, `r-full`, `caption` 500. Nonaktif: `surface` + border. Aktif: `primary-100` + teks `primary-700`. Titik warna kecil 6 px di kiri (opsional). Scroll horizontal, tanpa wrap. |
| **Badge status** | Tinggi 22, padding 0 10, `r-full`, 11/600, latar semantic lembut, teks semantic tua. |
| **Chip poin/saldo** (di header gradient) | Latar putih, teks `text-primary` 12/600, tinggi 28, `r-full`. |

Pemetaan badge status:
| Status | Warna |
|--------|-------|
| Baru | info |
| Diproses | warning |
| Dikirim | primary (`primary-100` / `primary-700`) |
| Selesai / Lunas | success |
| Belum lunas | warning |
| Dibatalkan | neutral (`border` / `text-secondary`) |
| Stok habis | danger |

### 8.4 Kartu
- **Kartu standar:** `surface`, `r-lg` (18), padding 16, `shadow-sm`, tanpa border.
- **Kartu hero ringkasan:** gradient primary, `r-xl` (24), padding 20, teks putih, `shadow-md`. Isi: label `body-sm` putih 80%, angka `display`, delta `caption` dalam chip putih 16%.
- **Kartu statistik kecil (grid 2 kolom):** `surface`, `r-lg`, padding 14; ikon bulat 32 di atas, label `caption` `text-tertiary`, nilai `title` 16/700, delta `caption` semantic. Gap 12.
- **Kartu tugas / to-do:** latar gradient pastel (aksen → putih), `r-lg`, padding 12 14, aksen kiri 2 px. Isi: label kategori `caption` 600 warna aksen, judul `subtitle`/`body` 600, meta `caption` `text-tertiary`, checkbox bulat di kanan.
- **Item list (pesanan, produk, transaksi):** `surface`, `r-md`, padding 12 14, tinggi minimal 60. Kiri: ikon/thumbnail bulat 40 (`r-sm` untuk foto produk 48). Tengah: judul `body` 600 + meta `caption`. Kanan: nilai/badge. Divider 1 px `border` inset 14 jika list tanpa kartu.
- Kartu bisa di-tap: pressed latar `primary-50`, tanpa perubahan layout.

### 8.5 Modal, Dialog, Bottom Sheet
**Bottom sheet (default untuk form, pilihan, aksi cepat, filter):**
- Latar `surface`, sudut atas `r-2xl` (28), padding 20 (atas 12), `shadow-lg`.
- Handle 36×4 `border-strong`, `r-full`, di tengah atas.
- Judul `title` (16/600), tombol tutup bulat 32 (opsional) di kanan.
- Tinggi maksimal 90% layar; isi scroll di dalam. Tombol aksi sticky di bawah.
- Backdrop `overlay`, tap untuk tutup. Animasi naik 280 ms.

**Dialog konfirmasi (hanya untuk keputusan singkat/berbahaya):**
- Lebar `min(320px, 100% − 48px)`, `r-xl` (24), padding 24, tengah layar.
- Ikon bulat 48 (semantic lembut) → judul `title` → deskripsi `body-sm` `text-secondary` → tombol bertumpuk penuh (primary/destructive di atas, secondary di bawah, gap 8).
- Hapus/Restore: tombol destructive di atas, teks jelas menyebut dampak ("Data saat ini akan ditimpa").

**Toast / Snackbar:** pill `r-md`, latar `#2B2542`, teks putih 12/500, padding 10 16, muncul 16 px di atas bottom nav, hilang 2.5 detik. Varian sukses/error: ikon 16 di kiri berwarna semantic.

### 8.6 Tab & Segmented Control
- **Segmented (Harian | Mingguan | Bulanan):** wadah `surface-muted` `r-full` tinggi 36 padding 3; segmen aktif `surface` + `shadow-sm` + teks `primary-700` 12/600; nonaktif `text-tertiary`.
- **Tab bertitik:** teks `body-sm`, aktif `primary-600` + garis 2 px `r-full` di bawah.

### 8.7 Empty State, Loading, Error
- **Empty:** lingkaran 120 `primary-100` + ikon/ilustrasi, judul `subtitle`, deskripsi `body-sm` `text-tertiary` (maks. 2 baris), tombol primary kecil. Rata tengah, ruang atas 40.
- **Skeleton:** blok `r-md` warna `#F0EEF8` + shimmer halus 1.4 detik; bentuk mengikuti komponen aslinya.
- **Error:** kartu `danger-bg`, ikon, pesan jelas dan tombol "Coba lagi".

### 8.8 Progress
- Linear: tinggi 6, `r-full`, track `primary-100`, isi `primary-600`.
- Lingkaran (target penjualan): stroke 8, ujung bulat, track `primary-100`.

---

## 9. Chart & Visualisasi Data

| Aturan | Spesifikasi |
|--------|-------------|
| Wadah | Kartu standar (`surface`, `r-lg`, padding 16). Judul `subtitle`, filter periode segmented di kanan atas. |
| Latar & grid | Tanpa latar; garis grid horizontal putus-putus 1 px `border`; tanpa garis sumbu. |
| Label sumbu | `micro` 10/500 `text-tertiary`. Maksimal 5–6 label sumbu X. |
| **Line / Area** | Garis 2.5 px, ujung bulat, kurva halus (smooth). Area fill gradient warna seri 20% → 0%. Titik aktif: lingkaran 8 px putih + stroke 2.5 px warna seri. |
| **Bar** | Lebar 12–16, radius atas `r-xs` (6), jarak antar bar 8–12. Bar tidak aktif: warna seri 35%. |
| **Donut** | Ketebalan 14, ujung bulat, celah antar segmen 3°. Angka total di tengah (`title-lg`) dengan label `caption`. |
| Legend | Di bawah chart, titik bulat 8 px + label `caption`, gap 12. Wrap bila perlu. |
| Tooltip | Pill `r-md`, latar `#2B2542`, teks putih `caption`, nilai 12/600. |
| Warna | Ikuti §2.5. Pemasukan vs pengeluaran memakai hijau dan merah. |
| Angka | Format ringkas pada sumbu (`1,2 jt`, `450 rb`), penuh di tooltip (`Rp 1.250.000`). |
| Empty chart | Garis datar putus-putus + teks "Belum ada data periode ini". |
| Animasi | Muncul 400 ms ease-out. Hormati *reduced motion*. |

---

## 10. Hierarki & Aturan Konsistensi

1. **Satu primary CTA per layar.** Aksi lain secondary atau ghost.
2. **Urutan hierarki visual:** angka/judul utama (`display`/`title-lg`) → judul section (`title`) → item (`subtitle`/`body`) → meta (`caption`).
3. **Maksimal 2 level kartu bersarang.** Jangan taruh kartu bershadow di dalam kartu bershadow; gunakan `surface-muted` untuk blok dalam.
4. **Form panjang** dipecah per section dengan judul `subtitle`, bukan satu list tak berujung. Aksi simpan di bar sticky bawah.
5. **Aksi destruktif** selalu lewat dialog konfirmasi dan tidak pernah jadi primary ungu.
6. **Jangan campur radius** dalam satu grup komponen yang sejenis.
7. **Teks aksi** berupa kata kerja singkat berbahasa Indonesia ("Simpan", "Buat Pesanan", "Bagikan Invoice").
8. **Konfirmasi hasil** dengan toast singkat, bukan dialog.
9. **Daftar panjang:** header section lengket (sticky) dan pull-to-refresh.
10. **Pemisahan visual** memakai jarak (spacing) dan perbedaan latar lembut, bukan garis tebal.

---

## 11. Format Data (Locale Indonesia)

| Jenis | Format | Contoh |
|-------|--------|--------|
| Uang penuh | `Rp` + spasi + titik ribuan | `Rp 1.250.000` |
| Uang negatif/pengeluaran | tanda − di depan | `−Rp 150.000` |
| Pemasukan | tanda + di depan | `+Rp 500.000` |
| Uang ringkas | `rb`, `jt`, `M` | `Rp 1,2 jt` |
| Tanggal | `Hari, D Bln YYYY` | `Rab, 7 Okt 2026` |
| Tanggal pendek | `DD/MM/YY` | `07/10/26` |
| Waktu | 24 jam | `08:58` |
| Waktu relatif | | `2 jam lagi`, `Kemarin` |
| Nomor invoice | `INV-YYYYMMDD-###` | `INV-20261007-001` |
| Stok | angka + satuan | `24 pcs` |

---

## 12. Pola Layar per Modul

### 12.1 Beranda / Dashboard
Pola A. Urutan: header gradient (sapaan + toko) → kartu hero (Omzet hari ini, delta vs kemarin) → grid 2×2 statistik (Pesanan, Pemasukan, Pengeluaran, Laba) → shortcut aksi (ikon bulat: Pesanan, Produk, Keuangan, Tugas) → "Tugas hari ini" (kartu tugas, maks. 3 + Lihat semua) → "Pesanan terbaru" (item list, maks. 5).

### 12.2 Tugas / To-Do (Pencatatan)
Pola B. Header: judul + tombol tambah. Segmented (Hari ini | Mendatang | Selesai). Kartu tugas per kategori dengan checkbox. Tambah/edit tugas lewat bottom sheet (judul, kategori chip, tanggal & jam, catatan, pengingat toggle). Swipe kiri = hapus, swipe kanan = selesai.

### 12.3 Produk
Pola B. Search bar + chip kategori. Item list (thumbnail 48, nama, harga, stok + badge). Tombol FAB/primary "Tambah Produk". **Form produk:** foto (kotak `r-lg` putus-putus `primary-200`, ikon kamera), nama, kategori (select sheet), harga jual, harga modal, stok, satuan, SKU (opsional), toggle aktif. CTA sticky "Simpan Produk".

### 12.4 Buat Pesanan (Flow 3 langkah)
Indikator langkah di atas: titik/pill kecil (aktif memanjang `primary-600`).
1. **Pilih produk:** search + grid/list produk, qty stepper langsung di item; bar ringkasan mengambang di bawah (jumlah item + total) dengan tombol "Lanjut".
2. **Detail pelanggan & pengiriman:** pelanggan (cari/tambah), tanggal, catatan, diskon, ongkir, metode bayar (chip).
3. **Ringkasan & konfirmasi:** daftar item, rincian total, status bayar (Lunas / Belum lunas). CTA "Simpan Pesanan", lalu halaman sukses dengan tombol "Bagikan Invoice".

### 12.5 Detail Pesanan & Invoice
Pola B. Kartu header (nomor, tanggal, badge status), daftar item, rincian total, riwayat pembayaran. Aksi bawah: **Bagikan Invoice** (primary), Ubah Status (secondary), menu ⋯ (cetak, duplikat, batalkan).

### 12.6 Laporan Penjualan
Pola B. Segmented periode (Hari | Minggu | Bulan | Kustom). Kartu hero total omzet + delta. Chart line/area omzet. Grid ringkasan (jumlah pesanan, rata-rata nilai, produk terjual). Donut/bar "Produk terlaris" dan daftar top 5. Tombol ghost "Export Excel".

### 12.7 Keuangan (Pemasukan & Pengeluaran)
Pola B. Kartu ringkasan 2 kolom (Pemasukan hijau | Pengeluaran merah) + Saldo/Laba. Chart bar berpasangan pemasukan vs pengeluaran. Tab: Semua | Pemasukan | Pengeluaran. Daftar transaksi dikelompokkan per tanggal (header `caption` `text-tertiary`). Tambah transaksi lewat bottom sheet: tipe (segmented), nominal besar (`display`, tengah), kategori (chip), tanggal, catatan, lampiran (opsional).

### 12.8 Data: Export, Backup & Restore
Pola B, layar "Data & Cadangan". Tiga kartu aksi dengan ikon bulat:
| Kartu | Isi | Aksi |
|-------|-----|------|
| **Export Excel (.xlsx)** | Pilih data (Pesanan, Produk, Keuangan) dan periode | Primary "Export" |
| **Backup (.json)** | Info "Terakhir backup: 7 Okt 2026, 08:58" + ukuran | Primary "Backup Sekarang" |
| **Restore dari file** | Pilih file .json | Secondary "Pilih File" lalu **dialog konfirmasi destructive** |

Aturan: nama file `backup-penjualan-YYYYMMDD-HHmm.json` dan `laporan-penjualan-YYYYMMDD.xlsx`. Sebelum restore tampilkan pratinjau jumlah data (X pesanan, Y produk), validasi versi file, dan sarankan backup otomatis terlebih dulu. Selesai → toast sukses. Progress memakai bar linear pada bottom sheet.

### 12.9 Pengaturan / Lainnya
Daftar grup (kartu `surface`, item dengan ikon bulat kiri, chevron kanan): Profil Toko, Pelanggan, Kategori, Template Invoice, Data & Cadangan, Notifikasi, Tentang. Versi aplikasi `caption` di bawah.

---

## 13. Invoice (Gambar JPEG untuk WhatsApp)

Invoice dirender dari HTML ke gambar (mis. `html2canvas`/`dom-to-image`) lalu dibagikan sebagai **JPEG**.

| Properti | Nilai |
|----------|-------|
| Lebar render | 540 CSS px, `devicePixelRatio = 2` → hasil **1080 px** lebar, tinggi menyesuaikan |
| Format | JPEG kualitas 0.92 (latar putih solid, tanpa transparansi) |
| Latar | `#FFFFFF`; area ringkasan total `primary-50` |
| Padding | 32 (di sekeliling), antar blok 20 |
| Font | Plus Jakarta Sans (dimuat sebelum render). Angka `tabular-nums`. |
| Radius | Blok total `r-lg` (18); badge `r-full` |

**Susunan (atas ke bawah):**
1. **Header:** logo toko bulat 40 + nama toko `title` 16/700 + alamat/kontak `caption`. Di kanan: kata "INVOICE" `caption` 700 `primary-600` (+0.08em), nomor `body-sm`, tanggal `caption`.
2. **Garis tipis** 1 px `border`, atau gradient halus ungu → transparan.
3. **Ditagihkan kepada:** label `caption` `text-tertiary`, nama pelanggan `subtitle`, kontak `body-sm`. Badge status (LUNAS hijau / BELUM LUNAS oranye) di kanan.
4. **Tabel item:** kolom *Item · Qty · Harga · Subtotal*. Header `caption` 600 `text-tertiary`, baris `body` 13 dengan divider 1 px `border`, tanpa zebra. Nama produk 600, varian/catatan `caption`.
5. **Ringkasan total** (blok `primary-50`, `r-lg`, padding 16): Subtotal, Diskon (`danger`), Ongkir, **Total** (`title-lg` 20/700 `primary-700`), Dibayar, Sisa.
6. **Info pembayaran:** bank/e-wallet, nomor rekening, atas nama (`body-sm`). Catatan (opsional).
7. **Footer:** "Terima kasih telah berbelanja 💜" `body-sm` `text-tertiary`, tengah.

**Pembagian:** gunakan Web Share API (`navigator.share({ files })`) agar langsung bisa dipilih WhatsApp. Sediakan fallback unduh + salin teks ringkas. Nama file: `INV-YYYYMMDD-###.jpg`.

---

## 14. Motion

| Jenis | Durasi | Easing |
|-------|--------|--------|
| Tekan tombol/kartu (skala 0.98) | 120 ms | ease-out |
| Hover/warna/fokus | 150 ms | ease-out |
| Transisi halaman (geser horizontal 16 px + fade) | 220 ms | `cubic-bezier(0.2, 0.8, 0.2, 1)` |
| Bottom sheet naik/turun | 280 ms | `cubic-bezier(0.2, 0.8, 0.2, 1)` |
| Toast masuk/keluar | 200 ms | ease-out |
| Chart masuk | 400 ms | ease-out |

Gerak harus halus dan fungsional, tanpa efek memantul berlebihan. Hormati `prefers-reduced-motion`.

---

## 15. Aksesibilitas

- Kontras teks minimal WCAG AA (4.5:1 untuk teks biasa). `primary-600` di atas putih sudah memenuhi.
- Target sentuh minimal 44×44 px (area tap boleh lebih besar dari tampilan visual).
- Status dan keuangan tidak boleh hanya berbeda warna: tambahkan ikon, tanda +/−, atau teks.
- Fokus terlihat jelas (`focus-ring`) untuk navigasi keyboard/switch.
- Label wajib untuk setiap input. Placeholder bukan pengganti label.
- Tombol ikon wajib punya `aria-label`.

---

## 16. Dilarang (Anti-Pattern)

- Sudut tajam atau radius kecil (< 10 px) pada kartu, tombol, input.
- Border tebal (> 1 px), outline hitam, dan blok warna pekat berukuran besar.
- Warna di luar token (hitam murni, abu netral, merah/hijau terang jenuh).
- Font di bawah 10 px atau di atas 28 px; ALL CAPS pada judul.
- Lebih dari satu tombol primary per layar.
- Shadow gelap/keras, atau shadow berlapis pada kartu bersarang.
- Dropdown native kecil untuk pilihan penting (pakai bottom sheet).
- Modal tengah untuk form panjang (pakai bottom sheet).
- Chart dengan lebih dari 6 seri, 3D, atau warna acak.
- Dekorasi yang tidak berfungsi (gradient/pola tanpa makna).

---

## 17. Design Tokens (siap pakai)

### 17.1 CSS Variables
```css
:root {
  /* Primary */
  --primary-50:#F4F1FF; --primary-100:#ECE7FF; --primary-200:#DDD5FF; --primary-300:#C4B7FA;
  --primary-400:#A292F0; --primary-500:#8068E0; --primary-600:#6C4FD3; --primary-700:#5A3FB8;
  --primary-800:#47318F; --primary-900:#342468;
  --gradient-hero: linear-gradient(160deg,#7B5FE0 0%,#5E43C4 100%);

  /* Neutral */
  --bg-app:#F5F3FF; --bg-canvas:#EAE6FF; --surface:#FFFFFF; --surface-muted:#F8F7FD;
  --border:#ECE9F7; --border-strong:#DDD9EE;
  --text-primary:#1F1B33; --text-secondary:#5B5675; --text-tertiary:#8E89A6;
  --text-disabled:#BDB9CF; --text-on-primary:#FFFFFF;
  --overlay:rgba(31,27,51,0.40);

  /* Semantic */
  --success:#2FB583; --success-bg:#E4F7EF; --success-text:#1E8A63;
  --warning:#F2A23A; --warning-bg:#FFF3E0; --warning-text:#B7701A;
  --danger:#E5566D;  --danger-bg:#FDE9ED;  --danger-text:#C13A52;
  --info:#4C9BEB;    --info-bg:#E6F1FD;    --info-text:#2D77C4;

  /* Aksen pastel (bg / label) */
  --accent-lavender-bg:#E9E2FF; --accent-lavender:#7B5FE0;
  --accent-blue-bg:#DDEEFF;     --accent-blue:#3D8FE0;
  --accent-pink-bg:#FFE3F1;     --accent-pink:#D857A5;
  --accent-peach-bg:#FFEBD9;    --accent-peach:#E08A3C;
  --accent-mint-bg:#D9F5EA;     --accent-mint:#2FB583;

  /* Chart */
  --chart-1:#6C4FD3; --chart-2:#4C9BEB; --chart-3:#F08BC3;
  --chart-4:#F2A23A; --chart-5:#2FB583; --chart-6:#A292F0;

  /* Spacing */
  --space-0-5:2px; --space-1:4px; --space-2:8px; --space-3:12px; --space-4:16px;
  --space-5:20px; --space-6:24px; --space-8:32px; --space-10:40px;

  /* Radius */
  --r-xs:6px; --r-sm:10px; --r-md:14px; --r-lg:18px; --r-xl:24px; --r-2xl:28px; --r-full:999px;

  /* Shadow */
  --shadow-sm:0 2px 8px rgba(108,79,211,0.06);
  --shadow-md:0 6px 20px rgba(108,79,211,0.10);
  --shadow-lg:0 12px 32px rgba(52,36,104,0.14);
  --focus-ring:0 0 0 3px rgba(108,79,211,0.25);

  /* Typography */
  --font-sans:'Plus Jakarta Sans','Poppins','Inter',system-ui,sans-serif;
  --fs-display:28px; --fs-title-lg:20px; --fs-title:16px; --fs-subtitle:14px;
  --fs-body:13px; --fs-body-sm:12px; --fs-caption:11px; --fs-micro:10px;

  /* Layout */
  --screen-px:20px; --appbar-h:56px; --nav-h:56px; --btn-h:48px; --btn-h-sm:36px; --input-h:44px;
}
```

### 17.2 Tailwind (`tailwind.config.js` — extend)
```js
theme: {
  extend: {
    colors: {
      primary: { 50:'#F4F1FF',100:'#ECE7FF',200:'#DDD5FF',300:'#C4B7FA',400:'#A292F0',
                 500:'#8068E0',600:'#6C4FD3',700:'#5A3FB8',800:'#47318F',900:'#342468' },
      app: '#F5F3FF', canvas: '#EAE6FF', muted: '#F8F7FD',
      line: '#ECE9F7', 'line-strong': '#DDD9EE',
      ink: { DEFAULT:'#1F1B33', 2:'#5B5675', 3:'#8E89A6', 4:'#BDB9CF' },
      success: { DEFAULT:'#2FB583', bg:'#E4F7EF', text:'#1E8A63' },
      warning: { DEFAULT:'#F2A23A', bg:'#FFF3E0', text:'#B7701A' },
      danger:  { DEFAULT:'#E5566D', bg:'#FDE9ED', text:'#C13A52' },
      info:    { DEFAULT:'#4C9BEB', bg:'#E6F1FD', text:'#2D77C4' },
    },
    fontFamily: { sans: ['Plus Jakarta Sans','Poppins','Inter','system-ui','sans-serif'] },
    fontSize: {
      display:['28px','36px'], 'title-lg':['20px','28px'], title:['16px','24px'],
      subtitle:['14px','20px'], body:['13px','20px'], 'body-sm':['12px','16px'],
      caption:['11px','14px'], micro:['10px','12px'],
    },
    borderRadius: { xs:'6px', sm:'10px', md:'14px', lg:'18px', xl:'24px', '2xl':'28px' },
    boxShadow: {
      sm:'0 2px 8px rgba(108,79,211,0.06)',
      md:'0 6px 20px rgba(108,79,211,0.10)',
      lg:'0 12px 32px rgba(52,36,104,0.14)',
    },
  },
}
```

---

## 18. Instruksi Singkat untuk AI Pembangun UI

Saat membuat atau mengubah layar/komponen apa pun, patuhi:

1. Gunakan **hanya token** dari dokumen ini (warna, spacing, radius, shadow, font). Jangan memakai nilai bebas.
2. Gaya **soft & tiny**: semua sudut membulat, teks 11–14 px, ikon line 20 px, shadow tinted ungu halus.
3. Tombol dan input berbentuk **pill (`r-full`)**, kartu **`r-lg`/`r-xl`**, sheet **`r-2xl`** di sudut atas.
4. Satu **primary CTA** per layar. Aksi destruktif memakai gaya destructive plus konfirmasi.
5. Form, pilihan, dan filter memakai **bottom sheet**. Dialog tengah hanya untuk konfirmasi singkat.
6. Pemasukan = hijau (+), pengeluaran = merah (−), laba/omzet = ungu. Selalu sertakan tanda atau ikon.
7. Chart mengikuti §9 (palet urut, grid putus-putus, bar berujung bulat, tooltip gelap).
8. Format data mengikuti §11 (Rupiah titik ribuan, tanggal Indonesia).
9. Layout mobile 390 px, padding layar 20, bottom nav 5 slot dengan FAB tengah.
10. Jika ada kebutuhan yang belum tercakup, **ikuti pola komponen terdekat** lalu usulkan penambahan di dokumen ini, bukan membuat gaya baru.
