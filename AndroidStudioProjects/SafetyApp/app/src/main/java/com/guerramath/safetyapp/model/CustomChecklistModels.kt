package com.guerramath.safetyapp.domain.model

data class CustomChecklist(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val items: MutableList<CustomChecklistItem> = mutableListOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class CustomChecklistItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    var isChecked: Boolean = false
)