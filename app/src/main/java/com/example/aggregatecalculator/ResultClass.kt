package com.example.aggregatecalculator

import android.os.Parcelable
import com.google.protobuf.Timestamp
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize

 class  ResultClass(var id: String ="", var league: String = "", var division: String = "",
                    var matchDate: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
                    var homeTeam: String = "",
                    var awayTeam: String = "",
                    var homePlayer1: String = "", var homePlayer1H: String = "", var homePlayer1S: String = "",
                    var homePlayer2: String = "", var homePlayer2H: String = "", var homePlayer2S: String = "",
                    var homePlayer3: String = "", var homePlayer3H: String = "", var homePlayer3S: String = "",
                    var homePlayer4: String = "", var homePlayer4H: String = "", var homePlayer4S: String = "",
                    var homePlayer5: String = "", var homePlayer5H: String = "", var homePlayer5S: String = "",
                    var homePlayer6: String = "", var homePlayer6H: String = "", var homePlayer6S: String = "",
                    var homePlayer7: String = "", var homePlayer7H: String = "", var homePlayer7S: String = "",
                    var homePlayer8: String = "", var homePlayer8H: String = "", var homePlayer8S: String = "",
                    var awayPlayer1: String = "", var awayPlayer1H: String = "", var awayPlayer1S: String = "",
                    var awayPlayer2: String = "", var awayPlayer2H: String = "", var awayPlayer2S: String = "",
                    var awayPlayer3: String = "", var awayPlayer3H: String = "", var awayPlayer3S: String = "",
                    var awayPlayer4: String = "", var awayPlayer4H: String = "", var awayPlayer4S: String = "",
                    var awayPlayer5: String = "", var awayPlayer5H: String = "", var awayPlayer5S: String = "",
                    var awayPlayer6: String = "", var awayPlayer6H: String = "", var awayPlayer6S: String = "",
                    var awayPlayer7: String = "", var awayPlayer7H: String = "", var awayPlayer7S: String = "",
                    var awayPlayer8: String = "", var awayPlayer8H: String = "", var awayPlayer8S: String = "",
                    var homeTeamScore: String = "", var awayTeamScore: String = ""):  Parcelable {



}

data class meritResults(val game: Int = 0, val teamGames: Int = 0, val player1: String = "", val player1Hcap: Int = 0, val player1Score: Int = 0,
                            val player2: String = "", val player2Hcap: Int = 0, val player2Score: Int = 0)