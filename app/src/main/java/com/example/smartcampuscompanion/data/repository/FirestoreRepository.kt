package com.example.smartcampuscompanion.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class FirestoreUser(
    val username: String = "",
    val password: String = "",
    val role: String = "student",
    val displayName: String = ""
)

data class FirestoreAnnouncement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val category: String = "General",
    val postedAtMillis: Long = 0L,
    val isImportant: Boolean = false,
    val postedBy: String = ""
)

object FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    // ---------- AUTH ----------
    /** Returns null if credentials don't match any user */
    suspend fun login(username: String, password: String): FirestoreUser? {
        val snap = db.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get().await()
        if (snap.isEmpty) return null
        val doc = snap.documents.first()
        return FirestoreUser(
            username = doc.getString("username") ?: "",
            password = doc.getString("password") ?: "",
            role = doc.getString("role") ?: "student",
            displayName = doc.getString("displayName") ?: ""
        )
    }

    suspend fun register(username: String, password: String, role: String = "student", displayName: String = "") {
        db.collection("users").document(username).set(
            mapOf(
                "username" to username,
                "password" to password,
                "role" to role,
                "displayName" to displayName
            )
        ).await()
    }

    // ---------- ANNOUNCEMENTS ----------
    fun observeAnnouncements(): Flow<List<FirestoreAnnouncement>> = callbackFlow {
        val listener = db.collection("announcements")
            .orderBy("postedAtMillis", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener
                val list = snap.documents.map { doc ->
                    FirestoreAnnouncement(
                        id             = doc.id,
                        title          = doc.getString("title") ?: "",
                        body           = doc.getString("body") ?: "",
                        category       = doc.getString("category") ?: "General",
                        postedAtMillis = doc.getLong("postedAtMillis") ?: 0L,
                        isImportant    = doc.getBoolean("isImportant") ?: false,
                        postedBy       = doc.getString("postedBy") ?: ""
                    )
                }
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun postAnnouncement(
        title: String, body: String, category: String,
        isImportant: Boolean, postedBy: String
    ) {
        db.collection("announcements").add(
            mapOf(
                "title"          to title,
                "body"           to body,
                "category"       to category,
                "postedAtMillis" to System.currentTimeMillis(),
                "isImportant"    to isImportant,
                "postedBy"       to postedBy
            )
        ).await()
    }
}
