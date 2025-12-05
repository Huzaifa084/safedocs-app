package org.devaxiom.safedocs.data.model

import java.util.Date

data class Document(
    val id: String,
    val title: String,
    val category: String,
    val expiryDate: Date,
    val visibilityType: String,
    val storagePath: String
)
