package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

//import com.firebase.ui.auth.AuthUI
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


        fun onStart(){
            super.onStart()
           // val currentUser = auth.currentUser
         //   updateUI(currentUser)
    }


    }

    fun register(view: View){
        val email = findViewById<EditText>(R.id.editEmail)
        val password1 = findViewById<EditText>(R.id.editPassword)
        val password2 = findViewById<EditText>(R.id.editRepeatPassword)

        var emailAdd = checkEmail(email.text.toString().trim())
        val passwordOk = checkPassword(password1.text.toString().trim(), password2.text.toString().trim())
       // var auth: FirebaseAuth
        val auth = FirebaseAuth.getInstance()

        //Toast.makeText(this, auth.toString(),Toast.LENGTH_LONG).show()
       // val RC_SIGN_IN = 9001

        if(emailAdd == true && passwordOk == true){
         //   Toast.makeText(this, "Now to register", Toast.LENGTH_LONG).show()
            val epass = email.text.toString().trim() + " " + password1.text.toString().trim()
            //Toast.makeText(this, epass, Toast.LENGTH_LONG).show()
            auth.createUserWithEmailAndPassword(email.text.toString().trim(),password1.text.toString().trim())
                .addOnCompleteListener(this) {task ->
            if(task.isSuccessful){
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)

               Toast.makeText(this, "Task successful", Toast.LENGTH_LONG).show()
            }else{
              Toast.makeText(this, "Task not successful", Toast.LENGTH_LONG).show()
            }

                }

           // val providers = arrayListOf(
           //     AuthUI.IdpConfig.EmailBuilder().build()
           // )

          //  startActivityForResult(
          //      AuthUI.getInstance()
           //         .createSignInIntentBuilder()
           //         .setAvailableProviders(providers)
           //         .build(),
           //     RC_SIGN_IN)
        }


    }

    fun cancel(view: View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun checkEmail(address: String):Boolean{
        val pattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,18}".toRegex()
        when  (address.matches(pattern)){
           true ->  {Toast.makeText(this, address, Toast.LENGTH_SHORT).show()
                        return true}
            false ->{Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_LONG).show()
            return false}
        }
    }

    fun checkPassword(password1: String, password2: String):Boolean{

        if(password1 == "" || password2 == ""){
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_LONG).show()
            return false
        }

        when(password1==password2){
           true -> return true

           false -> {Toast.makeText(this, "Please make sure passwords match", Toast.LENGTH_LONG).show()
            return false}
        }
    }
}
