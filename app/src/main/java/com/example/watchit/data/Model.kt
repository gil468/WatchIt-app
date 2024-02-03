package com.example.watchit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User
import java.util.concurrent.Executors

class Model private constructor() {

    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val database = AppLocalDatabase.db
    private var reviewsExecutor = Executors.newSingleThreadExecutor()
    private var usersExecutor = Executors.newSingleThreadExecutor()
    private val firebaseModel = FirebaseModel()
    private val reviews: LiveData<MutableList<Review>>? = null
    private val users: LiveData<MutableList<User>>? = null
    val reviewsListLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.LOADED)


    companion object {
        val instance: Model = Model()
    }

    fun getAllReviews(): LiveData<MutableList<Review>> {
        refreshAllReviews()
        return reviews ?: database.reviewDao().getAll()
    }

    fun getAllUsers(): LiveData<MutableList<User>> {
        refreshAllUsers()
        return users ?: database.userDao().getAll()
    }

    fun refreshAllUsers() {
        val lastUpdated: Long = User.lastUpdated

        firebaseModel.getAllUsers(lastUpdated) { list ->
            var time = lastUpdated
            for (user in list) {
                firebaseModel.getImage("users", user.id) { uri ->
                    usersExecutor.execute {
                        user.profileImage = uri.toString()
                        database.userDao().insert(user)
                    }
                }

                user.lastUpdated?.let {
                    if (time < it)
                        time = user.lastUpdated ?: System.currentTimeMillis()
                }
                User.lastUpdated = time
            }
        }
    }


    fun refreshAllReviews() {
        reviewsListLoadingState.value = LoadingState.LOADING

        val lastUpdated: Long = Review.lastUpdated

        firebaseModel.getAllReviews(lastUpdated) { list ->
            var time = lastUpdated
            for (review in list) {
                firebaseModel.getImage("reviews", review.id) { uri ->
                    reviewsExecutor.execute {
                        review.reviewImage = uri.toString()
                        database.reviewDao().insert(review)
                    }
                }

                review.timestamp?.let {
                    if (time < it)
                        time = review.timestamp ?: System.currentTimeMillis()
                }

                Review.lastUpdated = time
            }
            reviewsListLoadingState.postValue(LoadingState.LOADED)
        }
    }

    fun addReview(review: Review, callback: () -> Unit) {
        firebaseModel.addReview(review) {
            refreshAllReviews()
            callback()
        }
    }

    fun addUser(user: User, callback: () -> Unit) {
        firebaseModel.addUser(user) {
            refreshAllUsers()
            callback()
        }
    }
}