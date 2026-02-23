package com.example.smartcampuscompanion.data.local


import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface AnnouncementDao {


    @Query("SELECT * FROM announcements ORDER BY postedAtMillis DESC")
    fun observeAnnouncements(): Flow<List<AnnouncementEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AnnouncementEntity>)


    @Update
    suspend fun update(item: AnnouncementEntity)


    @Query("UPDATE announcements SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Int)


    @Query("SELECT COUNT(*) FROM announcements")
    suspend fun countAll(): Int
}
