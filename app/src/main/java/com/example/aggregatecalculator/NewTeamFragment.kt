package com.example.aggregatecalculator

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException


class NewTeamFragment : DialogFragment() {

    interface OnTeamSelected {
        fun NewTeam(team: String, homeOrAway: String)
    }

    lateinit var newTeamEntered: OnTeamSelected

    override fun onAttach(context: Context){
        super.onAttach(context)

        try {
            newTeamEntered = context as OnTeamSelected
        }catch (e: ClassCastException){
            e.printStackTrace()
        }

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //companion object{}
Log.i("New Team Fragment", "in On Create")
        val rootView = inflater.inflate(R.layout.fragment_new_team, container)
        val arg = arguments
        val league = arg?.getString("league", "No league passed ")
        val division = arg?.getString("division", "No division passed")
        val venue = arg?.getString("home or away", "")
        league!!.trim()
        division!!.trim()
        venue!!.trim()

        val popUp = dialog
        //val test = context!!
        val newTeamLeague = rootView.findViewById<TextView>(R.id.textNewTeamLeague)
        newTeamLeague.text = league

        val newTeamDivision = rootView.findViewById<TextView>(R.id.newTeamDivision)
        newTeamDivision.text = division

/*
        val leagueSpinner = rootView.findViewById<Spinner>(R.id.spinNewTeamLeague)
        ArrayAdapter.createFromResource(context!!, R.array.league, android.R.layout.simple_spinner_item)
            .also {adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        leagueSpinner.adapter = adapter}
        val divSpinner = rootView.findViewById<Spinner>(R.id.spinnerNewTeamDivision)
        ArrayAdapter.createFromResource(context!!, R.array.divisions, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                divSpinner.adapter = adapter
            }

*/
        val saveButton = rootView.findViewById<Button>(R.id.buttonSave)
        saveButton.setOnClickListener {
            var newTeam =
                rootView.findViewById<EditText>(R.id.editTextNewTeamName).text.toString().trim()
            Log.i("New Team Fragment", "new team is $newTeam")




            if (newTeam == "") {
                popUp?.dismiss()
            } else {
                newTeam = standardiseNames(newTeam).trim()
                val check = checkTeamName(newTeam, league, division, context!!)
                 Toast.makeText(context, "return from check teams is "+check, Toast.LENGTH_LONG).show()
               // val b = Bundle()
               // b.putString("newTeam", newTeam)
                // listener?.sendRequest(newTeam)
                Log.i("newteamfragment","before selected team call")
               //selectedTeam.NewTeam(newTeam)
                newTeamEntered.NewTeam(newTeam, venue)
                Log.i("ne team fragment", "after selected team call")
                popUp?.dismiss()
            }
        }

        val cancelButton = rootView.findViewById<Button>(R.id.buttonCancel)
        cancelButton.setOnClickListener {
            popUp?.dismiss()
        }


        return rootView
    }
}
    /*
    companion object {
        fun newInstance(position: Int): NewTeamFragment {
            val fragment = NewTeamFragment()
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment

        }

     */

/*
     fun onClick(v: View?) {
        when (v?.id){
            R.id.buttonSave ->{
                listener?.sendRequest("didthiswork")
            }

        }
        TODO("Not yet implemented")
    }

 */




/*
companion object {
    fun newInstance(position: Int): NewTeamFragment {
        val fragment = NewTeamFragment()
        val args = Bundle()
        args.putInt("position", position)
        fragment.arguments = args
        return fragment

    }
}
*/