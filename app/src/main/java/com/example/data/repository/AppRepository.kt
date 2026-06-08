package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class AppRepository(private val appDao: AppDao) {

    // Streams
    val allUMKMs: Flow<List<UMKM>> = appDao.getAllUMKMs()
    val allShops: Flow<List<Shop>> = appDao.getAllShops()
    val allProducts: Flow<List<Product>> = appDao.getAllProducts()
    val allProposals: Flow<List<Proposal>> = appDao.getAllProposals()
    val allStocks: Flow<List<ConsignmentStock>> = appDao.getAllConsignmentStocks()
    val allTransactions: Flow<List<Transaction>> = appDao.getAllTransactions()
    val allInvoices: Flow<List<PaymentInvoice>> = appDao.getAllInvoices()
    val allDisputes: Flow<List<Dispute>> = appDao.getAllDisputes()

    // Sub-streams
    fun getProductsByUMKM(umkmId: Int): Flow<List<Product>> = appDao.getProductsByUMKM(umkmId)
    fun getProposalsByUMKM(umkmId: Int): Flow<List<Proposal>> = appDao.getProposalsByUMKM(umkmId)
    fun getProposalsByShop(shopId: Int): Flow<List<Proposal>> = appDao.getProposalsByShop(shopId)
    fun getStocksByShop(shopId: Int): Flow<List<ConsignmentStock>> = appDao.getStocksByShop(shopId)
    fun getInvoicesForUMKM(umkmId: Int): Flow<List<PaymentInvoice>> = appDao.getInvoicesForUMKM(umkmId)
    fun getInvoicesForShop(shopId: Int): Flow<List<PaymentInvoice>> = appDao.getInvoicesForShop(shopId)

    // Current Date helper
    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getDueDateString(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(cal.time)
    }

    // Seeding mock data if DB empty
    suspend fun checkAndSeedData() {
        val currentUMKMs = allUMKMs.first()
        if (currentUMKMs.isEmpty()) {
            // Seed UMKMs
            val u1 = appDao.insertUMKM(
                UMKM(
                    name = "Kripik Tempe Mak Nyos",
                    ktpNib = "3404123456780001 / NIB-12093",
                    address = "Gamping, Sleman, Yogyakarta",
                    category = "Makanan Ringan",
                    rating = 4.8f,
                    reviewCount = 12,
                    isVerified = true,
                    certs = "PIRT, Halal"
                )
            ).toInt()

            val u2 = appDao.insertUMKM(
                UMKM(
                    name = "Batik Tulis Sekar Jagad",
                    ktpNib = "3404876543210002 / NIB-99201",
                    address = "Sanden, Bantul, Yogyakarta",
                    category = "Fashion",
                    rating = 4.9f,
                    reviewCount = 20,
                    isVerified = true,
                    certs = "NIB Resmi"
                )
            ).toInt()

            val u3 = appDao.insertUMKM(
                UMKM(
                    name = "Sirup Rosella Kaliurang",
                    ktpNib = "3404567812340003 / NIB-33045",
                    address = "Pakem, Sleman, Yogyakarta",
                    category = "Minuman",
                    rating = 4.6f,
                    reviewCount = 8,
                    isVerified = true,
                    certs = "PIRT, Halal LPPOM"
                )
            ).toInt()

            val u4 = appDao.insertUMKM(
                UMKM(
                    name = "Sambal Cumi Bu Retno",
                    ktpNib = "3402345612340005 / NIB-88219",
                    address = "Kraton, Kota Jogja",
                    category = "Makanan Ringan",
                    rating = 4.7f,
                    reviewCount = 15,
                    isVerified = true,
                    certs = "PIRT, Halal MUI"
                )
            ).toInt()

            // Seed Shops
            val s1 = appDao.insertShop(
                Shop(
                    name = "Toserba Berkah Sleman",
                    ownerName = "Pak Siswanto",
                    address = "Jl. Kaliurang KM 10, Sleman",
                    locationRegion = "Sleman, DIY",
                    categoriesAccepted = "Makanan Ringan, Minuman",
                    capacityMax = 150,
                    capacityCurrent = 40,
                    rating = 4.8f,
                    reviewCount = 32,
                    isVerified = true,
                    operHours = "08:00 - 21:00"
                )
            ).toInt()

            val s2 = appDao.insertShop(
                Shop(
                    name = "Oleh-Oleh Jogja Semesta",
                    ownerName = "Bu Retno Wulandari",
                    address = "Jl. Malioboro No. 45, Yogyakarta",
                    locationRegion = "Malioboro, DIY",
                    categoriesAccepted = "Makanan Ringan, Minuman, Fashion",
                    capacityMax = 300,
                    capacityCurrent = 110,
                    rating = 4.9f,
                    reviewCount = 142,
                    isVerified = true,
                    operHours = "09:00 - 22:00"
                )
            ).toInt()

            val s3 = appDao.insertShop(
                Shop(
                    name = "Koperasi Pegawai Depok",
                    ownerName = "Pak Budiman",
                    address = "Condongcatur, Sleman, Yogyakarta",
                    locationRegion = "Sleman, DIY",
                    categoriesAccepted = "Makanan Ringan, Minuman",
                    capacityMax = 100,
                    capacityCurrent = 15,
                    rating = 4.4f,
                    reviewCount = 14,
                    isVerified = true,
                    operHours = "08:00 - 17:30"
                )
            ).toInt()

            val s4 = appDao.insertShop(
                Shop(
                    name = "Galeri UMKM Prambanan",
                    ownerName = "Ibu Sri Hartati",
                    address = "Kawasan Candi Prambanan, Kalasan",
                    locationRegion = "Prambanan, DIY",
                    categoriesAccepted = "Fashion",
                    capacityMax = 80,
                    capacityCurrent = 20,
                    rating = 4.7f,
                    reviewCount = 18,
                    isVerified = true,
                    operHours = "08:30 - 17:00"
                )
            ).toInt()

            // Seed Products linked to UMKMs
            val p1 = appDao.insertProduct(
                Product(
                    umkmId = u1,
                    name = "Kripik Tempe Original (Pack)",
                    description = "Kripik tempe renyah bumbu bawang warisan nusantara",
                    price = 15000.0,
                    commissionRate = 10.0, // 10%
                    category = "Makanan Ringan",
                    certs = "Halal, PIRT",
                    barcode = "TP-ORIG-01",
                    totalStock = 120,
                    soldCount = 45
                )
            ).toInt()

            val p2 = appDao.insertProduct(
                Product(
                    umkmId = u1,
                    name = "Kripik Tempe Pedas Daun Jeruk",
                    description = "Kripik tempe pedas mantap aroma segar daun jeruk",
                    price = 17500.0,
                    commissionRate = 12.0, // 12%
                    category = "Makanan Ringan",
                    certs = "Halal, PIRT",
                    barcode = "TP-PEDAS-02",
                    totalStock = 90,
                    soldCount = 30
                )
            ).toInt()

            val p3 = appDao.insertProduct(
                Product(
                    umkmId = u2,
                    name = "Selendang Batik Sutera",
                    description = "Batik tulis sutera motif modern warna indigo alami",
                    price = 250000.0,
                    commissionRate = 15.0, // 15%
                    category = "Fashion",
                    certs = "NIB Resmi",
                    barcode = "BTK-SLD-01",
                    totalStock = 15,
                    soldCount = 3
                )
            ).toInt()

            val p4 = appDao.insertProduct(
                Product(
                    umkmId = u3,
                    name = "Sirup Rosella Premium 350ml",
                    description = "Sirup rosella asli manis asam segar tanpa pengawet buatan",
                    price = 30000.0,
                    commissionRate = 15.0,
                    category = "Minuman",
                    certs = "PIRT, Halal LPPOM",
                    barcode = "RSL-SYR-03",
                    totalStock = 50,
                    soldCount = 12
                )
            ).toInt()

            val p5 = appDao.insertProduct(
                Product(
                    umkmId = u4,
                    name = "Sambal Cumi Judes Botol",
                    description = "Sambal cumi ekstra pedas gurih nagih dalam jar higienis",
                    price = 28000.0,
                    commissionRate = 15.0,
                    category = "Makanan Ringan",
                    certs = "PIRT, Halal MUI",
                    barcode = "SBL-CUMI-04",
                    totalStock = 80,
                    soldCount = 34
                )
            ).toInt()

            // Seed Proposals and Consignment stocks for initial matching simulation
            appDao.insertProposal(
                Proposal(
                    umkmId = u1,
                    shopId = s1,
                    umkmName = "Kripik Tempe Mak Nyos",
                    shopName = "Toserba Berkah Sleman",
                    proposedCommission = 10.0,
                    status = "Accepted",
                    notes = "Kerjasama lancar, komisi 10% disepakati harian/mingguan",
                    dateCreated = "25 May 2026",
                    durationMonths = 12,
                    eAgreementSigned = true
                )
            )

            appDao.insertProposal(
                Proposal(
                    umkmId = u3,
                    shopId = s1,
                    umkmName = "Sirup Rosella Kaliurang",
                    shopName = "Toserba Berkah Sleman",
                    proposedCommission = 15.0,
                    status = "CounteredByShop",
                    notes = "Kami minta counter ke 12% untuk botol kaca.",
                    dateCreated = "01 Jun 2026",
                    durationMonths = 6,
                    eAgreementSigned = false
                )
            )

            appDao.insertProposal(
                Proposal(
                    umkmId = u2,
                    shopId = s4,
                    umkmName = "Batik Tulis Sekar Jagad",
                    shopName = "Galeri UMKM Prambanan",
                    proposedCommission = 15.0,
                    status = "Submitted",
                    notes = "Proposal display kain sutera kualitas galeri premium",
                    dateCreated = "05 Jun 2026",
                    durationMonths = 6,
                    eAgreementSigned = false
                )
            )

            // Seed ACTIVE Consignment Stocks
            appDao.insertConsignmentStock(
                ConsignmentStock(
                    productId = p1,
                    shopId = s1,
                    productName = "Kripik Tempe Original (Pack)",
                    umkmId = u1,
                    currentStock = 25, // Available in store
                    soldCount = 15,
                    returnedCount = 0
                )
            )

            appDao.insertConsignmentStock(
                ConsignmentStock(
                    productId = p2,
                    shopId = s1,
                    productName = "Kripik Tempe Pedas Daun Jeruk",
                    umkmId = u1,
                    currentStock = 15,
                    soldCount = 8,
                    returnedCount = 0
                )
            )

            // Seed billing histories (Invoices)
            appDao.insertInvoice(
                PaymentInvoice(
                    umkmId = u1,
                    umkmName = "Kripik Tempe Mak Nyos",
                    shopId = s1,
                    shopName = "Toserba Berkah Sleman",
                    amount = 365000.0,
                    platformCommission = 9125.0, // 2.5% fee
                    status = "Pending",
                    billingDate = "01 Jun 2026",
                    dueDate = "15 Jun 2026"
                )
            )

            appDao.insertInvoice(
                PaymentInvoice(
                    umkmId = u1,
                    umkmName = "Kripik Tempe Mak Nyos",
                    shopId = s1,
                    shopName = "Toserba Berkah Sleman",
                    amount = 220000.0,
                    platformCommission = 5500.0,
                    status = "Paid",
                    billingDate = "15 May 2026",
                    dueDate = "30 May 2026",
                    paymentMethodUsed = "QRIS (GoPay)"
                )
            )

            // Seed a dispute
            appDao.insertDispute(
                Dispute(
                    complainantName = "Kripik Tempe Mak Nyos",
                    complainantRole = "UMKM",
                    opponentName = "Toserba Berkah Sleman",
                    description = "Selisih 2 bungkus kripik rusak di log barang masuk, mohon verifikasi.",
                    status = "Resolved",
                    resolution = "Pihak toko sudah mengganti nilai retur barang penyok sesuai kesepakatan.",
                    dateString = "28 May 2026"
                )
            )
        }
    }

    // --- Profile Operations ---
    suspend fun registerUMKM(name: String, ktpNib: String, address: String, category: String, certs: String): Int {
        return appDao.insertUMKM(
            UMKM(
                name = name,
                ktpNib = ktpNib,
                address = address,
                category = category,
                isVerified = true, // Auto verified for smooth emulator experience
                certs = certs
            )
        ).toInt()
    }

    suspend fun registerShop(name: String, owner: String, address: String, region: String, categories: String, capacity: Int): Int {
        return appDao.insertShop(
            Shop(
                name = name,
                ownerName = owner,
                address = address,
                locationRegion = region,
                categoriesAccepted = categories,
                capacityMax = capacity,
                capacityCurrent = 0,
                isVerified = true
            )
        ).toInt()
    }

    // --- Product Operations ---
    suspend fun addProduct(umkmId: Int, name: String, desc: String, price: Double, rate: Double, category: String, certs: String, barcode: String, stock: Int) {
        appDao.insertProduct(
            Product(
                umkmId = umkmId,
                name = name,
                description = desc,
                price = price,
                commissionRate = rate,
                category = category,
                certs = certs,
                barcode = barcode,
                totalStock = stock,
                soldCount = 0
            )
        )
    }

    suspend fun deleteProduct(id: Int) {
        appDao.deleteProduct(id)
    }

    // --- Proposal Operations ---
    suspend fun submitProposal(umkmId: Int, umkmName: String, shopId: Int, shopName: String, proposedComm: Double, notes: String, duration: Int) {
        appDao.insertProposal(
            Proposal(
                umkmId = umkmId,
                shopId = shopId,
                umkmName = umkmName,
                shopName = shopName,
                proposedCommission = proposedComm,
                status = "Submitted",
                notes = notes,
                dateCreated = getCurrentDateString(),
                durationMonths = duration
            )
        )
    }

    suspend fun signAgreement(proposalId: Int) {
        val proposals = allProposals.first()
        val prop = proposals.find { it.id == proposalId }
        if (prop != null) {
            val updated = prop.copy(status = "Accepted", eAgreementSigned = true)
            appDao.updateProposal(updated)
        }
    }

    suspend fun respondToProposal(proposalId: Int, accept: Boolean, counterRate: Double? = null) {
        val proposals = allProposals.first()
        val prop = proposals.find { it.id == proposalId }
        if (prop != null) {
            val updated = if (accept) {
                prop.copy(status = "Accepted", eAgreementSigned = true)
            } else if (counterRate != null) {
                prop.copy(status = "CounteredByShop", proposedCommission = counterRate, notes = "Toko menawarkan komisi $counterRate%")
            } else {
                prop.copy(status = "Declined")
            }
            appDao.updateProposal(updated)
        }
    }

    // --- Consignment Stock POS & Logistics ---
    suspend fun deliverStock(productId: Int, shopId: Int, quantity: Int) {
        val product = appDao.getProductById(productId) ?: return
        
        // Subtract from UMKM main catalog totalStock
        if (product.totalStock >= quantity) {
            appDao.updateProduct(product.copy(totalStock = product.totalStock - quantity))
        }

        // Add or Update in ConsignmentStock for the Shop
        val existingStock = appDao.getStockItemByProductAndShop(productId, shopId)
        if (existingStock != null) {
            appDao.updateConsignmentStock(
                existingStock.copy(currentStock = existingStock.currentStock + quantity)
            )
        } else {
            appDao.insertConsignmentStock(
                ConsignmentStock(
                    productId = productId,
                    shopId = shopId,
                    productName = product.name,
                    umkmId = product.umkmId,
                    currentStock = quantity,
                    soldCount = 0
                )
            )
        }

        // Increment Shop's active capacity usage
        val shop = appDao.getShopById(shopId)
        if (shop != null) {
            appDao.updateShop(shop.copy(capacityCurrent = shop.capacityCurrent + quantity))
        }
    }

    // Simulate simple Cashier / POS selling of a consignment item
    suspend fun sellConsignmentProduct(shopId: Int, productId: Int, quantity: Int): Boolean {
        val stockItem = appDao.getStockItemByProductAndShop(productId, shopId)
        if (stockItem == null || stockItem.currentStock < quantity) {
            return false // Out of stock or not consigned
        }

        val product = appDao.getProductById(productId) ?: return false

        // Update Consignment stock levels
        val updatedStock = stockItem.copy(
            currentStock = stockItem.currentStock - quantity,
            soldCount = stockItem.soldCount + quantity
        )
        appDao.updateConsignmentStock(updatedStock)

        // Decrement Shop's capacity
        val shop = appDao.getShopById(shopId)
        if (shop != null) {
            val newCap = (shop.capacityCurrent - quantity).coerceAtLeast(0)
            appDao.updateShop(shop.copy(capacityCurrent = newCap))
        }

        // Increment overall sold count parameter
        appDao.updateProduct(product.copy(soldCount = product.soldCount + quantity))

        // Record Cashier POS Transaction
        val totalAmount = product.price * quantity
        appDao.insertTransaction(
            Transaction(
                shopId = shopId,
                productId = productId,
                productName = product.name,
                umkmId = product.umkmId,
                quantity = quantity,
                price = product.price,
                totalPrice = totalAmount,
                dateString = getCurrentDateString()
            )
        )

        // Generate dynamic billing/invoice for the UMKM (konsinyasi)
        // Auto-accumulate or create billing per sales
        val umkm = appDao.getUMKMById(product.umkmId)
        if (umkm != null && shop != null) {
            // Calculate how much goes to UMKM based on product commission:
            // UMKM receives price * (100 - commissionRate)%
            val netUMKMReceive = totalAmount * (1.0 - (product.commissionRate / 100.0))
            val platformFee = netUMKMReceive * 0.025 // 2.5% platform facilitation fee

            appDao.insertInvoice(
                PaymentInvoice(
                    umkmId = product.umkmId,
                    umkmName = umkm.name,
                    shopId = shopId,
                    shopName = shop.name,
                    amount = netUMKMReceive,
                    platformCommission = platformFee,
                    status = "Pending",
                    billingDate = getCurrentDateString(),
                    dueDate = getDueDateString(7)
                )
            )
        }

        return true
    }

    suspend fun processReturn(stockId: Int, quantity: Int) {
        val stocks = allStocks.first()
        val stock = stocks.find { it.id == stockId }
        if (stock != null && stock.currentStock >= quantity) {
            // Deduct from store stock
            val updated = stock.copy(
                currentStock = stock.currentStock - quantity,
                returnedCount = stock.returnedCount + quantity
            )
            appDao.updateConsignmentStock(updated)

            // Send back to UMKM catalog
            val product = appDao.getProductById(stock.productId)
            if (product != null) {
                appDao.updateProduct(product.copy(totalStock = product.totalStock + quantity))
            }

            // Adjust Shop capacity
            val shop = appDao.getShopById(stock.shopId)
            if (shop != null) {
                val newCap = (shop.capacityCurrent - quantity).coerceAtLeast(0)
                appDao.updateShop(shop.copy(capacityCurrent = newCap))
            }
        }
    }

    // --- Financial Operations ---
    suspend fun payInvoice(invoiceId: Int, method: String) {
        val invoices = allInvoices.first()
        val inv = invoices.find { it.id == invoiceId }
        if (inv != null) {
            appDao.updateInvoice(
                inv.copy(
                    status = "Paid",
                    paymentMethodUsed = method
                )
            )
        }
    }

    // --- Dispute Operations ---
    suspend fun submitDispute(compBy: String, role: String, oppName: String, desc: String) {
        appDao.insertDispute(
            Dispute(
                complainantName = compBy,
                complainantRole = role,
                opponentName = oppName,
                description = desc,
                status = "Submitted",
                dateString = getCurrentDateString()
            )
        )
    }

    suspend fun resolveDispute(id: Int, res: String) {
        val disputes = allDisputes.first()
        val disp = disputes.find { it.id == id }
        if (disp != null) {
            appDao.updateDispute(
                disp.copy(status = "Resolved", resolution = res)
            )
        }
    }

    // --- Verification Operations ---
    suspend fun verifyEntity(id: Int, isUmkm: Boolean) {
        if (isUmkm) {
            val u = appDao.getUMKMById(id)
            if (u != null) {
                appDao.updateUMKM(u.copy(isVerified = true))
            }
        } else {
            val s = appDao.getShopById(id)
            if (s != null) {
                appDao.updateShop(s.copy(isVerified = true))
            }
        }
    }
}
