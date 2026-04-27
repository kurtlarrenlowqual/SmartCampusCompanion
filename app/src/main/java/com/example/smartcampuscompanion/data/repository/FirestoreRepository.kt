package com.example.smartcampuscompanion.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await

object FirestoreRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    // ── LOGIN ──────────────────────────────────────────────────────────────
    // Returns role string ("student" or "admin"), or null if failed
    suspend fun login(username: String, password: String): String? {
        return try {
            // 1. Look up email by username in Firestore
            val snap = db.collection("users")
                .whereEqualTo("username", username)
                .get().await()

            if (snap.isEmpty) return null

            val doc   = snap.documents.first()
            val email = doc.getString("email") ?: return null
            val role  = doc.getString("role")  ?: "student"

            // 2. Sign in with Firebase Auth using the stored email
            auth.signInWithEmailAndPassword(email, password).await()

            role  // return the role on success
        } catch (e: Exception) {
            null
        }
    }

    // ── REGISTER (student self-register) ──────────────────────────────────
    suspend fun register(
        username: String,
        password: String,
        role: String = "student",
        displayName: String = "",
        email: String          // now a required real email
    ) {
        // 1. Create Firebase Auth account with real email
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid    = result.user?.uid ?: throw Exception("Auth failed")

        // 2. Save full profile in Firestore
        db.collection("users").document(uid).set(mapOf(
            "uid"         to uid,
            "username"    to username,
            "displayName" to displayName,
            "email"       to email,
            "role"        to role
        )).await()
    }

//
//    // ── ADMIN: CREATE ACCOUNT ─────────────────────────────────────────────
//    suspend fun adminCreateAccount(
//        username: String,
//        password: String,
//        role: String,
//        displayName: String = ""
//    ) {
//        register(username, password, role, displayName)
//    }

    // ── POST ANNOUNCEMENT ─────────────────────────────────────────────────
    suspend fun postAnnouncement(
        title: String,
        body: String,
        category: String,
        isImportant: Boolean,
        postedBy: String
    ) {
        db.collection("announcements").add(mapOf(
            "title"        to title,
            "body"         to body,
            "category"     to category,
            "isImportant"  to isImportant,
            "postedBy"     to postedBy,
            "postedAtMillis" to System.currentTimeMillis()
        )).await()
    }

    // ── OBSERVE ANNOUNCEMENTS (real-time) ─────────────────────────────────
    fun observeAnnouncements(): Flow<List<com.example.smartcampuscompanion.data.local.AnnouncementEntity>> =
        callbackFlow {
            val listener = db.collection("announcements")
                .orderBy("postedAtMillis",
                    com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.documents?.mapNotNull { doc ->
                        com.example.smartcampuscompanion.data.local.AnnouncementEntity(
                            id            = doc.id.hashCode() and Int.MAX_VALUE,
                            title         = doc.getString("title")    ?: return@mapNotNull null,
                            body          = doc.getString("body")     ?: return@mapNotNull null,
                            category      = doc.getString("category") ?: "General",
                            postedAtMillis = doc.getLong("postedAtMillis")
                                ?: System.currentTimeMillis(),
                            isImportant   = doc.getBoolean("isImportant") ?: false
                        )
                    } ?: emptyList()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }


    data class DepartmentInfo(
        val name: String,
        val email: String,
        val facebookUrl: String
    )
}