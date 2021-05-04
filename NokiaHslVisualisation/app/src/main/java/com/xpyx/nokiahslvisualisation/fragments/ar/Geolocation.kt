/**
 * Description:
 *
 * GeoLocation
 * Course: Mobiiliprojekti TX00CK67-3008
 * Name: Mikael Ylivaara
 *
 */
package com.xpyx.nokiahslvisualisation.fragments.ar

class Geolocation(
    val latitude: String?,
    val longitude: String?
) {
    companion object {
        val EMPTY_GEOLOCATION = Geolocation(null, null)
    }
}