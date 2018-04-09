package fr.bmartel.bboxapi.router

import java.util.regex.Pattern

class BboxApiUtils {
    companion object {
        fun getVersionPattern(input: String?, index: Int): Int {
            val match = Pattern.compile("(\\d+).(\\d+).(\\d+)").matcher(input)
            return if (match.matches()) Integer.parseInt(match.group(index)) else -1
        }
    }
}