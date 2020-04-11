package com.example.aggregatecalculator

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.coroutineContext
fun savePlayer(player: String, handicap: String, team: String, league: String){
    val db = FirebaseFirestore.getInstance()
    val data = hashMapOf(
        "league" to league,
        "player" to player,
        "playerHandicap" to handicap,
        "team" to team
    )
    db.collection("SnookerPlayers").document().set(data)
}


fun standardiseNames(str :String) :String {
     str.trim().toLowerCase()

     val words = str.split(" ").toMutableList()

     var output = ""
     for (word in words) {
         output += word.capitalize() + " "
     }
     output.trim()
     return output
 }

fun stripSpaces(str :String) :String {
    val words = str.split(" ").toMutableList()
    var output = ""
    for (word in words) {
        output += word.capitalize()
    }
    return output
}

fun checkTeamName(team: String, league: String, division: String, context: Context) :Boolean{
    team.trim()
    league.trim()
    division.trim()
    var newTeamAdded = false
    //val team2 = TeamClass(team,league,division)

    val db = FirebaseFirestore.getInstance()
    db.collection("snookerTeams")
      //  .whereEqualTo("teamName",team)
        .whereEqualTo("league", league)
        .whereEqualTo("division", division)
        .get()
        .addOnSuccessListener { task ->
            val teamsArray = arrayListOf<String>()
            for(docList in task) {
                val teamF = docList["teamName"].toString().trim()
                //val leagueF = docList["league"].toString().trim()
                //val divF = docList["division"].toString().trim()
                //val fullTeam = TeamClass(teamF,leagueF,divF)
                teamsArray.add(teamF)
                //Toast.makeText(context, teamF + leagueF + divF, Toast.LENGTH_LONG).show()
            }
    //Toast.makeText(context, teamsArray[0].teamName+ teamsArray[0].teamLeague +teamsArray[0].teamDivision + team2.teamName, Toast.LENGTH_LONG).show()
            if(teamsArray.contains(team)){
                Toast.makeText(context,"That team already exists", Toast.LENGTH_LONG).show()

            }else{
                val data = hashMapOf(
                    "teamName" to team,
                    "league" to league,
                    "division" to division
                )
                db.collection("snookerTeams").document()
                    .set(data)
                newTeamAdded = true

            }


        }
        .addOnFailureListener{
            Toast.makeText(context, "you failed ", Toast.LENGTH_LONG).show()
        }

   // val t = team2.teamName



    return newTeamAdded

}