 package com.example.aggregatecalculator

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_edit_teams.*
import kotlinx.coroutines.delay
import java.lang.ClassCastException
import kotlin.concurrent.thread


 class EditTeams : DialogFragment() {

/*
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        Toast.makeText(context, "You clicked to delete in edit teams", Toast.LENGTH_LONG).show()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {

    }

 */




    interface teamChanges{
            fun changedTeam(league : String)
        }
        lateinit var newTeam: teamChanges

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            newTeam = context as teamChanges
        }catch (e: ClassCastException){
            e.printStackTrace()
        }
    }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val args = arguments
            val team = args?.getString("teamName")
            val league = args?.getString("teamLeague")
            val division = args?.getString("teamDivision")
            val teamId = args?.getString("teamId")
            val existingTeam = team!!
            val existingLeague = league!!


            //val itemId = args?.getInt("itemId")

            val rootView = inflater.inflate(R.layout.fragment_edit_teams, container)
            val leagueSpinner = rootView.findViewById<Spinner>(R.id.spinnerLeague)
            val divisionSpinner = rootView.findViewById<Spinner>(R.id.spinnerDivision)
            val teamEdit = rootView.findViewById<EditText>(R.id.editTeam)
            val editTeamButton = rootView.findViewById<Button>(R.id.buttonSaveExisting)
            //val saveNewButton = rootView.findViewById<Button>(R.id.buttonSaveNew)
            val deleteTeamButton = rootView.findViewById<Button>(R.id.buttonDelete)
            val cancelButton = rootView.findViewById<Button>(R.id.buttonCancel)
            val popUp = dialog

            ArrayAdapter.createFromResource(context!!, R.array.league, android.R.layout.simple_spinner_item)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    val leagueArray = resources.getStringArray(R.array.league).toList()
                    val index = leagueArray.indexOf(league)
                    leagueSpinner.adapter = adapter
                    if (index != -1){leagueSpinner.setSelection(index)}
                }

            ArrayAdapter.createFromResource(context!!, R.array.divisions, android.R.layout.simple_spinner_item)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    val divisionArray = resources.getStringArray(R.array.divisions).toList()
                    val index =  divisionArray.indexOf(division)
                    divisionSpinner.adapter = adapter
                    if (index != -1){divisionSpinner.setSelection(index)}
                }

            teamEdit.setText(team)

           editTeamButton.setOnClickListener {
                var editButtonteam = teamEdit.text.toString()
                val editButtonleague = leagueSpinner.selectedItem.toString()
                val editButtondivision = divisionSpinner.selectedItem.toString()
               editButtonteam = standardiseNames(editButtonteam).trim()
                val teamReturned = Team(editButtonteam)
               teamReturned.teamLeague = editButtonleague
               teamReturned.teamDivision = editButtondivision
               teamReturned.teamId = teamId!!
               // Log.i("saveedit", "editbutton team $editButtonteam, existing team $existingTeam," +
                 //       "league $league, existingg league $existingLeague, team id $teamId ")
               if (editButtonleague == "Choose league" || editButtondivision == "Choose division" || editButtonteam == ""){
                   Toast.makeText(context, "Team must have a league, division & name to be saved", Toast.LENGTH_LONG).show()
                   return@setOnClickListener
               }
               if (teamId == ""){
                   val check = checkTeamName(editButtonteam, editButtonleague, editButtondivision, context!!)
                   Log.i("saveedit", "after team saved check is $check")
                   //if (check){
                   Thread.sleep(1000)
                   newTeam.changedTeam(teamReturned.teamLeague)
                   //popUp?.dismiss()}
                   popUp?.dismiss()
                   //return@setOnClickListener
               }else {
                   /*             if (editButtonteam == existingTeam && league == existingLeague && teamId == ""){
                   Toast.makeText(context, "You cannot have two teams with the same name in the " +
                           "same league", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
               }

   */

                   saveTeam(editButtonteam, editButtonleague, editButtondivision, teamId)
                   Log.i("saveedit", "returned team ${teamReturned.teamId}")
                   newTeam.changedTeam(teamReturned.teamLeague)
               }

               popUp?.dismiss()
            }



/*            saveNewButton.setOnClickListener {
                var saveButtonTeam = editTeam.text.toString()
                val saveButtonLeague = leagueSpinner.selectedItem.toString()
                val saveButtonDivision = divisionSpinner.selectedItem.toString()
                val id = ""
                 saveButtonTeam = standardiseNames(saveButtonTeam).trim()

                if (saveButtonTeam == existingTeam && league == existingLeague){
                    Toast.makeText(context, "You cannot have two teams with the same name in the " +
                            "same league", Toast.LENGTH_LONG).show()

                }else {
                    saveTeam(saveButtonTeam, saveButtonLeague, saveButtonDivision, id)
                    newTeam.changedTeam(league)
                    popUp?.dismiss()
                }

            }

 */

            deleteTeamButton.setOnClickListener {
                val dialog = ConfirmDeleteDialog()
                val trans = activity?.supportFragmentManager


                val deleteButtonTeam = teamEdit.text.toString()
                val deleteButtonLeague = leagueSpinner.selectedItem.toString()
                val deleteButtonDivision = divisionSpinner.selectedItem.toString()


                val b = Bundle()
                b.putString("team", deleteButtonTeam)
                b.putString("league", deleteButtonLeague)
                b.putString("division", deleteButtonDivision)
                b.putString("teamId", teamId)


                dialog.arguments = b

                dialog.show(trans!!, "confirm dialog")

                popUp?.dismiss()
            }


            cancelButton.setOnClickListener {
                popUp?.dismiss()
            }




            return rootView
        }




}
