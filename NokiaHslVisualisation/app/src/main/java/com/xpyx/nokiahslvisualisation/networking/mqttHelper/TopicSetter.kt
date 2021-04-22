package com.xpyx.nokiahslvisualisation.networking.mqttHelper

import com.xpyx.nokiahslvisualisation.model.late.Late

class TopicSetter {

    fun setTopic(vehicleInfo: Late): String {

        val temporalType = "ongoing"                        // The status of the journey, ongoing or upcoming

        val eventType = "vp"                                // One of vp, due, arr, dep, ars, pde, pas, wait, doo,
                                                            // doc, tlr, tla, da, dout, ba, bout, vja, vjout
                                                            // Note: events are not available for metros (metro),
                                                            // U-line buses (ubus), robot buses (robot) and ferries (ferry).
        val transportMode = vehicleInfo.transportMode       // The type of the vehicle. One of bus, tram, train,
                                                            // ferry, metro, ubus (used by U-line buses and other
                                                            // vehicles with limited realtime information) or robot (used by robot buses).

        val routeId = vehicleInfo.routeId                   // The ID of the route the vehicle is running on. This
                                                            // matches route_id in GTFS (field gtfsId of Route in the routing API).

        val directionId = vehicleInfo.directionId           // The line direction of the trip, either 1 or 2.
                                                            // Note: This does not exactly match direction_id in GTFS or the routing API.
                                                            // Value 1 here is same as 0 in GTFS and the Routing API.
                                                            // Value 2 here is same as 1 in GTFS and the Routing API.

        val startTime = "+"                                 // The scheduled start time of the trip, i.e. the scheduled departure time
                                                            // from the first stop of the trip. The format follows HH:mm in 24-hour
                                                            // local time, not the 30-hour overlapping operating days present in GTFS.

        val nextStop = "+"                                  // The ID of next stop or station. Updated on each departure from or
                                                            // passing of a stop. EOL (end of line) after final stop and empty if the vehicle
                                                            // is leaving HSL area. Matches stop_id in GTFS (value of gtfsId field, without
                                                            // HSL: prefix, in Stop type in the routing API).

        val geohashLevel = "+"                              // By subscribing to specific geohash levels, you can reduce the amount of
                                                            // traffic into the client. By only subscribing to level 0 the client gets
                                                            // the most important status changes. The rough percentages of messages with a
                                                            // specific geohash_level value out of all ongoing messages are:
                                                            // 0: 3 % / 1: 0.09 % / 2: 0.9 % / 3: 8 % / 4: 43 % / 5: 44 %

        val geohash = "+"                                   // The latitude and the longitude of the vehicle. The digits of the integer
                                                            // parts are separated into their own level in the format <lat>;<long>, e.g. 60;24.
                                                            // The digits of the fractional parts are split and interleaved into a custom format
                                                            // so that e.g. (60.123, 24.789) becomes 60;24/17/28/39.
                                                            // This format enables subscribing to specific geographic boundaries easily.
                                                            // If the coordinates are missing, geohash_level and geohash have the concatenated
                                                            // value 0////. This geohash scheme is greatly simplified from the original geohash scheme

        val topic: String =
            "/hfp/v2/journey" +
            "/$temporalType" +
            "/$eventType" +
            "/$transportMode" +
            "/+/+" +
            "/$routeId" +
            "/$directionId" +
            "/$startTime" +
            "/$nextStop" +
            "/$geohashLevel" +
            "/$geohash/#"

        return topic
    }
}