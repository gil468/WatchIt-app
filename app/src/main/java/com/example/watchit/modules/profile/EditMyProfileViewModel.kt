import android.net.Uri
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchit.data.Model
import com.example.watchit.data.review.Review
import com.example.watchit.data.user.User

class EditMyProfileViewModel : ViewModel() {
    var imageChanged = false
    var selectedImageURI: MutableLiveData<Uri> = MutableLiveData()
    var user: LiveData<User>? = null

    var firstName: String? = null
    var lastName: String? = null
    var firstNameError = MutableLiveData("")
    var lastNameError = MutableLiveData("")

    fun loadUser() {
        this.user = Model.instance.getCurrentUser()
        this.firstName = user!!.value!!.firstName
        this.lastName = user!!.value!!.lastName

        Model.instance.getUserImage(user!!.value!!.id) {
            selectedImageURI.postValue(it)
        }
    }

    fun updateUser(
        updatedUserCallback: () -> Unit
    ) {
        if (validateUserUpdate()) {
            val updatedUser = User(
                user!!.value!!.id,
                firstName!!,
                lastName!!)

            Model.instance.updateUser(updatedUser) {
                if (imageChanged) {
                    Model.instance.updateUserImage(user!!.value!!.id, selectedImageURI.value!!) {
                        updatedUserCallback()
                    }
                } else {
                    updatedUserCallback()
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
