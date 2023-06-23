package com.daily.dayo.common

import android.content.Context
import com.daily.dayo.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object TimeChangerUtil {
    fun timeChange(context: Context, time: String): String {
        val currentDateTime = LocalDateTime.now()
        var timePassed: String = ""

        Period.between(getStringTime(time).toLocalDate(), currentDateTime.toLocalDate())
            .let { durationDate ->
                if (durationDate.months < 1) {
                    Duration.between(getStringTime(time), currentDateTime).let { durationTime ->
                        timePassed = when {
                            durationTime.toDays() >= 1 -> "${durationTime.toDays()}${
                                context.getString(R.string.time_day_ago)
                            }"
                            durationTime.toHours() >= 1 -> "${durationTime.toHours()}${
                                context.getString(R.string.time_hour_ago)
                            }"
                            durationTime.toMinutes() >= 1 -> "${durationTime.toMinutes()}${
                                context.getString(R.string.time_minute_ago)
                            }"
                            else -> context.getString(R.string.time_now)
                        }
                    }
                } else {
                    timePassed = when {
                        durationDate.years >= 1 -> "${durationDate.years}${context.getString(R.string.time_year_ago)}"
                        else -> {
                            "${durationDate.months}${context.getString(R.string.time_month_ago)}"
                        }
                    }
                }
            }
        return timePassed
    }

    private fun getStringTime(time: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val detailTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        return try {
            LocalDateTime.parse(time, formatter)
        } catch (e: DateTimeParseException) {
            return LocalDateTime.parse(time, detailTimeFormatter)
        }
    }

    fun getFullStringTime(time: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val detailTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        return try {
            val currentTime = LocalDateTime.parse(time, formatter)
            return "${currentTime.year}.${currentTime.monthValue}.${currentTime.dayOfMonth}"
        } catch (e: DateTimeParseException) {
            val currentTime = LocalDateTime.parse(time, detailTimeFormatter)
            return "${currentTime.year}.${currentTime.monthValue}.${currentTime.dayOfMonth}"
        }
    }
}