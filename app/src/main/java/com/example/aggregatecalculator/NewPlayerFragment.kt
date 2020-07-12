package com.example.aggregatecalculator

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class NewPlayerFragment : DialogFragment(){

    interface onNewPlayer{
        fun newPlayer(player: String, team: String, arrayList: ArrayList<String>, callingSpinner: Int, playerObject: ArrayList<Player>)
    }
    lateinit var newPlayerEntered: onNewPlayer

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            newPlayerEntered = context as onNewPlayer
        }catch (e: ClassCastException){
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // return super.onCreateView(inflater, container, savedInstanceState)
        val arg = arguments
        val league = arg?.getString("league", "No league")
        val division = arg?.getString("division", "No division")
        val team = arg?.getString("team", "No team")
        val spinner =arg?.getString("Spinner", "No spinner")
        val array = arg?.getStringArrayList("array")
        val callingSpinner = arg?.getInt("calling spinner")
        val playerObject = arg?.getParcelableArrayList<Player>("player object")
        val rootView = inflater.inflate(R.layout.fragment_new_player, container)

        val leagueText = rootView.findViewById<TextView>(R.id.textLeague)
        val divisionText = rootView.findViewById<TextView>(R.id.textDivision)
        val teamText = rootView.findViewById<TextView>(R.id.textTeam)

        leagueText.text = league
        divisionText.text = division
        teamText.text = team

        val cancelButton = rootView.findViewById<Button>(R.id.buttonCancel)
        cancelButton.setOnClickListener{
            dialog?.dismiss()
        }

        val save = rootView.findViewById<Button>(R.id.buttonSave)
        save.setOnClickListener{
            var player = rootView.findViewById<EditText>(R.id.editNewPlayer).text.toString()
            val handicap = rootView.findViewById<EditText>(R.id.editNewPlayerHandicap).text.toString()

            if(player == "" || handicap == ""){
                dialog?.dismiss()
            }else{
                Toast.makeText(context, player + handicap, Toast.LENGTH_LONG).show()


                if (team != null && league != null) {
                    player = standardiseNames(player)
                    savePlayer(player, handicap, team, league)
                    val newPlayer = Player(player)
                    newPlayer.playerHandicap = handicap
                    playerObject?.add(newPlayer)
                    playerObject?.sortBy { player -> player.playerHandicap.toInt()  }

                    array?.clear()
                    for(x in playerObject!!){
                        array?.add(x.playerName)
                    }
                    array?.add(0, "Choose Player")
                    array?.add("New Player")

                    newPlayerEntered.newPlayer(player, team, array!!, callingSpinner!!,
                        playerObject
                    )
                    dialog?.dismiss()
                }
                dialog?.dismiss()

            }
        }



        return rootView
    }


}