package com.example.aggregatecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_players.*

class EditPlayers : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_players)


        val spin = findViewById<Spinner>(R.id.spinChooseLeague)
        ArrayAdapter.createFromResource(this, R.array.league, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                spin.adapter = adapter
                spin.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedItem = parent?.getItemAtPosition(position).toString()

                        if(selectedItem == "Please choose a league"){
                            return
                        }else{
                            getPlayers(selectedItem)
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }


                }
            }

            //buttonGetPlayers.setOnClickListener { getPlayers()  }
    }

    fun getPlayers(league: String){
        val db = FirebaseFirestore.getInstance()



        Toast.makeText(this, "selected league is $league", Toast.LENGTH_LONG).show()
    }
}
