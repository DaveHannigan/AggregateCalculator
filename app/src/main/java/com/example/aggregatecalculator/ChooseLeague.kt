package com.example.aggregatecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore

class ChooseLeague : AppCompatActivity(), NewTeamFragment.OnTeamSelected{
    override fun NewTeam(team: String, homeOrAway: String) {

        //call add new team & pass right array

        if(homeOrAway == "away" && team != ""){
            addNewTeam(team, awayTeams, homeOrAway)
        }
        if(homeOrAway == "home" && team != "")
            addNewTeam(team, homeTeams, homeOrAway)
    }

        //declare global arrays for teams

        val homeTeams = arrayListOf<String>()
        val awayTeams = arrayListOf<String>()

    //Logging tag
    private val TAG = "Choose league"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onResume()
        setContentView(R.layout.activity_choose_league)

        //declare variables & get value of ui elements

        val chooseDivisionText = findViewById<TextView>(R.id.textChooseDivision)
        val chooseDivisionSpinner = findViewById<Spinner>(R.id.spinnerChooseDivision)
        val chooseHomeTeamText = findViewById<TextView>(R.id.textChooseHomeTeam)
        val chooseAwayTeamText = findViewById<TextView>(R.id.textChooseAwayTeam)
        val chooseHomeTeamSpinner = findViewById<Spinner>(R.id.spinChooseHomeTeam)
        val chooseAwayTeamSpinner = findViewById<Spinner>(R.id.spinnerChooseAwayTeam)
        val saveButton = findViewById<Button>(R.id.buttonSave)
        var league = ""
        var homeTeamSelected = ""
        var awayTeamSelected = ""

        //Set the league spinner from resource array
        val leagueSpinner = findViewById<Spinner>(R.id.spinnerChooseLeague)
        ArrayAdapter.createFromResource(this, R.array.league, android.R.layout.simple_spinner_item)
           .also {adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)

                leagueSpinner.adapter = adapter
            }
        //set on item selected listener
        leagueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()

                // set to chosen league
                league = selectedItem

                //if no league selected then reset everything
                if(selectedItem == "Please choose a league"){
                    chooseDivisionText.visibility = View.INVISIBLE
                    chooseDivisionSpinner.visibility = View.INVISIBLE
                    chooseHomeTeamText.visibility = View.INVISIBLE
                    chooseHomeTeamSpinner.visibility = View.INVISIBLE
                    chooseAwayTeamText.visibility = View.INVISIBLE
                    chooseAwayTeamSpinner.visibility = View.INVISIBLE
                    homeTeams.clear()
                    awayTeams.clear()
                    return
                } else{
                    //show the choose division spinner
                    chooseDivisionText.visibility = View.VISIBLE
                    chooseDivisionSpinner.visibility = View.VISIBLE
                    homeTeams.clear()
                    awayTeams.clear()

                    //set up the division spinner
                    ArrayAdapter.createFromResource(this@ChooseLeague,R.array.divisions, android.R.layout.simple_spinner_item)
                        .also { adapter ->
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                            chooseDivisionSpinner.adapter = adapter
                        }
                    //set the division listener
                    chooseDivisionSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                            val selectedDivision = parent?.getItemAtPosition(position).toString()
                            //if no division selected reset to choose league
                            if(selectedDivision == "Please Choose a division"){
                                chooseHomeTeamText.visibility = View.INVISIBLE
                                chooseHomeTeamSpinner.visibility = View.INVISIBLE
                                chooseAwayTeamText.visibility = View.INVISIBLE
                                chooseAwayTeamSpinner.visibility = View.INVISIBLE
                                homeTeams.clear()
                                awayTeams.clear()

                            }else{
                                //if division chosen then make home team spinner visible

                                chooseHomeTeamText.visibility = View.VISIBLE
                                chooseHomeTeamSpinner.visibility = View.VISIBLE
                                //clear arrays before call to firestore
                                homeTeams.clear()
                                awayTeams.clear()

                                //call database and populate home & away arrays using
                                // league and division values from previous spinners
                                val db = FirebaseFirestore.getInstance()
                                    db.collection("snookerTeams")
                                        .whereEqualTo("league", league )
                                        .whereEqualTo("division", selectedDivision)
                                        .get()
                                        .addOnSuccessListener { task ->
                                            for (docList in task) {
                                                val team = docList["teamName"].toString()
                                                homeTeams.add(team)
                                                awayTeams.add(team)
                                            }
                                            //sort arrays and add navigation choices
                                            homeTeams.sort()
                                            awayTeams.sort()
                                            homeTeams.add(0, "Choose the home team")
                                            awayTeams.add(0,"Now choose the away team")
                                            homeTeams.add("Add team")


                                            //set up the adapter to display the home team
                                            ArrayAdapter(
                                                this@ChooseLeague,
                                                android.R.layout.simple_spinner_item,
                                                homeTeams
                                            )
                                                .also { adapter ->
                                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                                                    chooseHomeTeamSpinner.adapter = adapter
                                                }.setNotifyOnChange(true)
                                            chooseHomeTeamSpinner.onItemSelectedListener =
                                                object : AdapterView.OnItemSelectedListener {
                                                    override fun onItemSelected(
                                                        parent: AdapterView<*>?,
                                                        view: View,
                                                        position: Int,
                                                        id: Long
                                                    ) {
                                                        val selectedItem = parent?.getItemAtPosition(position).toString()
                                                        // if nothing selected do nothing
                                                        if(selectedItem == "Choose the home team"){return}
                                                        //if add team selected the call function to add a team
                                                        //passing league division and home or away
                                                        if(selectedItem == "Add team"){
                                                            addNewTeam(league, selectedDivision, "home")
                                                        }

                                                        //if the home team has been changed the add back to away teams array
                                                         if(homeTeamSelected != ""){
                                                             if(homeTeamSelected != "Add team"){
                                                             awayTeams.add(homeTeamSelected)}
                                                         }
                                                        //make away spinner visible
                                                        chooseAwayTeamText.visibility = View.VISIBLE
                                                        chooseAwayTeamSpinner.visibility =View.VISIBLE
                                                        saveButton.visibility = View.INVISIBLE
                                                        // take the home team selected from the away team array cos
                                                        //same team cannot play itself
                                                        awayTeams.remove(selectedItem)
                                                        // to avoid to entries for add team in away team array
                                                        if(!awayTeams.contains("Add team")) {
                                                            awayTeams.add("Add team")
                                                            Log.i(TAG, "Add team added to aawyTeams")
                                                        }
                                                        homeTeamSelected = selectedItem //keep record of chosen team


                                                        //set adapter to show possible away teams
                                                        ArrayAdapter(this@ChooseLeague,android.R.layout.simple_spinner_item,awayTeams)
                                                            .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                                                            chooseAwayTeamSpinner.adapter = adapter
                                                            }
                                                        chooseAwayTeamSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                                            override fun onItemSelected(
                                                                parent: AdapterView<*>?,
                                                                view: View?,
                                                                position: Int,
                                                                id: Long
                                                            ) {
                                                                val selectedItem = parent?.getItemAtPosition(position).toString()
                                                                if(selectedItem == "Now choose the away team"){return}
                                                                if(selectedItem == "Add team"){
                                                                    addNewTeam(league, selectedDivision, "away")
                                                                }
                                                                awayTeamSelected = selectedItem
                                                                saveButton.visibility = View.VISIBLE
                                                                saveButton.setOnClickListener{
                                                                    save(View(this@ChooseLeague), homeTeamSelected, awayTeamSelected)
                                                                }
                                                            }

                                                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                                                TODO("Not yet implemented")
                                                            }
                                                        }

                                                    }
                                                

                                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                            }
                                        }
                                }

                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    }

                    return
                }

                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }




    }

    fun addNewTeam(league: String, division: String, homeOrAway: String) {
        NewTeam("",homeOrAway)
        val b = Bundle()
        b.putString("league", league)
        b.putString("division", division)
        b.putString("home or away", homeOrAway)
        val dialog = NewTeamFragment()
        Log.i(TAG,"before fragment shown")
        Toast.makeText(this@ChooseLeague,dialog.toString(), Toast.LENGTH_LONG).show()
        val trans = supportFragmentManager
        dialog.arguments = b
        dialog.show(trans, "dialog")



    }

    fun save(view: View, homeTeamSelected: String, awayTeamSelected: String){

        val leagueSpinner = findViewById<Spinner>(R.id.spinnerChooseLeague).selectedItem.toString()
        val divisionSpinner = findViewById<Spinner>(R.id.spinnerChooseDivision).selectedItem.toString()
        val intent = Intent(this, MainActivity::class.java)
        val arrayValue = arrayListOf<String>(leagueSpinner,divisionSpinner)
        Toast.makeText(this, arrayValue[0] + " " + arrayValue[1], Toast.LENGTH_LONG).show()

        intent.putExtra("league", leagueSpinner)
        intent.putExtra("division", divisionSpinner)
        intent.putExtra("homeTeam", homeTeamSelected)
        intent.putExtra("awayTeam",awayTeamSelected)
        startActivity(intent)
    }

    fun cancel(view: View){
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)

    }
    fun addNewTeam(test: String, homeOrAway: ArrayList<String>, venue: String){
        var chooseTeamSpinner = View(this)

        if(venue == "away"){
             chooseTeamSpinner = findViewById<Spinner>(R.id.spinnerChooseAwayTeam)
        }else {
            chooseTeamSpinner = findViewById<Spinner>(R.id.spinChooseHomeTeam)
        }
        val index = homeOrAway.indexOf("Add team")
        val removed = homeOrAway[index]
        homeOrAway[index] = test

        if(!homeOrAway.contains("Add team")){
        homeOrAway.add(removed)}
       // homeOrAway.remove("")



         Toast.makeText(this,"you are now in testy fun " + test,Toast.LENGTH_LONG).show()
         ArrayAdapter(this@ChooseLeague,android.R.layout.simple_spinner_item, homeTeams)
          .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        chooseTeamSpinner.adapter = adapter}
        chooseTeamSpinner.setSelection(index)
    }




}

