package com.example.aggregatecalculator

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize


data class Team(val name: String): Parcelable{
    val teamName = name
    var teamLeague = ""
    var teamDivision = ""
    var teamId =""

}

