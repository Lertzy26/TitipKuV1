package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.MatchmakingScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.UMKMScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TitipViewModel
import com.example.ui.viewmodel.UserRole

class MainActivity : ComponentActivity() {
    private val viewModel: TitipViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppLayout(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(viewModel: TitipViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = "Logo TitipKu",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TitipKu",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Mitra",
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                val navItems = listOf(
                    NavItem(role = UserRole.UMKM, label = "UMKM Penitip", icon = Icons.Default.AccountBalance, tag = "nav_umkm_tab"),
                    NavItem(role = UserRole.TOKO, label = "Toko Penerima", icon = Icons.Default.Storefront, tag = "nav_shop_tab"),
                    NavItem(role = UserRole.ADMIN, label = "Admin Hub", icon = Icons.Default.AdminPanelSettings, tag = "nav_admin_tab")
                )

                navItems.forEach { item ->
                    val selected = currentRole == item.role
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.setRole(item.role) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Context Selection Bar & Navigation Sub-flow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Aktif Berperan Sebagai:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Matchmaking Shortcut Quick link
                Row(
                    modifier = Modifier
                        .clickable { viewModel.setRole(UserRole.UMKM) } // Matchmaking lives inside the MSME flow context or acts as mutual directory
                        .background(MaterialTheme.colorScheme.primary.copy(0.12f), MaterialTheme.shapes.extraSmall)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CompareArrows, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Digital Matchmaking", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                }
            }

            // Central Workspace Frame router
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentRole) {
                    UserRole.UMKM -> {
                        // UMKM profile workspace includes both internal inventory management AND matchmaking direct portals.
                        // We will display a choice or embed directories inside UMKM flow as subsections.
                        var innerPerspective by remember { mutableIntStateOf(0) } // 0: UMKM Workspace, 1: Matchmaking Directory
                        
                        Column(modifier = Modifier.fillMaxSize()) {
                            TabRow(
                                selectedTabIndex = innerPerspective,
                                containerColor = MaterialTheme.colorScheme.background,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Tab(selected = innerPerspective == 0, onClick = { innerPerspective = 0 }) {
                                    Text("Layanan Penitip Anda", modifier = Modifier.padding(10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Tab(selected = innerPerspective == 1, onClick = { innerPerspective = 1 }) {
                                    Text("Matchmaking Marketplace", modifier = Modifier.padding(10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            if (innerPerspective == 0) {
                                UMKMScreen(viewModel = viewModel, modifier = Modifier.weight(1f))
                            } else {
                                MatchmakingScreen(viewModel = viewModel, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    UserRole.TOKO -> {
                        ShopScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    }
                    UserRole.ADMIN -> {
                        AdminScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

data class NavItem(
    val role: UserRole,
    val label: String,
    val icon: ImageVector,
    val tag: String
)
