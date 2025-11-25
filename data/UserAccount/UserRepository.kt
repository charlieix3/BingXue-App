package com.example.assignmentexample.data.UserAccount

import com.example.assignmentexample.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    //signs up a user with Email and Password using Firebase Auth
    suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    //logs in a user with Email and Password using Firebase Auth
    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    //creates a user profile document in Firestore after sign-up
    suspend fun createUserProfile(username: String) {
        val firebaseUser = auth.currentUser ?: throw Exception("No authenticated user found")
        val userProfile = UserProfile( // Use the data class
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            username = username
        )
        firestore.collection("users").document(firebaseUser.uid).set(userProfile).await()
    }

    //sends a password reset email using Firebase Auth
    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }


    fun getUserProfile(): Flow<UserProfile?> {
        return authStateChanges().flatMapLatest { user ->
            if (user == null) {
                //if user is null (logged out), emit null
                flowOf(null)
            } else {
                //if user is not null (logged in), fetch their Firestore document
                firestore.collection("users").document(user.uid)
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObject<UserProfile>()
                    }
            }
        }
    }

    private fun authStateChanges(): Flow<com.google.firebase.auth.FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }


    suspend fun updateUserProfile(profile: UserProfile) {
        val currentUser = auth.currentUser ?: throw Exception("No authenticated user found")
        firestore.collection("users").document(currentUser.uid).set(profile).await()
    }

    fun logout() {
        auth.signOut()
    }
}

