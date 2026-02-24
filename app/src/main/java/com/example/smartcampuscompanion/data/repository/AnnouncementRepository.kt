package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.local.AnnouncementDao
import com.example.smartcampuscompanion.data.local.AnnouncementEntity
import kotlinx.coroutines.flow.Flow

class AnnouncementRepository(private val dao: AnnouncementDao) {


    fun observeAnnouncements(): Flow<List<AnnouncementEntity>> = dao.observeAnnouncements()


    suspend fun ensureSeedData() {
        if (dao.countAll() > 0) return


        // Seed data (counts as "local JSON / local data" source)
        val seed = listOf(
            AnnouncementEntity(title = "Welcome Week", body = "Orientation events start Monday."),
            AnnouncementEntity(title = "Library Notice", body = "Library closes at 6PM this Friday."),
            AnnouncementEntity(title = "System Maintenance", body = "Campus portal will be down 10PM–12AM.")
        )


        dao.insertAll(seed)
    }


    suspend fun markRead(id: Int) = dao.markRead(id)
}
