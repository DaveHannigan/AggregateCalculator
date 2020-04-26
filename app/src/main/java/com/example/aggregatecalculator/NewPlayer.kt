package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.BundleCompat
import com.google.firebase.firestore.FirebaseFirestore
import io.grpc.internal.SharedResourceHolder

class NewPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_player)
        val TAG = "NewPlayerClass"

        val saveButton = findViewById<Button>(R.id.buttonSave)


        val name = intent.getStringExtra("name")
        val handicap = intent.getStringExtra("handicap")
        val league = intent.getStringExtra("league")
        val team2 = intent.getStringExtra("team")
        val id = intent.getStringExtra("id")
        saveButton.setOnClickListener { save(id!!) }
        Log.i(TAG, "player name is $handicap")

        if(name != null || name != ""){
            val editName = findViewById<EditText>(R.id.editTextNewPlayer)
            val editHandicap = findViewById<EditText>(R.id.editTextPlayerHandicap)
            val spinLeague = findViewById<Spinner>(R.id.spinChooseLeague)
            val spinTeam = findViewById<Spinner>(R.id.spinChooseTeam)
            editName.setText(name)
            editHandicap.setText(handicap!!.toString())

        }


        val spinnerLeague = findViewById<Spinner>(R.id.spinChooseLeague)
        ArrayAdapter.createFromResource(
            this,
            R.array.league,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinnerLeague.adapter = adapter
            val leagueArray  = resources.getStringArray(R.array.league)
            val index = leagueArray.indexOf(league)
            if(index != -1){
                spinnerLeague.setSelection(index)}

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
                                val index = teamArray.indexOf(team2)
                                if(index != -1){
                                    teamSpinner.setSelection(index)
                                }
                            }


                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun save(playerId: String){

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
        if(playerId != "" ){
            db.collection("SnookerPlayers").document(playerId).set(data)
                .addOnSuccessListener {
                    val intent = Intent(this, EditPlayers::class.java)
                    intent.putExtra("league", league)
                    startActivity(intent)
                }

        }else {
            db.collection("SnookerPlayers").document().set(data)
                .addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
        }

        Toast.makeText(this, "New player is "+ playerstrip  + " and their handicap is " + handicap, Toast.LENGTH_LONG).show()
    }

    fun cancel(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
