package com.daily.dayo.util

import android.content.Context
import com.daily.dayo.R
import java.text.SimpleDateFormat
import java.util.*

object TimeChangerUtil {
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

    fun timeChange(context: Context, time: String): String {
        val removeTimeDifference = 32400
        val seconds = ((System.currentTimeMillis() - getStringTime(time)) / 1000) - removeTimeDifference

        when {
            seconds < 60 -> {
                return context.getString(R.string.time_now)
            }
            seconds < 60 * 60 -> {
                return getStringFormat(context, seconds / 60, R.string.time_minute_ago)
            }
            seconds < 60 * 60 * 24 -> {
                return getStringFormat(context, seconds / 3600, R.string.time_hour_ago)
            }
            seconds < 60 * 60 * 24 * 30 -> {
                return getStringFormat(context, seconds / 86400, R.string.time_day_ago)
            }
            else -> {
                return getStringFormat(context, seconds / 31104000, R.string.time_year_ago)
            }
        }
    }

    private fun getStringTime(time: String) =
        requireNotNull(format.parse(time.replace("T", " "))).time

    private fun getStringFormat(context: Context, time: Long, id: Int) = String.format(
        context.getString(R.string.time_format),
        time,
        context.getString(id)
    )
}