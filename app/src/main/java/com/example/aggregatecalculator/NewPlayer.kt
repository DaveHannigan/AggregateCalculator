package com.example.aggregatecalculator

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.BundleCompat
import com.example.aggregatecalculator.databinding.ActivityNewPlayerBinding
import com.google.firebase.firestore.FirebaseFirestore
import io.grpc.internal.SharedResourceHolder

class NewPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){

        val positiveButtonClick = {
            dialog: DialogInterface, which: Int ->
            Toast.makeText(this, "you heartless bastard", Toast.LENGTH_SHORT).show()
            deletePlayer()
        }


        fun confirmDialog(name: String){
            val builder = AlertDialog.Builder(this)
             with(builder){
                 setTitle("Delete Player")
                 setMessage("Are you sure you want to delete poor old $name")
                 setPositiveButton("Yes", DialogInterface.OnClickListener(function = positiveButtonClick))
                 setNegativeButton("No", null)
                 show()
             }
        }

        super.onCreate(savedInstanceState)
        val binding = ActivityNewPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val TAG = "NewPlayerClass"

        val saveButton = findViewById<Button>(R.id.buttonSave)


        val name = intent.getStringExtra("name")
        val handicap = intent.getStringExtra("handicap")
        val league = intent.getStringExtra("league")
        val team2 = intent.getStringExtra("team")
        val id = intent.getStringExtra("id")
        binding.textPlayerID.text = id
        if (id == ""){
            binding.buttonDeletePlayer.visibility = View.INVISIBLE
        }
        binding.buttonDeletePlayer.setOnClickListener {
            confirmDialog( name!!.toString())
          //  val dialog = ConfirmDeleteDialog()
           // val trans = this.supportFragmentManager
           // dialog.show(trans, "confirm dialog")
        }
        saveButton.setOnClickListener { save(id!!, binding) }
        Log.i(TAG, "player name is $handicap")

        if(name != null || name != ""){
            val editName = findViewById<EditText>(R.id.editTextNewPlayer)
            val editHandicap = findViewById<EditText>(R.id.editTextPlayerHandicap)
            editName.setText(name)
            editHandicap.setText(handicap!!.toString())

        }


        val spinnerLeague = findViewById<Spinner>(R.id.spinChooseLeague)
        ArrayAdapter.createFromResource(
            this,
            R.array.league,
            R.layout.custom_spinner_normal
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.custom_spinner_normal)
            spinnerLeague.adapter = adapter
            val leagueArray  = resources.getStringArray(R.array.league)
            val index = leagueArray.indexOf(league)
            if(index != -1){
                spinnerLeague.setSelection(index)}

        }

        spinnerLeague.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>,p1: View?, p2: Int,p3: Long){
                val team = parent.getItemAtPosition(p2).toString()

                val db = FirebaseFirestore.getInstance()
                db.collection("snookerTeams")
                    .whereEqualTo("league", team)
                    .get()
                    .addOnSuccessListener{ task ->
                        val teamArray = arrayListOf<String>()
                        for(docList in task){
                            val team = docList["teamName"].toString()
                            teamArray.add(team)
                        }
                        teamArray.add(0, "Choose team")

                        val teamSpinner = findViewById<Spinner>(R.id.spinChooseTeam)
                        ArrayAdapter(this@NewPlayer, R.layout.custom_spinner_normal, teamArray)
                            .also{
                                adapter -> adapter.setDropDownViewResource(R.layout.custom_spinner_normal)
                                teamSpinner.adapter = adapter
                                val index = teamArray.indexOf(team2)
                                if(index != -1){
                                    teamSpinner.setSelection(index)
                                }
                            }


                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun deletePlayer(){
        val name = findViewById<TextView>(R.id.textPlayerID).text.toString().trim()
        val db = FirebaseFirestore.getInstance()
        db.collection("SnookerPlayers").document(name)
            .delete()
            .addOnSuccessListener { Log.d("deletePlayer", "Document deleted") }
            .addOnFailureListener {e -> Log.w("deletePlayer", "Error deleting document", e)}
        val league = findViewById<Spinner>(R.id.spinChooseLeague).selectedItem.toString().trim()
        val intent = Intent(this, EditPlayers::class.java)
        intent.putExtra("league", league)
        startActivity(intent)
    }

    fun save(playerId: String, binding: ActivityNewPlayerBinding){

        val player = findViewById<EditText>(R.id.editTextNewPlayer).text.toString()
        val handicap = findViewById<EditText>(R.id.editTextPlayerHandicap).text.toString()
        val league = findViewById<Spinner>(R.id.spinChooseLeague).selectedItem.toString()
        val team = findViewById<Spinner>(R.id.spinChooseTeam).selectedItem.toString()

        val newPlayer = standardiseNames(player).trim()
        val playerstrip = stripSpaces(player)

        val player1 = Player(newPlayer)
        player1.playerHandicap = handicap

        if(player == ""){
            Toast.makeText(this, "Player must have a name", Toast.LENGTH_SHORT).show()
            return
        }
        if (handicap == "" || handicap.toInt() < -50 || handicap.toInt() > 50){
            Toast.makeText(this, "Player must have a believable handicap", Toast.LENGTH_SHORT).show()
            return
        }
        if (league == "Choose league"){
            Toast.makeText(this, "Player must play in a league", Toast.LENGTH_SHORT).show()
            return
        }
        if (team == "Choose team"){
            Toast.makeText(this, "Player must play for a team", Toast.LENGTH_SHORT).show()
            return
        }



        val db = FirebaseFirestore.getInstance()

        val data = hashMapOf(
            "player" to player1.playerName,
            "playerHandicap" to player1.playerHandicap,
            "league" to league,
            "team" to team
        )
        if(playerId != "" ){
            db.collection("SnookerPlayers").document(playerId).set(data)
                .addOnSuccessListener {
                    val intent = Intent(this, EditPlayers::class.java)
                    intent.putExtra("league", league)
                    startActivity(intent)
                }

        }else {
            val teamPlayer = arrayListOf<String>()
            db.collection("SnookerPlayers")
                .whereEqualTo("league", league)
                .whereEqualTo("team", team)
                .get()
                .addOnSuccessListener { task ->
                    for (doclist in task) {
                        val player2 = doclist["player"].toString().trim()
                        Log.i("newPlayer", "player is $player2")
                        teamPlayer.add(player2)
                    }

                    Log.i(
                        "newPlayer",
                        "player array is ${teamPlayer} and player is ${player1.name}"
                    )
                    if (!teamPlayer.contains(player1.playerName.trim())) {
                        db.collection("SnookerPlayers").document().set(data)
                            .addOnSuccessListener {
                                val intent = Intent(this, EditPlayers::class.java)
                                intent.putExtra("league", league)
                                startActivity(intent)
                            }
                        }else{
                        Toast.makeText(
                            this,
                            "A player of that name already plays for $team please differentiate them",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnSuccessListener
                    }

                }




        }

    }

    fun cancel(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
