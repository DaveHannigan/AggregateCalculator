package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }

    fun editTeam(view: View){
        val intent = Intent(this, NewTeam::class.java)
        startActivity(intent)

    }

    fun enterScores(view: View){
        val intent = Intent(this, ChooseLeague::class.java)
        startActivity(intent)
    }

    fun editPlayers(view: View){
        val intent = Intent(this, EditPlayers::class.java)
        startActivity(intent)
    }

    fun checkResults(view: View){
        Toast.makeText(this, "your having a laugh", Toast.LENGTH_LONG).show()
    }
}
