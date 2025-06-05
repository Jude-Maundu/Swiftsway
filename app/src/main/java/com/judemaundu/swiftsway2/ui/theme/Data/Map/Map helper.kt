//package com.judemaundu.swiftsway2.ui.theme.Data.Map
//
//import android.content.Context
//import android.graphics.Color
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.model.*
////import com.google.maps.android.PolyUtil
//import okhttp3.*
//import org.json.JSONObject
//import java.io.IOException
//
//object MapHelper {
//
//    private const val TAG = "MapHelper"
//
//    fun drawRouteBetween(
//        origin: LatLng,
//        destination: LatLng,
//        map: GoogleMap,
//        apiKey: String
//    ) {
//        val url = getDirectionsUrl(origin, destination, apiKey)
//        val client = OkHttpClient()
//        val request = Request.Builder().url(url).build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e(TAG, "Route fetch failed: ${e.message}")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val data = response.body?.string() ?: return
//                val json = JSONObject(data)
//
//                val routes = json.getJSONArray("routes")
//                if (routes.length() == 0) return
//
//                val points = routes.getJSONObject(0)
//                    .getJSONObject("overview_polyline")
//                    .getString("points")
//
//                val decodedPath = PolyUtil.decode(points)
//
//                Handler(Looper.getMainLooper()).post {
//                    map.addPolyline(
//                        PolylineOptions()
//                            .addAll(decodedPath)
//                            .width(10f)
//                            .color(Color.BLUE)
//                    )
//                }
//            }
//        })
//    }
//
//    private fun getDirectionsUrl(origin: LatLng, destination: LatLng, apiKey: String): String {
//        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
//        val strDest = "destination=${destination.latitude},${destination.longitude}"
//        val mode = "mode=driving"
//        return "https://maps.googleapis.com/maps/api/directions/json?$strOrigin&$strDest&$mode&key=$apiKey"
//    }
//
//    fun isNearby(userLat: Double, userLng: Double, targetLat: Double, targetLng: Double, radiusMeters: Float = 5000f): Boolean {
//        val results = FloatArray(1)
//        android.location.Location.distanceBetween(userLat, userLng, targetLat, targetLng, results)
//        return results[0] <= radiusMeters
//    }
//
//    fun addUserMarker(
//        map: GoogleMap,
//        id: String,
//        location: LatLng,
//        title: String,
//        iconRes: Int? = null,
//        context: Context
//    ): Marker {
//        val markerOptions = MarkerOptions()
//            .position(location)
//            .title(title)
//
//        iconRes?.let {
//            val iconBitmap = BitmapDescriptorFactory.fromResource(it)
//            markerOptions.icon(iconBitmap)
//        } ?: run {
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//        }
//
//        return map.addMarker(markerOptions)!!
//    }
//}
