package com.xpyx.nokiahslvisualisation.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xpyx.nokiahslvisualisation.model.traffic.TRAFFIC_ITEM

class Converters {

    @TypeConverter
    fun listToJson(value: MutableList<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toMutableList()

    @TypeConverter
    fun fromTrafficItemsList(trafficList: List<TRAFFIC_ITEM?>?): String? {
        val type = object : TypeToken<List<TRAFFIC_ITEM>>() {}.type
        return Gson().toJson(trafficList, type)
    }

    @TypeConverter
    fun toTrafficItemsList(trafficListString: String?): List<TRAFFIC_ITEM>? {
        val type = object : TypeToken<List<TRAFFIC_ITEM>>() {}.type
        return Gson().fromJson<List<TRAFFIC_ITEM>>(trafficListString, type)
    }

}