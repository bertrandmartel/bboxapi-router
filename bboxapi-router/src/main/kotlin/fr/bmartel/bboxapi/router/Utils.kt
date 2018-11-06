package fr.bmartel.bboxapi.router

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.regex.Pattern

class BboxApiUtils {
    companion object {
        fun getVersionPattern(input: String?, index: Int): Int {
            val match = Pattern.compile("(\\d+).(\\d+).(\\d+)").matcher(input)
            return if (match.matches()) Integer.parseInt(match.group(index)) else -1
        }

        inline fun <reified T> fromJson(json: String) = Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)
    }
}
