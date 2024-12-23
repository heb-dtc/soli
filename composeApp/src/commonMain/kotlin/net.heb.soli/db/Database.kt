package net.heb.soli.db

import androidx.room.ColumnInfo
import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Update
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
        YoutubeVideoEntity::class,
        AmbientEntity::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun radioDao(): RadioDao
    abstract fun youtubeVideoDao(): YoutubeVideoDao
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
interface YoutubeVideoDao {
    @Query("SELECT * FROM YoutubeVideoEntity")
    fun observe(): Flow<List<YoutubeVideoEntity>>

    @Query("SELECT * FROM YoutubeVideoEntity")
    suspend fun get(): List<YoutubeVideoEntity>

    @Upsert
    suspend fun upsert(entity: YoutubeVideoEntity)

    @Query("UPDATE YoutubeVideoEntity SET downloaded = 1 WHERE id == :id")
    suspend fun markAsDownloaded(id: Long)
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

    @Query("SELECT * FROM PodcastFeedEntity WHERE id == :id")
    suspend fun get(id: Long): PodcastFeedEntity?

    @Upsert
    suspend fun upsert(entity: PodcastFeedEntity)
}

@Dao
interface PodcastEpisodeEntityDao {
    @Query("SELECT * FROM PodcastEpisodeEntity WHERE feedId == :feedId")
    fun observe(feedId: Long): Flow<List<PodcastEpisodeEntity>>

    // The insert is used when fetching episode from the server. The data is considered the more up to date
    // so we erase any local one.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(episode: PodcastEpisodeEntity)

    // The upsert is used when fetching episode from the remote source where played and timecode
    // are not known. This is why we use a RemotePodcastEpisode object so played and timecode are
    // not changed if the episode already exists.
    @Upsert(entity = PodcastEpisodeEntity::class)
    suspend fun upsert(episode: RemotePodcastEpisode)

    @Update(entity = PodcastEpisodeEntity::class)
    suspend fun updateTimeCode(update: TimeCodeUpdate)

    @Update(entity = PodcastEpisodeEntity::class)
    suspend fun updatePlayedStatus(update: PlayedStatusUpdate)
}

@Dao
interface TrackEntityDao {
    @Query("SELECT * FROM TrackEntity")
    fun observe(): Flow<List<TrackEntity>>

    @Upsert
    suspend fun upsert(entity: TrackEntity)
}

@Entity
data class YoutubeVideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val url: String,
    val downloaded: Boolean = false,
)

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
    @PrimaryKey val id: Long,
    val name: String,
    val imageUrl: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = PodcastFeedEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("feedId"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class PodcastEpisodeEntity(
    @PrimaryKey val id: Long,
    val feedId: Long,
    val name: String,
    val url: String,
    val duration: Long,
    val description: String,
    val datePublished: Int,
    @ColumnInfo(defaultValue = "0") val played: Boolean,
    @ColumnInfo(defaultValue = "0") val timeCode: Long = 0
)

data class RemotePodcastEpisode(
    @PrimaryKey val id: Long,
    val feedId: Long,
    val name: String,
    val url: String,
    val duration: Long,
    val description: String,
    val datePublished: Int,
)

data class PlayedStatusUpdate(
    val id: Long,
    val played: Boolean,
)

data class TimeCodeUpdate(
    val id: Long,
    val timeCode: Long,
)

@Entity
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val url: String,
    val duration: Long
)
