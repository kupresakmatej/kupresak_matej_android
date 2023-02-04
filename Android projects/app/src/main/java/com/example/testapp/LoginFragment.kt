package com.example.testapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        auth = FirebaseAuth.getInstance()

        var emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        var passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<Button>(R.id.registerButton)

        val registerRedirect = view.findViewById<TextView>(R.id.registerRedirectText)

        loginButton.setOnClickListener {
            var email = emailEditText.text.toString()
            var password = passwordEditText.text.toString()
            if(email.length > 0 && password.length > 0)
            {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            val homeFragment = HomeFragment()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            transaction?.replace(R.id.mainFrame, homeFragment)?.commit()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(context, "You are not registered.", Toast.LENGTH_LONG).show()
                    }
            }
            else
            {
                Toast.makeText(context, "These fields cannot be empty. Try again.", Toast.LENGTH_LONG).show()
            }
        }

        registerRedirect.setOnClickListener {
            val registerFragment = RegisterFragment()
            val fragmentManager = fragmentManager?.beginTransaction()
            fragmentManager?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            fragmentManager?.replace(R.id.mainFrame, registerFragment)?.commit()
        }

        return view
    }
}