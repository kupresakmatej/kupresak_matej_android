package com.example.testapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        var emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        var passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        var fullNameEditText = view.findViewById<EditText>(R.id.fullNameEditText)
        var heightEditText = view.findViewById<EditText>(R.id.heightEditText)
        var weightEditText = view.findViewById<EditText>(R.id.weightEditText)

        val registerButton = view.findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            var email = emailEditText.text.toString()
            var password = passwordEditText.text.toString()

            var fullName = fullNameEditText.text.toString()
            var height = heightEditText.text.toString()
            var weight = weightEditText.text.toString()

            var userData = mapOf(
                "email" to email,
                "fullName" to fullName,
                "height" to height,
                "weight" to weight
            )

            if(email.length > 0 && password.length > 0)
            {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            database.child("users/" + auth.currentUser?.uid).setValue(userData)

                            Toast.makeText(context, "Registered successfully.",
                                Toast.LENGTH_SHORT).show()

                            val loginFragment = LoginFragment()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            transaction?.replace(R.id.mainFrame, loginFragment)?.commit()
                        } else {
                            Toast.makeText(context, "Error"+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                            Log.d("Error", task.getException().toString())
                        }
                    }
            }
            else
            {
                Toast.makeText(context, "Email and password fields cannot be empty. Try again.", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }
    class User(val username: String? = null, val email: String? = null) {
        // Null default values create a no-argument default constructor, which is needed
        // for deserialization from a DataSnapshot.
    }
}