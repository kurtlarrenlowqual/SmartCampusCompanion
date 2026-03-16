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

    @Query("SELECT COUNT(*) FROM announcements")
    suspend fun countAll(): Int

    // THE FIX: Changed 'id: Long' to 'id: Int' so it matches the rest of your app!
    @Query("UPDATE announcements SET isRead = :isRead WHERE id = :id")
    suspend fun updateReadStatus(id: Int, isRead: Boolean)

}