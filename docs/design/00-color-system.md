# 00 · Color System — Rencar

Jetpack Compose `Color` token'ları. Tüm renkler buradan türetilir;
hardcoded hex **asla** kullanılmaz.

# renCar App - Renk Sistemi

> Bu dosya renCar isimli uygulamanın renk paleti için
> **tek doğruluk kaynağıdır** (single source of truth) ve
> doğrudan bir **Android Jetpack Compose** projesinde kullanılmak
> üzere düzenlenmiştir.

---

## 1. Temel Kurallar

> Hiçbir `@Composable` içinde ham `Color(0xFF)` yazılmaz.
> Renkler daima `MaterialTheme.colorSchema.<slot>` üzerinden
> okunmak zorundadır.
>
> Ham `Color(..)` tanımı yalnızca `Color.kt` içinde, sabit değişken tanımlanırken kullanılır.

---

## 1. `Color.kt` — Ham Token Tanımları

```kotlin
package com.turkcell.rencarapp.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand Blue ──────────────────────────────────────────────
val Blue50  = Color(0xFFEFF6FF)
val Blue100 = Color(0xFFDBEAFE)
val Blue500 = Color(0xFF3B82F6)
val Blue600 = Color(0xFF2563EB)   // Primary / CTA
val Blue700 = Color(0xFF1D4ED8)
val Blue800 = Color(0xFF1E40AF)
 
// ── Neutrals ─────────────────────────────────────────────────
val Neutral0   = Color(0xFFFFFFFF)
val Neutral50  = Color(0xFFF9FAFB)
val Neutral100 = Color(0xFFF2F4F7)  // Light background
val Neutral200 = Color(0xFFE5E7EB)  // Divider / border
val Neutral300 = Color(0xFFD1D5DB)
val Neutral400 = Color(0xFF9CA3AF)  // Placeholder
val Neutral500 = Color(0xFF6B7280)  // Secondary text
val Neutral700 = Color(0xFF374151)
val Neutral900 = Color(0xFF111827)  // Dark surface
val Neutral950 = Color(0xFF0D0D0D)  // Dark background
 
// ── Semantic ─────────────────────────────────────────────────
val Success500 = Color(0xFF22C55E)  // "Yüklendi" badge, checkmark
val Success100 = Color(0xFFDCFCE7)
val Error500   = Color(0xFFEF4444)  // "Kiralamayı Bitir" button
val Error100   = Color(0xFFFEE2E2)
val Warning500 = Color(0xFFF59E0B)  // Uyarı üçgeni (araç teslim)
 
// ── Vehicle Category ─────────────────────────────────────────
val CategoryEkonomik = Color(0xFFFF6B35)   // Turuncu marker
val CategoryKonfor   = Color(0xFF7C3AED)   // Mor marker
val CategorySUV      = Color(0xFFEAB308)   // Sarı marker
val CategoryYesil    = Color(0xFF10B981)   // Yeşil marker (₺26)
 
// ── Map Marker ───────────────────────────────────────────────
val MapMarkerSelected   = Color(0xFF2563EB)
val MapMarkerUnselected = Color(0xFFFFFFFF)
```
 
---

## Semantic Role Mapping

### Light Theme

| Role | Token | Hex | Kullanım |
|------|-------|-----|---------|
| `primary` | Blue600 | `#2563EB` | CTA buton, aktif nav, link |
| `onPrimary` | Neutral0 | `#FFFFFF` | Primary üstü metin/ikon |
| `primaryContainer` | Blue50 | `#EFF6FF` | Chip background (selected) |
| `onPrimaryContainer` | Blue700 | `#1D4ED8` | Chip metin (selected) |
| `background` | Neutral100 | `#F2F4F7` | Ekran arka planı |
| `onBackground` | Neutral900 | `#111827` | Ana başlık metni |
| `surface` | Neutral0 | `#FFFFFF` | Kart, bottom sheet, input |
| `onSurface` | Neutral900 | `#111827` | Kart içi başlık |
| `onSurfaceVariant` | Neutral500 | `#6B7280` | İkincil / placeholder metin |
| `outline` | Neutral200 | `#E5E7EB` | Input border, divider |
| `outlineVariant` | Neutral300 | `#D1D5DB` | Dashed border (fotoğraf alanı) |
| `error` | Error500 | `#EF4444` | Hata, "Bitir" butonu |
| `success` | Success500 | `#22C55E` | Onay badge |

### Dark Theme

| Role | Token | Hex | Kullanım |
|------|-------|-----|---------|
| `primary` | Blue500 | `#3B82F6` | (Dark'ta biraz açılır) |
| `onPrimary` | Neutral0 | `#FFFFFF` | |
| `background` | Neutral950 | `#0D0D0D` | Ekran arka planı |
| `onBackground` | Neutral0 | `#FFFFFF` | Ana başlık |
| `surface` | Neutral900 | `#111827` | Kart, bottom sheet |
| `onSurface` | Neutral0 | `#FFFFFF` | Kart içi başlık |
| `onSurfaceVariant` | Neutral400 | `#9CA3AF` | İkincil metin |
| `outline` | Neutral700 | `#374151` | Input border, divider |
| `outlineVariant` | Neutral700 | `#374151` | Dashed border |
 
---

## Compose ColorScheme Tanımı

```kotlin
// ui/theme/Color.kt (devam)
 
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
 
val RencarLightColorScheme = lightColorScheme(
    primary              = Blue600,
    onPrimary            = Neutral0,
    primaryContainer     = Blue50,
    onPrimaryContainer   = Blue700,
    secondary            = Neutral500,
    onSecondary          = Neutral0,
    background           = Neutral100,
    onBackground         = Neutral900,
    surface              = Neutral0,
    onSurface            = Neutral900,
    onSurfaceVariant     = Neutral500,
    outline              = Neutral200,
    outlineVariant       = Neutral300,
    error                = Error500,
    onError              = Neutral0,
    surfaceVariant       = Neutral50,
)
 
val RencarDarkColorScheme = darkColorScheme(
    primary              = Blue500,
    onPrimary            = Neutral0,
    primaryContainer     = Blue800,
    onPrimaryContainer   = Blue100,
    secondary            = Neutral400,
    onSecondary          = Neutral950,
    background           = Neutral950,
    onBackground         = Neutral0,
    surface              = Neutral900,
    onSurface            = Neutral0,
    onSurfaceVariant     = Neutral400,
    outline              = Neutral700,
    outlineVariant       = Neutral700,
    error                = Error500,
    onError              = Neutral0,
    surfaceVariant       = Color(0xFF1F2937),
)
```
 
---

## Özel Renkler (MaterialTheme dışı)

Harita markerları, kategori badge'leri gibi M3 scheme'ine girmeyen
renkler için `CompositionLocal` ile iletilir:

```kotlin
// ui/theme/RencarColors.kt
 
data class RencarExtendedColors(
    val categoryEkonomik: Color,
    val categoryKonfor:   Color,
    val categorySuv:      Color,
    val categoryYesil:    Color,
    val success:          Color,
    val successContainer: Color,
    val warning:          Color,
    val errorStrong:      Color,     // "Kiralamayı Bitir" kırmızısı
)
 
val LocalRencarColors = staticCompositionLocalOf {
    RencarExtendedColors(
        categoryEkonomik = CategoryEkonomik,
        categoryKonfor   = CategoryKonfor,
        categorySuv      = CategorySUV,
        categoryYesil    = CategoryYesil,
        success          = Success500,
        successContainer = Success100,
        warning          = Warning500,
        errorStrong      = Error500,
    )
}
 
// Kullanım: val ext = LocalRencarColors.current
//           ext.categoryEkonomik
```
 
---

## Alfa / Opasite Kuralları

| Durum | Alfa |
|-------|------|
| Disabled metin | `0.38f` |
| Disabled container | `0.12f` |
| Hover overlay (light) | `0.08f` |
| Pressed overlay | `0.12f` |
| Scrim (bottom sheet arka planı) | `0.5f` |

```kotlin
// Örnek — disabled buton rengi
val disabledContainer = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
val disabledContent   = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
```
 