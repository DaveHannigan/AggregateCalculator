package com.example.aggregatecalculator

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import androidx.core.text.toSpanned
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.aggregatecalculator.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class MainActivity : AppCompatActivity(), NewPlayerFragment.onNewPlayer {


    override fun newPlayer(player: String, team: String, playerArray: ArrayList<String>,
                           callingSpinner: Int, playerObject: ArrayList<Player>) {
        resetSpinner(player, team, playerArray, callingSpinner, playerObject)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
             fun onSaveInstanceState(
                outState: Bundle,
                outPersistentState: PersistableBundle
            ) {
                super.onSaveInstanceState(outState, outPersistentState)
            }

        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


//            updateAggregate(findViewById<EditText>(R.id.constLayout))
            val passedData = intent.extras
            var league = passedData?.getString("league")
            var division = passedData?.getString("division")
            var homeTeam = passedData?.getString("homeTeam")
            var awayTeam = passedData?.getString("awayTeam")
            var matchDate1 = passedData?.getString("matchDate")
            val matchId = passedData?.getString("matchId")

            if (homeTeam == null && awayTeam == null && matchId != null){
                Toast.makeText(this, "match id is $matchId", Toast.LENGTH_SHORT).show()
                getdetails(binding, matchId)
                //delay(1000)
                league = binding.textLeague.text.toString()
                homeTeam = binding.spinHomeTeam.text.toString()
                awayTeam = binding.spinAwayTeam.text.toString()
                division = binding.textDivision.text.toString()
            }


            if(league == null || division == null || homeTeam == null || awayTeam == null){
                Toast.makeText(this, "Please make sure that all enteries are valid", Toast.LENGTH_LONG).show()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                return
            }

            binding.buttonCancel?.setOnClickListener { this.finish() }
            binding.buttonSave.setOnClickListener { saveInfo(binding, matchId!!) }
            val homePlayersArray = arrayListOf<String>()
            val homePlayerObject = arrayListOf<Player>()
            val awayPlayersArray = arrayListOf<String>()
            val awayPlayerObject = arrayListOf<Player>()
            getPlayers(awayTeam!!, league!!, awayPlayerObject, awayPlayersArray, awayTeamSpinners())
            getPlayers(homeTeam!!, league, homePlayerObject, homePlayersArray, homeTeamSpinners())
            setListener(homeTeamSpinners(), league, homeTeam, division!!, homePlayersArray, homeHandicapTexts(), homePlayerObject, homeTeamSpinners())
            setListener(awayTeamSpinners(), league, awayTeam, division, awayPlayersArray, awayHandicapTexts(), awayPlayerObject, awayTeamSpinners() )





            val leagueText = findViewById<TextView>(R.id.textLeague)
            leagueText.text = league
            val divisionText = findViewById<TextView>(R.id.textDivision)
            divisionText.text = division
            val matchDate = findViewById<TextView>(R.id.textMatchDate)
            matchDate.text = matchDate1//getDate()
            matchDate.setOnClickListener { pickDate() }
            val homeTeamText = findViewById<TextView>(R.id.spinHomeTeam)
            homeTeamText.text = homeTeam
            val awayTeamText = findViewById<TextView>(R.id.spinAwayTeam)
            awayTeamText.text = awayTeam



        val editText1 = findViewById<EditText>(R.id.homeScore1)
            editText1.requestFocus()
            val scoreTexts = scoreEditTexts()
            for(x in scoreTexts) {
                x.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {

                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(P0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        focusChanged(false)
                    }
                })
            }


    }

    fun getdetails(binding: ActivityMainBinding, matchId: String){
        val db = FirebaseFirestore.getInstance()

        db.collection("snookerResults").document(matchId)
            .get()
            .addOnSuccessListener { task ->
                binding.textLeague.text = task["league"].toString()
                binding.textDivision.text = task["division"].toString()
                binding.spinHomeTeam.text = task["homeTeam"].toString()
                binding.spinAwayTeam.text = task["awayTeam"].toString()
                Log.i("getdet", "${task["league"].toString()}, ${task["awayTeam"].toString()}," +
                        "${task["homeTeam"].toString()},${task["division"].toString()}")
            }

    }

    fun setListener(spinners: Array<Spinner>, league: String, team: String, division: String,
                    playerArray: ArrayList<String>, editTexts: Array<EditText>, playerObject: ArrayList<Player>,
    teamSpinners: Array<Spinner>){
      for(player in spinners) {
          player.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
              override fun onItemSelected(
                  parent: AdapterView<*>?,
                  view: View?,
                  position: Int,
                  id: Long
              ) {
                  val selectedItem = parent?.getItemAtPosition(position)
                  if (selectedItem == "Choose player"){ return}
                  if(selectedItem == "New Player"){
                      val spinnerId = parent.id
                      getNewPlayer(league, team, division, playerArray, spinnerId, playerObject)
                  }else{
                      val spin = parent?.id
                      var spinIndex = 0
                      for(x in spinners){
                         // Log.i("set listener", "in for x is ${x.id} and spin is $spin and spin index is $spinIndex")
                          if(x.id == spin){break }
                          spinIndex +=1

                      }


                      val handicap = playerObject[id.toInt()-1].playerHandicap
                      editTexts[spinIndex].setText(handicap)
                  }
              }

              override fun onNothingSelected(parent: AdapterView<*>?) {
                  TODO("Not yet implemented")
              }
          }
      }
    }

    fun resetSpinner(player: String, team: String, playerArray: ArrayList<String>, callingSpinner: Int, playerObject: ArrayList<Player>){
        val homeTeam = findViewById<TextView>(R.id.spinHomeTeam)
        val awayTeam = findViewById<TextView>(R.id.awayTeam)

        var spinners = homeTeamSpinners()


        if(homeTeam.text.toString() != team){

             spinners = awayTeamSpinners()
        }
        var spinnerIndex = 0

        for(x in spinners){
            if(x.id == callingSpinner){break}
            spinnerIndex += 1
        }

        val index = playerArray.indexOf(player)
        Log.i("in reset spinners", "spinner index is:- $spinnerIndex and the player index is $index so")

      ArrayAdapter(this, R.layout.custom_spinner_item,playerArray)
            .also { adapter ->
               adapter.setDropDownViewResource(R.layout.custom_spinner_item)
                for(x in spinners) {
                    spinners[spinnerIndex].adapter = adapter
                }
            }


        spinners[spinnerIndex].setSelection(index)
    }



    fun pickDate(){
        val cal = Calendar.getInstance()
Log.i("datePicker", "Your here")
        val dateSetListener = object : DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd-MM-YYYY")

                val date = dateFormat.format(cal.time)

                val dateText = findViewById<TextView>(R.id.textMatchDate)

                dateText.text = date
            }
        }

        DatePickerDialog(this@MainActivity, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()




    }

    fun setDate(view: View, year: Int, month: Int, day: Int){

    }

    fun getDate():String{

        val cal = Calendar.getInstance()
        val dateFormat =  SimpleDateFormat("dd-MM-yyyy")
        val date = dateFormat.format(cal.time)

        return date
    }

    fun getPlayers(team: String, league: String, playerOb: ArrayList<Player>, players: ArrayList<String>, spinners: Array<Spinner>){
        Log.i("MainAct", "In get Players")
        val db = FirebaseFirestore.getInstance()

        db.collection("SnookerPlayers")
            .whereEqualTo("team", team)
            .whereEqualTo("league", league)
            .get()
            .addOnSuccessListener { task ->
                for (doclist in task){
                    val player = Player(doclist["player"].toString())
                    player.playerHandicap = doclist["playerHandicap"].toString()
                    playerOb.add(player)
                    Log.i("Get players function", "players should now be loaded")
                }
               if(playerOb.isNotEmpty()){
                playerOb.sortBy {player ->   player.playerHandicap.toInt() }
                val testPlayer = playerOb[0].playerName
                Log.i("Get player", "player at position 0 is $testPlayer")
                for(x in playerOb) {
                    players.add(x.playerName)
                    }
                }

                players.add(0,"Choose player")
                players.add("New Player")

                ArrayAdapter(this, R.layout.custom_spinner_item, players )
                    .also { adapter ->
                        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
                        for(spinner in spinners) {
                            spinner.adapter = adapter
                            //spinner.setSelection(0, false)
                            //spinner.prompt = "Choose player"
                        }
                    }
            }
    }





    fun getNewPlayer (league: String, team: String, division: String, playerArray: ArrayList<String>, spinnerId: Int, playerObject: ArrayList<Player>){
        val b = Bundle()
        b.putString("league", league)
        b.putString("division",division)
        b.putString("team", team)
        b.putStringArrayList("array", playerArray)
        b.putInt("calling spinner", spinnerId)
        b.putParcelableArrayList("player object", playerObject)
        val dialog = NewPlayerFragment()
        val trans = supportFragmentManager
        dialog.arguments = b
        dialog.show(trans, "New Player")
    }




    fun saveInfo(binding: ActivityMainBinding, matchId: String) {

        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = findViewById<TextView>(R.id.textMatchDate).text.toString()
        val timeStamp = dateFormat.parse(date)
        val season = getSeason(date)

        val data = hashMapOf(
        "date" to timeStamp,
        "season" to season,
        "league" to  findViewById<TextView>(R.id.textLeague).text.toString(),
        "division" to findViewById<TextView>(R.id.textDivision).text.toString(),
        "homeTeam" to findViewById<TextView>(R.id.spinHomeTeam).text.toString(),
        "awayTeam" to findViewById<TextView>(R.id.spinAwayTeam).text.toString(),
        "home team score" to findViewById<TextView>(R.id.ourMatchScore).text.toString(),
        "away team score" to findViewById<TextView>(R.id.theirMatchScore).text.toString(),
        "home aggregate" to binding.ourAgg.text.toString(),
        "away aggregate" to binding.theirAgg.text.toString(),
        "home frame score" to binding.ourGamesWon.text.toString(),
        "away frame score" to binding.theirGamesWon.text.toString(),
        "home player 1" to arrayListOf(findViewById<Spinner>(R.id.spinHomePlayer1).selectedItem.toString(),
            if (binding.editHomePlayer1Handicap.text.toString() == ""){
                0
            }else{
            findViewById<EditText>(R.id.editHomePlayer1Handicap).text.toString().toInt()},
            if (binding.homeScore1.text.toString() == ""){
                0
            }else{
            findViewById<EditText>(R.id.homeScore1).text.toString().toInt()}),
        "home player 2" to arrayListOf(findViewById<Spinner>(R.id.spinHomePlayer2).selectedItem.toString(),
              findViewById<EditText>(R.id.editHomePlayer2Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.homeScore2).text.toString().toInt()),
        "home player 3" to arrayListOf(findViewById<Spinner>(R.id.spinHomePlayer3).selectedItem.toString(),
            findViewById<EditText>(R.id.editHomePlayer3Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.homeScore3).text.toString().toInt()),
         "home player 4" to arrayListOf(findViewById<Spinner>(R.id.spinHomePlayer4).selectedItem.toString(),
             findViewById<EditText>(R.id.editHomePlayer4Handicap).text.toString().toInt(),
             findViewById<EditText>(R.id.homeScore4).text.toString().toInt()),
        "home player 5" to  arrayListOf(findViewById<Spinner>(R.id.spinHomePlayer5).selectedItem.toString(),
            findViewById<EditText>(R.id.editHomePlayer5Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.homeScore5).text.toString().toInt()),
        "home player 6" to arrayListOf(findViewById<Spinner>(R.id.spinHomePlayer6).selectedItem.toString(),
            findViewById<EditText>(R.id.editHomePlayer6Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.homeScore6).text.toString().toInt()),
        "home player 7" to arrayListOf(findViewById<Spinner>(R.id.spinHomePlayer7).selectedItem.toString(),
                findViewById<EditText>(R.id.editHomePlayer7Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.homeScore7).text.toString().toInt()),
        "home player 8" to arrayListOf(findViewById<Spinner>(R.id.spinHomePlayer8).selectedItem.toString(),
            findViewById<EditText>(R.id.editHomePlayer8Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.homeScore8).text.toString().toInt()),
        "away player 1" to arrayListOf(findViewById<Spinner>(R.id.spinAwayPlayer1).selectedItem.toString(),
            findViewById<EditText>(R.id.editAwayPlayer1Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.awayScore1).text.toString().toInt()),
        "away player 2" to arrayListOf(findViewById<Spinner>(R.id.spinAwayPlayer2).selectedItem.toString(),
            findViewById<EditText>(R.id.editAwayPlayer2Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.awayScore2).text.toString().toInt()),
        "away player 3" to arrayListOf(findViewById<Spinner>(R.id.spinAwayPlayer3).selectedItem.toString(),
            findViewById<EditText>(R.id.editAwayPlayer3Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.awayScore3).text.toString().toInt()),
        "away player 4" to arrayListOf(findViewById<Spinner>(R.id.spinAwayPlayer4).selectedItem.toString(),
            findViewById<EditText>(R.id.editAwayPlayer4Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.awayScore4).text.toString().toInt()),
        "away player 5" to arrayListOf(findViewById<Spinner>(R.id.spinAwayPlayer5).selectedItem.toString(),
            findViewById<EditText>(R.id.editAwayPlayer5Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.awayScore5).text.toString().toInt()),
        "away player 6" to arrayListOf(findViewById<Spinner>(R.id.spinAwayPlayer6).selectedItem.toString(),
            findViewById<EditText>(R.id.editAwayPlayer6Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.awayScore6).text.toString().toInt()),
        "away player 7" to arrayListOf(findViewById<Spinner>(R.id.spinAwayPlayer7).selectedItem.toString(),
            findViewById<EditText>(R.id.editAwayPlayer7Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.awayScore7).text.toString().toInt()),
        "away player 8" to arrayListOf(findViewById<Spinner>(R.id.spinAwayPlayer8).selectedItem.toString(),
            findViewById<EditText>(R.id.editAwayPlayer8Handicap).text.toString().toInt(),
            findViewById<EditText>(R.id.awayScore8).text.toString().toInt())


        )


        val db = FirebaseFirestore.getInstance()

        if (matchId == "") {

            db.collection("snookerResults").document()
                .set(data)
                .addOnSuccessListener { Log.d("in saved info", "Success") }
                .addOnFailureListener { e -> Log.w("in saved info", "Failure", e) }
        }else{
            db.collection("snookerResults").document(matchId)
                .set(data)
                .addOnSuccessListener { Log.d("in saved info", "Success") }
                .addOnFailureListener { e -> Log.w("in saved info", "Failure", e) }

        }

        val intent = Intent(this, DashboardActivity:: class.java)
        startActivity(intent)


    }

    fun focusChanged(state :Boolean){
        if(!state) {
            val v = findViewById<View>(R.id.homeScore1)
            updateAggregate(v)
        }

    }

    fun updateAggregate(view: View){
        var ourTotal = 0
        var theirTotal = 0

        val ourScores = getOurScores()
        val theirScores = getTheirScores()


        for(x in ourScores){
            ourTotal += scoreReturn(x)
        }

        for(x in theirScores){
            theirTotal += scoreReturn(x)
        }


        val ourAgg = findViewById<TextView>(R.id.ourAgg)
        ourAgg.text = ourTotal.toString()

        val theirAgg = findViewById<TextView>(R.id.theirAgg)
        theirAgg.text = theirTotal.toString()

        val aggResult = findViewById<TextView>(R.id.result)
        //val finalResult = ourAgg - theirAgg
        aggResult.text = (ourTotal-theirTotal).toString()

        gamesScore()

        matchScore()



    }

    fun matchScore(){
        val ourGames = findViewById<TextView>(R.id.ourGamesWon)
        val theirGames = findViewById<TextView>(R.id.theirGamesWon)
        val aggy = parseInt(findViewById<TextView>(R.id.result).text.toString())

        var ourGamesScore = parseInt(ourGames.text.toString())
        var theirGamesScore = parseInt(theirGames.text.toString())

        if(aggy > 0){
            ourGamesScore += 2
        }

        if(aggy < 0){
            theirGamesScore +=2
        }

        findViewById<TextView>(R.id.ourMatchScore).text = ourGamesScore.toString()
        findViewById<TextView>(R.id.theirMatchScore).text = theirGamesScore.toString()


    }

    fun gamesScore(){
        val ourScores = getOurScores()
        val theirScores = getTheirScores()
        var ourScoreTotal = 0
        var theirScoreTotal = 0


        for(x in 0..ourScores.size-1) {

            val scoreTotal :Int = scoreReturn(ourScores[x])-scoreReturn(theirScores[x])




           if (scoreTotal > 0) {
                 ourScoreTotal++
            }

            if(scoreTotal < 0){
                theirScoreTotal++
            }


        }



        val ourGames = findViewById<TextView>(R.id.ourGamesWon)
        ourGames.text = ourScoreTotal.toString()

        val theirGames = findViewById<TextView>(R.id.theirGamesWon)
        theirGames.text = theirScoreTotal.toString()



    }

    fun clearScores(view: View){

        val ourScore = getOurScores()
        for(x in ourScore){
            x.setText("")
        }
        val theirScore = getTheirScores()
        for(x in theirScore){
            x.setText("")
        }

        updateAggregate(view)
        val focus = findViewById<EditText>(R.id.homeScore1)
        focus.requestFocus()

    }

    fun getOurScores():Array<EditText>{

        val ourScores = arrayOf(findViewById<EditText>(R.id.homeScore1),findViewById(R.id.homeScore2),
            findViewById(R.id.homeScore3), findViewById(R.id.homeScore4),
            findViewById(R.id.homeScore5), findViewById(R.id.homeScore6),
            findViewById(R.id.homeScore7), findViewById(R.id.homeScore8))

        return ourScores

    }

    fun getTheirScores():Array<EditText>{

        val theirScores = arrayOf(findViewById<EditText>(R.id.awayScore1), findViewById(R.id.awayScore2),
            findViewById(R.id.awayScore3), findViewById(R.id.awayScore4),
            findViewById(R.id.awayScore5), findViewById(R.id.awayScore6),
            findViewById(R.id.awayScore7),findViewById(R.id.awayScore8))

        return theirScores

    }

    fun scoreReturn(editValue: EditText): Int{
        val score: Int? = editValue.text.toString().toIntOrNull()

        when(score){

            null-> return 0
            else -> return score
        }


    }

    fun scoreEditTexts(): Array<EditText>{
        val texts = arrayOf(findViewById<EditText>(R.id.homeScore1),findViewById(R.id.homeScore2),findViewById(R.id.homeScore3),
                               findViewById(R.id.homeScore4),findViewById(R.id.homeScore5),findViewById(R.id.homeScore6),
                        findViewById(R.id.homeScore7),findViewById(R.id.homeScore8),findViewById(R.id.awayScore1),
                        findViewById(R.id.awayScore2),findViewById(R.id.awayScore3),findViewById(R.id.awayScore4),
                        findViewById(R.id.awayScore5),findViewById(R.id.awayScore6),findViewById(R.id.awayScore7),
                        findViewById(R.id.awayScore8))

        return texts
    }

    fun homeHandicapTexts():Array<EditText>{
        val homeHandicapEdits = arrayOf(findViewById<EditText>(R.id.editHomePlayer1Handicap),findViewById(R.id.editHomePlayer2Handicap),
        findViewById(R.id.editHomePlayer3Handicap),findViewById(R.id.editHomePlayer4Handicap),findViewById(R.id.editHomePlayer5Handicap),
        findViewById(R.id.editHomePlayer6Handicap),findViewById(R.id.editHomePlayer7Handicap),findViewById(R.id.editHomePlayer8Handicap))

        return homeHandicapEdits
    }

    fun awayHandicapTexts():Array<EditText>{
        val awayHandicapEdits = arrayOf(findViewById<EditText>(R.id.editAwayPlayer1Handicap),findViewById(R.id.editAwayPlayer2Handicap),
        findViewById(R.id.editAwayPlayer3Handicap),findViewById(R.id.editAwayPlayer4Handicap),findViewById(R.id.editAwayPlayer5Handicap),
        findViewById(R.id.editAwayPlayer6Handicap),findViewById(R.id.editAwayPlayer7Handicap),findViewById(R.id.editAwayPlayer8Handicap))
        return awayHandicapEdits
    }

    fun homeTeamSpinners(): Array<Spinner>{
        val homePlayerSpinner = arrayOf(findViewById<Spinner>(R.id.spinHomePlayer1),findViewById(R.id.spinHomePlayer2),
        findViewById(R.id.spinHomePlayer3),findViewById(R.id.spinHomePlayer4),findViewById(R.id.spinHomePlayer5),
        findViewById(R.id.spinHomePlayer6), findViewById(R.id.spinHomePlayer7),findViewById(R.id.spinHomePlayer8))

        return homePlayerSpinner
    }

    fun awayTeamSpinners(): Array<Spinner>{
        val homePlayerSpinner = arrayOf(findViewById<Spinner>(R.id.spinAwayPlayer1),findViewById(R.id.spinAwayPlayer2),
            findViewById(R.id.spinAwayPlayer3),findViewById(R.id.spinAwayPlayer4),findViewById(R.id.spinAwayPlayer5),
            findViewById(R.id.spinAwayPlayer6), findViewById(R.id.spinAwayPlayer7),findViewById(R.id.spinAwayPlayer8))

        return homePlayerSpinner
    }
}
