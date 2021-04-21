package com.xpyx.nokiahslvisualisation.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xpyx.nokiahslvisualisation.StopTimesListQuery
import com.xpyx.nokiahslvisualisation.model.traffic.*
import java.util.*

class Converters {

    @TypeConverter
    fun stoptimesForPatternsToJson(value: MutableList<StopTimesListQuery.StoptimesForPattern>?) = Gson().toJson(value)
    @TypeConverter
    fun stoptimesForPatternsToList(value: String) = Gson().fromJson(value, Array<StopTimesListQuery.StoptimesForPattern>::class.java).toMutableList()


//    @TypeConverter
//    fun stoptimesWithoutPatternsToJson(value: MutableList<StopTimesListQuery.StoptimesWithoutPattern>?) = Gson().toJson(value)
//    @TypeConverter
//    fun stoptimesWithoutPatternsToList(value: String) = Gson().fromJson(value, Array<StopTimesListQuery.StoptimesWithoutPattern>::class.java).toMutableList()

    @TypeConverter
    fun trafficItemDescsToJson(value: MutableList<TrafficItemDescriptionElement>?) = Gson().toJson(value)
    @TypeConverter
    fun trafficItemDescsToList(value: String) = Gson().fromJson(value, Array<TrafficItemDescriptionElement>::class.java).toMutableList()

    @TypeConverter
    fun rDSTMCToJson(value: MutableList<RDSTmc>?) = Gson().toJson(value)
    @TypeConverter
    fun rDSTMCToList(value: String) = Gson().fromJson(value, Array<RDSTmc>::class.java).toMutableList()

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

    // Location.kt
    @TypeConverter
    fun definedToString(value: Defined?) = Gson().toJson(value)
    @TypeConverter
    fun stringToDefined(value: String) = Gson().fromJson(value, Defined::class.java)

    @TypeConverter
    fun intersectionToString(value: Intersection?) = Gson().toJson(value)
    @TypeConverter
    fun stringToIntersection(value: String) = Gson().fromJson(value, Intersection::class.java)

    @TypeConverter
    fun geolocToString(value: Geoloc?) = Gson().toJson(value)
    @TypeConverter
    fun stringToGeoloc(value: String) = Gson().fromJson(value, Geoloc::class.java)

    @TypeConverter
    fun navTechToString(value: NavTech?) = Gson().toJson(value)
    @TypeConverter
    fun stringToNavTech(value: String) = Gson().fromJson(value, NavTech::class.java)

    @TypeConverter
    fun politicalBoundaryToString(value: PoliticalBoundary?) = Gson().toJson(value)
    @TypeConverter
    fun stringToPoliticalBoundary(value: String) = Gson().fromJson(value, PoliticalBoundary::class.java)

    // TrafficItemDetail.kt
    @TypeConverter
    fun eventToString(value: Event?) = Gson().toJson(value)
    @TypeConverter
    fun stringToEvent(value: String) = Gson().fromJson(value, Event::class.java)

    @TypeConverter
    fun incidentToString(value: Incident?) = Gson().toJson(value)
    @TypeConverter
    fun stringToIncident(value: String) = Gson().fromJson(value, Incident::class.java)

}