package com.example.aggregatecalculator

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize

data class Player(val name: String): Parcelable{
    val playerName = name
    var playerHandicap = ""
    var playerLeague = ""
    var playerTeam = ""
    var playerId = ""
    var gamesPlayed: Int = 0
    var gamesWon: Int = 0
    var aggregateTotalfor = 0
    var aggregateTotalAgainst = 0
    var aggregateHandicap = 0

    fun gamesLost(): Int{
        return gamesPlayed-gamesWon
    }
    fun winPercent(): String{
        val percent = gamesWon.toFloat()/gamesPlayed.toFloat()*100
        val winString = "%.2f".format(percent) + "%"
        return winString
    }

    fun handicapChange(): Int{
        if (gamesPlayed < 4){return 0}
        return ((aggregateHandicap/gamesPlayed)/2)*-1
    }

    fun newHandicap(): Int{
        if (playerHandicap.toInt() + handicapChange() > 50){
            return 50
        }
        if (playerHandicap.toInt() + handicapChange() < -50){
            return -50
        }
        return playerHandicap.toInt() + handicapChange()

    }

}