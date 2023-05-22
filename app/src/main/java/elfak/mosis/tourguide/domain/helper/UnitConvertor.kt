package elfak.mosis.tourguide.domain.helper

import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class UnitConvertor {
    fun metersToKilometers(valueMeters: Int): Float {
        val value = valueMeters / 1000f
        val roundedValue = (ceil(value * 10) / 10)
        return roundedValue
    }
    fun secondsToMinutesAndHours(time: Int): Pair<Int, Int> {
        val hours = time / 3600
        val minutes = (time % 3600) / 60

        return Pair(hours, minutes)
    }
    fun secondsToMinutes(time: Int): Int {
        return time / 60
    }

    fun extractIntTimeFromString(patternString: String): Int {
        val intTimeString = patternString.filter { it.isDigit() }
        return intTimeString.toInt()
    }

    fun formatDistance(distance: Int) : String {
        if(distance > 1000) {
            val distanceKm = metersToKilometers(distance)
            return "$distanceKm km"
        }
        return "$distance m"
    }
    fun formatTime(time: String): String {
        val timeExtracted = extractIntTimeFromString(time)
        if (timeExtracted < 3600) {
            val timeConverted = secondsToMinutes(timeExtracted)
            return "$timeConverted min"
        }
        if(timeExtracted == 3600) {
            return "1 hr"
        }
        val timeConverted = secondsToMinutesAndHours(timeExtracted)
        return "${timeConverted.first} hr ${timeConverted.second} min"

    }


}