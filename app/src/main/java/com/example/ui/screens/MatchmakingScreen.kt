package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BatikNavy
import com.example.ui.theme.SunsetCoral
import com.example.data.database.Shop
import com.example.data.database.UMKM
import com.example.ui.viewmodel.TitipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchmakingScreen(
    viewModel: TitipViewModel,
    modifier: Modifier = Modifier
) {
    val umkms by viewModel.allUMKMs.collectAsState()
    val shops by viewModel.allShops.collectAsState()
    val activeUMKM by viewModel.activeUMKM.collectAsState()

    var activeScreenTab by remember { mutableIntStateOf(0) } // 0: Rekomendasi Pintar, 1: Direktori Toko, 2: Direktori UMKM
    var filterCategory by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }
    
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
        // Quick Outlined Introductory Banner
        OutlinedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Matchmaking Hub TitipKu",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Temukan mitra bisnis konsinyasi terbaik berdasarkan kategori dagang dan lokasi terdekat.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Search Bar & Filter Chips Row
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari Berdasarkan Nama / Daerah...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Subtabs
        TabRow(
            selectedTabIndex = activeScreenTab,
            containerColor = Color.Transparent,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Tab(selected = activeScreenTab == 0, onClick = { activeScreenTab = 0 }) {
                Text("Rekomendasi Pintar", modifier = Modifier.padding(10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeScreenTab == 1, onClick = { activeScreenTab = 1 }) {
                Text("Direktori Toko", modifier = Modifier.padding(10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeScreenTab == 2, onClick = { activeScreenTab = 2 }) {
                Text("Direktori UMKM", modifier = Modifier.padding(10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Filtering Logic
        when (activeScreenTab) {
            0 -> {
                // Smart Matchmaking Recommendation algorithms
                if (activeUMKM == null) {
                    EmptyStateView(
                        message = "Pilih profil UMKM aktif Anda untuk melihat rekomendasi toko.",
                        tip = "Pilihlah salah satu Mitra di tab UMKM.",
                        icon = Icons.Default.DirectionsRun
                    )
                } else {
                    val catMatch = activeUMKM!!.category
                    val recommendedShops = shops.filter { shop ->
                        shop.categoriesAccepted.contains(catMatch, ignoreCase = true)
                    }

                    SectionHeader(
                        title = "Toko yang Cocok dengan Kategori ${activeUMKM!!.category}",
                        icon = Icons.Default.Eco
                    )

                    // Recommendation Mini-Banner (High Density AI Banner)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = "MATCHMAKING AI",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Ada 3 Toko baru sesuai kriteria produk ${activeUMKM!!.category} Anda!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Membuka Proposal Kemitraan Pintar!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp).testTag("action_proposal_ai")
                            ) {
                                Text("PROPOSAL", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    if (recommendedShops.isEmpty()) {
                        EmptyStateView(
                            message = "Belum ada Toko yang menerima kategori ${activeUMKM!!.category} saat ini.",
                            tip = "Kunjungi Direktori Toko untuk melihat daftar lengkap Toko Jaringan.",
                            icon = Icons.Default.ShoppingBag
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recommendedShops) { shop ->
                                MatchmakingInfoCard(
                                    shop = shop,
                                    matchScore = 95, // Simulated Match score
                                    isRecommended = true,
                                    onRate = { score ->
                                        Toast.makeText(context, "Makasih! Anda menilai ${shop.name} sebesar $score Bintang.", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                // Filtered shop directories
                val filteredShops = shops.filter {
                    it.name.contains(searchQuery, ignoreCase = true) || it.locationRegion.contains(searchQuery, ignoreCase = true)
                }

                if (filteredShops.isEmpty()) {
                    EmptyStateView(message = "Toko Jaringan yang Anda cari tidak ditemukan.", icon = Icons.Default.HourglassEmpty)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredShops) { shop ->
                            MatchmakingInfoCard(
                                shop = shop,
                                matchScore = 0,
                                isRecommended = false,
                                onRate = { score ->
                                    Toast.makeText(context, "Berhasil menilai ${shop.name} $score bintang!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
            2 -> {
                // Filtered UMKM directories
                val filteredUMKMs = umkms.filter {
                    it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
                }

                if (filteredUMKMs.isEmpty()) {
                    EmptyStateView(message = "Mitra UMKM yang dicari tidak ditemukan.", icon = Icons.Default.FolderOff)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredUMKMs) { umkm ->
                            UMKMDirectoryCard(
                                umkm = umkm,
                                onRate = { score ->
                                    Toast.makeText(context, "Berhasil menilai ${umkm.name} $score bintang!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchmakingInfoCard(
    shop: Shop,
    matchScore: Int,
    isRecommended: Boolean,
    onRate: (Int) -> Unit
) {
    var userRatingInput by remember { mutableIntStateOf(0) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = shop.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (shop.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Shop",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = shop.address,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                if (isRecommended && matchScore > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Kecocokan $matchScore%",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Kategori Yang Diterima:", fontSize = 10.sp, color = Color.Gray)
                    Text(shop.categoriesAccepted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                RatingBarView(rating = shop.rating)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Berikan Rating Anda (UMKM menilai Toko):", fontSize = 11.sp, color = Color.Gray)
                Row {
                    (1..5).forEach { star ->
                        val active = userRatingInput >= star
                        Icon(
                            imageVector = if (active) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (active) Color(0xFFFFB300) else Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    userRatingInput = star
                                    onRate(star)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UMKMDirectoryCard(
    umkm: UMKM,
    onRate: (Int) -> Unit
) {
    var userRatingInput by remember { mutableIntStateOf(0) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = umkm.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (umkm.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified UMKM",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = umkm.address,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = umkm.category,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Sertifikasi Produk:", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        text = if (umkm.certs.isNotEmpty()) umkm.certs else "Proses Srtf",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                RatingBarView(rating = umkm.rating)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Beri Rating Anda (Toko menilai UMKM):", fontSize = 11.sp, color = Color.Gray)
                Row {
                    (1..5).forEach { star ->
                        val active = userRatingInput >= star
                        Icon(
                            imageVector = if (active) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (active) Color(0xFFFFB300) else Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    userRatingInput = star
                                    onRate(star)
                                }
                        )
                    }
                }
            }
        }
    }
}
