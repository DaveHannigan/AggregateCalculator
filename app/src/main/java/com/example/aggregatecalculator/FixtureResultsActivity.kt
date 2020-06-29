package com.example.aggregatecalculator

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.example.aggregatecalculator.databinding.ActivityFixtureResultsBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*

class FixtureResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFixtureResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCancel.setOnClickListener { finish() }

        ArrayAdapter.createFromResource(this, R.array.league, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                binding.spinChooseLeague.adapter  = adapter
                binding.spinChooseLeague.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        //TODO("Not yet implemented")
                        binding.spinAwayTeam.visibility = View.INVISIBLE
                        binding.spinHomeTeam.visibility = View.INVISIBLE
                        binding.buttonAddAsFixture.visibility = View.INVISIBLE
                        binding.buttonEnterResults.visibility =View.INVISIBLE
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                       // TODO("Not yet implemented")
                    }

                }
            }
        ArrayAdapter.createFromResource(this, R.array.divisions, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                binding.spinChooseDivision.adapter = adapter
                binding.spinChooseDivision.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        binding.spinHomeTeam.visibility = View.INVISIBLE
                        binding.spinAwayTeam.visibility = View.INVISIBLE
                        binding.buttonAddAsFixture.visibility = View.INVISIBLE
                        binding.buttonEnterResults.visibility =View.INVISIBLE
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        //
                    }
                }
            }
        binding.editMatchDate.showSoftInputOnFocus = false
        binding.editMatchDate.setOnClickListener{
            setDate(binding)}
        binding.buttonGetTeam.setOnClickListener {
            if (binding.spinChooseLeague.selectedItem == "Choose league" ||
                binding.spinChooseDivision.selectedItem == "Choose division" ||
                    binding.editMatchDate.text.toString() == ""){
                Toast.makeText(this, "Ensure you have selected league, division and date", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val date = binding.editMatchDate.text.toString()

            binding.spinHomeTeam.visibility = View.VISIBLE
            binding.spinAwayTeam.visibility = View.VISIBLE
            binding.buttonAddAsFixture.visibility = View.VISIBLE
            binding.buttonEnterResults.visibility =View.VISIBLE

            getTeams(binding)
        }
        binding.buttonAddAsFixture.setOnClickListener {
            if (binding.spinHomeTeam.selectedItem.toString() == binding.spinAwayTeam.selectedItem.toString()){
                Toast.makeText(this, "A team cannot play itself!!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else {
                    checkFixture(binding, true)
                }
        }
        binding.buttonEnterResults.setOnClickListener {
            if (binding.spinHomeTeam.selectedItem.toString() == binding.spinAwayTeam.selectedItem.toString()){
                Toast.makeText(this, "A team cannot play itself!!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else {
                checkFixture(binding, false)
            }
        }

    }

            fun checkFixture(binding: ActivityFixtureResultsBinding, fixture: Boolean){
            val season = getSeason(binding.editMatchDate.text.toString())
            val league = binding.spinChooseLeague.selectedItem.toString()
            val division = binding.spinChooseDivision.selectedItem.toString()
            val homeTeam = binding.spinHomeTeam.selectedItem.toString()
            val awayTeam = binding.spinAwayTeam.selectedItem.toString()
            val exists = arrayListOf<String>()
        var test = ""
        val db = FirebaseFirestore.getInstance()

        db.collection("snookerResults")
            .whereEqualTo("season", season)
            .whereEqualTo("league", league)
            .whereEqualTo("division", division)
            .whereEqualTo("homeTeam", homeTeam)
            .whereEqualTo("awayTeam", awayTeam)
            .get()
            .addOnSuccessListener { task ->
                for (doclist in task){
                    exists.add(doclist.id)
                }
                when{
                    exists.size > 0 && fixture ->{
                         Toast.makeText(this, "Fixture already exists", Toast.LENGTH_SHORT).show()
                         return@addOnSuccessListener}
                    exists.size == 0 && fixture == true ->{
                        Toast.makeText(this, "Fixture added", Toast.LENGTH_SHORT).show()
                        saveFixture(binding)}
                    exists.size > 0 && fixture == false ->{
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("league", league)
                        intent.putExtra("division", division)
                        intent.putExtra("homeTeam", homeTeam)
                        intent.putExtra("awayTeam", awayTeam)
                        intent.putExtra("matchDate", binding.editMatchDate.text.toString())
                        intent.putExtra("matchId", exists[0])
                        startActivity(intent)
                        Toast.makeText(this, "fixture exists redirect to results with id ${exists[0]}", Toast.LENGTH_SHORT).show()
                    }
                    exists.size == 0 && fixture == false ->{
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("league", league)
                        intent.putExtra("division", division)
                        intent.putExtra("homeTeam", homeTeam)
                        intent.putExtra("awayTeam", awayTeam)
                        intent.putExtra("matchDate", binding.editMatchDate.text.toString())
                        intent.putExtra("matchId", "")
                        startActivity(intent)
                    }
                    }

            }
    }

    fun saveFixture(binding: ActivityFixtureResultsBinding){
        val db = FirebaseFirestore.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = binding.editMatchDate.text.toString()
        Log.i("saveFix", "retrieved date $date")
        val timestamp = dateFormat.parse(date)
        val data = hashMapOf(
           "date" to  timestamp,
            "homeTeam" to binding.spinHomeTeam.selectedItem.toString(),
            "awayTeam" to binding.spinAwayTeam.selectedItem.toString(),
            "league" to binding.spinChooseLeague.selectedItem.toString(),
            "division" to binding.spinChooseDivision.selectedItem.toString(),
            "season" to getSeason(binding.editMatchDate.text.toString())
        )
        db.collection("snookerResults").document()
            .set(data)
    }

    fun getTeams(binding: ActivityFixtureResultsBinding){
        val db = FirebaseFirestore.getInstance()
        val teams = arrayListOf<String>()

       db.collection("snookerTeams")
           .whereEqualTo("league", binding.spinChooseLeague.selectedItem.toString())
           .whereEqualTo("division", binding.spinChooseDivision.selectedItem.toString())
           .get()
           .addOnSuccessListener { task ->
               for (doclist in task){
                   teams.add(doclist["teamName"].toString())
               }
               teams.sort()

               ArrayAdapter(this, android.R.layout.simple_spinner_item, teams)
                   .also { adapter ->
                       adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                       binding.spinHomeTeam.adapter = adapter
                       binding.spinAwayTeam.adapter = adapter
                   }
           }

    }

    fun setDate(binding: ActivityFixtureResultsBinding){
        val cal = Calendar.getInstance()

        val dateSetListener = object: DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd-MM-yyyy")

                val date = dateFormat.format(cal.time)

                binding.editMatchDate.setText(date)
            }

        }
        DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
    }
}