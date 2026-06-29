# 01 · Typography — Rencar

Jetpack Compose `TextStyle` token'ları. Tüm yazı stilleri buradan türetilir.
 
---

## Font Ailesi

Tasarımda kullanılan font **Inter**'dır (system-ui fallback: Roboto).

```kotlin
// ui/theme/Type.kt
 
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
 
val InterFamily = FontFamily(
    Font(R.font.inter_regular,     FontWeight.Normal),
    Font(R.font.inter_medium,      FontWeight.Medium),
    Font(R.font.inter_semibold,    FontWeight.SemiBold),
    Font(R.font.inter_bold,        FontWeight.Bold),
    Font(R.font.inter_extrabold,   FontWeight.ExtraBold),
)
```

> **Kurulum:** `res/font/` klasörüne Inter .ttf dosyalarını ekle.
> Alternatif olarak Google Fonts Compose kütüphanesi kullanılabilir:
> ```
> implementation("androidx.compose.ui:ui-text-google-fonts:<version>")
> ```
 
---

## Type Scale

M3 type scale'e birebir map'lenir. Rencar'ın tasarımından ölçekler:

```kotlin
// ui/theme/Type.kt (devam)
 
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
 
val RencarTypography = Typography(
 
    // ── Display ─────────────────────────────────────────────
    // Kullanım: Splash ekranı "Rencar" logosu — doğrudan Image ile yapılır,
    //           TextStyle olarak nadiren kullanılır.
    displayLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize   = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.5).sp,
    ),
 
    // ── Headline ─────────────────────────────────────────────
    // Kullanım: "Tekrar hoş geldin", "Telefonunu doğrula",
    //           "Yolculuk tamamlandı", "Kiralamalarım"
    headlineLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.3).sp,
    ),
 
    // Kullanım: "Araç durumu", "Rezervasyon Onayı", "Cüzdan"
    headlineMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.2).sp,
    ),
 
    // ── Title ────────────────────────────────────────────────
    // Kullanım: TopBar başlığı ("Ehliyet doğrulama"), kart başlığı
    titleLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
    ),
 
    // Kullanım: "Renault Clio" (araç adı), "Kiralamalarım" liste başlığı
    titleMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
 
    // Kullanım: "Kiralama planı" bölüm başlığı, "Yakınımdaki kartlar"
    titleSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
 
    // ── Body ─────────────────────────────────────────────────
    // Kullanım: "Telefon numaranı gir, SMS ile doğrulama…" açıklama metni
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
 
    // Kullanım: Genel liste item alt metni, ücret satırları
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
 
    // Kullanım: "SMS ücreti operatörüne bağlıdır", info metni
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    ),
 
    // ── Label ────────────────────────────────────────────────
    // Kullanım: Buton metni ("Hemen Başla", "Kod Gönder"),
    //           nav bar item etiketi
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
 
    // Kullanım: Chip etiketi ("Dakikalık", "Saatlik"), badge metni
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp,
    ),
 
    // Kullanım: Icon altı nav label ("Harita", "Geçmiş"…),
    //           "MÜSAIT" badge, "Varsayılan" badge
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
```
 
---

## Özel Stiller (M3 Scale Dışı)

```kotlin
// ui/theme/Type.kt (devam)
 
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
 
// Aktif kiralama sayacı: "00:24:18"
val timerTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize   = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-1).sp,
)
 
// Fiyat büyük gösterim: "₺4,50" (araç detay)
val priceLargeTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.3).sp,
)
 
// Fiyat birim: "/dk", "/sa"
val priceUnitTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Normal,
    fontSize   = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
)
 
// Cüzdan bakiyesi: "₺340,00"
val walletBalanceTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = (-0.5).sp,
)
 
// OTP kutucuk rakamı
val otpDigitTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp,
)
```
 
---

## Kullanım Referansı

| Ekran | Eleman | Style |
|-------|--------|-------|
| Splash | "Rencar" | `headlineLarge` / logo |
| Giriş | "Tekrar hoş geldin" | `headlineLarge` |
| Giriş | "Telefon numarası" label | `labelSmall` |
| OTP | "Telefonunu doğrula" | `headlineLarge` |
| OTP | OTP digit | `otpDigitTextStyle` |
| OTP | "0:42" geri sayım | `labelMedium` |
| Harita | "Yakınında 12 araç" | `titleMedium` |
| Harita | "Kadıköy çevresinde · 3 dk" | `bodySmall` |
| Harita | Nav bar label | `labelSmall` |
| Araç Detay | "Renault Clio" | `titleMedium` |
| Araç Detay | "₺4,50" fiyat | `priceLargeTextStyle` |
| Araç Detay | "/dk" birim | `priceUnitTextStyle` |
| Araç Detay | "%72" yakıt değeri | `titleMedium` |
| Araç Detay | "~480 km" | `titleMedium` |
| Rezervasyon | "Rezervasyon Onayı" | `headlineMedium` |
| Aktif Kira | "00:24:18" sayaç | `timerTextStyle` |
| Aktif Kira | "₺108,00" anlık ücret | `titleLarge` |
| Ödeme Özeti | "₺110,50 Öde" buton | `labelLarge` |
| Cüzdan | "₺340,00" bakiye | `walletBalanceTextStyle` |
| Geçmiş | Tarih alt metin | `bodySmall` |
| Profil | Kullanıcı adı | `titleMedium` |
| Profil | Telefon numarası | `bodyMedium` |
 
---

## Kurallar

1. Ekranda bir tane büyük/display ağırlıklı metin olabilir — hiyerarşiyi koru.
2. Buton metinleri her zaman **SemiBold / Bold** — `labelLarge`.
3. Badge ve chip metinleri **Medium** ağırlık — `labelMedium` / `labelSmall`.
4. Placeholder ve helper metinler `bodySmall` + `onSurfaceVariant` rengi.
5. Sayısal veriler (süre, km, ücret) **Bold** ya da **ExtraBold** — görsel ağırlık hissettirmeli.