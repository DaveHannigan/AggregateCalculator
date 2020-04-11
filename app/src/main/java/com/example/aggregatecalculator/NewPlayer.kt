package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore

class NewPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_player)


        val spinnerLeague = findViewById<Spinner>(R.id.spinChooseLeague)
        ArrayAdapter.createFromResource(
            this,
            R.array.league,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinnerLeague.adapter = adapter
        }

        spinnerLeague.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>,p1: View?, p2: Int,p3: Long){
                val team = parent.getItemAtPosition(p2).toString()

                val db = FirebaseFirestore.getInstance()
                db.collection("snookerTeams")
                    .whereEqualTo("league", team)
                    .get()
                    .addOnSuccessListener{ task ->
                        val teamArray = arrayListOf<String>()
                        for(docList in task){
                            val team = docList["teamName"].toString()
                            teamArray.add(team)
                        }

                        val teamSpinner = findViewById<Spinner>(R.id.spinChooseTeam)
                        ArrayAdapter(this@NewPlayer, android.R.layout.simple_spinner_item, teamArray)
                            .also{
                                adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                                teamSpinner.adapter = adapter
                            }


                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun save(view: View){

        val player = findViewById<EditText>(R.id.editTextNewPlayer).text.toString()
        val handicap = findViewById<EditText>(R.id.editTextPlayerHandicap).text.toString()
        val league = findViewById<Spinner>(R.id.spinChooseLeague).selectedItem.toString()
        val team = findViewById<Spinner>(R.id.spinChooseTeam).selectedItem.toString()

        val newPlayer = standardiseNames(player)
        val playerstrip = stripSpaces(player)

        val player1 = Player(newPlayer)
        player1.playerHandicap = handicap


        val db = FirebaseFirestore.getInstance()

        val data = hashMapOf(
            "player" to player1.playerName,
            "playerHandicap" to player1.playerHandicap,
            "league" to league,
            "team" to team
        )

        db.collection("SnookerPlayers").document().set(data)
            .addOnSuccessListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        Toast.makeText(this, "New player is "+ playerstrip  + " and their handicap is " + handicap, Toast.LENGTH_LONG).show()
    }

    fun cancel(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
