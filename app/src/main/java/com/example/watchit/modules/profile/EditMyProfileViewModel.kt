import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditMyProfileViewModel : ViewModel() {
    private val _firstName = MutableLiveData<String>()
    val firstName: LiveData<String> get() = _firstName

    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> get() = _lastName

    private val _profileImageUri = MutableLiveData<Uri>()
    val profileImageUri: LiveData<Uri> get() = _profileImageUri

    init {
        // Initialize your LiveData objects if needed
    }

    fun setFirstName(firstName: String) {
        _firstName.value = firstName
    }

    fun setLastName(lastName: String) {
        _lastName.value = lastName
    }

    fun setProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
    }
}
