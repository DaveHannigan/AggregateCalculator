package com.example.aggregatecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Integer.parseInt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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
        val focus = findViewById<EditText>(R.id.ourScore1)
        focus.requestFocus()

    }

    fun getOurScores():Array<EditText>{

        val ourScores = arrayOf(findViewById<EditText>(R.id.ourScore1),findViewById(R.id.ourScore2),
            findViewById(R.id.ourScore3), findViewById(R.id.ourScore4),
            findViewById(R.id.ourScore5), findViewById(R.id.ourScore6),
            findViewById(R.id.ourScore7), findViewById(R.id.ourScore8))

        return ourScores

    }

    fun getTheirScores():Array<EditText>{

        val theirScores = arrayOf(findViewById<EditText>(R.id.theirScore1), findViewById(R.id.theirScore2),
            findViewById(R.id.theirScore3), findViewById(R.id.theirScore4),
            findViewById(R.id.theirScore5), findViewById(R.id.theirScore6),
            findViewById(R.id.theirScore7),findViewById(R.id.theirScore8))

        return theirScores

    }

    fun scoreReturn(editValue: EditText): Int{
        val score: Int? = editValue.text.toString().toIntOrNull()

        when(score){

            null-> return 0
            else -> return score
        }
/*
      if(score != null){
          return score
     }else{
          return 0
      }

*/

    }
}
