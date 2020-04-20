package com.example.aggregatecalculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class PlayerAdapter (context: Context, players: List<Player>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var context: Context
    var player: List<Player>
    var TAG = "playerAdapter"


    init {
        this.context = context
        this.player = players
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return PlayerHolder(inflater.inflate(R.layout.player_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val player1 = player[position]
        val ph = holder as PlayerHolder

        ph.pl_name.setText(player1.playerName)
        ph.pl_handicap.setText(player1.playerHandicap)
        ph.pl_team.setText(player1.playerTeam)
        ph.pl_league.setText(player1.playerLeague)
        if(position % 2 == 1){
            holder.itemView.setBackgroundColor(context.resources.getColor(R.color.colorGreen))
        }else{
            holder.itemView.setBackgroundColor(context.resources.getColor(R.color.colorBeige))
        }
    }

    override fun getItemCount(): Int {
        return player.size
    }

    internal class PlayerHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var pl_name: TextView
        var pl_handicap: TextView
        var pl_team: TextView
        var pl_league: TextView

        init {
            pl_name = itemView.findViewById(R.id.playerName) as TextView
            pl_handicap = itemView.findViewById(R.id.playerHandicap) as TextView
            pl_team = itemView.findViewById(R.id.playerTeam) as TextView
            pl_league = itemView.findViewById(R.id.playerLeague)

        }
    }


}




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