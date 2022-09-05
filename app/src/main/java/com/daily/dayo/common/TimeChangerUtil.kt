package com.daily.dayo.common

import android.content.Context
import android.os.Build
import com.daily.dayo.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

object TimeChangerUtil {
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

    fun timeChange(context: Context, time: String): String {
        val removeTimeDifference = 32400
        val seconds =
            ((System.currentTimeMillis() - getStringTime(time)) / 1000) - removeTimeDifference

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
                // 1년 이상인 경우 연 단위로 표시
                if (seconds / 31104000 > 0) {
                    return getStringFormat(context, seconds / 31104000, R.string.time_year_ago)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        val currentTimeString: String = LocalDateTime.now().format(formatter)
                        val currentTimeDate: LocalDateTime =
                            LocalDateTime.parse(currentTimeString, formatter)
                        val parseTimeDate: LocalDateTime =
                            LocalDateTime.parse(time.substring(0, 19), formatter)
                        val period = Period.between(
                            parseTimeDate.toLocalDate(),
                            currentTimeDate.toLocalDate()
                        )
                        return getStringFormat(
                            context,
                            period.months.toLong(),
                            R.string.time_month_ago
                        )
                    } else {
                        val currentTime: Calendar = Calendar.getInstance()
                        currentTime.time = Date()
                        val parseTime: Calendar = Calendar.getInstance()
                        parseTime.time = format.parse(time.replace("T", " "))

                        return if (currentTime.get(Calendar.YEAR) == parseTime.get(Calendar.YEAR)) {
                            getStringFormat(
                                context,
                                (currentTime.get(Calendar.MONTH) - parseTime.get(Calendar.MONTH)).toLong(),
                                R.string.time_month_ago
                            )
                        } else {
                            getStringFormat(
                                context,
                                (currentTime.get(Calendar.MONTH) + (11 - parseTime.get(Calendar.MONTH))).toLong(),
                                R.string.time_month_ago
                            )
                        }
                    }
                }
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