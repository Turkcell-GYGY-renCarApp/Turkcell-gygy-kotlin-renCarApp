package com.turkcell.rencarapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.rencarapp.ui.icons.RencarIcons
import com.turkcell.rencarapp.ui.theme.LocalRencarSpacing
import com.turkcell.rencarapp.ui.theme.RencarTheme
import com.turkcell.rencarapp.ui.components.DashboardTab
import com.turkcell.rencarapp.ui.components.RencarBottomNavigationBar
import androidx.hilt.navigation.compose.hiltViewModel
import com.turkcell.rencarapp.ui.viewmodel.AuthViewModel
import com.turkcell.rencarapp.ui.viewmodel.LicenseViewModel
import com.turkcell.rencarapp.ui.contract.LicenseIntent

@Composable
fun MainDashboardScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogoutClick: () -> Unit,
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(DashboardTab.Map) }
    val authViewModel: AuthViewModel = hiltViewModel()
    val licenseViewModel: LicenseViewModel = hiltViewModel()

    LaunchedEffect(currentTab) {
        if (currentTab == DashboardTab.Profile) {
            authViewModel.getProfile()
            licenseViewModel.onIntent(LicenseIntent.GetStatus)
        }
    }

    val fullName by authViewModel.userFullName.collectAsState()
    val phone by authViewModel.userPhone.collectAsState()
    val role by authViewModel.userRole.collectAsState()

    val licenseState by licenseViewModel.state.collectAsState()
    val licenseStatus = licenseState.statusResponse?.status ?: "NOT_SUBMITTED"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            RencarBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                DashboardTab.Map -> Box(modifier = Modifier.fillMaxSize())
                DashboardTab.History -> PlaceholderTabContent("Geçmiş Yolculuklarım", "Tamamlanmış veya iptal edilmiş sürüş geçmişinizi burada görebilirsiniz.")
                DashboardTab.Wallet -> PlaceholderTabContent("Cüzdanım", "Kayıtlı kartlarınızı, bakiye bilgilerinizi ve ödeme geçmişinizi yönetin.")
                DashboardTab.Profile -> ProfileScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onLogoutClick = {
                        authViewModel.logout(onLogoutClick)
                    },
                    userFullName = fullName,
                    userPhone = phone,
                    userRole = role,
                    licenseStatus = licenseStatus,
                    onLicenseClick = onLicenseClick
                )
            }
        }
    }
}

@Composable
fun MapTabContent() {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0D0D0D)
    val spacing = LocalRencarSpacing.current

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Vector Map Background
        MapCanvasBackground(isDark = isDark)

        // 2. Floating Price Tags (Vehicle Pins)
        // Tag 1 (Orange): ₺28, top-left
        PriceTagPin(
            price = "₺28",
            color = Color(0xFFF97316),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 90.dp, top = 180.dp)
        )

        // Tag 2 (Purple): ₺38, center-right
        PriceTagPin(
            price = "₺38",
            color = Color(0xFF8B5CF6),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 80.dp, top = 220.dp)
        )

        // Tag 3 (Yellow): ₺32, center-left
        PriceTagPin(
            price = "₺32",
            color = Color(0xFFEAB308),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 140.dp, top = 80.dp)
        )

        // Tag 4 (Teal): ₺26, bottom-left
        PriceTagPin(
            price = "₺26",
            color = Color(0xFF14B8A6),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 75.dp, bottom = 260.dp)
        )

        // 3. Top Floating Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.sm)
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = spacing.md, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = RencarIcons.LocationPin,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(spacing.sm))
            Text(
                text = "Nereden araç alacaksın?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { /* Filter Action */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RencarIcons.Filter,
                    contentDescription = "Filtrele",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // 4. Floating Navigation Arrow Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = spacing.md, bottom = 220.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
                .clickable { /* Center on current location */ },
            contentAlignment = Alignment.Center
        ) {
            // Drawn up-right blue navigation arrow
            Canvas(modifier = Modifier.size(18.dp)) {
                val path = Path().apply {
                    moveTo(0f, size.height)
                    lineTo(size.width, 0f)
                    lineTo(size.width * 0.3f, size.height * 0.1f)
                    moveTo(size.width, 0f)
                    lineTo(size.width * 0.9f, size.height * 0.7f)
                }
                drawPath(
                    path = path,
                    color = Color(0xFF0F62CD),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // 5. Bottom Info Sheet/Card (Above Bottom Navigation)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    clip = false
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = spacing.md, vertical = spacing.sm)
        ) {
            // Drag Handle
            Box(
                modifier = Modifier
                    .size(40.dp, 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            // Text Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Yakınında 12 araç",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Kadıköy çevresinde · 3 dk uzaklıkta",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Filter icon badge on the right
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RencarIcons.Filter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Category Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                CategoryChip("Tümü", isSelected = true, indicatorColor = null)
                CategoryChip("Ekonomik", isSelected = false, indicatorColor = Color(0xFFF97316))
                CategoryChip("Konfor", isSelected = false, indicatorColor = Color(0xFF8B5CF6))
                CategoryChip("SUV", isSelected = false, indicatorColor = Color(0xFFEAB308))
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // CTA Button: "En Yakın Aracı Bul"
            Button(
                onClick = { /* Find Nearest Car Action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = RencarIcons.LocationPin,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text(
                        text = "En Yakın Aracı Bul",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.xs))
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    indicatorColor: Color?,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable { /* Select */ }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (indicatorColor != null) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PriceTagPin(
    price: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tag Box
        Box(
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                .background(color = color, shape = RoundedCornerShape(8.dp))
                .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = RencarIcons.Car,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = price,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tiny pointing triangle under the tag box
        Canvas(modifier = Modifier.size(8.dp, 5.dp)) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(path = path, color = color)
        }
    }
}

@Composable
fun MapCanvasBackground(isDark: Boolean) {
    val blockColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF3F5F9)
    val roadColor = if (isDark) Color(0xFF1E293B) else Color.White
    val parkColor = if (isDark) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFE2EEDA)

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Clear background
        drawRect(color = blockColor)

        // Draw Park Area Polygon (Green area)
        drawRect(
            color = parkColor,
            topLeft = Offset(x = size.width * 0.45f, y = size.height * 0.25f),
            size = Size(width = size.width * 0.4f, height = size.height * 0.2f)
        )

        val roadWidth = 24.dp.toPx()

        // Draw horizontal roads
        drawLine(color = roadColor, start = Offset(0f, size.height * 0.25f), end = Offset(size.width, size.height * 0.25f), strokeWidth = roadWidth)
        drawLine(color = roadColor, start = Offset(0f, size.height * 0.55f), end = Offset(size.width, size.height * 0.55f), strokeWidth = roadWidth)

        // Draw vertical roads
        drawLine(color = roadColor, start = Offset(size.width * 0.25f, 0f), end = Offset(size.width * 0.25f, size.height), strokeWidth = roadWidth)
        drawLine(color = roadColor, start = Offset(size.width * 0.7f, 0f), end = Offset(size.width * 0.7f, size.height), strokeWidth = roadWidth)
    }
}

@Composable
fun PlaceholderTabContent(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun MainDashboardScreenLightPreview() {
    RencarTheme(darkTheme = false) {
        MainDashboardScreen(
            isDarkTheme = false,
            onThemeToggle = {},
            onLogoutClick = {},
            onLicenseClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun MainDashboardScreenDarkPreview() {
    RencarTheme(darkTheme = true) {
        MainDashboardScreen(
            isDarkTheme = true,
            onThemeToggle = {},
            onLogoutClick = {},
            onLicenseClick = {}
        )
    }
}
