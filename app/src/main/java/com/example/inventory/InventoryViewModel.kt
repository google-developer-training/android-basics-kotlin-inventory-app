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

package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {
    val allItems: MutableLiveData<List<Item>> = MutableLiveData<List<Item>>()
    // Cache all items form the database using LiveData.
    //    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    fun getItems(searchString: String=""){
        viewModelScope.launch(Dispatchers.IO) {
            allItems.postValue(itemDao.getSearchedItems(searchString))
        }
    }

    fun getAllItems(){
        viewModelScope.launch(Dispatchers.IO) {
            allItems.postValue(itemDao.getAllItems())
        }
    }
    fun filterItems(selectedLabel: String=""){
        viewModelScope.launch(Dispatchers.IO) {
            allItems.postValue(itemDao.getFilteredItems(selectedLabel))
        }
    }

    /**
     * Returns true if stock is available to sell, false otherwise.
     */
    fun isStockAvailable(item: Item): Boolean {
        return (item.quantity > 0)
    }

    /**
     * Updates an existing Item in the database.
     */
    fun updateItem(
        itemId: Int,
        name: String,
        expiryDate: String,
        label: String,
        quantity: String,
        unit: String,
        imageByte: ByteArray?,
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, name, expiryDate, label, quantity, unit, imageByte)
        updateItem(updatedItem)
    }


    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    /**
     * Decreases the stock by one unit and updates the database.
     */
    fun sellItem(item: Item) {
        if (item.quantity > 0) {
            // Decrease the quantity by 1
            val newItem = item.copy(quantity = item.quantity - 1)
            updateItem(newItem)
        }
    }

    fun incrementItem(item: Item) {
        if (item.quantity > 0) {
            // Decrease the quantity by 1
            val newItem = item.copy(quantity = item.quantity + 1)
            updateItem(newItem)
        }
    }

    /**
     * Inserts the new Item into database.
     */
    fun addNewItem(
        name: String,
        expiryDate: String,
        label: String,
        quantity: String,
        unit: String,
        imageByte: ByteArray?,
    ) {
        val newItem = getNewItemEntry(name, expiryDate, label, quantity, unit, imageByte)
        insertItem(newItem)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(field: String): Boolean {
        if (field.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Item] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewItemEntry(
        name: String,
        expiryDate: String,
        label: String,
        quantity: String,
        unit:String,
        imageByte: ByteArray?,
    ): Item {
        return Item(
            name = name,
            expiryDate = expiryDate,
            label = label,
            quantity = quantity.toDouble(),
            unit = unit,
            imageByte = imageByte
        )
    }

    /**
     * Called to update an existing entry in the Inventory database.
     * Returns an instance of the [Item] entity class with the item info updated by the user.
     */
    private fun getUpdatedItemEntry(
        itemId: Int,
        name: String,
        expiryDate: String,
        label: String,
        quantity: String,
        unit: String,
        imageByte: ByteArray?
    ): Item {
        return Item(
            id = itemId,
            name = name,
            expiryDate = expiryDate,
            label = label,
            quantity = quantity.toDouble(),
            unit = unit,
            imageByte = imageByte
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

