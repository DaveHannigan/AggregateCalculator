package com.example.aggregatecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_edit_players.*

class EditPlayers : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_players)


        val spin = findViewById<Spinner>(R.id.spinChooseLeague)
        ArrayAdapter.createFromResource(this, R.array.league, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                spin.adapter = adapter
                spin.setOnClickListener { getPlayers( ) }
            }

        buttonGetPlayers.setOnClickListener { getPlayers() }
    }

    fun getPlayers(){

    }
}
