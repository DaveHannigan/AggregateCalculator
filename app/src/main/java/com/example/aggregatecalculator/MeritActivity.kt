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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aggregatecalculator.databinding.ActivityMeritBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

class MeritActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMeritBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ArrayAdapter.createFromResource(this, R.array.league, android.R.layout.simple_spinner_item)
            .also { arrayAdapter ->
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    binding.spinChooseLeague.adapter = arrayAdapter
            }
        ArrayAdapter.createFromResource(this, R.array.season, android.R.layout.simple_spinner_item)
            .also { arrayAdapter ->
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                binding.spinChooseSeason.adapter = arrayAdapter
            }
        binding.spinChooseLeague.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

               val league = parent?.getItemAtPosition(position).toString()
                if (league == "Choose league"){
                    binding.spinChooseTeam.visibility = View.INVISIBLE
                    binding.spinChooseSeason.visibility = View.INVISIBLE
                }else{
                    binding.spinChooseSeason.visibility = View.VISIBLE
                    binding.spinChooseSeason.setSelection(0)
                }
                binding.spinChooseSeason.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        var season = parent?.getItemAtPosition(position).toString()
                        if (season == "Choose Season"){
                            binding.spinChooseTeam.visibility = View.INVISIBLE
                            return
                        }else{
                            binding.spinChooseTeam.visibility = View.VISIBLE
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


                                    ArrayAdapter(this@MeritActivity, android.R.layout.simple_spinner_item, teamArray)
                                        .also { adapter ->
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                                            binding.spinChooseTeam.adapter = adapter
                                        }
                                    binding.spinChooseTeam.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            parent: AdapterView<*>?,
                                            view: View?,
                                            position: Int,
                                            id: Long
                                        ) {
                                            val team = parent?.getItemAtPosition(position).toString()
                                            val db = FirebaseFirestore.getInstance()

                                            db.collection("SnookerPlayers")
                                                .whereEqualTo("league", league)
                                                .whereEqualTo("team", team)
                                                .get()
                                                .addOnSuccessListener { task ->
                                                    val players = arrayListOf<Player>()
                                                    for (doclist in task) {
                                                        val player =
                                                            Player(doclist["player"].toString().trim())
                                                        player.playerHandicap =
                                                            doclist["playerHandicap"].toString()
                                                        players.add(player)
                                                    }

                                                        val results = arrayListOf<meritResults>()
                                                            var gameCount = 0

                                                        db.collection("snookerResults")
                                                            .whereEqualTo("awayTeam", team)
                                                            .whereEqualTo("season", season)
                                                            .get()
                                                            .addOnSuccessListener { task ->
                                                                for (doclist in task) {
                                                                    if (doclist["home player 1"] == null) {
                                                                        continue
                                                                    }
                                                                    val result =
                                                                        arrayListOf<String>()
                                                                    for (i in 1..8) {
                                                                        gameCount++
                                                                        val result1 =
                                                                            resultSplit(doclist["home player $i"].toString())
                                                                        val result2 =
                                                                            resultSplit(doclist["away player $i"].toString())
                                                                        val result3 = meritResults(
                                                                            gameCount,
                                                                            0,
                                                                            result1[0],
                                                                            result1[1].toInt(),
                                                                            result1[2].toInt(),
                                                                            result2[0],
                                                                            result2[1].toInt(),
                                                                            result2[2].toInt()
                                                                        )
                                                                        results.add(result3)
                                                                    }

                                                                }
                                                                db.collection("snookerResults")
                                                                    .whereEqualTo("homeTeam", team)
                                                                    .whereEqualTo("season", season)
                                                                    .get()
                                                                    .addOnSuccessListener { task ->
                                                                        for (doclist in task) {
                                                                            if (doclist["home player 1"] == null) {
                                                                                continue
                                                                            }
                                                                            val result =
                                                                                arrayListOf<String>()
                                                                            for (i in 1..8) {
                                                                                gameCount++
                                                                                val result1 =
                                                                                    resultSplit(
                                                                                        doclist["home player $i"].toString()
                                                                                    )
                                                                                val result2 =
                                                                                    resultSplit(
                                                                                        doclist["away player $i"].toString()
                                                                                    )
                                                                                val result3 =
                                                                                    meritResults(
                                                                                        gameCount,
                                                                                        0,
                                                                                        result1[0],
                                                                                        result1[1].toInt(),
                                                                                        result1[2].toInt(),
                                                                                        result2[0],
                                                                                        result2[1].toInt(),
                                                                                        result2[2].toInt()
                                                                                    )
                                                                                results.add(result3)
                                                                            }
                                                                        }
                                                                        for (player in players) {
                                                                            for (result in results) {
                                                                                if (result.player1 == player.playerName || result.player2 == player.playerName) {
                                                                                    player.gamesPlayed++
                                                                                    if (result.player1 == player.playerName) {
                                                                                        if (result.player1Score > result.player2Score) {
                                                                                            player.gamesWon++
                                                                                        }
                                                                                    }
                                                                                    if (result.player2 == player.playerName) {
                                                                                        if (result.player2Score > result.player1Score) {
                                                                                            player.gamesWon++

                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        val sortedPlayers =
                                                                            players.sortedWith(
                                                                                compareByDescending<Player> { it.gamesWon }.thenByDescending { it.winPercent() })

                                                                        val adapter = MeritAdapter(
                                                                            this@MeritActivity,
                                                                            sortedPlayers
                                                                        )
                                                                        val layoutManager =
                                                                            LinearLayoutManager(
                                                                                this@MeritActivity,
                                                                                LinearLayoutManager.VERTICAL,
                                                                                false
                                                                            )
                                                                        binding.recyclerViewMerit.layoutManager =
                                                                            layoutManager
                                                                        binding.recyclerViewMerit.adapter =
                                                                            adapter
                                                                    }
                                                            }

                                                }
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>?) {

                                        }
                                    }
                                }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }
}

fun resultSplit( resultArray: String): ArrayList<String>{
    val list = resultArray.split(",")
    val list2 = arrayListOf<String>()
    for(i in list) {
        var j = i.replace("[", "")
        j = j.replace("]", "")
        j = j.trim()
        list2.add(j)
    }
    return list2
}

suspend fun getTeams(league: String):ArrayList<String>{
    val teams = arrayListOf<String>()
    val db = FirebaseFirestore.getInstance()
    db.collection("snookerTeams")
        .whereEqualTo("league", league)
        .get()
        .addOnSuccessListener { task ->
            for (doclist in task){
                val team = doclist["teamName"].toString()
                teams.add(team)
            }
            teams.sort()
            teams.add(0, "Choose team")
        }
    return teams
}

class MeritAdapter(context: Context, players: List<Player> ): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var context: Context = context
    var player: List<Player> = players

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val player1 = player[position]
        val ph = holder as PlayerHolder


        ph.pl_name.text = player1.playerName
        ph.pl_handicap.text = player1.playerHandicap
        ph.pl_gamesPlayed.text = player1.gamesPlayed.toString()
        ph.pl_gamesWon.text = player1.gamesWon.toString()
        ph.pl_gamesLost.text = player1.gamesLost().toString()
        ph.pl_percentageWin.text = player1.winPercent()
        if (position % 2 == 1){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGreen))
        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBeige))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return PlayerHolder(inflater.inflate(R.layout.merit_list, parent, false))
    }

    override fun getItemCount(): Int {
        return player.size
    }

    internal class PlayerHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var pl_name: TextView
        var pl_handicap: TextView
        var pl_gamesPlayed: TextView
        var pl_gamesWon: TextView
        var pl_gamesLost: TextView
        var pl_percentageWin: TextView

        init {
            pl_name = itemView.findViewById(R.id.playerName)
            pl_handicap = itemView.findViewById(R.id.playerHandicap)
            pl_gamesPlayed = itemView.findViewById(R.id.playerGamesPlayed)
            pl_gamesWon = itemView.findViewById(R.id.playerGamesWon)
            pl_gamesLost = itemView.findViewById(R.id.playerGamesLost)
            pl_percentageWin = itemView.findViewById(R.id.playerWinPercentage)
        }

    }

}