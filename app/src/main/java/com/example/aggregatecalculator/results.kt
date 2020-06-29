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
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aggregatecalculator.databinding.ActivityResultsBinding
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_results.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class results : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCancel.setOnClickListener { finish() }


        val leagueSpin = findViewById<Spinner>(R.id.spinChooseLeague)
        ArrayAdapter.createFromResource(this, R.array.league,android.R.layout.simple_spinner_item )
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                leagueSpin.adapter = adapter
            }
        leagueSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                //getTeams(selectedItem)
                getResults(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }

    fun getTeams(selectedLeague: String){
        val teamsArray = ArrayList<Team>()

        val db = FirebaseFirestore.getInstance()
        db.collection("snookerTeams")
            .whereEqualTo("league", selectedLeague)
            .get()
            .addOnSuccessListener { task ->
                for(doclist in task) {
                    val teamName = doclist["teamName"].toString()
                    val teamId = doclist.id
                    val teamDivision = doclist["division"].toString()
                    val team = Team(teamName)
                    team.teamLeague = selectedLeague
                    team.teamDivision = teamDivision
                    team.teamId = teamId
                    teamsArray.add(team)
                }
                teamsArray.sortedBy { team: Team -> team.teamName}
                val adapter = TeamAdapter(this, teamsArray)
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recycleTeams.layoutManager =layoutManager
                recycleTeams.adapter = adapter
            }
    }

    fun getResults(league: String){
        val resultsArray = ArrayList<ResultClass>()
        val db = FirebaseFirestore.getInstance()
        db.collection("snookerResults")
            .whereEqualTo("league", league )
            .get()
            .addOnSuccessListener { task ->
                for (doclist in task){
                    val result = ResultClass()

                    result.homeTeam = doclist["homeTeam"].toString()
                    result.awayTeam = doclist["awayTeam"].toString()
                    result.homeTeamScore = doclist["home team score"].toString()
                    if (result.homeTeamScore == "null"){result.homeTeamScore = "0"}
                    result.awayTeamScore = doclist["away team score"].toString()
                    if (result.awayTeamScore == "null"){result.awayTeamScore = "0"}
                    result.matchDate = doclist["date"] as Timestamp
                    result.id = doclist.id
                    result.league = league
                    result.division = doclist["division"].toString()
                    resultsArray.add(result)
                }
                resultsArray.sortBy {result: ResultClass -> result.matchDate  }
                val adapter = ResultsAdapter(this, resultsArray)
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                searchResults.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String): Boolean{
                        return false
                    }
                    override fun onQueryTextChange(newText: String?): Boolean{
                        adapter.filter.filter(newText)
                        return false
                    }
                })

                recycleTeams.layoutManager = layoutManager
                recycleTeams.adapter = adapter
            }
    }

}

class ResultsAdapter (context: Context, results: ArrayList<ResultClass>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable{
    var context: Context = context
    var results: ArrayList<ResultClass> = results
    var filteredResults = results
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.UK)

    override fun getFilter(): Filter{
        return  object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()){
                    filteredResults = results
                }else{
                    val result = ArrayList<ResultClass>()
                    for (row in results){
                        if (row.homeTeam.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT)) || row.awayTeam.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT)) || dateFormat.format(row.matchDate.toDate()).contains(charSearch))
                            //Log.i("seeDateFormat", dateFormat.format(row.matchDate.toDate()))
                            result.add(row)


                    }
                    filteredResults = result
                }
                val filterResults = FilterResults()
                filterResults.values = filteredResults
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredResults = results?.values as ArrayList<ResultClass>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ResultsHolder(inflater.inflate(R.layout.result_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val result1 = filteredResults[position]
        val rh = holder as ResultsHolder
        rh.result_home_team.text = result1.homeTeam
        rh.result_away_team.text = result1.awayTeam
        rh.result_home_team_score.text = result1.homeTeamScore
        rh.result_away_team_score.text = result1.awayTeamScore
        rh.result_Id.text = result1.id
        rh.result_league.text = result1.league
        rh.result_division.text = result1.division

        val timestamp = result1.matchDate
        val date = timestamp.toDate()
        val dateformat = SimpleDateFormat("dd-MM-yyyy",  Locale.UK)
        val displayDate = dateformat.format(date)
        rh.result_date.text = displayDate
        if (position % 2 == 1){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGreen))

        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBeige))
        }


    }

    override fun getItemCount(): Int {
        //return results.size
        return filteredResults.size
    }

    internal class ResultsHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var result_home_team: TextView = itemView.findViewById(R.id.homeTeam)
        var result_away_team: TextView = itemView.findViewById(R.id.awayTeam)
        var result_home_team_score: TextView = itemView.findViewById(R.id.homeTeamScore)
        var result_away_team_score: TextView = itemView.findViewById(R.id.awayTeamScore)
        var result_date: TextView = itemView.findViewById(R.id.date)
        var result_Id: TextView = itemView.findViewById(R.id.resultId)
        var result_league: TextView = itemView.findViewById(R.id.textLeague)
        var result_division: TextView = itemView.findViewById(R.id.textDivision)

        init {
            itemView.setOnClickListener {
                if (result_home_team_score.text.toString().toInt() == 0 &&
                        result_away_team_score.text.toString().toInt() == 0){
                    val context = itemView.context
                   // val binding = ActivityResultsBinding()
                    //val league = binding.spinChooseLeague.selectedItem.toString()

                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("league", result_league.text.toString())
                    intent.putExtra("division", result_division.text.toString())
                    intent.putExtra("homeTeam", result_home_team.text.toString())
                    intent.putExtra("awayTeam", result_away_team.text.toString())
                    intent.putExtra("matchDate", result_date.text.toString())
                    intent.putExtra("matchId", result_Id.text.toString())
                    val bundle = Bundle()
                    startActivity(context, intent, bundle)
                    return@setOnClickListener
                }
                val activity = itemView.context as results
                val dialog  = ResultsFragment()
                val b = Bundle()
                b.putString("id",result_Id.text.toString())
                dialog.arguments = b

                dialog.show(activity.supportFragmentManager, "fragment")


            }
        }

    }
    fun displayDialog(){

    }
}
