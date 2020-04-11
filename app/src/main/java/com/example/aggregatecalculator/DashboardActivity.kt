package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }

    fun editTeam(view: View){

    }

    fun enterScores(view: View){
        val intent = Intent(this, ChooseLeague::class.java)
        startActivity(intent)
    }
}
