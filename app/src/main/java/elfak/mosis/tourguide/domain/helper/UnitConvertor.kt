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
}