package com.example.aggregatecalculator

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aggregatecalculator.databinding.ActivityEditPlayersBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_players.*

class EditPlayers : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val passedLeague = intent.getStringExtra("league")



        binding.buttonNewPlayer.setOnClickListener {
            val intent = Intent(this, NewPlayer::class.java)
            val b = Bundle()
            intent.putExtra("name", "")
            intent.putExtra("handicap", "")
            intent.putExtra("league", "")
            intent.putExtra("team", "")
            intent.putExtra("id", "")
            startActivity(intent,b)
        }

        val spin = findViewById<Spinner>(R.id.spinChooseLeague)
        ArrayAdapter.createFromResource(this, R.array.league, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                spin.adapter = adapter

                val leagueArray = resources.getStringArray(R.array.league)
                val index = leagueArray.indexOf(passedLeague)
                if(index != -1){
                    spin.setSelection(index)
                }


                spin.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedItem = parent?.getItemAtPosition(position).toString()

                        if(selectedItem == "Please choose a league"){
                            return
                        }else{
                            getPlayers(selectedItem)
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }


                }
            }

            //buttonGetPlayers.setOnClickListener { getPlayers()  }
    }

    fun getPlayers(league: String){
        val db = FirebaseFirestore.getInstance()
        val playersArray = ArrayList<Player>()
        db.collection("SnookerPlayers")
            .whereEqualTo("league", league)
            .get()
            .addOnSuccessListener { task ->
                for(document in task){
                    val playerName = document["player"].toString()
                    val playerId = document.id
                    val playerHandicap = document["playerHandicap"].toString()
                    val playerLeague = document["league"].toString()
                    val playerTeam = document["team"].toString()

                    val player = Player(playerName)
                    player.playerHandicap = playerHandicap
                    player.playerLeague = playerLeague
                    player.playerTeam = playerTeam
                    player.playerId = playerId
                    playersArray.add(player)
                    playersArray.sortBy {player: Player -> player.playerName }
                }
                //var adapter: PlayerAdapter
                val adapter = PlayerAdapter(this, playersArray)

                searchPlayers.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return false
                    }
                })



                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclePlayers.layoutManager = layoutManager
                recyclePlayers.adapter = adapter
                //recyclePlayers.setOnClickListener { Toast.makeText(this, "you clicked", Toast.LENGTH_LONG).show() }

                }




        //Toast.makeText(this, "selected league is $league", Toast.LENGTH_LONG).show()
    }

    fun editPlayer(player: Player){

    }

    fun cancel(view: View) {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }
}
/*
private fun setOnQueryTextListener() {


    Log.i("queryTextListener", "I'm here")
   // Toast.makeText(context, "inside query listener", Toast.LENGTH_LONG).show()
}

 */
