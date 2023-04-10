package com.android.gymaround

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Gym::class],
    version = 2,
    exportSchema = false
)
abstract class GymsDatabase: RoomDatabase() {
    abstract val dao: Dao

    companion object {

        @Volatile
        private var daoInstance: Dao? = null

        private fun builderDatabase(context: Context): GymsDatabase =
            Room.databaseBuilder(context.applicationContext,
                GymsDatabase::class.java,
                "gyms_database"
            ).fallbackToDestructiveMigration().build()

        fun getDeoInstance(context: Context): Dao {

            synchronized(this) {

                if (daoInstance == null) {
                    daoInstance = builderDatabase(context).dao
                }
                return daoInstance as Dao
            }
        }
    }
}