package com.example.aggregatecalculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.common.primitives.Ints.hashCode
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date
import kotlinx.android.synthetic.main.activity_new_player.*
import kotlinx.android.synthetic.main.result_list.*
import java.lang.Integer.hashCode
import kotlin.math.log


class ResultsFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_results, container, false)
        val id = arguments?.getString("id")
        val db = FirebaseFirestore.getInstance()
        val league = rootView.findViewById<TextView>(R.id.leagueAndDate)
        val homeTeam = rootView.findViewById<TextView>(R.id.homeTeam)
        val awayTeam = rootView.findViewById<TextView>(R.id.awayTeam)
        //val test = rootView.findViewById<TextView>(R.id.testName1)
        // val testH = rootView.findViewById<TextView>(R.id.testNameH)
        val homePlayer1 = rootView.findViewById<TextView>(R.id.homePlayer1)
        val homePlayer1handicap = rootView.findViewById<TextView>(R.id.homePlayer1Handicap)
        val homePlayer1Score = rootView.findViewById<TextView>(R.id.homePlayer1Score)
        val homePlayer2 = rootView.findViewById<TextView>(R.id.homePlayer2)
        val homePlayer2handicap = rootView.findViewById<TextView>(R.id.homePlayer2Handicap)
        val homePlayer2Score = rootView.findViewById<TextView>(R.id.homePlayer2Score)
        val homePlayer3 = rootView.findViewById<TextView>(R.id.homePlayer3)
        val homePlayer3Handicap = rootView.findViewById<TextView>(R.id.homePlayer3Handicap)
        val homePlayer3Score = rootView.findViewById<TextView>(R.id.homePlayer3Score)
        val homePlayer4 = rootView.findViewById<TextView>(R.id.homePlayer4)
        val homePlayer4Handicap = rootView.findViewById<TextView>(R.id.homePlayer4Handicap)
        val homePlayer4Score = rootView.findViewById<TextView>(R.id.homePlayer4Score)
        val homePlayer5 = rootView.findViewById<TextView>(R.id.homePlayer5)
        val homePlayer5Handicap = rootView.findViewById<TextView>(R.id.homePlayer5Handicap)
        val homePlayer5Score = rootView.findViewById<TextView>(R.id.homePlayer5Score)
        val homePlayer6 = rootView.findViewById<TextView>(R.id.homePlayer6)
        val homePlayer6Handicap = rootView.findViewById<TextView>(R.id.homePlayer6Handicap)
        val homePlayer6Score = rootView.findViewById<TextView>(R.id.homePlayer6Score)
        val homePlayer7 = rootView.findViewById<TextView>(R.id.homePlayer7)
        val homePlayer7Handicap = rootView.findViewById<TextView>(R.id.homePlayer7Handicap)
        val homePlayer7Score = rootView.findViewById<TextView>(R.id.homePlayer7Score)
        val homePlayer8 = rootView.findViewById<TextView>(R.id.homePlayer8)
        val homePlayer8Handicap = rootView.findViewById<TextView>(R.id.homePlayer8Handicap)
        val homePlayer8Score = rootView.findViewById<TextView>(R.id.homePlayer8Score)

        val awayPlayer1 = rootView.findViewById<TextView>(R.id.awayPlayer1)
        val awayPlayer1Handicap = rootView.findViewById<TextView>(R.id.awayPlayer1Handicap)
        val awayPlayer1Score = rootView.findViewById<TextView>(R.id.awayPlayer1Score)
        val awayPlayer2 = rootView.findViewById<TextView>(R.id.awayPlayer2)
        val awayPlayer2Handicap = rootView.findViewById<TextView>(R.id.awayPlayer2Handicap)
        val awayPlayer2Score = rootView.findViewById<TextView>(R.id.awayPlayer2Score)
        val awayPlayer3 = rootView.findViewById<TextView>(R.id.awayPlayer3)
        val awayPlayer3Handicap = rootView.findViewById<TextView>(R.id.awayPlayer3Handicap)
        val awayPlayer3Score = rootView.findViewById<TextView>(R.id.awayPlayer3Score)
        val awayPlayer4 = rootView.findViewById<TextView>(R.id.awayPlayer4)
        val awayPlayer4Handicap = rootView.findViewById<TextView>(R.id.awayPlayer4Handicap)
        val awayPlayer4Score = rootView.findViewById<TextView>(R.id.awayPlayer4Score)
        val awayPlayer5 = rootView.findViewById<TextView>(R.id.awayPlayer5)
        val awayPlayer5Handicap = rootView.findViewById<TextView>(R.id.awayPlayer5Handicap)
        val awayPlayer5Score = rootView.findViewById<TextView>(R.id.awayPlayer5Score)
        val awayPlayer6 = rootView.findViewById<TextView>(R.id.awayPlayer6)
        val awayPlayer6Handicap = rootView.findViewById<TextView>(R.id.awayPlayer6Handicap)
        val awayPlayer6Score = rootView.findViewById<TextView>(R.id.awayPlayer6Score)
        val awayPlayer7 = rootView.findViewById<TextView>(R.id.awayPlayer7)
        val awayPlayer7Handicap = rootView.findViewById<TextView>(R.id.awayPlayer7Handicap)
        val awayPlayer7Score = rootView.findViewById<TextView>(R.id.awayPlayer7Score)
        val awayPlayer8 = rootView.findViewById<TextView>(R.id.awayPlayer8)
        val awayPlayer8Handicap = rootView.findViewById<TextView>(R.id.awayPlayer8Handicap)
        val awayPlayer8Score = rootView.findViewById<TextView>(R.id.awayPlayer8Score)

        val homeAgg = rootView.findViewById<TextView>(R.id.homeAggregate)
        val awayAgg = rootView.findViewById<TextView>(R.id.awayAggregate)

        val homeFrames = rootView.findViewById<TextView>(R.id.homeGames)
        val awayFrames = rootView.findViewById<TextView>(R.id.awayGames)

        val homeScore = rootView.findViewById<TextView>(R.id.homeScore)
        val awayScore = rootView.findViewById<TextView>(R.id.awayScore)


        val result = ResultClass()
        //result.league =
        //getResults(id!!, result)
        db.collection("snookerResults").document(id!!)
            //.whereEqualTo(FieldPath.documentId(), resultId)
            .get()
            .addOnSuccessListener { doc ->
                val date = getDate(doc["date"] as Timestamp)
                result.id = doc.id
                result.matchDate = doc["date"] as Timestamp
                result.league = doc["league"].toString()
                result.division = doc["division"].toString()
                league.text = "${result.league}, ${result.division}, $date"
                result.homeTeam = doc["homeTeam"].toString()
                homeTeam.text = result.homeTeam
                result.awayTeam = doc["awayTeam"].toString()
                awayTeam.text = result.awayTeam
                val homePlayer1Array = playerSplit(doc["home player 1"].toString())
                    result.homePlayer1 = homePlayer1Array[0]
                    homePlayer1.text = homePlayer1Array[0]
                    result.homePlayer1H = homePlayer1Array[1]
                    homePlayer1handicap.text = homePlayer1Array[1]
                    homePlayer1Score.text = homePlayer1Array[2]
                    result.homePlayer1S = homePlayer1Array[2]
                val homePlayer2Array = playerSplit(doc["home player 2"].toString())
                    homePlayer2.text = homePlayer2Array[0]
                    result.homePlayer2 = homePlayer2Array[0]
                    homePlayer2handicap.text = homePlayer2Array[1]
                    result.homePlayer2H = homePlayer2Array[1]

                    homePlayer2Score.text = homePlayer2Array[2]
                    result.homePlayer2S = homePlayer2Array[2]

                val homePlayer3Array = playerSplit(doc["home player 3"].toString())
                    homePlayer3.text = homePlayer3Array[0]
                    result.homePlayer3 = homePlayer3Array[0]
                    homePlayer3Handicap.text = homePlayer3Array[1]
                    result.homePlayer3H = homePlayer3Array[1]
                    homePlayer3Score.text = homePlayer3Array[2]
                    result.homePlayer3S = homePlayer3Array[2]

                val homePlayer4Array = playerSplit(doc["home player 4"].toString())
                    homePlayer4.text = homePlayer4Array[0]
                    result.homePlayer4 = homePlayer4Array[0]
                    homePlayer4Handicap.text = homePlayer4Array[1]
                    result.homePlayer4H = homePlayer4Array[1]
                    homePlayer4Score.text = homePlayer4Array[2]
                    result.homePlayer4S = homePlayer4Array[2]

                val homePlayer5Array = playerSplit(doc["home player 5"].toString())
                    homePlayer5.text = homePlayer5Array[0]
                    result.homePlayer5 = homePlayer5Array[0]
                    homePlayer5Handicap.text = homePlayer5Array[1]
                    result.homePlayer5H = homePlayer5Array[1]
                    homePlayer5Score.text = homePlayer5Array[2]
                    result.homePlayer5S = homePlayer5Array[2]

                val homePlayer6Array = playerSplit(doc["home player 6"].toString())
                    homePlayer6.text = homePlayer6Array[0]
                    result.homePlayer6 = homePlayer6Array[0]
                    homePlayer6Handicap.text = homePlayer6Array[1]
                    result.homePlayer6H = homePlayer6Array[1]
                    homePlayer6Score.text = homePlayer6Array[2]
                    result.homePlayer6S = homePlayer6Array[2]

                val homePlayer7Array = playerSplit(doc["home player 7"].toString())
                    homePlayer7.text = homePlayer7Array[0]
                    result.homePlayer7 = homePlayer7Array[0]
                    homePlayer7Handicap.text = homePlayer7Array[1]
                    result.homePlayer7H = homePlayer7Array[1]
                    homePlayer7Score.text = homePlayer7Array[2]
                    result.homePlayer7S = homePlayer7Array[2]

                val homePlayer8Array = playerSplit(doc["home player 8"].toString())
                    homePlayer8.text = homePlayer8Array[0]
                    result.homePlayer8 = homePlayer8Array[0]
                    homePlayer8Handicap.text = homePlayer8Array[1]
                    result.homePlayer8H = homePlayer8Array[1]
                    homePlayer8Score.text = homePlayer8Array[2]
                    result.homePlayer8S = homePlayer8Array[2]


                val awayPlayer1Array = playerSplit(doc["away player 1"].toString())
                    awayPlayer1.text = awayPlayer1Array[0]
                    result.awayPlayer1 = awayPlayer1Array[0]
                    awayPlayer1Handicap.text = awayPlayer1Array[1]
                    result.awayPlayer1H = awayPlayer1Array[1]

                    awayPlayer1Score.text = awayPlayer1Array[2]
                    result.awayPlayer1S = awayPlayer1Array[2]

                val awayPlayer2Array = playerSplit(doc["away player 2"].toString())
                    awayPlayer2.text = awayPlayer2Array[0]
                    result.awayPlayer2 = awayPlayer2Array[0]
                    awayPlayer2Handicap.text = awayPlayer2Array[1]
                    result.awayPlayer2H = awayPlayer2Array[1]

                    awayPlayer2Score.text = awayPlayer2Array[2]
                    result.awayPlayer2S = awayPlayer2Array[2]

                val awayPlayer3Array = playerSplit(doc["away player 3"].toString())
                    awayPlayer3.text = awayPlayer3Array[0]
                    result.awayPlayer3 = awayPlayer3Array[0]

                    awayPlayer3Handicap.text = awayPlayer3Array[1]
                    result.awayPlayer3H = awayPlayer3Array[1]

                    awayPlayer3Score.text = awayPlayer3Array[2]
                    result.awayPlayer3S = awayPlayer3Array[2]

                val awayPlayer4Array = playerSplit(doc["away player 4"].toString())
                    awayPlayer4.text = awayPlayer4Array[0]
                    result.awayPlayer4 = awayPlayer4Array[0]

                    awayPlayer4Handicap.text = awayPlayer4Array[1]
                    result.awayPlayer4H = awayPlayer4Array[1]

                    awayPlayer4Score.text = awayPlayer4Array[2]
                    result.awayPlayer4S = awayPlayer4Array[2]

                val awayPlayer5Array = playerSplit(doc["away player 5"].toString())
                    awayPlayer5.text = awayPlayer5Array[0]

                    result.awayPlayer5 = awayPlayer5Array[0]
                    awayPlayer5Handicap.text = awayPlayer5Array[1]
                    result.awayPlayer5H = awayPlayer5Array[1]

                    awayPlayer5Score.text = awayPlayer5Array[2]
                    result.awayPlayer5S = awayPlayer5Array[2]

                val awayPlayer6Array = playerSplit(doc["away player 6"].toString())
                    awayPlayer6.text = awayPlayer6Array[0]
                    result.awayPlayer6 = awayPlayer6Array[0]

                    awayPlayer6Handicap.text = awayPlayer6Array[1]
                    result.awayPlayer6H = awayPlayer6Array[1]

                    awayPlayer6Score.text = awayPlayer6Array[2]
                    result.awayPlayer6S = awayPlayer6Array[2]

                val awayPlayer7Array = playerSplit(doc["away player 7"].toString())
                    awayPlayer7.text = awayPlayer7Array[0]
                    result.awayPlayer7 = awayPlayer7Array[0]

                    awayPlayer7Handicap.text = awayPlayer7Array[1]
                    result.awayPlayer7H = awayPlayer7Array[1]

                    awayPlayer7Score.text = awayPlayer7Array[2]
                    result.awayPlayer7S = awayPlayer7Array[2]

                val awayPlayer8Array = playerSplit(doc["away player 8"].toString())
                    awayPlayer8.text = awayPlayer8Array[0]

                    result.awayPlayer8 = awayPlayer8Array[0]
                    awayPlayer8Handicap.text = awayPlayer8Array[1]
                    result.awayPlayer8H = awayPlayer8Array[1]

                    awayPlayer8Score.text = awayPlayer8Array[2]
                    result.awayPlayer8S = awayPlayer8Array[2]

                homeAgg.text = doc["home aggregate"].toString()
                awayAgg.text = doc["away aggregate"].toString()

                homeFrames.text = doc["home frame score"].toString()
                awayFrames.text = doc["away frame score"].toString()


                homeScore.text = doc["home team score"].toString()
                awayScore.text = doc["away team score"].toString()

                if (homePlayer1Score.text.toString().toInt() < awayPlayer1Score.text.toString().toInt()){
                    homePlayer1.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed))
                }else{
                    awayPlayer1.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed ))
                }
                if (homePlayer2Score.text.toString().toInt() < awayPlayer2Score.text.toString().toInt()){
                    homePlayer2.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed))
                }else{
                    awayPlayer2.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed ))
                }
                if (homePlayer3Score.text.toString().toInt() < awayPlayer3Score.text.toString().toInt()){
                    homePlayer3.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed))
                }else{
                    awayPlayer3.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed ))
                }
                if (homePlayer4Score.text.toString().toInt() < awayPlayer4Score.text.toString().toInt()){
                    homePlayer4.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed))
                }else{
                    awayPlayer4.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed ))
                }
                if (homePlayer5Score.text.toString().toInt() < awayPlayer5Score.text.toString().toInt()){
                    homePlayer5.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed))
                }else{
                    awayPlayer5.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed ))
                }
                if (homePlayer6Score.text.toString().toInt() < awayPlayer6Score.text.toString().toInt()){
                    homePlayer6.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed))
                }else{
                    awayPlayer6.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed ))
                }
                if (homePlayer7Score.text.toString().toInt() < awayPlayer7Score.text.toString().toInt()){
                    homePlayer7.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed))
                }else{
                    awayPlayer7.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed ))
                }
                if (homePlayer8Score.text.toString().toInt() < awayPlayer8Score.text.toString().toInt()){
                    homePlayer8.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed))
                }else{
                    awayPlayer8.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorRed ))
                }

                val editButton = rootView.findViewById<Button>(R.id.buttonEditTeam)
                editButton.setOnClickListener {
                    val intent = Intent(context, EditResultsActivity::class.java)
                    intent.putExtra("result", result)
                    startActivity(intent)
                     dismiss()
                    //Toast.makeText(context, "you clicked", Toast.LENGTH_LONG).show()
                }


                //= result .homeTeam


                //task ->
                //for (doclist in task){
                //  results.homeTeam = doclist["homeTeam"].toString()
                //Log.i("getResults", "home team ${results.homeTeam}")


            }
        // getResults(id!!, result)
        // Log.i("resultReturned", "home team ${result.homeTeam}")


        return rootView
    }

    fun playerSplit(playerArray: String): Array<String> {
        var player = playerArray.removeRange(0, 1)
        player = player.removeRange(player.length - 1, player.length)
        val split = player.split(",").toMutableList()
        var p = split[0].trim()

         val handicap = split[1].trim()
        val score = split[2].trim()
        //p.padEnd(20)
        val splitPlayer = arrayOf(p, handicap, score)
        return splitPlayer
    }


    fun  getResults(resultId: String, results: ResultClass) {
        //val results = ResultClass()

        val db = FirebaseFirestore.getInstance()
        db.collection("snookerResults").document(resultId)
            //.whereEqualTo(FieldPath.documentId(), resultId)
            .get()
            .addOnSuccessListener { doc ->
                val homeT = doc["homeTeam"].toString()
                Log.i(
                    "onsuccessListener",
                    "retrieved from db $homeT, currently results is ${results.homeTeam}"
                )

                results.homeTeam = homeT

                //task ->
                //for (doclist in task){
                //  results.homeTeam = doclist["homeTeam"].toString()
                //Log.i("getResults", "home team ${results.homeTeam}")


            }
        Log.i("justb4return", "home team ${results.homeTeam}")

        //return results
    }


}
