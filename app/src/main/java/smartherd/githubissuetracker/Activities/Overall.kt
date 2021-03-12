package smartherd.githubissuetracker.Activities

import java.text.DateFormatSymbols

fun getMonth(month: Int): String? {
    return DateFormatSymbols().months[month - 1]

}