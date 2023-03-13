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
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "quantity")
    val quantity: String,
    @ColumnInfo(name = "name", defaultValue = "Apple")
    val name: String = "Apple",
    @ColumnInfo(name = "expiryDate", defaultValue = "1680291840000" /* default = March 31 2023*/)
    val expiryDate: String = "expiry string",
    @ColumnInfo(name = "label", defaultValue = "")
    val label: String = "",
    @ColumnInfo(name = "image", defaultValue = "")
    val imagePath: String = "",
    @ColumnInfo(name = "discarded", defaultValue = false.toString())
    val discarded: Boolean = false,
    @ColumnInfo(name = "addedOn", defaultValue = "1678394640000" /* default = March 1 2023*/)
    val addedOn: Long = 1678394640000,
    @ColumnInfo(name = "updatedOn", defaultValue = "1678394640000" /* default = March 1 2023*/)
    val updatedOn: Long = 1678394640000,
)
/**
 * Returns the passed in price in currency format.
 */
//fun Item.getFormattedPrice(): String =
//    NumberFormat.getCurrencyInstance().format(itemPrice)