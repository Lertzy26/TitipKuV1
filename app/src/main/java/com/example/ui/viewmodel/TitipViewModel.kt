package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class UserRole {
    UMKM,
    TOKO,
    ADMIN
}

class TitipViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    // Role-switching state
    private val _currentRole = MutableStateFlow(UserRole.UMKM)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Active IDs under each role context
    private val _activeUmkmId = MutableStateFlow<Int?>(null)
    val activeUmkmId: StateFlow<Int?> = _activeUmkmId.asStateFlow()

    private val _activeShopId = MutableStateFlow<Int?>(null)
    val activeShopId: StateFlow<Int?> = _activeShopId.asStateFlow()

    // Base flows from DB
    val allUMKMs: StateFlow<List<UMKM>>
    val allShops: StateFlow<List<Shop>>
    val allProducts: StateFlow<List<Product>>
    val allProposals: StateFlow<List<Proposal>>
    val allStocks: StateFlow<List<ConsignmentStock>>
    val allTransactions: StateFlow<List<Transaction>>
    val allInvoices: StateFlow<List<PaymentInvoice>>
    val allDisputes: StateFlow<List<Dispute>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.appDao())

        // Fetch flows & convert to StateFlows
        allUMKMs = repository.allUMKMs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        allShops = repository.allShops.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        allProducts = repository.allProducts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        allProposals = repository.allProposals.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        allStocks = repository.allStocks.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        allTransactions = repository.allTransactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        allInvoices = repository.allInvoices.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        allDisputes = repository.allDisputes.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        viewModelScope.launch {
            repository.checkAndSeedData()
            // Set initial active IDs once data is loaded
            allUMKMs.filter { it.isNotEmpty() }.first().let { list ->
                _activeUmkmId.value = list.firstOrNull()?.id
            }
            allShops.filter { it.isNotEmpty() }.first().let { list ->
                _activeShopId.value = list.firstOrNull()?.id
            }
        }
    }

    // Role switcher
    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun selectActiveUmkm(id: Int) {
        _activeUmkmId.value = id
    }

    fun selectActiveShop(id: Int) {
        _activeShopId.value = id
    }

    // --- Active states helper ---
    val activeUMKM: StateFlow<UMKM?> = combine(allUMKMs, _activeUmkmId) { umkms, id ->
        umkms.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val activeShop: StateFlow<Shop?> = combine(allShops, _activeShopId) { shops, id ->
        shops.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Current active UMKM's products
    val activeUmkmProducts: StateFlow<List<Product>> = combine(allProducts, _activeUmkmId) { products, id ->
        id?.let { umkmId -> products.filter { it.umkmId == umkmId } } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Current active Shop's stocks
    val activeShopStocks: StateFlow<List<ConsignmentStock>> = combine(allStocks, _activeShopId) { stocks, id ->
        id?.let { shopId -> stocks.filter { it.shopId == shopId } } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Current active Shop's POS Transactions
    val activeShopTransactions: StateFlow<List<Transaction>> = combine(allTransactions, _activeShopId) { txs, id ->
        id?.let { shopId -> txs.filter { it.shopId == shopId } } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Active UMKM relevant invoices
    val activeUmkmInvoices: StateFlow<List<PaymentInvoice>> = combine(allInvoices, _activeUmkmId) { invs, id ->
        id?.let { umkmId -> invs.filter { it.umkmId == umkmId } } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Active Shop relevant invoices
    val activeShopInvoices: StateFlow<List<PaymentInvoice>> = combine(allInvoices, _activeShopId) { invs, id ->
        id?.let { shopId -> invs.filter { it.shopId == shopId } } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- Form actions - UMKM ---
    fun registerNewUMKM(name: String, ktpNib: String, address: String, category: String, certs: String) {
        viewModelScope.launch {
            val nextId = repository.registerUMKM(name, ktpNib, address, category, certs)
            _activeUmkmId.value = nextId
        }
    }

    fun addNewProduct(name: String, desc: String, price: Double, rate: Double, category: String, certs: String, barcode: String, stock: Int) {
        viewModelScope.launch {
            _activeUmkmId.value?.let { umkmId ->
                repository.addProduct(umkmId, name, desc, price, rate, category, certs, barcode, stock)
            }
        }
    }

    fun removeProduct(productId: Int) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun submitProposalToShop(shopId: Int, shopName: String, rate: Double, notes: String, duration: Int) {
        viewModelScope.launch {
            val umkm = activeUMKM.value ?: return@launch
            repository.submitProposal(umkm.id, umkm.name, shopId, shopName, rate, notes, duration)
        }
    }

    fun signAgreement(proposalId: Int) {
        viewModelScope.launch {
            repository.signAgreement(proposalId)
        }
    }

    fun payInvoice(invoiceId: Int, method: String) {
        viewModelScope.launch {
            repository.payInvoice(invoiceId, method)
        }
    }

    // --- Form actions - SHOP ---
    fun registerNewShop(name: String, owner: String, address: String, region: String, categories: String, capacity: Int) {
        viewModelScope.launch {
            val nextId = repository.registerShop(name, owner, address, region, categories, capacity)
            _activeShopId.value = nextId
        }
    }

    fun confirmShipmentReceipt(productId: Int, quantity: Int) {
        viewModelScope.launch {
            _activeShopId.value?.let { shopId ->
                repository.deliverStock(productId, shopId, quantity)
            }
        }
    }

    fun sellProductViaPOS(productId: Int, quantity: Int, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) {
        viewModelScope.launch {
            val shop = activeShop.value ?: return@launch
            val success = repository.sellConsignmentProduct(shop.id, productId, quantity)
            if (success) onSuccess() else onFailure()
        }
    }

    fun processReturnStock(stockId: Int, quantity: Int) {
        viewModelScope.launch {
            repository.processReturn(stockId, quantity)
        }
    }

    fun responseToProposal(proposalId: Int, accept: Boolean, counterRate: Double? = null) {
        viewModelScope.launch {
            repository.respondToProposal(proposalId, accept, counterRate)
        }
    }

    // --- Shared Operations ---
    fun submitDispute(opponent: String, desc: String) {
        viewModelScope.launch {
            val roleName = when (_currentRole.value) {
                UserRole.UMKM -> "UMKM"
                UserRole.TOKO -> "Toko"
                UserRole.ADMIN -> "Admin"
            }
            val myName = when (_currentRole.value) {
                UserRole.UMKM -> activeUMKM.value?.name ?: "UMKM Tanpa Nama"
                UserRole.TOKO -> activeShop.value?.name ?: "Toko Tanpa Nama"
                UserRole.ADMIN -> "Platform Admin"
            }
            repository.submitDispute(myName, roleName, opponent, desc)
        }
    }

    // --- Admin Operations ---
    fun verifyPartner(id: Int, isUmkm: Boolean) {
        viewModelScope.launch {
            repository.verifyEntity(id, isUmkm)
        }
    }

    fun resolveDispute(disputeId: Int, resolution: String) {
        viewModelScope.launch {
            repository.resolveDispute(disputeId, resolution)
        }
    }

    // --- Analytical State Computations (Admin/Global) ---
    val platformAnalytics: StateFlow<PlatformAnalytics> = combine(
        allTransactions,
        allInvoices,
        allUMKMs,
        allShops,
        allDisputes
    ) { txs, invs, umkms, shops, disputes ->
        val totalGmv = txs.sumOf { it.totalPrice }
        val platformComm = invs.sumOf { it.platformCommission }
        val activeDeals = invs.size
        val pendingDisputes = disputes.count { it.status == "Submitted" }
        
        PlatformAnalytics(
            totalGmv = totalGmv,
            platformCommission = platformComm,
            totalUMKMs = umkms.size,
            totalShops = shops.size,
            pendingDisputes = pendingDisputes,
            activeDeals = activeDeals
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, PlatformAnalytics())
}

data class PlatformAnalytics(
    val totalGmv: Double = 0.0,
    val platformCommission: Double = 0.0,
    val totalUMKMs: Int = 0,
    val totalShops: Int = 0,
    val pendingDisputes: Int = 0,
    val activeDeals: Int = 0
)
