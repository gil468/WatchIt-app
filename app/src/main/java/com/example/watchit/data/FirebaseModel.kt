package com.example.watchit.data

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.storage

class FirebaseModel {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    companion object {
        const val REVIEWS_COLLECTION_PATH = "reviews"
        const val USERS_COLLECTION_PATH = "users"
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
        db.firestoreSettings = settings
    }


    fun getAllReviews(since: Long, callback: (List<Review>) -> Unit) {
        db.collection(REVIEWS_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(Review.LAST_UPDATED_KEY, Timestamp(since, 0))
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val reviews: MutableList<Review> = mutableListOf()
                        for (json in it.result) {
                            val student = Review.fromJSON(json.data)
                            reviews.add(student)
                        }
                        callback(reviews)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun getAllUsers(since: Long, callback: (List<User>) -> Unit) {
        db.collection(USERS_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(User.LAST_UPDATED_KEY, Timestamp(since, 0))
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val reviews: MutableList<User> = mutableListOf()
                        for (json in it.result) {
                            val user = User.fromJSON(json.data)
                            reviews.add(user)
                        }
                        callback(reviews)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun getImage(path: String, imageId: String, callback: (Uri?) -> Unit) {
        storage.reference.child("images/$path/$imageId")
            .downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri)
            }
    }

    fun addReview(review: Review, callback: () -> Unit) {
        db.collection(REVIEWS_COLLECTION_PATH).document(review.id).set(review.json)
            .addOnSuccessListener {
                callback()
            }
    }

    fun deleteReview(review: Review?, callback: () -> Unit) {
        db.collection(REVIEWS_COLLECTION_PATH)
            .document(review!!.id).update("isDeleted",true).addOnSuccessListener {
                callback()
            }.addOnFailureListener {
                Log.d("Error", "Can't delete this review document: " + it.message)
            }
    }

    fun updateReview(review: Review?, callback: () -> Unit) {
        db.collection(REVIEWS_COLLECTION_PATH)
            .document(review!!.id).update("score", review.score, "description", review.description).addOnSuccessListener {
                storage.reference.child("images/reviews/${review.id}")
                    .putFile(review.reviewImage!!.toUri())
                    .addOnSuccessListener {
                        callback()
                    }
//                callback()
            }.addOnFailureListener {
                Log.d("Error", "Can't update this review document: " + it.message)
            }
    }

    fun getReviewId(review: Review?): String {
        return review!!.id
    }


    fun addUser(user: User, callback: () -> Unit) {
        db.collection(REVIEWS_COLLECTION_PATH).document(user.id).set(user.json)
            .addOnSuccessListener {
                callback()
            }
    }
}
