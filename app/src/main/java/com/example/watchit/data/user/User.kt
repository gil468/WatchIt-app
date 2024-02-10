package com.example.watchit.data.user

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.watchit.WatchItApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
class User(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    var profileImage: String? = null,
    var lastUpdated: Long? = null,
) {
    companion object {
        var lastUpdated: Long
            get() {
                return WatchItApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(USER_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                WatchItApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(USER_LAST_UPDATED, value)?.apply()
            }

        const val ID_KEY = "id"
        const val FIRST_NAME_KEY = "firstName"
        const val LAST_NAME_KEY = "lastName"
        const val LAST_UPDATED_KEY = "lastUpdated"
        const val USER_LAST_UPDATED = "user_last_updated"

        fun fromJSON(json: Map<String, Any>): User {
            val id = json[ID_KEY] as? String ?: ""
            val firstName = json[FIRST_NAME_KEY] as? String ?: ""
            val lastName = json[LAST_NAME_KEY] as? String ?: ""
            val user = User(id, firstName, lastName)

            val lastUpdated: Timestamp? = json[LAST_UPDATED_KEY] as? Timestamp
            lastUpdated?.let {
                user.lastUpdated = it.seconds
            }
            return user
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                FIRST_NAME_KEY to firstName,
                LAST_NAME_KEY to lastName,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }

    val updateJson: Map<String, Any>
        get() {
            return hashMapOf(
                FIRST_NAME_KEY to firstName,
                LAST_NAME_KEY to lastName,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }

}
