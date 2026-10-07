package com.stockita.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// DESIGN SYSTEM TOKENS (design.md)
// ==========================================

// 2.1 Palet Primary (Violet)
val Primary50   = Color(0xFFF4F1FF)
val Primary100  = Color(0xFFECE7FF)
val Primary200  = Color(0xFFDDD5FF)
val Primary300  = Color(0xFFC4B7FA)
val Primary400  = Color(0xFFA292F0)
val Primary500  = Color(0xFF8068E0)
val Primary600  = Color(0xFF6C4FD3) // Warna utama: tombol primary, ikon aktif, link, fokus
val Primary700  = Color(0xFF5A3FB8) // Pressed state tombol primary
val Primary800  = Color(0xFF47318F)
val Primary900  = Color(0xFF342468)

// Hero Gradient
val GradientHeroStart = Color(0xFF7B5FE0)
val GradientHeroEnd   = Color(0xFF5E43C4)

// 2.2 Neutral (agak keunguan, bukan abu murni)
val BgApp          = Color(0xFFF5F3FF) // Latar halaman
val BgCanvas       = Color(0xFFEAE6FF)
val SurfaceColor   = Color(0xFFFFFFFF) // Kartu, sheet, modal, input
val SurfaceMuted   = Color(0xFFF8F7FD) // Latar tombol secondary netral, bottom nav
val Border         = Color(0xFFECE9F7) // Garis pemisah, border kartu dan input (1 px)
val BorderStrong   = Color(0xFFDDD9EE) // Border input fokus-awal, handle sheet
val TextPrimary    = Color(0xFF1F1B33) // Judul, angka penting, isi utama
val TextSecondary  = Color(0xFF5B5675) // Teks isi, label
val TextTertiary   = Color(0xFF8E89A6) // Caption, placeholder, hint
val TextDisabled   = Color(0xFFBDB9CF) // Teks nonaktif
val TextOnPrimary  = Color(0xFFFFFFFF) // Teks di atas gradient/primary
val Overlay        = Color(0x661F1B33) // Backdrop modal & bottom sheet (40%)

// 2.3 Semantic (status & keuangan)
val Success        = Color(0xFF2FB583)
val SuccessBg      = Color(0xFFE4F7EF)
val SuccessText    = Color(0xFF1E8A63)
val Warning        = Color(0xFFF2A23A)
val WarningBg      = Color(0xFFFFF3E0)
val WarningText    = Color(0xFFB7701A)
val Danger         = Color(0xFFE5566D)
val DangerBg       = Color(0xFFFDE9ED)
val DangerText     = Color(0xFFC13A52)
val Info           = Color(0xFF4C9BEB)
val InfoBg         = Color(0xFFE6F1FD)
val InfoText       = Color(0xFF2D77C4)

// 2.4 Warna Aksen Pastel (kategori, kartu tugas, ikon bulat)
val AccentLavenderBg = Color(0xFFE9E2FF)
val AccentLavender   = Color(0xFF7B5FE0)
val AccentBlueBg     = Color(0xFFDDEEFF)
val AccentBlue       = Color(0xFF3D8FE0)
val AccentPinkBg     = Color(0xFFFFE3F1)
val AccentPink       = Color(0xFFD857A5)
val AccentPeachBg    = Color(0xFFFFEBD9)
val AccentPeach      = Color(0xFFE08A3C)
val AccentMintBg     = Color(0xFFD9F5EA)
val AccentMint       = Color(0xFF2FB583)

// 2.5 Chart Tokens
val Chart1 = Color(0xFF6C4FD3)
val Chart2 = Color(0xFF4C9BEB)
val Chart3 = Color(0xFFF08BC3)
val Chart4 = Color(0xFFF2A23A)
val Chart5 = Color(0xFF2FB583)
val Chart6 = Color(0xFFA292F0)

// Backward-compatible aliases for legacy screens
val Orange        = Primary600
val OrangeSoft    = Primary100
val OrangeDeep    = Primary800
val Ink           = TextPrimary
val InkSoft       = TextSecondary
val Line          = Border
val Canvas        = BgApp
val ErrorColor    = Danger
