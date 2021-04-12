package com.xpyx.nokiahslvisualisation.model.traffic

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class DirectionClass (
    @SerializedName("DESCRIPTION") val directionClassDescription: List<TrafficItemDescriptionElement>? = null,
    @SerializedName("ID") val directionClassId: Long? = null
) {
    @TypeConverter
    fun trafficItemDescsToJson(value: List<TrafficItemDescriptionElement>?) = Gson().toJson(value)
    @TypeConverter
    fun trafficItemDescsToList(value: String) = Gson().fromJson(value, Array<TrafficItemDescriptionElement>::class.java).toList()
}
