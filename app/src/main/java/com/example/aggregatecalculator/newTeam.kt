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
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_team.*
import java.util.*
import kotlin.collections.ArrayList

class NewTeam : AppCompatActivity(), EditTeams.teamChanges  , ConfirmDeleteDialog.ConfirmDeleteListener {


    override fun onDialogPositiveClick(dialog: DialogFragment, team: Array<String>) {

        deleteTeam(team[3])
        getTeams(team[1])
    }

    override fun onDialogNegativeClick(dialog: DialogFragment, team: Array<String>) {

    }



    override fun changedTeam(league: String) {
        Log.i("returned", "itemid is ${league}")
        getTeams(league)
    }

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
                    val team = Team(teamName)
                    team.teamId = teamnId
                    team.teamDivision = teamDivision
                    team.teamLeague = league
                    teamArray.add(team)
                }
                teamArray.sortBy { team: Team -> team.teamName  }
                val adapter = TeamAdapter(this, teamArray)


                searchTeam.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return false
                    }
                })


                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                listTeams.layoutManager = layoutManager
                listTeams.adapter = adapter



            }

    }
}




    class TeamAdapter(context: Context, teams: ArrayList<Team>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

        public fun test(returnedTeam: Team){
            Log.i("TestFun", "id is ${returnedTeam.teamId}")
        }


        var context: Context

        var team: ArrayList<Team>
        var filteredTeams = ArrayList<Team>()

        init {
            this.context = context
            this.team = teams
            this.filteredTeams = teams
        }

        override fun getFilter(): Filter {
            return object : Filter(){
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val charSearch = constraint.toString()
                    if (charSearch.isEmpty()){
                        filteredTeams = team
                    }else{
                        val results = ArrayList<Team>()
                        for(row in team){
                            if (row.teamName.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                    Locale.ROOT)))
                                results.add(row)
                        }
                    filteredTeams = results
                    }
                    val filterResults = FilterResults()
                    filterResults.values = filteredTeams
                    return filterResults
                }

                @Suppress("UNCHECKED_CAST")
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredTeams = results?.values as ArrayList<Team>
                    notifyDataSetChanged()
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(context)
            Log.i("myContext", "inside team adapter is ${R.layout.team_list_item}")
            Log.i("myContext", "inflater is $inflater")

            return TeamHolder(inflater.inflate(R.layout.team_list_item, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val team1 = filteredTeams[position]
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
            return filteredTeams.size
        }



        internal class TeamHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


            var tm_name = itemView.findViewById<TextView>(R.id.teamName)
            var tm_league = itemView.findViewById<TextView>(R.id.league)
            var tm_division = itemView.findViewById<TextView>(R.id.division)
            var tm_id = itemView.findViewById<TextView>(R.id.teamId)



            init {

                itemView.setOnClickListener {
                    Log.i("TeamSelection", "team is ${tm_name.text.toString()}")
                    val activty = itemView.context as NewTeam
                    val bundle = Bundle()
                    bundle.putString("teamName", tm_name.text.toString())
                    bundle.putString("teamLeague", tm_league.text.toString())
                    bundle.putString("teamDivision", tm_division.text.toString())
                    bundle.putString("teamId", tm_id.text.toString())

                    val dialog = EditTeams()
                    val tans = activty.supportFragmentManager
                    dialog.arguments = bundle
                    dialog.show(tans, "NewTeamFragment" )
                }
            }



        }
    }

