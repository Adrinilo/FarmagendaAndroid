package net.adrinilo.farmagenda.utils

import android.widget.TextView
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

private val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)

fun LocalTime.toTimeText(): String {
    return format(formatter)
}

fun TextView.setTime(time: LocalTime) {
    text = time.toTimeText()
}
