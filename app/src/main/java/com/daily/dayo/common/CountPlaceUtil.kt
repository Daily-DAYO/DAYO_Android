package com.daily.dayo.common

fun convertCountPlace(count: Int): String {
    return when(count){
        in 0 until 1000 -> count.toString()
        in 1000 until 10000 -> (count / 1000).toString() + "K"
        in 10000 until  Int.MAX_VALUE -> (count / 10000).toString() + "M"
        else -> "0"
    }
}