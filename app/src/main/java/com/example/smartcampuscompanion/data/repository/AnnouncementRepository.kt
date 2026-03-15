package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.local.AnnouncementDao
import com.example.smartcampuscompanion.data.local.AnnouncementEntity
import kotlinx.coroutines.flow.Flow

class AnnouncementRepository(private val dao: AnnouncementDao) {

    fun observeAnnouncements(): Flow<List<AnnouncementEntity>> {
        return dao.observeAnnouncements()
    }

    suspend fun ensureSeedData() {
        // Checking if the database is empty, and seeding it if it is
        if (dao.countAll() == 0) {
            val seedData = listOf(
                AnnouncementEntity(
                    title = "Welcome Week",
                    body = "Orientation events start Monday. All freshmen are encouraged to attend.",
                    postedAtMillis = System.currentTimeMillis() - 86400000, // 1 day ago
                    isRead = false
                ),
                AnnouncementEntity(
                    title = "Library Notice",
                    body = "Library closes at 6PM this Friday due to a special event.",
                    postedAtMillis = System.currentTimeMillis() - 172800000, // 2 days ago
                    isRead = false
                ),
                AnnouncementEntity(
                    title = "System Maintenance",
                    body = "Campus portal will be down 10PM-12AM tonight for scheduled maintenance.",
                    postedAtMillis = System.currentTimeMillis() - 259200000, // 3 days ago
                    isRead = false
                )
            )
            dao.insertAll(seedData)
        }
    }

    // THE FIX: Ensures 'id' is an Int, passing it directly to the DAO
    suspend fun updateReadStatus(id: Int, isRead: Boolean) {
        dao.updateReadStatus(id, isRead)
    }
}