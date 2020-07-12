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

        binding.buttonCheckResults.setOnClickListener {
            val intent = Intent(this, results::class.java)
            startActivity(intent)
            Toast.makeText(this, "your having a laugh", Toast.LENGTH_LONG).show()
        }

        binding.buttonFixtures.setOnClickListener {
            val intent = Intent(this, FixtureResultsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonEditTeams.setOnClickListener {
            val intent = Intent(this, NewTeam::class.java)
            startActivity(intent)
        }

        binding.buttonEditPlayers.setOnClickListener {
            val intent = Intent(this, EditPlayers::class.java)
            startActivity(intent)
        }

        binding.buttonCheckTeamPlayers.setOnClickListener {
            val intent = Intent(this, SeeTeamPlayers::class.java)
            startActivity(intent)
            Toast.makeText(this, "check players", Toast.LENGTH_SHORT).show()
        }
    }


}
