package com.example.aggregatecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.aggregatecalculator.databinding.ActivityFixtureListBinding
import com.example.aggregatecalculator.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class FixtureListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFixtureListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ArrayAdapter.createFromResource(this, R.array.season, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                binding.spinChooseSeason.adapter = adapter

            }
        ArrayAdapter.createFromResource(this, R.array.league, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                binding.spinChooseLeague.adapter = adapter
            }
        ArrayAdapter.createFromResource(this,R.array.divisions, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                binding.spinChooseDivision.adapter = adapter
            }

        binding.buttonAddFixtures.setOnClickListener {
            val season = binding.spinChooseSeason.selectedItem.toString()
            val league = binding.spinChooseLeague.selectedItem.toString()
            val division = binding.spinChooseDivision.selectedItem.toString()

            if (season == "Choose Season" || league == "Choose league" || division == "Choose division" ){
                Toast.makeText(this, "Make sure you have made all selections",
                        Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            fixtures(binding) }

    }

    fun fixtures(binding: ActivityFixtureListBinding){

        val season = binding.spinChooseSeason.selectedItem.toString()
        val league = binding.spinChooseLeague.selectedItem.toString()
        val division = binding.spinChooseDivision.selectedItem.toString()
        val fixtures = arrayListOf<ResultClass>()

        val db = FirebaseFirestore.getInstance()
        db.collection("snookerResults")
            .whereEqualTo("season", season)
            .whereEqualTo("league", league)
            .whereEqualTo("division", division)
            .get()
            .addOnSuccessListener { task ->
                for (doclist in task){
                    val homeTeam = doclist["homeTeam"].toString()
                    val awayTeam = doclist["awayTeam"].toString()
                    val matchDate = doclist["date"].toString()
                    val id = doclist.id
                    val fixture = ResultClass(id)
                    fixture.homeTeam = homeTeam
                    fixture.awayTeam = awayTeam
                    fixture.matchDate = matchDate as Timestamp
                    fixtures.add(fixture)
                }
            }

        Log.i("fixtures", "$season $league $division")
    }

}