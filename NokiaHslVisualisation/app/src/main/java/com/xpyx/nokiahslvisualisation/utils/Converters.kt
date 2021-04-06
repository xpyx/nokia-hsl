package com.xpyx.nokiahslvisualisation.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xpyx.nokiahslvisualisation.model.traffic.*
import java.util.*

class Converters {

    @TypeConverter
    fun stringListToJson(value: MutableList<String>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToStringList(value: String) = Gson().fromJson(value, Array<String>::class.java).toMutableList()

    @TypeConverter
    fun trafficItemDescsToJson(value: MutableList<TrafficItemDescriptionElement>?) = Gson().toJson(value)
    @TypeConverter
    fun trafficItemDescsToList(value: String) = Gson().fromJson(value, Array<TrafficItemDescriptionElement>::class.java).toMutableList()

    @TypeConverter
    fun rDSTMCToJson(value: MutableList<RDSTmc>?) = Gson().toJson(value)
    @TypeConverter
    fun rDSTMCToList(value: String) = Gson().fromJson(value, Array<RDSTmc>::class.java).toMutableList()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun geolocToToJson(value: MutableList<GeolocLocation>?) = Gson().toJson(value)
    @TypeConverter
    fun geolocToToList(value: String) = Gson().fromJson(value, Array<GeolocLocation>::class.java).toMutableList()

    @TypeConverter
    fun shapesToJson(value: MutableList<Shape>?) = Gson().toJson(value)
    @TypeConverter
    fun shapesToList(value: String) = Gson().fromJson(value, Array<Shape>::class.java).toMutableList()

    @TypeConverter
    fun alertCToElement(value: MutableList<AlertCElement>?) = Gson().toJson(value)
    @TypeConverter
    fun alertCToList(value: String) = Gson().fromJson(value, Array<AlertCElement>::class.java).toMutableList()




    @TypeConverter
    fun fromTrafficItemsList(trafficList: List<TrafficItem?>?): String? {
        val type = object : TypeToken<List<TrafficItem>>() {}.type
        return Gson().toJson(trafficList, type)
    }

    @TypeConverter
    fun toTrafficItemsList(trafficListString: String?): List<TrafficItem?>? {
        val type = object : TypeToken<List<TrafficItem>>() {}.type
        return Gson().fromJson<List<TrafficItem>>(trafficListString, type)
    }

}