package com.example.aggregatecalculator

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.renderscript.ScriptGroup
import android.util.Log
import android.widget.DatePicker
import android.widget.EditText
import androidx.core.content.ContextCompat.startActivity
import com.example.aggregatecalculator.databinding.ActivityFixtureListBinding
import com.example.aggregatecalculator.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.Month
import kotlin.coroutines.coroutineContext

fun getSeason(date: String): String{
    val dateSplit = date.split("-")
    var season = ""

    if (dateSplit[1].toInt() < 7){
        val firstYear = dateSplit[2].toInt() - 1
        val secondYear = dateSplit[2].removeRange(0,2)
         season = "$firstYear/$secondYear"
    }else{
        val firstYear = dateSplit[2]
        var secondYear = dateSplit[2].toInt() + 1
        var secondYear1 = secondYear.toString()
        secondYear1 =secondYear1.removeRange(0,2)
        season = "$firstYear/$secondYear1"
    }

    return season
}

fun pickDate(binding: ActivityMainBinding, context: Context){
    var oldDate = binding.textMatchDate.text.toString().split("-")
    //oldDate[1] = oldDate[1]-1

    val cal = Calendar.getInstance()
    Log.i("datePicker", "Your here old date $oldDate ")
    val dateSetListener = object : DatePickerDialog.OnDateSetListener{
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = SimpleDateFormat("dd-MM-yyyy")

            val date = dateFormat.format(cal.time)

            binding.textMatchDate.text = date
        }

    }

     val dialog = DatePickerDialog(context, dateSetListener, cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    Log.i("showDialog", "year ${oldDate[2]}, month ${oldDate[1]}, day of month ${oldDate[0]} ")
        dialog.updateDate(oldDate[2].toInt(), oldDate[1].toInt()-1,oldDate[0].toInt())
        dialog.show()

}

fun getHomeTeamScoreViews(binding: ActivityMainBinding): Array<EditText>{
    return arrayOf(binding.homeScore1, binding.homeScore2, binding.homeScore3,
        binding.homeScore4, binding.homeScore5, binding.homeScore6, binding.homeScore7,
        binding.homeScore8)
}

fun getAwayTeamScoreViews(binding: ActivityMainBinding): Array<EditText>{
    return arrayOf(binding.awayScore1, binding.awayScore2, binding.awayScore3, binding.awayScore4,
                    binding.awayScore5, binding.awayScore6, binding.awayScore7, binding.awayScore8)
}

fun saveResults(binding: ActivityMainBinding, id: String): Boolean{
    val dateFormat = SimpleDateFormat("dd-MM-yyyy")
    val date = binding.textMatchDate.text.toString()
    val timestamp = dateFormat.parse(date)
    val season = getSeason(date)

    val data = hashMapOf(
        "date" to timestamp,
        "season" to season,
        "league" to binding.textLeague.text.toString(),
        "division" to binding.textDivision.text.toString(),
        "homeTeam" to binding.spinHomeTeam.text.toString(),
        "awayTeam" to binding.spinAwayTeam.text.toString(),
        "home team score" to binding.ourMatchScore.text.toString(),
        "away team score" to binding.theirMatchScore.text.toString(),
        "home aggregate" to binding.ourAgg.text.toString(),
        "away aggregate" to binding.theirAgg.text.toString(),
        "home frame score" to binding.ourGamesWon.text.toString(),
        "away frame score" to binding.theirGamesWon.text.toString(),
        "home match score" to binding.ourMatchScore.text.toString(),
        "away match score" to binding.theirMatchScore.text.toString(),
        "home player 1" to arrayListOf(
            binding.spinHomePlayer1.selectedItem.toString(),
            binding.editHomePlayer1Handicap.text.toString().toInt(),
            binding.homeScore1.text.toString().toInt()),
        "home player 2" to arrayListOf(
            binding.spinHomePlayer2.selectedItem.toString(),
            binding.editHomePlayer2Handicap.text.toString().toInt(),
            binding.homeScore2.text.toString().toInt()),
        "home player 3" to arrayListOf(
            binding.spinHomePlayer3.selectedItem.toString(),
            binding.editHomePlayer3Handicap.text.toString().toInt(),
            binding.homeScore3.text.toString().toInt()),
        "home player 4" to arrayListOf(
            binding.spinHomePlayer4.selectedItem.toString(),
            binding.editHomePlayer4Handicap.text.toString().toInt(),
            binding.homeScore4.text.toString().toInt()),
        "home player 5" to  arrayListOf(
            binding.spinHomePlayer5.selectedItem.toString(),
            binding.editHomePlayer5Handicap.text.toString().toInt(),
            binding.homeScore5.text.toString().toInt()),
        "home player 6" to arrayListOf(
            binding.spinHomePlayer6.selectedItem.toString(),
            binding.editHomePlayer6Handicap.text.toString().toInt(),
            binding.homeScore6.text.toString().toInt()),
        "home player 7" to arrayListOf(
            binding.spinHomePlayer7.selectedItem.toString(),
            binding.editHomePlayer7Handicap.text.toString().toInt(),
            binding.homeScore7.text.toString().toInt()),
        "home player 8" to arrayListOf(
            binding.spinHomePlayer8.selectedItem.toString(),
            binding.editHomePlayer8Handicap.text.toString().toInt(),
            binding.homeScore8.text.toString().toInt()),
        "away player 1" to arrayListOf(
            binding.spinAwayPlayer1.selectedItem.toString(),
            binding.editAwayPlayer1Handicap.text.toString().toInt(),
            binding.awayScore1.text.toString().toInt()),
        "away player 2" to arrayListOf(
            binding.spinAwayPlayer2.selectedItem.toString(),
            binding.editAwayPlayer2Handicap.text.toString().toInt(),
            binding.awayScore2.text.toString().toInt()),
        "away player 3" to arrayListOf(
            binding.spinAwayPlayer3.selectedItem.toString(),
            binding.editAwayPlayer3Handicap.text.toString().toInt(),
            binding.awayScore3.text.toString().toInt()),
        "away player 4" to arrayListOf(
            binding.spinAwayPlayer4.selectedItem.toString(),
            binding.editAwayPlayer4Handicap.text.toString().toInt(),
            binding.awayScore4.text.toString().toInt()),
        "away player 5" to arrayListOf(
            binding.spinAwayPlayer5.selectedItem.toString(),
            binding.editAwayPlayer5Handicap.text.toString().toInt(),
            binding.awayScore5.text.toString().toInt()),
        "away player 6" to arrayListOf(
            binding.spinAwayPlayer6.selectedItem.toString(),
            binding.editAwayPlayer6Handicap.text.toString().toInt(),
            binding.awayScore6.text.toString().toInt()),
        "away player 7" to arrayListOf(
            binding.spinAwayPlayer7.selectedItem.toString(),
            binding.editAwayPlayer7Handicap.text.toString().toInt(),
            binding.awayScore7.text.toString().toInt()),
        "away player 8" to arrayListOf(
            binding.spinAwayPlayer8.selectedItem.toString(),
            binding.editAwayPlayer8Handicap.text.toString().toInt(),
            binding.awayScore8.text.toString().toInt())
    )
    Log.i("saving edit", "document id $id")
    val db = FirebaseFirestore.getInstance()
    db.collection("snookerResults").document(id)
        .set(data)
        .addOnSuccessListener { Log.d("saving edit", "success") }
        .addOnFailureListener {e -> Log.w ("saving edit", "Failure", e)}

    //val intent = Intent(context, EditResultsActivity::class.java)
    //intent.putExtra("league", binding.textLeague.text.toString())
    return true
}