package com.example.watchit

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso

class Profile : Fragment() {

    private var auth = Firebase.auth
    private val storage = Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        setUserNameTextView(root)
        setProfileImage(root)
        root.findViewById<Button>(R.id.MyReviewsButton).setOnClickListener{
            toMyReviews()
        }
        root.findViewById<Button>(R.id.EditMyProfileButton).setOnClickListener{
            toEditMyProfile()

        }
        root.findViewById<Button>(R.id.FollowingButton).setOnClickListener{
            toFollowing()
        }
        root.findViewById<Button>(R.id.LogOutButton).setOnClickListener{
            logOutUser()
        }

        return root
    }

    private fun toFollowing() {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.FragmentLayout,UserFollowing())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun toEditMyProfile() {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.FragmentLayout,EditMyProfile())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun toMyReviews() {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.FragmentLayout,MyReviews())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setProfileImage(root: View) {
        val imageRef = storage.reference.child("images/users/${auth.currentUser?.uid}")

        imageRef.downloadUrl.addOnSuccessListener {uri ->
            Picasso.get().load(uri).into(root.findViewById<ImageView>(R.id.ProfileImageView))
        }.addOnFailureListener{exception ->
            Log.d("FirebaseStorage", "Error getting download image URI: $exception")
        }
    }

    private fun setUserNameTextView(root: View) {
        root.findViewById<TextView>(R.id.UserNameTextView).text = "${auth.currentUser?.displayName}"
    }

    private fun logOutUser() {
        auth.signOut()
        Toast.makeText(
            requireContext(),
            "Logged out, Bye!",
            Toast.LENGTH_SHORT
        ).show()
//            activity?.finish() //This close the app only if i am in the MainActivity
        activity?.finishAffinity() //To check it's nor crashing the app
    }
}