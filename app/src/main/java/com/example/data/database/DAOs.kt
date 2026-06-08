package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- UMKM ---
    @Query("SELECT * FROM umkm_profiles ORDER BY name ASC")
    fun getAllUMKMs(): Flow<List<UMKM>>

    @Query("SELECT * FROM umkm_profiles WHERE id = :id LIMIT 1")
    suspend fun getUMKMById(id: Int): UMKM?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUMKM(umkm: UMKM): Long

    @Update
    suspend fun updateUMKM(umkm: UMKM)

    // --- Shop ---
    @Query("SELECT * FROM shop_profiles ORDER BY name ASC")
    fun getAllShops(): Flow<List<Shop>>

    @Query("SELECT * FROM shop_profiles WHERE id = :id LIMIT 1")
    suspend fun getShopById(id: Int): Shop?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: Shop): Long

    @Update
    suspend fun updateShop(shop: Shop)

    // --- Products ---
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE umkmId = :umkmId ORDER BY name ASC")
    fun getProductsByUMKM(umkmId: Int): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: Int)

    // --- Proposals ---
    @Query("SELECT * FROM proposals ORDER BY id DESC")
    fun getAllProposals(): Flow<List<Proposal>>

    @Query("SELECT * FROM proposals WHERE umkmId = :umkmId ORDER BY id DESC")
    fun getProposalsByUMKM(umkmId: Int): Flow<List<Proposal>>

    @Query("SELECT * FROM proposals WHERE shopId = :shopId ORDER BY id DESC")
    fun getProposalsByShop(shopId: Int): Flow<List<Proposal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProposal(proposal: Proposal): Long

    @Update
    suspend fun updateProposal(proposal: Proposal)

    // --- Consignment Stocks ---
    @Query("SELECT * FROM consignment_stocks ORDER BY id DESC")
    fun getAllConsignmentStocks(): Flow<List<ConsignmentStock>>

    @Query("SELECT * FROM consignment_stocks WHERE shopId = :shopId")
    fun getStocksByShop(shopId: Int): Flow<List<ConsignmentStock>>

    @Query("SELECT * FROM consignment_stocks WHERE productId = :productId AND shopId = :shopId LIMIT 1")
    suspend fun getStockItemByProductAndShop(productId: Int, shopId: Int): ConsignmentStock?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsignmentStock(stock: ConsignmentStock)

    @Update
    suspend fun updateConsignmentStock(stock: ConsignmentStock)

    // --- Transactions ---
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE shopId = :shopId ORDER BY id DESC")
    fun getTransactionsByShop(shopId: Int): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    // --- Payment Invoices ---
    @Query("SELECT * FROM payment_invoices ORDER BY id DESC")
    fun getAllInvoices(): Flow<List<PaymentInvoice>>

    @Query("SELECT * FROM payment_invoices WHERE umkmId = :umkmId ORDER BY id DESC")
    fun getInvoicesForUMKM(umkmId: Int): Flow<List<PaymentInvoice>>

    @Query("SELECT * FROM payment_invoices WHERE shopId = :shopId ORDER BY id DESC")
    fun getInvoicesForShop(shopId: Int): Flow<List<PaymentInvoice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: PaymentInvoice): Long

    @Update
    suspend fun updateInvoice(invoice: PaymentInvoice)

    // --- Disputes ---
    @Query("SELECT * FROM disputes ORDER BY id DESC")
    fun getAllDisputes(): Flow<List<Dispute>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: Dispute): Long

    @Update
    suspend fun updateDispute(dispute: Dispute)
}
