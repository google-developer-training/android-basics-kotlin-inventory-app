/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Entity data class represents a single row in the database.
 */
@Entity(foreignKeys = [ForeignKey(
    entity = Label::class,
    parentColumns = ["name"],
    childColumns = ["label"],
    onDelete = ForeignKey.SET_NULL
)]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name", defaultValue = "Apple")
    val name: String = "Apple",
    @ColumnInfo(name = "expiryDate", defaultValue = "1680291840000" /* default = March 31 2023*/)
    val expiryDate: String = "expiry string",
    @ColumnInfo(name = "label", defaultValue = "")
    val label: String = "",
    @ColumnInfo(name = "quantity")
    val quantity: Double,
    @ColumnInfo(name = "unit", defaultValue = "")
    val unit: String = "",
    @ColumnInfo(name = "image", defaultValue = "")
    val imageByte: ByteArray?,
    @ColumnInfo(name = "discarded", defaultValue = false.toString())
    val discarded: Boolean = false,
    @ColumnInfo(name = "addedOn", defaultValue = "1678394640000" /* default = March 1 2023*/)
    val addedOn: Long = 1678394640000,
    @ColumnInfo(name = "updatedOn", defaultValue = "1678394640000" /* default = March 1 2023*/)
    val updatedOn: Long = 1678394640000,
) {
    // Processing for ImagePath property (as it's a ByteArray)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Item
        if (!imageByte.contentEquals(other.imageByte)) return false
        return true
    }

    override fun hashCode(): Int {
        return imageByte.contentHashCode()
    }
}
/**
 * Returns the passed in price in currency format.
 */
//fun Item.getFormattedPrice(): String =
//    NumberFormat.getCurrencyInstance().format(itemPrice)

/**
 * Returns the remaining number of days until the ingredient expired.
 */
fun Item.getDaysToExpiry(): Long {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val parsedExpiryDate = formatter.parse(expiryDate)
    val parsedCurrentDate = formatter.parse(LocalDateTime.now().toString())
    val diffInMillies: Long = parsedExpiryDate.time - parsedCurrentDate.time
    val diffInDays: Long = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)

    return diffInDays
}

/**
 * Returns boolean indicating if item has been consumed or not.
 */
fun Item.isConsumed(): Boolean {
    return quantity <= 0
}

/**
 * Returns boolean indicating if item has expired or not.
 */
fun Item.hasExpired(): Boolean {
    return getDaysToExpiry() < 0
}