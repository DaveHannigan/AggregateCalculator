package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewParent
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Integer.parseInt
import java.time.temporal.ValueRange
//import com.google.firebase.firestore.ktx


class MainActivity : AppCompatActivity() {

        //lateinit var db: DocumentReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


            getTeams(findViewById(R.id.constLayout))
            getPlayers(findViewById(R.id.constLayout))
           // onResume((findViewById(R.id.constLayout))
            updateAggregate(findViewById<EditText>(R.id.constLayout))
            //val homeSpinner = findViewById<Spinner>(R.id.spinHomeTeam)
           // homeSpinner.setOnItemSelectedListener(this)









        val editText1 = findViewById<EditText>(R.id.homeScore1)
        editText1.setOnFocusChangeListener{_, b ->  focusChanged(b)}

        val editText2 = findViewById<EditText>(R.id.awayScore1)
        editText2.setOnFocusChangeListener{_, b -> focusChanged(b)}

        val editText3 = findViewById<EditText>(R.id.homeScore2)
        editText3.setOnFocusChangeListener{_, b ->  focusChanged(b)}

        val editText4 = findViewById<EditText>(R.id.awayScore2)
        editText4.setOnFocusChangeListener{_, b -> focusChanged(b)}

        val editText5 = findViewById<EditText>(R.id.homeScore3)
        editText5.setOnFocusChangeListener{_, b ->  focusChanged(b)}

        val editText6 = findViewById<EditText>(R.id.awayScore3)
        editText6.setOnFocusChangeListener{_, b -> focusChanged(b)}

        val editText7 = findViewById<EditText>(R.id.homeScore4)
        editText7.setOnFocusChangeListener{_, b ->  focusChanged(b)}

        val editText8 = findViewById<EditText>(R.id.awayScore4)
        editText8.setOnFocusChangeListener{_, b -> focusChanged(b)}

        val editText9 = findViewById<EditText>(R.id.homeScore5)
        editText9.setOnFocusChangeListener{_, b ->  focusChanged(b)}

        val editText10 = findViewById<EditText>(R.id.awayScore5)
        editText10.setOnFocusChangeListener{_, b -> focusChanged(b)}

        val editText11 = findViewById<EditText>(R.id.homeScore6)
        editText11.setOnFocusChangeListener{_, b ->  focusChanged(b)}

        val editText12 = findViewById<EditText>(R.id.awayScore6)
        editText12.setOnFocusChangeListener{_, b -> focusChanged(b)}

        val editText13 = findViewById<EditText>(R.id.homeScore7)
        editText13.setOnFocusChangeListener{_, b ->  focusChanged(b)}

        val editText14 = findViewById<EditText>(R.id.awayScore7)
        editText14.setOnFocusChangeListener{_, b -> focusChanged(b)}

        val editText15 = findViewById<EditText>(R.id.homeScore8)
        editText15.setOnFocusChangeListener{_, b ->  focusChanged(b)}

        val editText16 = findViewById<EditText>(R.id.awayScore8)
        editText16.setOnFocusChangeListener{_, b -> focusChanged(b)}

        editText16.setOnClickListener{updateAggregate(editText1)}





    }

    fun getPlayers(view: View){
        val db = FirebaseFirestore.getInstance()
        db.collection("snookerPlayer")
            .get()
            .addOnSuccessListener { task ->
                val playersArray = arrayListOf<String>()
                for (docList in task){
                    val player = docList["playerName"]
                    playersArray.add(player.toString())
                }
                playersArray.add("New Player")

                val homePlayer1Spinner = findViewById<Spinner>(R.id.spinHomePlayer1)
                val homePlayer2Spinner = findViewById<Spinner>(R.id.spinHomePlayer2)
                ArrayAdapter(this,   android.R.layout.simple_spinner_item, playersArray)
                    .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    homePlayer1Spinner.adapter = adapter
                    homePlayer2Spinner.adapter = adapter}

                homePlayer1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        p1: View?,
                        position: Int,
                        p3: Long){

                         val selectedItem = parent.getItemAtPosition(position).toString()
                        if(selectedItem == "New Player"){
                            val intent = Intent(this@MainActivity, NewPlayer::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                }
            }
    }

    fun getTeams(view: View){
        val db = FirebaseFirestore.getInstance()
        db.collection("snookerTeams")
            .get()
            .addOnSuccessListener {task  ->
                val teamsArray = arrayListOf<String>()
                for (  docList in   task) {
                    val team = docList["teamName"]
                    teamsArray.add(team.toString())
                }

                teamsArray.add("New team")
                val homeSpinner = findViewById<Spinner>(R.id.spinHomeTeam)
                ArrayAdapter(this, android.R.layout.simple_spinner_item, teamsArray)
                    .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                        homeSpinner.adapter = adapter
                    }
                homeSpinner.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener{

                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        if(selectedItem == "New team") {
                            val intent = Intent(this@MainActivity,NewTeam::class.java)
                            startActivity(intent)
                            Toast.makeText(this@MainActivity, selectedItem, Toast.LENGTH_LONG)
                                .show()
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }






                val awaySpinner = findViewById<Spinner>(R.id.spinAwayTeam)
                ArrayAdapter(this,android.R.layout.simple_spinner_item, teamsArray)
                    .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                        awaySpinner.adapter = adapter
                    }

            }

    }

    fun saveInfo(view: View){

        val db = FirebaseFirestore.getInstance()
        val data = hashMapOf(
            "teamName" to "The Brunswick",
            "division" to "Div 4"
        )
        Toast.makeText(this, db.toString(), Toast.LENGTH_SHORT).show()
        Log.i("in save info", "in place of toast")

        db.collection("snookerTeams").document("Brunswick")
            .set(data)
            .addOnSuccessListener { Log.d("in saved info", "Success")

            }
           .addOnFailureListener{e -> Log.w("in saved info", "Failure", e)}


    }

    fun focusChanged(state :Boolean){
        if(!state) {
            val v = findViewById<View>(R.id.homeScore1)
            updateAggregate(v)
        }

    }

    fun updateAggregate(view: View){
        var ourTotal = 0
        var theirTotal =0

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
   // companion object{
     //   private val TAG: String? = MainActivity::class.simpleName
    //}
}

//class SnookerTeam(val teamName: String, val division: String)


