package mb.games.loveletter.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromListInt(value: List<Int>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toListInt(value: String): List<Int> {
        return gson.fromJson(value, object : TypeToken<List<Int>>() {}.type)
    }

    @TypeConverter
    fun fromListLong(value: List<Long>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toListLong(value: String): List<Long> {
        return gson.fromJson(value, object : TypeToken<List<Long>>() {}.type)
    }
}
