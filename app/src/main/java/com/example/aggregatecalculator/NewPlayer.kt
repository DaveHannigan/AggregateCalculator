package com.example.aggregatecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class NewPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_player)
    }

    fun save(view: View){

        val player = findViewById<EditText>(R.id.editTextNewPlayer).text.toString()
        val handicap = findViewById<EditText>(R.id.editTextPlayerHandicap).text.toString()

        val newPlayer = standardiseNames(player)
        val playerstrip = stripSpaces(player)

        Toast.makeText(this, "New player is "+ playerstrip  + " and their handicap is " + handicap, Toast.LENGTH_LONG).show()
    }
}
