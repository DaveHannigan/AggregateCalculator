package com.example.aggregatecalculator

 fun standardiseNames(str :String) :String {
     str.trim().toLowerCase()

     val words = str.split(" ").toMutableList()

     var output = ""
     for (word in words) {
         output += word.capitalize() + " "
     }
     output.trim()
     return output
 }

fun stripSpaces(str :String) :String {
    val words = str.split(" ").toMutableList()
    var output = ""
    for (word in words) {
        output += word.capitalize()
    }
    return output
}