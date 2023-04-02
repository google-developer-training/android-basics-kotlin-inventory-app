package com.example.inventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */

@Entity
data class Label(
    @PrimaryKey
    val name: String = "Vegetable",
) {
    override fun toString(): String {
        return name
    }
}
