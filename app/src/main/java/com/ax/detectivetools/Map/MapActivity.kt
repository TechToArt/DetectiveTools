package com.ax.detectivetools.Map

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.ax.detectivetools.R
import kotlinx.android.synthetic.main.activity_map.*


class MapActivity : AppCompatActivity(), GeocodeSearch.OnGeocodeSearchListener {
    private var lat = 0.0
    private var lon = 0.0
    private lateinit var aMap: AMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView.onCreate(savedInstanceState)
        aMap = mapView.map

        lat = intent.getDoubleExtra("lat", 0.0)
        lon = intent.getDoubleExtra("lon", 0.0)


        val geocoderSearch = GeocodeSearch(this)
        geocoderSearch.setOnGeocodeSearchListener(this)
        val query = RegeocodeQuery(LatLonPoint(lat, lon), 200f, GeocodeSearch.AMAP)
        geocoderSearch.getFromLocationAsyn(query)
    }

    private fun locate(address: String) {
        val latLng = LatLng(lat, lon)
        aMap.addMarker(MarkerOptions().position(latLng).title("地址：").snippet(address))
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(latLng, 10f, 0f, 0f)))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState)
    }

    companion object {
        private const val TAG = "MapActivity"
    }

    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {
        p0?.regeocodeAddress?.formatAddress?.let { locate(it) }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {

    }
}