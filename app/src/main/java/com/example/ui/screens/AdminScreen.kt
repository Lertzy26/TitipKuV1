package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.database.Dispute
import com.example.data.database.Shop
import com.example.data.database.UMKM
import com.example.ui.viewmodel.PlatformAnalytics
import com.example.ui.viewmodel.TitipViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: TitipViewModel,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.platformAnalytics.collectAsState()
    val umkms by viewModel.allUMKMs.collectAsState()
    val shops by viewModel.allShops.collectAsState()
    val disputes by viewModel.allDisputes.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Analytics, 1: Verification, 2: Dispute Resolution
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
        // Quick Stat Deck
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "Total GMV",
                value = "Rp ${String.format(Locale.US, "%,.0f", analytics.totalGmv)}",
                icon = Icons.Default.MonetizationOn,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1.2f)
            )
            StatCard(
                label = "Platform Fee",
                value = "Rp ${String.format(Locale.US, "%,.0f", analytics.platformCommission)}",
                icon = Icons.Default.Savings,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.Transparent,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                Text("Analisis", modifier = Modifier.padding(10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                Text("Verifikasi", modifier = Modifier.padding(10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                Text("Keluhan", modifier = Modifier.padding(10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        when (activeTab) {
            0 -> AnalyticsTab(analytics = analytics)
            1 -> VerificationTab(
                umkms = umkms,
                shops = shops,
                onVerify = { id, isUMKM ->
                    viewModel.verifyPartner(id, isUMKM)
                    Toast.makeText(context, "Profil Berhasil Diverifikasi!", Toast.LENGTH_SHORT).show()
                }
            )
            2 -> DisputesTab(
                disputes = disputes,
                onResolve = { id, res ->
                    viewModel.resolveDispute(id, res)
                    Toast.makeText(context, "Keluhan berhasil diselesaikan!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun AnalyticsTab(analytics: PlatformAnalytics) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Pertumbuhan Platform", icon = Icons.Default.QueryStats)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(label = "Mitra UMKM", value = "${analytics.totalUMKMs} Usaha", icon = Icons.Default.SupervisedUserCircle, modifier = Modifier.weight(1f))
            StatCard(label = "Toko Jaringan", value = "${analytics.totalShops} Gerai", icon = Icons.Default.Store, modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(label = "Kerjasama Aktif", value = "${analytics.activeDeals} Kontrak", icon = Icons.Default.Handshake, modifier = Modifier.weight(1f))
            StatCard(label = "Dispute Aktif", value = "${analytics.pendingDisputes} Kasus", icon = Icons.Default.Report, tint = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
        }

        // Beautiful Graphic Simulation
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pertumbuhan Bulanan GTV & Keaktifan Pengguna", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(10.dp))
                
                // Simple Canvas simulated Bar Graph of GMV Growth
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val heights = listOf(0.35f, 0.50f, 0.70f, 0.95f)
                    val months = listOf("Mar", "Apr", "Mei", "Jun")
                    heights.forEachIndexed { idx, h ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .fillMaxHeight(h)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(months[idx], fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VerificationTab(
    umkms: List<UMKM>,
    shops: List<Shop>,
    onVerify: (Int, Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Verifikasi KTP / NIB Mitra Baru", icon = Icons.Default.VerifiedUser)

        val pendingUMKMs = umkms.filter { !it.isVerified }
        val pendingShops = shops.filter { !it.isVerified }

        if (pendingUMKMs.isEmpty() && pendingShops.isEmpty()) {
            EmptyStateView(
                message = "Semua akun terdaftar sudah diverifikasi sistem.",
                tip = "Tidak ada antrian persetujuan dokumen baru.",
                icon = Icons.Default.TaskAlt
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pendingUMKMs) { u ->
                    OutlinedCard(
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
                                    Text(u.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Peran: Mitra UMKM (Penitip)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Button(
                                    onClick = { onVerify(u.id, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Text("Verifikasi", fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Identitas / NIB: ${u.ktpNib}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                items(pendingShops) { s ->
                    OutlinedCard(
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
                                    Text(s.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Peran: Toko Mitra (Penerima)", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                                Button(
                                    onClick = { onVerify(s.id, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Text("Verifikasi", fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Pemilik: ${s.ownerName} | Lokasi: ${s.locationRegion}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisputesTab(
    disputes: List<Dispute>,
    onResolve: (Int, String) -> Unit
) {
    var resolvingDisputeId by remember { mutableStateOf<Int?>(null) }
    var resolutionNotes by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Manajemen Keluhan (Dispute Desk)", icon = Icons.Default.Gavel)

        if (disputes.isEmpty()) {
            EmptyStateView(
                message = "Alhamdulillah, belum ada keluhan/dispute di platform.",
                icon = Icons.Default.Favorite
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(disputes) { disp ->
                    OutlinedCard(
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
                                    Text("Pelapor: ${disp.complainantName} (${disp.complainantRole})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Terlapor: ${disp.opponentName}", fontSize = 12.sp, color = Color.Gray)
                                }
                                StatusBadge(status = disp.status)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Keluhan: \"${disp.description}\"",
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(0.02f), RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            )

                            if (disp.status == "Submitted") {
                                Spacer(modifier = Modifier.height(8.dp))
                                if (resolvingDisputeId == disp.id) {
                                    CustomTextField(
                                        value = resolutionNotes,
                                        onValueChange = { resolutionNotes = it },
                                        label = "Keputusan Resolusi",
                                        testTagName = "resol_field_${disp.id}"
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                if (resolutionNotes.isNotEmpty()) {
                                                    onResolve(disp.id, resolutionNotes)
                                                    resolvingDisputeId = null
                                                    resolutionNotes = ""
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                            modifier = Modifier.testTag("resol_submit_${disp.id}")
                                        ) {
                                            Text("Selesaikan Masalah", fontSize = 11.sp)
                                        }
                                        TextButton(onClick = { resolvingDisputeId = null }) {
                                            Text("Batal")
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { resolvingDisputeId = disp.id },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Mediasi Kasus", fontSize = 11.sp)
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Resolusi Admin: \"${disp.resolution}\"",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
