package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "umkm_profiles")
data class UMKM(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val ktpNib: String,
    val address: String,
    val category: String, // e.g. "Makanan Ringan", "Kerajinan", "Minuman", "Fashion"
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val isVerified: Boolean = false,
    val certs: String = "" // "PIRT, Halal"
)

@Entity(tableName = "shop_profiles")
data class Shop(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val ownerName: String,
    val address: String,
    val locationRegion: String, // e.g., "Sleman, DIY", "Kecamatan Depok", etc.
    val categoriesAccepted: String, // Comma separated: "Makanan Ringan, Minuman"
    val capacityMax: Int = 100,
    val capacityCurrent: Int = 0,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val isVerified: Boolean = false,
    val operHours: String = "08:00 - 21:00"
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val umkmId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val commissionRate: Double, // percentage e.g., 15.0 for 15%
    val category: String,
    val certs: String = "", // e.g., "PIRT, Halal"
    val barcode: String = "",
    val totalStock: Int = 0,
    val soldCount: Int = 0,
    val imageResName: String = "ic_launcher_foreground"
)

@Entity(tableName = "proposals")
data class Proposal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val umkmId: Int,
    val shopId: Int,
    val umkmName: String,
    val shopName: String,
    val proposedCommission: Double,
    val status: String, // "Submitted", "CounteredByShop", "Accepted", "Declined"
    val notes: String = "",
    val dateCreated: String,
    val durationMonths: Int = 6,
    val eAgreementSigned: Boolean = false
)

@Entity(tableName = "consignment_stocks")
data class ConsignmentStock(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val shopId: Int,
    val productName: String,
    val umkmId: Int,
    val currentStock: Int,
    val soldCount: Int = 0,
    val returnedCount: Int = 0
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shopId: Int,
    val productId: Int,
    val productName: String,
    val umkmId: Int,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double,
    val dateString: String,
    val paymentMethod: String = "Cash"
)

@Entity(tableName = "payment_invoices")
data class PaymentInvoice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val umkmId: Int,
    val umkmName: String,
    val shopId: Int,
    val shopName: String,
    val amount: Double,
    val platformCommission: Double, // e.g. 2.5% platform fee
    val status: String, // "Pending", "Paid", "Overdue"
    val billingDate: String,
    val dueDate: String,
    val paymentMethodUsed: String = ""
)

@Entity(tableName = "disputes")
data class Dispute(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val complainantName: String,
    val complainantRole: String, // "UMKM" or "Toko"
    val opponentName: String,
    val description: String,
    val status: String, // "Submitted", "In Investigation", "Resolved"
    val resolution: String = "",
    val dateString: String
)
