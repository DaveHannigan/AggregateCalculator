package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
//import android.support.v7.widget.LinearLayoutManager
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aggregatecalculator.R.id.newTeamName
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldPath.documentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.model.Document
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_team.*

class NewTeam : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_team)
        val spinner = findViewById<Spinner>(R.id.spinnerNewTeamDivision)
        ArrayAdapter.createFromResource(
            this,
            R.array.divisions,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }
        val spinnerLeague = findViewById<Spinner>(R.id.spinNewTeamLeague)
        ArrayAdapter.createFromResource(
            this,
            R.array.league,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinnerLeague.adapter = adapter
        }
        val teams = arrayListOf<Player>()
         getTeams(teams)
       // Log.i("list view pop", "taems array is $teams")
      //  val playerId = ArrayList<String>()
        //for(x in teams){
        //    playerId.add(x.playerLeague)
          //  Log.i("NewTeamList", "player name is ${x.playerName}")
      //  }
       // val listView = findViewById<ListView>(R.id.listTeams)
        //val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, playerId)
        //listView.adapter = adapter
        val firstTeam = teams.size
        Log.i("NewTeamList", "first element of array is $firstTeam")

    }



    fun cancelNewTeam(view: View){
        val intent = Intent(this, DashboardActivity ::class.java)
        startActivity(intent)
    }

    fun save(view: View){
        val teamName = findViewById<EditText>(R.id.editTextNewTeamName)
        var teams  = teamName.text.toString()
        teams = standardiseNames(teams)
       // Toast.makeText(this, teams, Toast.LENGTH_LONG)
         //   .show()
       // val teamsArray = arrayListOf<String>()
        var saveTeam = true


        val db = FirebaseFirestore.getInstance()
        db.collection("snookerTeams")
            .get()
            .addOnSuccessListener { task ->
               val teamsArray = arrayListOf<String>()
                for (docList in task) {
                    val team = docList["teamName"].toString()
                    val team1 = standardiseNames(team)
                    teamsArray.add(team1)

                }
                if(teamsArray.contains(teams)){
                    Toast.makeText(this, "That team name already exists", Toast.LENGTH_LONG)
                        .show()
                    saveTeam = false
                }
            }



                if (teams == " ") {
                    Toast.makeText(this, "You have not entered a team name", Toast.LENGTH_LONG)
                        .show()
                    saveTeam = false
                }

        if(saveTeam){
            //var teamDiv = ""
            val db = FirebaseFirestore.getInstance()
            val div = findViewById<Spinner>(R.id.spinnerNewTeamDivision)
            var teamDiv = div.selectedItem.toString()
            val league = findViewById<Spinner>(R.id.spinNewTeamLeague)
            var teamLeague = league.selectedItem.toString()
            div.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
                    val teamDiv = parent.getItemAtPosition(p2).toString()

                    Toast.makeText(this@NewTeam,teamDiv,Toast.LENGTH_LONG).show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }

            val docName = stripSpaces(teams)

            val data = hashMapOf(
                "teamName" to teams,
                "division" to teamDiv,
                "league" to teamLeague)


            db.collection("snookerTeams").document()
                .set(data)
                .addOnSuccessListener { Log.d("in saved info", "Success")

                }
                .addOnFailureListener{e -> Log.w("in saved info", "Failure", e)}




        }

    }

    private fun getTeams(playerArray: ArrayList<Player>){
        //val teamsArray  = ArrayList<Player>()
       // var playerArray = ArrayList<Player>()
        var i = 0
        val db = FirebaseFirestore.getInstance()
        db.collection("SnookerPlayers")
            .whereEqualTo("league", "Big Table Monday")
            .get()
            .addOnSuccessListener { task ->
                for(docList in task){
                    val playerName = docList["player"].toString()
                   val playerId = docList.id
                    val playerHandicap = docList["playerHandicap"].toString()
                    val playerLeague = docList["league"].toString()
                    val playerTeam = docList["team"].toString()
                   Log.i("NewTeamGetTeam", "team is $playerName")
                    val player = Player(playerName)
                    player.playerHandicap = playerHandicap
                    player.playerId = playerId
                    player.playerLeague = playerLeague
                    player.playerTeam = playerTeam
                    playerArray.add(player)
                    Log.i("newteamgetteam", "player array size is ${playerArray[i].playerId}")
                    i++

                }
                var adapter: PlayerAdapter? = null
               adapter = PlayerAdapter(this, playerArray)

               val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
                 //val listView = findViewById<ListView>(R.id.listTeams)
                listTeams.layoutManager = layoutManager
                listTeams.adapter = adapter

                  //val playerId = ArrayList<String>()
                //for(x in playerArray){
                  //  playerId.add(x.playerName)
                  //Log.i("NewTeamList", "player name is ${x.playerName}")
                 // }
               // listView.layoutManager = LinearLayoutManager(this)
               // val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, playerId)
                //listView.adapter = adapter

            }

    }

}
