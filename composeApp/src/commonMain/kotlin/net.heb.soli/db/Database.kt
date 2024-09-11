package net.heb.soli.db

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Upsert
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

fun getRoomDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
    return builder.fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
        .setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO).build()
}

@Database(
    entities = [
        PodcastFeedEntity::class,
        RadioEntity::class,
        TrackEntity::class,
        PodcastEpisodeEntity::class,
        AmbientEntity::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun radioDao(): RadioDao
    abstract fun ambientDao(): AmbientDao
    abstract fun podcastFeedDao(): PodcastFeedDao
    abstract fun podcastEpisodeEntityDao(): PodcastEpisodeEntityDao
    abstract fun trackEntityDao(): TrackEntityDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>

@Dao
interface RadioDao {
    @Query("SELECT * FROM RadioEntity")
    fun observe(): Flow<List<RadioEntity>>

    @Query("SELECT * FROM RadioEntity")
    suspend fun get(): List<RadioEntity>

    @Upsert
    suspend fun upsert(entity: RadioEntity)
}

@Dao
interface AmbientDao {
    @Query("SELECT * FROM AmbientEntity")
    fun observe(): Flow<List<AmbientEntity>>

    @Query("SELECT * FROM AmbientEntity")
    suspend fun get(): List<AmbientEntity>

    @Upsert
    suspend fun upsert(entity: AmbientEntity)
}

@Dao
interface PodcastFeedDao {
    @Query("SELECT * FROM PodcastFeedEntity")
    fun observe(): Flow<List<PodcastFeedEntity>>

    @Query("SELECT * FROM PodcastFeedEntity")
    suspend fun get(): List<PodcastFeedEntity>

    @Upsert
    suspend fun upsert(entity: PodcastFeedEntity)
}

@Dao
interface PodcastEpisodeEntityDao {
    @Query("SELECT * FROM PodcastEpisodeEntity")
    fun observe(): Flow<List<PodcastEpisodeEntity>>

    @Upsert
    suspend fun upsert(entity: PodcastEpisodeEntity)
}

@Dao
interface TrackEntityDao {
    @Query("SELECT * FROM TrackEntity")
    fun observe(): Flow<List<TrackEntity>>

    @Upsert
    suspend fun upsert(entity: TrackEntity)
}

@Entity
data class RadioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val url: String,
)

@Entity
data class AmbientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val url: String,
)

@Entity
data class PodcastFeedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val remoteId: Long,
    val name: String,
    val imageUrl: String
)

@Entity
data class PodcastEpisodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val remoteId: Long,
    val name: String,
    val url: String,
)

@Entity
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val url: String,
)
