package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.example.aggregatecalculator.databinding.ActivityDashboardBinding
import com.example.aggregatecalculator.databinding.ActivityMainBinding

class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonFixtures.setOnClickListener {
            val intent = Intent(this, FixtureResultsActivity::class.java)
            startActivity(intent)
        }
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
        val intent = Intent(this, results::class.java)
        startActivity(intent)
        Toast.makeText(this, "your having a laugh", Toast.LENGTH_LONG).show()
    }
}
