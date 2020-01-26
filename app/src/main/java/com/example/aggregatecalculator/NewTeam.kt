package com.example.aggregatecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.example.aggregatecalculator.R.id.newTeamName
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_team.*

class NewTeam : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_team)
        val spinner = findViewById<Spinner>(R.id.spinnerNewTeamDivision)
        ArrayAdapter.createFromResource(
            this,
            R.array.divisions,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }
    }

    fun save(view: View){
        val teamName = findViewById<EditText>(R.id.editTextNewTeamName)
        var teams  = teamName.text.toString()
        teams = standardiseNames(teams)
       // Toast.makeText(this, teams, Toast.LENGTH_LONG)
         //   .show()
       // val teamsArray = arrayListOf<String>()
        var saveTeam = true


        val db = FirebaseFirestore.getInstance()
        db.collection("snookerTeams")
            .get()
            .addOnSuccessListener { task ->
               val teamsArray = arrayListOf<String>()
                for (docList in task) {
                    val team = docList["teamName"].toString()
                    val team1 = standardiseNames(team)
                    teamsArray.add(team1)

                }
                if(teamsArray.contains(teams)){
                    Toast.makeText(this, "That team name already exists", Toast.LENGTH_LONG)
                        .show()
                    saveTeam = false
                }
            }



                if (teams == " ") {
                    Toast.makeText(this, "You have not entered a team name", Toast.LENGTH_LONG)
                        .show()
                    saveTeam = false
                }

        if(saveTeam){
            var teamDiv = ""
            val db = FirebaseFirestore.getInstance()
            val div = findViewById<Spinner>(R.id.spinnerNewTeamDivision)
            teamDiv = div.selectedItem.toString()
            div.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
                    val teamDiv = parent.getItemAtPosition(p2).toString()

                    Toast.makeText(this@NewTeam,teamDiv,Toast.LENGTH_LONG).show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }

            val docName = stripSpaces(teams)

            val data = hashMapOf(
                "teamName" to teams,
                "division" to teamDiv)


            db.collection("snookerTeams").document(docName)
                .set(data)
                .addOnSuccessListener { Log.d("in saved info", "Success")

                }
                .addOnFailureListener{e -> Log.w("in saved info", "Failure", e)}




        }

    }

    fun standardiseNames(str :String) :String{
        str.trim().toLowerCase()

        val words = str.split(" ").toMutableList()

        var output = ""
        for(word in words){
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
}
