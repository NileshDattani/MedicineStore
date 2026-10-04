package pharmlane.com.PharmLaneStore.utills

import android.location.Address
import android.location.Geocoder

/** First address for a coordinate, or null when the geocoder finds nothing or has no network. */
fun Geocoder.firstAddress(latitude: Double, longitude: Double): Address? =
    try {
        @Suppress("DEPRECATION")
        getFromLocation(latitude, longitude, 1)?.firstOrNull()
    } catch (e: Exception) {
        null
    }
