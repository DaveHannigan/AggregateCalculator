package com.example.aggregatecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aggregatecalculator.databinding.ActivitySeeTeamPlayersBinding
import com.google.firebase.firestore.FirebaseFirestore

class SeeTeamPlayers : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySeeTeamPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val TAG = "seeTeamPlayers"

        binding.buttonCancel.setOnClickListener { onBackPressed() }

        ArrayAdapter.createFromResource(this, R.array.league, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    binding.spinChooseLeague.adapter = adapter
            }

        binding.spinChooseLeague.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val league = parent?.getItemAtPosition(position).toString()
                val db = FirebaseFirestore.getInstance()
                db.collection("snookerTeams")
                    .whereEqualTo("league", league)
                    .get()
                    .addOnSuccessListener { task ->
                        val teamArray = arrayListOf<String>()
                        for (doclist in task){
                            val team = doclist["teamName"].toString()
                            teamArray.add(team)
                            teamArray.sort()
                        }
                        teamArray.add(0, "Choose team")
                        ArrayAdapter(this@SeeTeamPlayers, android.R.layout.simple_spinner_item, teamArray)
                            .also { adapter ->
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                                binding.spinChooseTeam.adapter = adapter
                            }
                        binding.spinChooseTeam.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                getPlayers(binding, league, parent?.getItemAtPosition(position).toString())
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                               // TODO("Not yet implemented")
                            }
                        }
                    }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }
    fun getPlayers(binding: ActivitySeeTeamPlayersBinding, league: String, team: String){
        val db = FirebaseFirestore.getInstance()

        db.collection("SnookerPlayers")
            .whereEqualTo("league", league)
            .whereEqualTo("team", team)
            .get()
            .addOnSuccessListener { task ->
                val players = arrayListOf<Player>()
                for (doclist in task){
                    val player = Player(doclist["player"].toString())
                    player.playerHandicap = doclist["playerHandicap"].toString()
                    player.playerLeague = league
                    player.playerTeam = team
                    players.add(player )
                }
                players.sortBy { player -> player.playerHandicap.toInt()}
                val adapter = PlayerAdapter(this, players)
                val layOutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclePlayers.layoutManager = layOutManager
                binding.recyclePlayers.adapter = adapter
            }
    }
}