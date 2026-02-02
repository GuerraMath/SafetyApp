package com.guerramath.safetyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guerramath.safetyapp.domain.model.CustomChecklistItem

@Entity(tableName = "custom_checklists")
data class CustomChecklistEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val items: String, // JSON string
    val createdAt: Long,
    val updatedAt: Long
)

class Converters {
    @TypeConverter
    fun fromItemsList(items: List<CustomChecklistItem>): String {
        return Gson().toJson(items)
    }

    @TypeConverter
    fun toItemsList(itemsString: String): List<CustomChecklistItem> {
        val listType = object : TypeToken<List<CustomChecklistItem>>() {}.type
        return Gson().fromJson(itemsString, listType)
    }
}