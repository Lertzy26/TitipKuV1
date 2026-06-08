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
import com.example.data.database.ConsignmentStock
import com.example.data.database.Product
import com.example.data.database.Proposal
import com.example.data.database.Shop
import com.example.ui.viewmodel.TitipViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: TitipViewModel,
    modifier: Modifier = Modifier
) {
    val shops by viewModel.allShops.collectAsState()
    val activeShop by viewModel.activeShop.collectAsState()
    val activeStocks by viewModel.activeShopStocks.collectAsState()
    val proposals by viewModel.allProposals.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val activeShopTransactions by viewModel.activeShopTransactions.collectAsState()

    var showAddShopDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Stock, 1: Cashier POS, 2: Proposals, 3: Returns & Reports

    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        // Shop Profile Selection Banner
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Profil Toko Penerima",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = activeShop?.name ?: "Pilih/Daftar Toko",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Button(
                        onClick = { showAddShopDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Daftar Toko", fontSize = 12.sp)
                    }
                }

                if (shops.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ganti Akun Toko Aktif:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        shops.forEach { s ->
                            val isSelected = activeShop?.id == s.id
                            Surface(
                                modifier = Modifier
                                    .clickable { viewModel.selectActiveShop(s.id) }
                                    .testTag("shop_switch_${s.id}"),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Text(
                                    text = s.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                activeShop?.let { s ->
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Icon(Icons.Default.PinDrop, "Region", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(s.locationRegion, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Default.ViewAgenda, "Capacity", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kapasitas: ${s.capacityCurrent}/${s.capacityMax} unit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                    }
                }
            }
        }

        if (activeShop == null) {
            EmptyStateView(
                message = "Anda belum memilih atau mendaftarkan Toko Mitra.",
                tip = "Mulai daftarkan toko Anda agar UMKM bisa menitipkan dagangannya.",
                icon = Icons.Default.HomeWork
            )
            return
        }

        // Shop module internal tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Layers, null, modifier = Modifier.size(18.dp))
                    Text("Kelola Stok", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PointOfSale, null, modifier = Modifier.size(18.dp))
                    Text("Kasir POS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AssignmentReturned, null, modifier = Modifier.size(18.dp))
                    Text("Masuk Nego", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ReceiptLong, null, modifier = Modifier.size(18.dp))
                    Text("Retur & Lap", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (selectedTab) {
            0 -> ManageStockTab(
                activeStocks = activeStocks,
                products = products,
                onReceiveStock = { id, qty ->
                    viewModel.confirmShipmentReceipt(id, qty)
                    Toast.makeText(context, "Stok consignment berhasil dimasukkan!", Toast.LENGTH_SHORT).show()
                }
            )
            1 -> CashierPOSTab(
                activeStocks = activeStocks,
                onSellProduct = { prodId, qty ->
                    viewModel.sellProductViaPOS(
                        productId = prodId,
                        quantity = qty,
                        onSuccess = { Toast.makeText(context, "Kasir sukses mencatat penjualan!", Toast.LENGTH_SHORT).show() },
                        onFailure = { Toast.makeText(context, "Gagal: Stok tidak mencukupi!", Toast.LENGTH_LONG).show() }
                    )
                }
            )
            2 -> InboundProposalsTab(
                proposals = proposals.filter { it.shopId == activeShop!!.id },
                onRespond = { propId, accept, counter -> viewModel.responseToProposal(propId, accept, counter) }
            )
            3 -> ReturnsAndReportsTab(
                activeStocks = activeStocks,
                txs = activeShopTransactions,
                onReturnStock = { stockId, qty ->
                    viewModel.processReturnStock(stockId, qty)
                    Toast.makeText(context, "Retur barang diproses!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Modal - Register Shop
    if (showAddShopDialog) {
        var name by remember { mutableStateOf("") }
        var owner by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var region by remember { mutableStateOf("Sleman, DIY") }
        var categories by remember { mutableStateOf("Makanan Ringan, Minuman") }
        var capacity by remember { mutableStateOf("150") }

        AlertDialog(
            onDismissRequest = { showAddShopDialog = false },
            title = { Text("Daftar Toko Mitra Baru") },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        CustomTextField(value = name, onValueChange = { name = it }, label = "Nama Toko / Minimarket")
                    }
                    item {
                        CustomTextField(value = owner, onValueChange = { owner = it }, label = "Nama Pemilik Toko (KTP)")
                    }
                    item {
                        CustomTextField(value = address, onValueChange = { address = it }, label = "Alamat Toko Lengkap")
                    }
                    item {
                        CustomTextField(value = region, onValueChange = { region = it }, label = "Kabupaten / Kota")
                    }
                    item {
                        CustomTextField(value = categories, onValueChange = { categories = it }, label = "Kategori Produk Diterima", placeholder = "Makanan Ringan, Minuman, Fashion")
                    }
                    item {
                        CustomTextField(value = capacity, onValueChange = { capacity = it }, label = "Kapasitas Pajangan Maksimal (Unit)", placeholder = "e.g., 200")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val capInt = capacity.toIntOrNull() ?: 100
                        if (name.isNotEmpty() && owner.isNotEmpty()) {
                            viewModel.registerNewShop(name, owner, address, region, categories, capInt)
                            showAddShopDialog = false
                        }
                    }
                ) {
                    Text("Daftar Toko")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddShopDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun ManageStockTab(
    activeStocks: List<ConsignmentStock>,
    products: List<Product>,
    onReceiveStock: (Int, Int) -> Unit
) {
    var showReceiptForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(12.dp)) {
        SectionHeader(title = "Penerimaan Stok Barang Masuk", icon = Icons.Default.Layers) {
            Button(onClick = { showReceiptForm = true }, modifier = Modifier.testTag("btn_receive_stock")) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Terima Barang", fontSize = 12.sp)
            }
        }

        if (activeStocks.isEmpty()) {
            EmptyStateView(
                message = "Toko Anda belum memajang produk titipan apapun.",
                tip = "Klik 'Terima Barang' untuk menambah stok titipan produk UMKM.",
                icon = Icons.Default.Inbox
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(activeStocks) { stock ->
                    OutlinedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stock.productName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("ID Produk: PROD-${stock.productId}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Stok Pajang", fontSize = 10.sp, color = Color.Gray)
                                Text("${stock.currentStock} Unit", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReceiptForm) {
        var selectedProduct by remember { mutableStateOf<Product?>(null) }
        var dropdownOpen by remember { mutableStateOf(false) }
        var qtyString by remember { mutableStateOf("10") }

        AlertDialog(
            onDismissRequest = { showReceiptForm = false },
            title = { Text("Asupan Stok Titip Jual (Barcode/Manual)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pilih produk dari katalog UMKM yang disetujui:", fontSize = 12.sp, color = Color.Gray)

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedProduct?.let { "${it.name} (Stok: ${it.totalStock})" } ?: "- Pilih Produk -",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Produk") },
                            trailingIcon = {
                                IconButton(onClick = { dropdownOpen = !dropdownOpen }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().clickable { dropdownOpen = true }
                        )
                        DropdownMenu(expanded = dropdownOpen, onDismissRequest = { dropdownOpen = false }) {
                            products.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("${p.name} (Stok: ${p.totalStock})") },
                                    onClick = {
                                        selectedProduct = p
                                        dropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    CustomTextField(value = qtyString, onValueChange = { qtyString = it }, label = "Jumlah Unit Dikirim", placeholder = "e.g., 20", testTagName = "receive_qty_field")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = selectedProduct
                        val q = qtyString.toIntOrNull() ?: 10
                        if (p != null && q > 0) {
                            if (p.totalStock >= q) {
                                onReceiveStock(p.id, q)
                                showReceiptForm = false
                            } else {
                                // Error handling
                            }
                        }
                    },
                    modifier = Modifier.testTag("submit_receive_stock")
                ) {
                    Text("Konfirmasi Terima")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReceiptForm = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun CashierPOSTab(
    activeStocks: List<ConsignmentStock>,
    onSellProduct: (Int, Int) -> Unit
) {
    var selectedStockItem by remember { mutableStateOf<ConsignmentStock?>(null) }
    var qtyString by remember { mutableStateOf("1") }

    Column(modifier = Modifier.padding(12.dp)) {
        SectionHeader(title = "Simulasi Mesin Kasir Sederhana", icon = Icons.Default.PointOfSale)

        Text(
            text = "Pilih barang titipan UMKM untuk disimulasikan terjual di kasir took:",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (activeStocks.isEmpty()) {
            EmptyStateView(
                message = "Kasir Kosong. Belum ada barang konsinyasi di toko.",
                tip = "Silakan tambah stok dulu di tab 'Kelola Stok'.",
                icon = Icons.Default.ProductionQuantityLimits
            )
        } else {
            Row(modifier = Modifier.weight(1f)) {
                // Stock selections
                LazyColumn(
                    modifier = Modifier
                        .weight(1.3f)
                        .padding(end = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(activeStocks) { stock ->
                        val isSelected = selectedStockItem?.id == stock.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStockItem = stock }
                                .testTag("cashier_select_${stock.productId}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(stock.productName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Stok: ${stock.currentStock}", fontSize = 11.sp, color = Color.Gray)
                                    Text("Terjual: ${stock.soldCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Billing panel
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Struk Kasir", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        selectedStockItem?.let { s ->
                            Text("Item terpilih:", fontSize = 11.sp, color = Color.Gray)
                            Text(s.productName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            CustomTextField(
                                value = qtyString,
                                onValueChange = { qtyString = it },
                                label = "Qty Terjual",
                                testTagName = "cashier_qty_field"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val q = qtyString.toIntOrNull() ?: 1
                                    if (q > 0) {
                                        onSellProduct(s.productId, q)
                                        // Reset selection or update qty limits
                                        qtyString = "1"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_cashier_sell"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.ShoppingCartCheckout, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Catat Jual", fontSize = 12.sp)
                            }
                        } ?: run {
                            EmptyStateView(
                                message = "Ketuk item di sebelah kiri untuk checkout.",
                                icon = Icons.Default.TouchApp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InboundProposalsTab(
    proposals: List<Proposal>,
    onRespond: (Int, Boolean, Double?) -> Unit
) {
    Column(modifier = Modifier.padding(12.dp)) {
        SectionHeader(title = "Proposal Kerjasama Masuk", icon = Icons.Default.AssignmentReturned)

        if (proposals.isEmpty()) {
            EmptyStateView(
                message = "Belum ada proposal masuk dari UMKM saat ini.",
                tip = "Platform matchmaking sedang mempertemukan toko Anda dengan mitra terdekat yang relevan.",
                icon = Icons.Default.GroupAdd
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
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
                                    text = prop.umkmName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                StatusBadge(status = prop.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Usulan Margin Komisi Jual: ${prop.proposedCommission}%", fontSize = 12.sp)
                            
                            if (prop.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Usulan UMKM: \"${prop.notes}\"",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(0.03f), RoundedCornerShape(4.dp))
                                        .padding(6.dp)
                                )
                            }

                            if (prop.status == "Submitted") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onRespond(prop.id, true, null) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                    ) {
                                        Text("Setujui", fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { onRespond(prop.id, false, prop.proposedCommission + 2.5) }, // Counter nego +2.5%
                                        modifier = Modifier.weight(1.2f)
                                    ) {
                                        Text("Nego (+2.5%)", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { onRespond(prop.id, false, null) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Tolak", fontSize = 12.sp)
                                    }
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
fun ReturnsAndReportsTab(
    activeStocks: List<ConsignmentStock>,
    txs: List<com.example.data.database.Transaction>,
    onReturnStock: (Int, Int) -> Unit
) {
    var showReturnDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(12.dp)) {
        SectionHeader(title = "Laporan Sederhana & Retur Barang", icon = Icons.Default.ReceiptLong) {
            Button(
                onClick = { showReturnDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Proses Retur", fontSize = 11.sp)
            }
        }

        // Transactions History List
        Text("Histori Penjualan POS Kasir:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        if (txs.isEmpty()) {
            EmptyStateView(
                message = "Belum ada transaksi penjualan hari ini.",
                tip = "Silakan catat penjualan melalui Tab 'Kasir POS'.",
                icon = Icons.Default.QueryStats
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(txs) { tx ->
                    OutlinedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(tx.productName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${tx.quantity} unit x Rp ${String.format(Locale.US, "%,.0f", tx.price)}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Rp ${String.format(Locale.US, "%,.0f", tx.totalPrice)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                Text(tx.dateString, fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReturnDialog) {
        var selectedStock by remember { mutableStateOf<ConsignmentStock?>(null) }
        var dropdownOpen by remember { mutableStateOf(false) }
        var qtyString by remember { mutableStateOf("5") }

        AlertDialog(
            onDismissRequest = { showReturnDialog = false },
            title = { Text("Retur Barang Titipan ke UMKM") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pengembalian produk expired/tidak laku dengan dokumentasi tanda tangan digital.", fontSize = 12.sp, color = Color.Gray)

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedStock?.let { "${it.productName} (Sisa: ${it.currentStock})" } ?: "- Pilih Barang Retur -",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Barang Pajang") },
                            trailingIcon = {
                                IconButton(onClick = { dropdownOpen = !dropdownOpen }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().clickable { dropdownOpen = true }
                        )
                        DropdownMenu(expanded = dropdownOpen, onDismissRequest = { dropdownOpen = false }) {
                            activeStocks.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("${s.productName} (Sisa: ${s.currentStock})") },
                                    onClick = {
                                        selectedStock = s
                                        dropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    CustomTextField(value = qtyString, onValueChange = { qtyString = it }, label = "Jumlah Unit Diretur", placeholder = "e.g., 5")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = selectedStock
                        val q = qtyString.toIntOrNull() ?: 5
                        if (s != null && q > 0) {
                            if (s.currentStock >= q) {
                                onReturnStock(s.id, q)
                                showReturnDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Proses Kirim Balik")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDialog = false }) { Text("Batal") }
            }
        )
    }
}
