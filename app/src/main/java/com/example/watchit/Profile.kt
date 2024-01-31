package com.example.watchit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso

class Profile : Fragment() {

    private lateinit var root: View
    private var auth = Firebase.auth
    private val storage = Firebase.storage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_profile, container, false)

        setUserNameTextView()
        setProfileImage()
        root.findViewById<Button>(R.id.MyReviewsButton).setOnClickListener {
//            navigateToFragment(MyReviews())
            Navigation.findNavController(root).navigate(R.id.action_profile_to_myReviews)
        }
        root.findViewById<Button>(R.id.EditMyProfileButton).setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_profile_to_editMyProfile)
        }
        root.findViewById<Button>(R.id.LogOutButton).setOnClickListener {
            logOutUser()
        }
        return root
    }


//    private fun navigateToFragment(destFragment: Fragment) {
//        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
//        transaction.replace(R.id.FragmentLayout, destFragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }

    private fun setProfileImage() {
        val imageRef = storage.reference.child("images/users/${auth.currentUser?.uid}")

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(root.findViewById<ImageView>(R.id.ProfileImageView))
        }.addOnFailureListener { exception ->
            Log.d("FirebaseStorage", "Error getting download image URI: $exception")
        }
    }

    private fun setUserNameTextView() {
        root.findViewById<TextView>(R.id.UserNameTextView).text = "${auth.currentUser?.displayName}"
    }

    private fun logOutUser() {
        auth.signOut()
        Toast.makeText(
            requireContext(),
            "Logged out, Bye!",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}