package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.local.AnnouncementDao
import com.example.smartcampuscompanion.data.local.AnnouncementEntity
import kotlinx.coroutines.flow.Flow

class AnnouncementRepository(private val dao: AnnouncementDao) {

    fun observeAnnouncements(): Flow<List<AnnouncementEntity>> =
        dao.observeAnnouncements()

    fun observeUnreadCount(): Flow<Int> =
        dao.observeUnreadCount()

    suspend fun ensureSeedData() {
        if (dao.countAll() > 0) return

        val seed = listOf(
            AnnouncementEntity(
                title = "Welcome Week",
                body = "Orientation events start Monday. All freshmen are encouraged to attend."
            ),
            AnnouncementEntity(
                title = "Library Notice",
                body = "Library closes at 6PM this Friday due to a special event."
            ),
            AnnouncementEntity(
                title = "System Maintenance",
                body = "Campus portal will be down 10PM–12AM tonight for scheduled maintenance."
            )
        )

        dao.insertAll(seed)
    }

    suspend fun markRead(id: Int) = dao.markRead(id)

    suspend fun markAllRead() = dao.markAllRead()
}