package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class  LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun register(view: View){
        val intent = Intent(this,RegistrationActivity::class.java)
        startActivity(intent)
    }

    fun login(viewe: View){
        val email = findViewById<EditText>(R.id.editUserName)
        val password = findViewById<EditText>(R.id.editPassword)

        val auth = FirebaseAuth.getInstance()
        Toast.makeText(this, auth.toString(), Toast.LENGTH_LONG).show()

        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()

        auth.signInWithEmailAndPassword(emailText, passwordText)
            .addOnCompleteListener(this){task ->
            if(task.isSuccessful) {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Have another go, you got that one wrong", Toast.LENGTH_LONG).show()
            }
        }


    }
}
