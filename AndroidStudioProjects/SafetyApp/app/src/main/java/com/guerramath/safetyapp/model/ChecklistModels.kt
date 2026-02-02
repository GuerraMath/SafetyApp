package com.guerramath.safetyapp.domain.model

data class ChecklistItem(
    val id: String,
    val text: String,
    var isChecked: Boolean = false,
    var comment: String = ""
)

data class ChecklistCategory(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val items: MutableList<ChecklistItem>
)