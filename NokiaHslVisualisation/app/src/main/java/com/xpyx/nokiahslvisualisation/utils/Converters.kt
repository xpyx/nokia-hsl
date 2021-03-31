package com.xpyx.nokiahslvisualisation.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xpyx.nokiahslvisualisation.model.traffic.RDSTmc
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficItem
import com.xpyx.nokiahslvisualisation.model.traffic.TrafficItemDescriptionElement

class Converters {

    @TypeConverter
    fun listToJson(value: MutableList<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toMutableList()

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

    @TypeConverter
    fun trafficItemDescsToJson(value: MutableList<TrafficItemDescriptionElement>) = Gson().toJson(value)

    @TypeConverter
    fun trafficItemDescsToList(value: String) = Gson().fromJson(value, Array<TrafficItemDescriptionElement>::class.java).toMutableList()

    @TypeConverter
    fun rDSTMCToJson(value: MutableList<RDSTmc>) = Gson().toJson(value)

    @TypeConverter
    fun rDSTMCToList(value: String) = Gson().fromJson(value, Array<RDSTmc>::class.java).toMutableList()

}