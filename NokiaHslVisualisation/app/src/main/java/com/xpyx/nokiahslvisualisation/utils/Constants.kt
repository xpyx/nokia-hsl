package com.xpyx.nokiahslvisualisation.utils

class Constants {

    companion object {
        const val HSL_CLIENT_USER_NAME = "hsl-mqtt-client"
        const val HSL_CLIENT_PASSWORD = "no-pasword"
        const val HSL_MQTT_HOST = "tcp://mqtt.hsl.fi:1883"
        const val HSL_CONNECTION_TIMEOUT = 30
        const val HSL_CONNECTION_KEEP_ALIVE_INTERVAL = 15
        const val HSL_CONNECTION_CLEAN_SESSION = true
        const val HSL_CONNECTION_RECONNECT = true
    }
}