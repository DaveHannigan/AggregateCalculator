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
        val ourScores = arrayOf(findViewById<EditText>(R.id.ourScore1),findViewById<EditText>(R.id.ourScore2),
            findViewById<EditText>(R.id.ourScore3), findViewById<EditText>(R.id.ourScore4),
            findViewById<EditText>(R.id.ourScore5), findViewById<EditText>(R.id.ourScore6),
            findViewById<EditText>(R.id.ourScore7), findViewById<EditText>(R.id.ourScore8))

        val theirScores = arrayOf(findViewById<EditText>(R.id.theirScore1), findViewById<EditText>(R.id.theirScore2),
            findViewById<EditText>(R.id.theirScore3), findViewById<EditText>(R.id.theirScore4),
            findViewById<EditText>(R.id.theirScore5), findViewById<EditText>(R.id.theirScore6),
            findViewById<EditText>(R.id.theirScore7),findViewById<EditText>(R.id.theirScore8))


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

    }
    fun scoreReturn(editValue: EditText): Int{
        val score: Int? = editValue.text.toString().toIntOrNull()
        if(score != null){
            return score
        }else{
            return 0
        }

    }
}
