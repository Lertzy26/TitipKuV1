package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Product
import com.example.data.database.Proposal
import com.example.data.database.Shop
import com.example.data.database.UMKM
import com.example.data.database.PaymentInvoice
import java.util.Locale
import com.example.ui.viewmodel.TitipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UMKMScreen(
    viewModel: TitipViewModel,
    modifier: Modifier = Modifier
) {
    val umkms by viewModel.allUMKMs.collectAsState()
    val activeUMKM by viewModel.activeUMKM.collectAsState()
    val activeProducts by viewModel.activeUmkmProducts.collectAsState()
    val activeInvoices by viewModel.activeUmkmInvoices.collectAsState()
    val shops by viewModel.allShops.collectAsState()
    val proposals by viewModel.allProposals.collectAsState()

    var showAddUMKMDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Catalog, 1: Proposals, 2: Monitoring, 3: Finance

    Column(modifier = modifier.fillMaxSize()) {
        // UMKM Profile Selection & Header
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Profil Mitra UMKM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = activeUMKM?.name ?: "Pilih/Daftar UMKM",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Button(
                        onClick = { showAddUMKMDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Daftar Baru", fontSize = 12.sp)
                    }
                }

                if (umkms.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ganti Akun UMKM Aktif:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        umkms.forEach { umkm ->
                            val isSelected = activeUMKM?.id == umkm.id
                            Surface(
                                modifier = Modifier
                                    .clickable { viewModel.selectActiveUmkm(umkm.id) }
                                    .testTag("umkm_switch_${umkm.id}"),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text(
                                    text = umkm.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                activeUMKM?.let { umkm ->
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Icon(Icons.Default.FilePresent, "KTP", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify: ${umkm.ktpNib}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.VerifiedUser, "Cert", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sertifikasi: ${if (umkm.certs.isNotEmpty()) umkm.certs else "-"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                }
            }
        }

        if (activeUMKM == null) {
            EmptyStateView(
                message = "Anda belum memilih atau mendaftarkan partner UMKM.",
                tip = "Silakan klik 'Daftar Baru' di atas untuk memulai kemitraan konsinyasi.",
                icon = Icons.Default.Storefront
            )
            return
        }

        // Module Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Category, null, modifier = Modifier.size(18.dp))
                    Text("Katalog", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Assignment, null, modifier = Modifier.size(18.dp))
                    Text("Proposal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.TrendingUp, null, modifier = Modifier.size(18.dp))
                    Text("Monitoring", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Payments, null, modifier = Modifier.size(18.dp))
                    Text("Keuangan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (selectedTab) {
            0 -> CatalogTab(
                products = activeProducts,
                onAddProduct = { showAddProductDialog = true },
                onDeleteProduct = { viewModel.removeProduct(it) }
            )
            1 -> ProposalTab(
                proposals = proposals.filter { it.umkmId == activeUMKM!!.id },
                shops = shops,
                onSubmitProposal = { shopId, shopName, commission, notes, dur ->
                    viewModel.submitProposalToShop(shopId, shopName, commission, notes, dur)
                },
                onSignAgreement = { viewModel.signAgreement(it) }
            )
            2 -> MonitoringTab(
                products = activeProducts,
                proposals = proposals.filter { it.umkmId == activeUMKM!!.id },
                allStocks = viewModel.allStocks.collectAsState().value.filter { it.umkmId == activeUMKM!!.id }
            )
            3 -> FinanceTab(
                invoices = activeInvoices,
                onPayInvoice = { id, method -> viewModel.payInvoice(id, method) },
                onSubmitDispute = { opp, desc -> viewModel.submitDispute(opp, desc) }
            )
        }
    }

    // Modal - Register UMKM
    if (showAddUMKMDialog) {
        var name by remember { mutableStateOf("") }
        var ktpNib by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Makanan Ringan") }
        var certs by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddUMKMDialog = false },
            title = { Text("Daftar Mitra UMKM Baru") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CustomTextField(value = name, onValueChange = { name = it }, label = "Nama Usaha / UMKM", testTagName = "add_umkm_name")
                    CustomTextField(value = ktpNib, onValueChange = { ktpNib = it }, label = "No KTP / NIB", testTagName = "add_umkm_ktp")
                    CustomTextField(value = address, onValueChange = { address = it }, label = "Alamat Lengkap")
                    
                    // Category Picker
                    Text("Kategori Produk Utama:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    val cats = listOf("Makanan Ringan", "Minuman", "Fashion", "Kerajinan")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        cats.forEach { cat ->
                            val isSel = category == cat
                            SuggestionChip(
                                onClick = { category = cat },
                                label = { Text(cat) },
                                border = if (isSel) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }

                    CustomTextField(value = certs, onValueChange = { certs = it }, label = "Sertifikasi (PIRT, Halal, NIB)", placeholder = "Contoh: Halal No. 1293, PIRT")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotEmpty() && ktpNib.isNotEmpty()) {
                            viewModel.registerNewUMKM(name, ktpNib, address, category, certs)
                            showAddUMKMDialog = false
                        }
                    },
                    modifier = Modifier.testTag("submit_umkm")
                ) {
                    Text("Daftar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUMKMDialog = false }) { Text("Batal") }
            }
        )
    }

    // Modal - Add Product
    if (showAddProductDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var commRate by remember { mutableStateOf("10") }
        var category by remember { mutableStateOf(activeUMKM?.category ?: "Makanan Ringan") }
        var certs by remember { mutableStateOf("") }
        var barcode by remember { mutableStateOf("") }
        var stock by remember { mutableStateOf("50") }

        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Tambah Produk Konsinyasi") },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        CustomTextField(value = name, onValueChange = { name = it }, label = "Nama Produk", testTagName = "add_product_name")
                    }
                    item {
                        CustomTextField(value = desc, onValueChange = { desc = it }, label = "Deskripsi Pendek")
                    }
                    item {
                        CustomTextField(value = price, onValueChange = { price = it }, label = "Harga Jual (Rp)", placeholder = "e.g., 20000")
                    }
                    item {
                        CustomTextField(value = commRate, onValueChange = { commRate = it }, label = "Simulasi Komisi % per Produk (Toko)", placeholder = "e.g., 10 / 15")
                    }
                    item {
                        CustomTextField(value = stock, onValueChange = { stock = it }, label = "Stok Awal di UMKM", placeholder = "e.g. 50")
                    }
                    item {
                        CustomTextField(value = certs, onValueChange = { certs = it }, label = "Sertifikasi Produk", placeholder = "e.g., Halal, PIRT")
                    }
                    item {
                        CustomTextField(value = barcode, onValueChange = { barcode = it }, label = "Kode Barcode / SKU", placeholder = "e.g. SKU901-TEMPE")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pDouble = price.toDoubleOrNull() ?: 0.0
                        val commDouble = commRate.toDoubleOrNull() ?: 10.0
                        val stInt = stock.toIntOrNull() ?: 20
                        if (name.isNotEmpty() && pDouble > 0) {
                            viewModel.addNewProduct(name, desc, pDouble, commDouble, category, certs, barcode, stInt)
                            showAddProductDialog = false
                        }
                    },
                    modifier = Modifier.testTag("submit_product")
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun CatalogTab(
    products: List<Product>,
    onAddProduct: () -> Unit,
    onDeleteProduct: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(12.dp)) {
        SectionHeader(title = "Katalog Produk Titip", icon = Icons.Default.Category) {
            Button(onClick = onAddProduct, modifier = Modifier.testTag("btn_add_product_dialog")) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah Produk", fontSize = 12.sp)
            }
        }

        if (products.isEmpty()) {
            EmptyStateView(
                message = "Belum ada produk terdaftar.",
                tip = "Mulai tambahkan produk yang siap Anda tawarkan untuk konsinyasi.",
                icon = Icons.Default.ShoppingBag
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products) { prod ->
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
                                Text(
                                    text = prod.category,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { onDeleteProduct(prod.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prod.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rp ${String.format(Locale.US, "%,.0f", prod.price)}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Komisi Jual", fontSize = 9.sp, color = Color.Gray)
                                    Text("${prod.commissionRate}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Stok Rumah", fontSize = 9.sp, color = Color.Gray)
                                    Text("${prod.totalStock} unit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (prod.certs.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Verified, "Cert", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(prod.certs, fontSize = 8.sp, color = MaterialTheme.colorScheme.onTertiaryContainer)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProposalTab(
    proposals: List<Proposal>,
    shops: List<Shop>,
    onSubmitProposal: (Int, String, Double, String, Int) -> Unit,
    onSignAgreement: (Int) -> Unit
) {
    var showSendProposalDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(12.dp)) {
        SectionHeader(title = "Proposal Titip Jual Digital", icon = Icons.Default.Assignment) {
            Button(onClick = { showSendProposalDialog = true }) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kirim Baru", fontSize = 12.sp)
            }
        }

        if (proposals.isEmpty()) {
            EmptyStateView(
                message = "Anda belum mengirim proposal kerjasama.",
                tip = "Ketuk 'Kirim Baru' untuk menawarkan produk Anda ke toko terdekat.",
                icon = Icons.Default.Description
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(proposals) { prop ->
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
                                Text(
                                    text = prop.shopName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                StatusBadge(status = prop.status)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Komisi Diajukan: ${prop.proposedCommission}%", fontSize = 12.sp)
                                Text("Durasi: ${prop.durationMonths} Bulan", fontSize = 12.sp)
                            }
                            if (prop.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Catatan: \"${prop.notes}\"",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.background(MaterialTheme.colorScheme.onSurface.copy(0.04f), RoundedCornerShape(4.dp)).padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (prop.status == "Accepted" || prop.status == "CounteredByShop") {
                                if (!prop.eAgreementSigned) {
                                    Button(
                                        onClick = { onSignAgreement(prop.id) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Tanda Tangan Perjanjian Digital", fontSize = 12.sp)
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(0.2f))
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.CloudDone, "Done", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("E-Agreement Terkontrak Digital", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSendProposalDialog) {
        var selectedShop by remember { mutableStateOf<Shop?>(null) }
        var proposedComm by remember { mutableStateOf("15") }
        var notes by remember { mutableStateOf("") }
        var duration by remember { mutableStateOf("6") }
        var shopDropdownOpen by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showSendProposalDialog = false },
            title = { Text("Ajukan Proposal Konsinyasi Baru") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Custom Shop Selector Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedShop?.name ?: "- Pilih Toko Mitra -",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Toko Jaringan Sasaran") },
                            trailingIcon = {
                                IconButton(onClick = { shopDropdownOpen = !shopDropdownOpen }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().clickable { shopDropdownOpen = true }
                        )
                        DropdownMenu(
                            expanded = shopDropdownOpen,
                            onDismissRequest = { shopDropdownOpen = false }
                        ) {
                            shops.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("${s.name} (${s.locationRegion})") },
                                    onClick = {
                                        selectedShop = s
                                        shopDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    CustomTextField(value = proposedComm, onValueChange = { proposedComm = it }, label = "Tawaran Komisi Toko (%)", placeholder = "e.g., 10 / 12 / 15")
                    CustomTextField(value = duration, onValueChange = { duration = it }, label = "Rencana Kerjasama (Bulan)", placeholder = "e.g. 6 / 12")
                    CustomTextField(value = notes, onValueChange = { notes = it }, label = "Surat Pengantar / Catatan Produk")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sh = selectedShop
                        val rate = proposedComm.toDoubleOrNull() ?: 15.0
                        val dur = duration.toIntOrNull() ?: 6
                        if (sh != null) {
                            onSubmitProposal(sh.id, sh.name, rate, notes, dur)
                            showSendProposalDialog = false
                        }
                    }
                ) {
                    Text("Kirim Proposal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSendProposalDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun MonitoringTab(
    products: List<Product>,
    proposals: List<Proposal>,
    allStocks: List<com.example.data.database.ConsignmentStock>
) {
    Column(modifier = Modifier.padding(12.dp)) {
        SectionHeader(title = "Dashboard Stok Konsinyasi", icon = Icons.Default.TrendingUp)

        // Row of quick statistics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val totalConsignedItems = allStocks.sumOf { it.currentStock }
            val totalSold = allStocks.sumOf { it.soldCount }
            
            StatCard(
                label = "Stok di Toko",
                value = "$totalConsignedItems unit",
                icon = Icons.Default.Inventory,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Total Terjual",
                value = "$totalSold unit",
                icon = Icons.Default.LocalMall,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Rincian Stok & Alert Pengisian Ulang:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        if (allStocks.isEmpty()) {
            EmptyStateView(
                message = "Belum ada produk yang aktif diserahkan ke Toko.",
                tip = "Kirim stok lewat modul toko jika proposal sudah disepakati (Accepted).",
                icon = Icons.Default.NotificationImportant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(allStocks) { stock ->
                    val isLow = stock.currentStock <= 10
                    OutlinedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isLow) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (isLow) MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stock.productName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (isLow) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Warning, "Low Stock", tint = Color(0xFFF57C00), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Segera Kirim Stok!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                    }
                                } else {
                                    Text("Aman", fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tersisa di Mitra: ${stock.currentStock} unit", fontSize = 12.sp)
                                Text("Terjual: ${stock.soldCount} unit", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinanceTab(
    invoices: List<PaymentInvoice>,
    onPayInvoice: (Int, String) -> Unit,
    onSubmitDispute: (String, String) -> Unit
) {
    var showDisputeDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(12.dp)) {
        SectionHeader(title = "Rekonsiliasi & Status Keuangan", icon = Icons.Default.Payments) {
            Button(
                onClick = { showDisputeDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Komplain (Dispute)", fontSize = 11.sp)
            }
        }

        if (invoices.isEmpty()) {
            EmptyStateView(
                message = "Belum ada riwayat rekonsiliasi keuangan.",
                tip = "Tagihan otomatis dibuat saat Toko melakukan input penjualan.",
                icon = Icons.Default.AccountBalanceWallet
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(invoices) { inv ->
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
                                Text(
                                    text = inv.shopName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                StatusBadge(status = inv.status)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Pendapatan Anda (Net)", fontSize = 10.sp, color = Color.Gray)
                                    Text("Rp ${String.format(Locale.US, "%,.0f", inv.amount)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Platform Fee (2.5%)", fontSize = 10.sp, color = Color.Gray)
                                    Text("Rp ${String.format(Locale.US, "%,.0f", inv.platformCommission)}", fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tgl Tagihan: ${inv.billingDate}", fontSize = 10.sp, color = Color.Gray)
                                if (inv.status == "Pending") {
                                    Button(
                                        onClick = { onPayInvoice(inv.id, "QRIS Pembayaran Digital") },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Konf. Lunas / QRIS", fontSize = 11.sp)
                                    }
                                } else {
                                    Text("Selesai via ${inv.paymentMethodUsed}", fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDisputeDialog) {
        var opponent by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDisputeDialog = false },
            title = { Text("Ajukan Keluhan Kerja Sama") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Laporkan masalah selisih keuangan, retur tidak didokumentasi, atau barang rusak ke Admin.", fontSize = 12.sp, color = Color.Gray)
                    CustomTextField(value = opponent, onValueChange = { opponent = it }, label = "Toko yang Dilaporkan", placeholder = "Nama lengkap toko")
                    CustomTextField(value = description, onValueChange = { description = it }, label = "Deskripsi Masalah / Bukti Kwitansi")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (opponent.isNotEmpty() && description.isNotEmpty()) {
                            onSubmitDispute(opponent, description)
                            showDisputeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Laporkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisputeDialog = false }) { Text("Batal") }
            }
        )
    }
}
