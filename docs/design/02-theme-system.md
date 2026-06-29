# 02 · Theme System — Rencar

`MaterialTheme` wrapper'ı, shape, spacing, elevation ve global UI kuralları.
 
---

## Theme Entry Point

```kotlin
// ui/theme/RencarTheme.kt
 
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
 
@Composable
fun RencarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content:   @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) RencarDarkColorScheme else RencarLightColorScheme
 
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color          = Color.Transparent,
            darkIcons      = !darkTheme,
            isNavigationBarContrastEnforced = false,
        )
    }
 
    CompositionLocalProvider(
        LocalRencarColors  to if (darkTheme) rencarDarkExtended else rencarLightExtended,
        LocalRencarSpacing to RencarSpacing(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = RencarTypography,
            shapes      = RencarShapes,
            content     = content,
        )
    }
}
```
 
---

## Shape System

```kotlin
// ui/theme/Shape.kt
 
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
 
val RencarShapes = Shapes(
    // Chip, badge, küçük buton
    extraSmall = RoundedCornerShape(6.dp),
 
    // Input field, küçük kart
    small      = RoundedCornerShape(10.dp),
 
    // Standart kart (araç detay, kiralama geçmiş item)
    medium     = RoundedCornerShape(16.dp),
 
    // Bottom sheet, büyük modal kart
    large      = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
 
    // Pill buton ("Hemen Başla", "Kod Gönder", "Kilidi Aç")
    extraLarge = RoundedCornerShape(100.dp),
)
```

### Shape Kullanım Haritası

| Eleman | Shape Token | dp |
|--------|------------|-----|
| Ana CTA buton | `extraLarge` | pill |
| İkincil outline buton | `extraLarge` | pill |
| Input field | `small` | 10 |
| Ülke kodu kutusu (TR +90) | `small` | 10 |
| Araç detay bilgi kartı (%72, km) | `medium` | 16 |
| Kiralama geçmişi list item | `medium` | 16 |
| Cüzdan kart (bakiye) | `medium` | 16 |
| Kategori chip ("Ekonomik") | `extraSmall` | 6 |
| "MÜSAİT" badge | `extraSmall` | 6 |
| "Onaylı" badge | `extraSmall` | 6 |
| OTP digit box | `small` | 10 |
| Bottom sheet | `large` | 24 top |
| Avatar (profil) | `extraLarge` | circle |
| Map marker bubble | özel — bkz. MapMarker | — |
 
---

## Spacing System

```kotlin
// ui/theme/Spacing.kt
 
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
 
@Immutable
data class RencarSpacing(
    val xxxs: Dp =  2.dp,
    val xxs:  Dp =  4.dp,
    val xs:   Dp =  8.dp,
    val sm:   Dp = 12.dp,
    val md:   Dp = 16.dp,   // Standart yatay padding
    val lg:   Dp = 20.dp,
    val xl:   Dp = 24.dp,
    val xxl:  Dp = 32.dp,
    val xxxl: Dp = 48.dp,
)
 
val LocalRencarSpacing = staticCompositionLocalOf { RencarSpacing() }
 
// Erişim:
// val sp = LocalRencarSpacing.current
// sp.md  →  16.dp
```

### Spacing Kuralları

| Kural | Değer |
|-------|-------|
| Ekran kenar padding (horizontal) | `md` = 16.dp |
| Section arası boşluk | `xl` = 24.dp |
| Kart iç padding | `md` = 16.dp |
| Liste item dikey padding | `sm` = 12.dp |
| Icon-metin arası | `xs` = 8.dp |
| Badge iç padding (H) | `xs` = 8.dp |
| Badge iç padding (V) | `xxxs` = 2.dp |
| Buton iç padding (H) | `xl` = 24.dp |
| Buton iç padding (V) | `md` = 16.dp |
| Bottom nav yükseklik | 64.dp (sabit) |
 
---

## Elevation & Shadow

Material3'te elevation `Tonal` (renk katmanı) + `Shadow` olarak çalışır.
Rencar tasarımı **düz kartlar** tercih eder; gölge minimalist:

```kotlin
// Harita üstü bottom sheet
val BottomSheetElevation = 4.dp
 
// Araç detay bottom sheet (harita görünür arkada)
val DetailSheetElevation = 8.dp
 
// Floating buton (konuma git)
val FabElevation = 6.dp
 
// Kart (standart)
val CardElevation = 0.dp   // Gölge yok, border/bg renk ile ayrışır
```

> Dark mode'da elevation tonal overlay ile gösterilir (M3 default davranışı).
> `surfaceTint = primary` olduğu için dark kartlar hafif mavi ton alır — tasarımla tutarlı.
 
---

## Component Defaults

### Buton

```kotlin
// ui/component/RencarButton.kt (özet)
 
@Composable
fun RencarPrimaryButton(
    text:    String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    icon:    ImageVector? = null,
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        shape    = MaterialTheme.shapes.extraLarge,   // pill
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = MaterialTheme.colorScheme.primary,
            contentColor           = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(0.12f),
            disabledContentColor   = MaterialTheme.colorScheme.onSurface.copy(0.38f),
        ),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
 
@Composable
fun RencarOutlineButton(
    text:    String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick  = onClick,
        shape    = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.height(56.dp),
        border   = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
        ),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
```

### Input Field

```kotlin
// ui/component/RencarTextField.kt (özet)
 
@Composable
fun RencarTextField(
    value:       String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = {
            Text(placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        shape  = MaterialTheme.shapes.small,          // 10.dp rounded
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    )
}
```

### Kategori Chip

```kotlin
@Composable
fun VehicleCategoryChip(
    label:    String,
    color:    Color,
    selected: Boolean,
    onClick:  () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(label, style = MaterialTheme.typography.labelMedium) },
        leadingIcon = {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        },
        shape  = MaterialTheme.shapes.extraSmall,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor   = MaterialTheme.colorScheme.primary,
            selectedLabelColor       = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}
```
 
---

## Status Bar / System UI

| Durum | Renk | Icon tonu |
|-------|------|-----------|
| Light harita ekranı | Şeffaf | Koyu ikonlar |
| Dark harita ekranı | Şeffaf | Açık ikonlar |
| Light diğer ekranlar | `background` (#F2F4F7) | Koyu ikonlar |
| Dark diğer ekranlar | `background` (#0D0D0D) | Açık ikonlar |
 
---

## Dark / Light Toggle (Splash)

Splash ekranında sağ üstte bulunan dark/light toggle:

```kotlin
@Composable
fun ThemeToggleIcon(
    isDark:   Boolean,
    onToggle: () -> Unit,
) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
            contentDescription = if (isDark) "Açık tema" else "Koyu tema",
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
}
```
 
---

## Bottom Navigation Bar

```kotlin
// 4 tab: Harita · Geçmiş · Cüzdan · Profil
 
NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 0.dp,                // Flat, border ile ayrışır
) {
    tabs.forEach { tab ->
        NavigationBarItem(
            selected = currentRoute == tab.route,
            onClick  = { /* navigate */ },
            icon     = { Icon(tab.icon, contentDescription = tab.label) },
            label    = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = MaterialTheme.colorScheme.primary,
                selectedTextColor   = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor      = MaterialTheme.colorScheme.primaryContainer,
            ),
        )
    }
}
```
 
---

## Global Sabitler

```kotlin
object RencarDimens {
    // Buton
    val ButtonHeight = 56.dp
    val ButtonHeightSmall = 44.dp
 
    // OTP
    val OtpBoxSize = 52.dp
    val OtpBoxSpacing = 8.dp
 
    // Kart
    val CardRadius = 16.dp
    val CardImageHeight = 180.dp
 
    // Bottom sheet handle
    val SheetHandleWidth = 40.dp
    val SheetHandleHeight = 4.dp
 
    // Nav bar
    val NavBarHeight = 64.dp
 
    // Avatar
    val AvatarSizeLarge = 56.dp
    val AvatarSizeSmall = 40.dp
 
    // Map marker bubble
    val MapMarkerHeight = 32.dp
 
    // Vehicle category dot
    val CategoryDotSize = 8.dp
}
```