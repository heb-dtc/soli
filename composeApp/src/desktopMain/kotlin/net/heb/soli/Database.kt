package net.heb.soli

import androidx.room.Room
import androidx.room.RoomDatabase
import net.heb.soli.db.AppDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
        name = File(System.getProperty("java.io.tmpdir"), "room_desktop.db").absolutePath
    )
}