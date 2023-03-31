package com.example.inventory.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Label database
 */
@Dao
interface LabelDao {
    @Query("SELECT * from label ORDER BY name ASC")
    fun getLabels(): Flow<List<Label>>

    @Query("SELECT * from label where name like '%'||:searchText||'%'")
    fun getSearchedLabels(searchText:String):List<Label>

    @Query("SELECT * from label WHERE name = :name")
    fun getLabel(name: String): Flow<Label>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Label into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(label: Label)

    @Update
    suspend fun update(label: Label)

    @Delete
    suspend fun delete(label: Label)
}