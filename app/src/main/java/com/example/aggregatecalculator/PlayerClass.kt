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

}