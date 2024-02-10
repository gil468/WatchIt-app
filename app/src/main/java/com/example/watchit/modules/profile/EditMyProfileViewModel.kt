import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.Model
import com.example.watchit.data.user.User
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

class EditMyProfileViewModel : ViewModel() {
    val userId = Firebase.auth.currentUser!!.uid
    var imageChanged = false
    var selectedImageURI: MutableLiveData<Uri> = MutableLiveData()
    var user: LiveData<User> = Model.instance.getCurrentUser()

    var firstName: String? = null
    var lastName: String? = null
    var firstNameError = MutableLiveData("")
    var lastNameError = MutableLiveData("")

    fun loadUser() {
        Model.instance.getUserImage(userId) {
            selectedImageURI.postValue(it)
        }
    }

    fun updateUser(
        updatedUserCallback: () -> Unit
    ) {
        if (validateUserUpdate()) {
            val updatedUser = User(
                userId,
                firstName!!,
                lastName!!
            )

            Model.instance.updateUser(updatedUser) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(selectedImageURI.value!!)
                    .setDisplayName("$firstName $lastName")
                    .build()

                Firebase.auth.currentUser!!.updateProfile(profileUpdates).addOnSuccessListener {
                    if (imageChanged) {
                        Model.instance.updateUserImage(userId, selectedImageURI.value!!) {
                            updatedUserCallback()
                        }
                    } else {
                        updatedUserCallback()
                    }
                }
            }
        }
    }

    private fun validateUserUpdate(
    ): Boolean {
        if (firstName!!.isEmpty()) {
            firstNameError.postValue("First name cannot be empty")
            return false
        }
        if (lastName!!.isEmpty()) {
            lastNameError.postValue("Last name cannot be empty")
            return false
        }
        return true
    }
}
