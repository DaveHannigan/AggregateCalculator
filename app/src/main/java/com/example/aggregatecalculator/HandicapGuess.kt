package com.example.aggregatecalculator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aggregatecalculator.databinding.ActivityHandicapGuessBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar.*
import kotlin.concurrent.thread
import kotlin.math.log

class HandicapGuess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHandicapGuessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var teamArray: List<DocumentSnapshot>
        val spinner = binding.spinChooseTeam
        val context = this
        getTeams1(context, spinner)

        binding.buttonCancel.setOnClickListener { finish() }

        binding.spinChooseTeam.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val team = parent?.getItemAtPosition(position).toString()
                if (team == "Choose Team"){return}
        Log.i("handicapGuess", "after run blocking $team")

                getNewHcap(this@HandicapGuess, binding, team)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }





    }
}
/*
suspend fun updateSpinner(context: Context, spinner: Spinner) {

    val deferred = getTeams1()
    val teamsArray = arrayListOf<String>()
    for (docList in deferred) {
        val teams = docList["teamName"].toString()
        teamsArray.add(teams)
    }


    teamsArray.sort()
    teamsArray.add(0, "Choose Team")

    ArrayAdapter(context, android.R.layout.simple_spinner_item, teamsArray)
        .also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter


        }
}

 */
fun getNewHcap(context: Context, binding: ActivityHandicapGuessBinding, team: String){
    val players = arrayListOf<Player>()
    val db = FirebaseFirestore.getInstance()
    db.collection("SnookerPlayers")
        .whereEqualTo("league", "Big Table Monday")
        .whereEqualTo("team", team)
        .get()
        .addOnSuccessListener { task ->
            for (doclist in task) {
                val player = Player(doclist["player"].toString().trim())
                player.playerHandicap = doclist["playerHandicap"].toString()
                players.add(player)
            }
            val dates = hcapMinMaxDate()
            val startDate = Timestamp(dates[0])
            val endDate = Timestamp(dates[1])

            val results = arrayListOf<meritResults>()
            var teamGamesCount = 0
            var playerGamesCount = 0

            val db = FirebaseFirestore.getInstance()
            db.collection("snookerResults")
                .whereEqualTo("homeTeam", team)
                .whereGreaterThan("date", startDate)
                .whereLessThan("date", endDate)
                .get()
                .addOnSuccessListener { task ->
                    for (docList in task) {
                        //teamGamesCount++
                        if (docList["home player 1"] == null) {
                            continue
                        }
                        for (i in 1..8) {
                            teamGamesCount++
                            val result1 = resultSplit(docList["home player $i"].toString())
                            val result2 = resultSplit(docList["away player $i"].toString())
                            val result3 = meritResults(
                                teamGamesCount, 0, result1[0],
                                result1[1].toInt(), result1[2].toInt(), result2[0],
                                result2[1].toInt(), result2[2].toInt()
                            )
                            results.add(result3)
                            Log.i(
                                "merit2", "result added is $result3 " +
                                        "and game was between ${result3.player1} and ${result3.player2}"
                            )
                        }
                        //val date = docList["date"].toString()

                        //Log.i("handicapresults", "date is $date")
                    }


                    // }
                    db.collection("snookerResults")
                        .whereEqualTo("awayTeam", team)
                        .whereGreaterThan("date", startDate)
                        .whereLessThan("date", endDate)
                        .get()
                        .addOnSuccessListener { task ->
                            for (docList in task) {
                                if (docList["home player 1"] == null) {
                                    continue
                                }
                                for (i in 1..8) {
                                    teamGamesCount++
                                    val result1 = resultSplit(docList["home player $i"].toString())
                                    val result2 = resultSplit(docList["away player $i"].toString())
                                    val result3 = meritResults(
                                        teamGamesCount, 0, result1[0],
                                        result1[1].toInt(), result1[2].toInt(), result2[0],
                                        result2[1].toInt(), result2[2].toInt()
                                    )
                                    results.add(result3)
                                }
                            }
                            for (player in players) {
                                //Log.i("handicap", "player is $player")
                                for (result in results) {
                                    //Log.i("handicap", "result is ${result.player1}")
                                    if (result.player1 == player.playerName) {
                                        player.gamesPlayed++
                                        //Log.i("handicap", " ${player.playerName}")
                                        player.aggregateTotalfor += result.player1Score
                                        // Log.i("handicap", "result score ${result.player1Score}")
                                        player.aggregateTotalAgainst += result.player2Score
                                        if (result.player1Score - result.player2Score > 7 || result.player1Score - result.player2Score < -7) {
                                            Log.i(
                                                "handicap player 1",
                                                "${player.playerName}, ${result.player1Score}, ${result.player2Score}"
                                            )
                                            if (result.player1Score - result.player2Score < 0) {
                                                player.aggregateHandicap += (result.player1Score - result.player2Score) + 7
                                            } else {
                                                player.aggregateHandicap += (result.player1Score - result.player2Score) - 7
                                            }
                                        }
                                    }
                                    if (result.player2 == player.playerName) {
                                        player.gamesPlayed++
                                        player.aggregateTotalfor += result.player2Score
                                        player.aggregateTotalAgainst += result.player1Score
                                        if (result.player2Score - result.player1Score > 7 || result.player2Score - result.player1Score < -7) {
                                            Log.i(
                                                "handicap player 2",
                                                "${player.playerName}, ${result.player2Score}, ${result.player1Score}"
                                            )
                                            if (result.player2Score - result.player1Score < 0) {
                                                player.aggregateHandicap += (result.player2Score - result.player1Score) + 7
                                            } else {
                                                player.aggregateHandicap += (result.player2Score - result.player1Score) - 7
                                            }
                                        }


                                    }
                                }
                            }
                            // val context = Context
                            players.sortBy { player: Player -> player.playerHandicap.toInt() }
                            Thread.sleep(500)
                            val adapter = HandicapAdapter(context, players)
                            val layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            binding.recyclerHandicap.layoutManager = layoutManager
                            binding.recyclerHandicap.adapter = adapter

                        }
                }

        }
}

fun hcapMinMaxDate(): List<java.util.Date>{
    val cal = getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy")
    val year = cal.get(YEAR)
    var start: String
    var end: String

    if (year == 2020){
        val cutOff = "01-10-2020"
        var start1 = ""
        var end1 = ""
        if (cal.get(MONTH) < 10){
            start1 = "01-01-2020"
            end1 = "30-09-2020"
        }else{
            start1 = "01-10-2020"
            end1 = "31-12-2020"
        }
        val startTimestamp1 = dateFormat.parse(start1)
        val endTimestamp1 = dateFormat.parse(end1)
        return listOf(startTimestamp1, endTimestamp1)

        Log.i("hcapm20", "cut off date is $cutOff")
    }else{
        val cutOff = "01-06-${year}"
        if (cal.get(MONTH)< 6){
            start = "01-01-$year"
            end = "31-05-$year"
        }else{
            start = "01-07-$year"
            end = "31-12-$year"
        }
        val startTimestamp = dateFormat.parse(start)
        val endTimestamp = dateFormat.parse(end)
        val minMaxList = listOf(startTimestamp, endTimestamp)
        Log.i("hcapm20", "cut off date is $cutOff")
        return minMaxList
    }
    return emptyList()
}


 fun getTeams1(context: Context, spinner: Spinner) {
    val teamsArray = arrayListOf<String>()
    val db = FirebaseFirestore.getInstance()
    db.collection("snookerTeams")
        .whereEqualTo("league", "Big Table Monday")
        .get()

        .addOnSuccessListener { task ->
            for (docList in task) {
                val teams = docList["teamName"].toString()
                teamsArray.add(teams)
            }


            teamsArray.sort()
            teamsArray.add(0, "Choose Team")

            ArrayAdapter(context, android.R.layout.simple_spinner_item, teamsArray)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    spinner.adapter = adapter

                }
        }






}

class HandicapAdapter(context: Context, players: List<Player>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var context: Context = context
    var player: List<Player> = players

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val player1 = player[position]
        val ph = holder as PlayerHolder
        val newH = player1.newHandicap().toString()

        ph.pl_name.text = player1.playerName
        ph.pl_gamesPlayed.text = player1.gamesPlayed.toString()
        ph.pl_handicap.text = player1.playerHandicap
        ph.pl_handicapChange.text = player1.handicapChange().toString()
        ph.pl_newHandicap.text = newH
        if (position % 2 == 1){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGreen))
        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBeige))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return PlayerHolder(inflater.inflate(R.layout.handicap_list, parent, false))
    }

    override fun getItemCount(): Int {
        return player.size
    }

    internal class PlayerHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var pl_name: TextView
        var pl_handicap: TextView
        var pl_handicapChange: TextView
        var pl_newHandicap: TextView
        var pl_gamesPlayed: TextView

        init {
            pl_name = itemView.findViewById(R.id.playerName)
            pl_gamesPlayed = itemView.findViewById(R.id.gamesPlayed)
            pl_handicap = itemView.findViewById(R.id.existingHandicap)
            pl_handicapChange = itemView.findViewById(R.id.handicapChange)
            pl_newHandicap = itemView.findViewById(R.id.newHandicap)
        }
    }


}