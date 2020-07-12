package com.example.aggregatecalculator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.aggregatecalculator.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_results.*
import java.util.ArrayList

class EditResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val result = intent.getParcelableExtra<ResultClass>("result")!!
        val id = result.id
        binding.textLeague.text = result.league
        binding.textDivision.text = result.division
        binding.textMatchDate.text = getDate(result.matchDate)
        binding.awayTeam.text = "Away team"
        binding.homeTeam.text = "Home team"
        binding.spinHomeTeam.text = result.homeTeam
        binding.spinAwayTeam.text = result.awayTeam
        binding.editHomePlayer1Handicap.setText(result.homePlayer1H)
        binding.homeScore1.setText(result.homePlayer1S)
        binding.editHomePlayer2Handicap.setText(result.homePlayer2H)
        binding.homeScore2.setText(result.homePlayer2S)
        binding.editHomePlayer3Handicap.setText(result.homePlayer3H)
        binding.homeScore3.setText(result.homePlayer3S)
        binding.editHomePlayer4Handicap.setText(result.homePlayer4H)
        binding.homeScore4.setText(result.homePlayer4S)
        binding.editHomePlayer5Handicap.setText(result.homePlayer5H)
        binding.homeScore5.setText(result.homePlayer5S)
        binding.editHomePlayer6Handicap.setText(result.homePlayer6H)
        binding.homeScore6.setText(result.homePlayer6S)
        binding.editHomePlayer7Handicap.setText(result.homePlayer7H)
        binding.homeScore7.setText(result.homePlayer7S)
        binding.editHomePlayer8Handicap.setText(result.homePlayer8H)
        binding.homeScore8.setText(result.homePlayer8S)

        binding.editAwayPlayer1Handicap.setText(result.awayPlayer1H)
        binding.awayScore1.setText(result.awayPlayer1S)
        binding.editAwayPlayer2Handicap.setText(result.awayPlayer2H)
        binding.awayScore2.setText(result.awayPlayer2S)
        binding.editAwayPlayer3Handicap.setText(result.awayPlayer3H)
        binding.awayScore3.setText(result.awayPlayer3S)
        binding.editAwayPlayer4Handicap.setText(result.awayPlayer4H)
        binding.awayScore4.setText(result.awayPlayer4S)
        binding.editAwayPlayer5Handicap.setText(result.awayPlayer5H)
        binding.awayScore5.setText(result.awayPlayer5S)
        binding.editAwayPlayer6Handicap.setText(result.awayPlayer6H)
        binding.awayScore6.setText(result.awayPlayer6S)
        binding.editAwayPlayer7Handicap.setText(result.awayPlayer7H)
        binding.awayScore7.setText(result.awayPlayer7S)
        binding.editAwayPlayer8Handicap.setText(result.awayPlayer8H)
        binding.awayScore8.setText(result.awayPlayer8S)

        val homePlayerSpinners = arrayListOf<Spinner>(binding.spinHomePlayer1, binding.spinHomePlayer2,
        binding.spinHomePlayer3, binding.spinHomePlayer4, binding.spinHomePlayer5, binding.spinHomePlayer6,
        binding.spinHomePlayer7, binding.spinHomePlayer8  )
        val homePlayers = arrayListOf<String>(result.homePlayer1, result.homePlayer2, result.homePlayer3,
            result.homePlayer4, result.homePlayer5, result.homePlayer6, result.homePlayer7, result.homePlayer8)
        getPlayers(result.league, result.homeTeam, homePlayerSpinners,homePlayers)
        val awayPlayerSpinners = arrayListOf<Spinner>(binding.spinAwayPlayer1, binding.spinAwayPlayer2,
            binding.spinAwayPlayer3, binding.spinAwayPlayer4, binding.spinAwayPlayer5, binding.spinAwayPlayer6,
            binding.spinAwayPlayer7, binding.spinAwayPlayer8)
        val awayPlayers = arrayListOf<String>(result.awayPlayer1, result.awayPlayer2, result.awayPlayer3,
            result.awayPlayer4, result.awayPlayer5, result.awayPlayer6, result.awayPlayer7, result.awayPlayer8)
        getPlayers(result.league, result.awayTeam, awayPlayerSpinners, awayPlayers)
        val context = this

        binding.textMatchDate.setOnClickListener{ pickDate(binding, context) }

        binding.buttonCancel?.setOnClickListener { finish() }

        binding.buttonSave.setOnClickListener {
            if (saveResults(binding, id)){
                finish()
            } }

        setScoresListener(binding)
        frameScore(binding)
        matchScore(binding)

    }

  //  fun saveResult(){

    //}

    fun getPlayers(league: String, team: String, playerSpinners: ArrayList<Spinner>,
                   players1: ArrayList<String>){
        val db = FirebaseFirestore.getInstance()
        val players = arrayListOf<String>()

        db.collection("SnookerPlayers")
            .whereEqualTo("league", league)
            .whereEqualTo("team", team)
            .get()
            .addOnSuccessListener { task ->
                for (doclist in task){
                    players.add(doclist["player"].toString().trim())
                }
                ArrayAdapter(this, R.layout.custom_spinner_item, players)
                    .also { adapter ->
                        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
                        var count = 0
                        for (x in playerSpinners){
                            x.adapter = adapter
                            val index = players.indexOf(players1[count])
                            x.setSelection(index)
                            count++
                        }
                    }
            }

    }

    fun setScoresListener(binding: ActivityMainBinding){

            binding.ourAgg.text = getAggregate(binding, "home")
            for (x in getHomeTeamScoreViews(binding)) {
                x.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                        binding.ourAgg.text = getAggregate(binding, "home")
                        frameScore(binding)
                        matchScore(binding)
                        //return aggregate.toString()

                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        // TODO("Not yet implemented")
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        // TODO("Not yet implemented")
                    }
                })
            }
        binding.theirAgg.text = getAggregate(binding, "away")
        for (x in getAwayTeamScoreViews(binding)){
            x.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                    binding.theirAgg.text = getAggregate(binding, "away")
                    frameScore(binding)
                    matchScore(binding)
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //TODO("Not yet implemented")
                }
            })
        }
    }

    fun getAggregate(binding: ActivityMainBinding, venue: String): String {
        var total = 0
        if (venue == "home") {
            val scores = getHomeTeamScoreViews(binding)
            for (x in scores) {
                if (x.text.toString() == ""){
                    x.setText("0")
                }else {
                    total += x.text.toString().toInt()
                }
            }
            return total.toString()
        }else{
            for (x in getAwayTeamScoreViews(binding)){
                if (x.text.toString() == "") {
                    x.setText("0")
                    total += 0
                }else{
                    total += x.text.toString().toInt()

                }
            }
            return total.toString()
        }
    }

    fun frameScore(binding: ActivityMainBinding){
        var homeScore = 0
        var awayScore = 0

        for (x in 0..7){
            if (getHomeTeamScoreViews(binding)[x].text.toString().toInt() >
                    getAwayTeamScoreViews(binding)[x].text.toString().toInt()){
                homeScore ++
            }else{
                awayScore ++
            }
        }
        binding.ourGamesWon.text = homeScore.toString()
        binding.theirGamesWon.text = awayScore.toString()
    }

    fun matchScore(binding: ActivityMainBinding){

        if (binding.ourAgg.text.toString().toInt() >
                    binding.theirAgg.text.toString().toInt()){
              var aggPoints = binding.ourGamesWon.text.toString().toInt() + 2

            binding.ourMatchScore.text = aggPoints.toString()
            binding.theirMatchScore.text = binding.theirGamesWon.text
        }else{
            var aggPoints = binding.theirGamesWon.text.toString().toInt() + 2
            binding.theirMatchScore.text = aggPoints.toString()
            binding.ourMatchScore.text = binding.ourGamesWon.text
        }
    }
}
