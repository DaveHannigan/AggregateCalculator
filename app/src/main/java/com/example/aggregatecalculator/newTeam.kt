package com.example.aggregatecalculator

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
//import android.support.v7.widget.LinearLayoutManager
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aggregatecalculator.R.id.newTeamName
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldPath.documentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.model.Document
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_team.*

class NewTeam : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_team)

        val spinnerLeague = findViewById<Spinner>(R.id.spinNewTeamLeague)
        ArrayAdapter.createFromResource(
            this,
            R.array.league,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinnerLeague.adapter = adapter
        }
        spinnerLeague.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                    getTeams(selectedItem)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


    }


    fun cancelNewTeam(view: View) {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }



    private fun getTeams(league: String) {
        val teamArray = ArrayList<Team>()

        val db = FirebaseFirestore.getInstance()
        db.collection("snookerTeams")
            .whereEqualTo("league", league)
            .get()
            .addOnSuccessListener { task ->
                for (docList in task) {
                    val teamName = docList["teamName"].toString()
                    val teamnId = docList.id
                    val teamDivision = docList["division"].toString()
                    Log.i("NewTeamGetTeam", "team is $teamName")
                    val team = Team(teamName)
                    team.teamId = teamnId
                    team.teamDivision = teamDivision
                    team.teamLeague = league
                    teamArray.add(team)
                }
                teamArray.sortBy { team: Team -> team.teamName  }
                val adapter = TeamAdapter(this, teamArray)

                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                listTeams.layoutManager = layoutManager
                listTeams.adapter = adapter



            }

    }
}



    class TeamAdapter(context: Context, teams: ArrayList<Team>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var context: Context

        var team: ArrayList<Team>

        //var filteredTeams: ArrayList<Team>
        init {
            this.context = context
            this.team = teams
            //this.filteredTeams = teams
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(context)
            Log.i("myContext", "inside team adapter is ${R.layout.team_list_item}")
            Log.i("myContext", "inflater is $inflater")

            return TeamHolder(inflater.inflate(R.layout.team_list_item, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val team1 = team[position]
            val th = holder as TeamHolder

            th.tm_name.text = team1.teamName
            th.tm_league.text = team1.teamLeague
            th.tm_division.text = team1.teamDivision
            th.tm_id.text = team1.teamId

            if(position %2 == 1){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen))
            }else{
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBeige))
            }


        }

        override fun getItemCount(): Int {
            return team.size
        }

        internal class TeamHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


               // Log.i("my", "itemView is $tm_name")
            var tm_name = itemView.findViewById<TextView>(R.id.teamName)
            var tm_league = itemView.findViewById<TextView>(R.id.league)
            var tm_division = itemView.findViewById<TextView>(R.id.division)
            var tm_id = itemView.findViewById<TextView>(R.id.teamId)

        }
    }

