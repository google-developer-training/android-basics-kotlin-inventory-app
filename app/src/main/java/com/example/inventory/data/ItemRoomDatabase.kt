package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Specify the Item as the only class with the list of entities.
//Whenever you change the schema of the database table, you'll have to increase the version number.
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {

    //let the database know about the DAO
    abstract fun itemDao(): ItemDao

    /*companion object allows access to the methods for creating or getting the database using
     * the class name as the qualifier */
    companion object{

        //reference to the database, when one has been created
        /*value of a volatile variable will never be cached, and all writes and reads
         * will be done to and from the main memory */
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase( context : Context ) : ItemRoomDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }

        }

    }
}