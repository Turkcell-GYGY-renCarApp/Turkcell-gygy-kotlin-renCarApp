package com.turkcell.rencarapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import org.maplibre.android.geometry.LatLng
import com.turkcell.rencarapp.ui.components.RencarMap
import com.turkcell.rencarapp.ui.components.rememberRencarMapController
import com.turkcell.rencarapp.ui.components.RencarMapController
import com.turkcell.rencarapp.ui.icons.RencarIcons
import com.turkcell.rencarapp.ui.theme.LocalRencarSpacing
import com.turkcell.rencarapp.ui.theme.RencarTheme
import com.turkcell.rencarapp.ui.components.DashboardTab
import com.turkcell.rencarapp.ui.components.RencarBottomNavigationBar
import androidx.hilt.navigation.compose.hiltViewModel
import com.turkcell.rencarapp.ui.viewmodel.AuthViewModel
import com.turkcell.rencarapp.ui.viewmodel.LicenseViewModel
import com.turkcell.rencarapp.ui.viewmodel.VehicleViewModel
import com.turkcell.rencarapp.data.vehicle.VehicleResponseDto
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                DashboardTab.Map -> MapTabContent()
                DashboardTab.History -> PaymentSummaryScreen(
                    rentalId = "clx0rent1234567890",
                    onPaymentSuccess = { currentTab = DashboardTab.Map }
                )
                DashboardTab.Wallet -> WalletScreen()
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
    val context = LocalContext.current
    val spacing = LocalRencarSpacing.current
    val density = LocalDensity.current

    val vehicleViewModel: VehicleViewModel = hiltViewModel()
    val vehiclesState by vehicleViewModel.vehicles.collectAsState()
    val isLoading by vehicleViewModel.isLoading.collectAsState()

    val mapController = rememberRencarMapController()
    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    var cameraTrigger by remember { mutableStateOf(0) }

    var selectedSegment by remember { mutableStateOf<String?>(null) } // null = Tümü, "ECONOMY", "COMFORT", "SUV"

    // Seçilen segmente göre araçları API'den çekiyoruz
    LaunchedEffect(selectedSegment) {
        vehicleViewModel.fetchVehicles(segment = selectedSegment)
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
        permissionDenied = !granted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) return@DisposableEffect onDispose {}

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    myLocation = LatLng(loc.latitude, loc.longitude)
                }
            }
        }

        startLocationUpdates(fusedClient, callback)

        onDispose {
            fusedClient.removeLocationUpdates(callback)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RencarMap(
            myLocation = myLocation,
            modifier = Modifier.fillMaxSize(),
            controller = mapController,
            initialZoom = 14.5,
            onCameraMove = {
                cameraTrigger++
            }
        )

        val projection = mapController.map?.projection
        if (projection != null) {
            val trigger = cameraTrigger 
            vehiclesState.forEach { vehicle ->
                // Araç durumuna göre renk haritalaması: Müsait olanlar segment renginde, dolu olanlar gri
                val color = if (vehicle.status != "AVAILABLE") {
                    Color(0xFF94A3B8)
                } else {
                    when (vehicle.segment) {
                        "ECONOMY" -> Color(0xFFF97316)
                        "COMFORT" -> Color(0xFF8B5CF6)
                        "SUV" -> Color(0xFFEAB308)
                        else -> Color(0xFF14B8A6)
                    }
                }

                val screenPos = projection.toScreenLocation(LatLng(vehicle.latitude, vehicle.longitude))
                val xDp = with(density) { screenPos.x.toDp() }
                val yDp = with(density) { screenPos.y.toDp() }

                PriceTagPin(
                    price = "₺${vehicle.pricePerMinute.toInt()}",
                    color = color,
                    modifier = Modifier
                        .offset(x = xDp - 35.dp, y = yDp - 45.dp)
                )
            }
        }

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
                    .clickable { },
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = spacing.md, bottom = spacing.md),
                contentAlignment = Alignment.CenterEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        if (hasLocationPermission) {
                            fetchCurrentLocation(fusedClient) { target ->
                                myLocation = target
                                mapController.animateTo(target, zoom = 14.5)
                            }
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = RencarIcons.MyLocation,
                        contentDescription = "Konumuma git"
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                Box(
                    modifier = Modifier
                        .size(40.dp, 4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        val count = vehiclesState.count { it.status == "AVAILABLE" }
                        Text(
                            text = "Yakınında $count araç",
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

                LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                item {
                    CategoryChip(
                        text = "Tümü",
                        isSelected = selectedSegment == null,
                        indicatorColor = null,
                        onClick = { selectedSegment = null }
                    )
                }
                item {
                    CategoryChip(
                        text = "Ekonomik",
                        isSelected = selectedSegment == "ECONOMY",
                        indicatorColor = Color(0xFFF97316),
                        onClick = { selectedSegment = "ECONOMY" }
                    )
                }
                item {
                    CategoryChip(
                        text = "Konfor",
                        isSelected = selectedSegment == "COMFORT",
                        indicatorColor = Color(0xFF8B5CF6),
                        onClick = { selectedSegment = "COMFORT" }
                    )
                }
                item {
                    CategoryChip(
                        text = "SUV",
                        isSelected = selectedSegment == "SUV",
                        indicatorColor = Color(0xFFEAB308),
                        onClick = { selectedSegment = "SUV" }
                    )
                }
            }

                Spacer(modifier = Modifier.height(spacing.md))

                Button(
                    onClick = { },
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
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    indicatorColor: Color?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable { onClick() }
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
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false
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

@SuppressLint("MissingPermission")
private fun fetchCurrentLocation(
    fusedClient: FusedLocationProviderClient,
    onLocation: (LatLng) -> Unit
) {
    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
        .addOnSuccessListener { location ->
            if (location != null) onLocation(LatLng(location.latitude, location.longitude))
        }
}

@SuppressLint("MissingPermission")
private fun startLocationUpdates(
    fusedClient: FusedLocationProviderClient,
    callback: LocationCallback
) {
    val request = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5_000L
    ).setMinUpdateIntervalMillis(2_000L).build()

    fusedClient.lastLocation.addOnSuccessListener { location ->
        Log.d("LOKASYON", "LOKASYON DEĞİŞTİ")
        if (location != null) {
            callback.onLocationResult(LocationResult.create(listOf(location)))
        }
    }

    fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
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
