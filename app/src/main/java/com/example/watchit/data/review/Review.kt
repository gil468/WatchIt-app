package com.example.watchit.data.review

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.watchit.WatchItApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.io.Serializable

@Entity
data class Review(
    @PrimaryKey
    val id: String,
    val score: Double,
    val userId: String,
    val description: String,
    val movieName: String,
    var isDeleted: Boolean = false,
    var reviewImage: String? = null,
    var timestamp: Long? = null,
) : Serializable {

    companion object {
        var lastUpdated: Long
            get() {
                return WatchItApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(REVIEW_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                WatchItApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(REVIEW_LAST_UPDATED, value)?.apply()
            }

        const val ID_KEY = "id"
        const val USER_ID_KEY = "userId"
        const val SCORE_KEY = "score"
        const val LAST_UPDATED_KEY = "timestamp"
        const val DESCRIPTION_KEY = "description"
        const val MOVIE_NAME_KEY = "movieName"
        const val IS_DELETED_KEY = "is_deleted"
        const val REVIEW_LAST_UPDATED = "review_last_updated"

        fun fromJSON(json: Map<String, Any>): Review {
            val id = json[ID_KEY] as? String ?: ""
            val score = json[SCORE_KEY] as? Double ?: 0.0
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val movieName = json[MOVIE_NAME_KEY] as? String ?: ""
            val isDeleted = json[IS_DELETED_KEY] as? Boolean ?: false
            val userId = json[USER_ID_KEY] as? String ?: ""
            val review = Review(id, score, userId, description, movieName, isDeleted)

            val timestamp: Timestamp? = json[LAST_UPDATED_KEY] as? Timestamp
            timestamp?.let {
                review.timestamp = it.seconds
            }

            return review
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                USER_ID_KEY to userId,
                SCORE_KEY to score,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
                DESCRIPTION_KEY to description,
                MOVIE_NAME_KEY to movieName,
                IS_DELETED_KEY to isDeleted
            )
        }

    val deleteJson: Map<String, Any>
        get() {
            return hashMapOf(
                IS_DELETED_KEY to true,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }

    val updateJson: Map<String, Any>
        get() {
            return hashMapOf(
                SCORE_KEY to score,
                DESCRIPTION_KEY to description,
                DESCRIPTION_KEY to FieldValue.serverTimestamp(),
            )
        }
}