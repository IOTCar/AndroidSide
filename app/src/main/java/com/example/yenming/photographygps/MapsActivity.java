package com.example.yenming.photographygps;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Parcelable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity {
    private final String TAG = "Map";

    /**取得地圖物件*/
    private GoogleMap mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
    private TextView txtOutput;
    private Marker markerMe;
    /** GPS */
    private LocationManager locationMgr;
    /**trace*/
    private ArrayList<LatLng> traceOfMe;

    public void getMapInformation(double latitude,double longitude){

        locationMgr=(LocationManager)getSystemService(Context.LOCATION_SERVICE);//取得系統服務
        Location location= locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);//取得最新定位
        latitude=location.getLatitude();    //取得緯度
        longitude=location.getLongitude();  //取得經度

    }








	private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initView();
        initMap();
        if (initLocationProvider()) {
            whereAmI();
        }else{
            txtOutput.setText("open GPS please");
        }
    }

    @Override
    protected void onStop() {
        locationMgr.removeUpdates(locationListener);
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initMap();
        drawPolyline();
    }

    private void initView(){
        txtOutput = (TextView) findViewById(R.id.txtOutput);
    }
    private void drawPolyline(){

        getMapInformation(x,y);
        PolylineOptions polylineOpt = new PolylineOptions();
        polylineOpt.add(new LatLng(x,y));

        polylineOpt.color(Color.BLUE);

        Polyline polyline = mMap.addPolyline(polylineOpt);
        polyline.setWidth(10);

    }

    /*
    * Gps Initialization
    * Made available location provider
    *
    *
    * */

    private  void initMap(){
        if(mMap!=null){
            mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null){
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


            }
        }



    }
    private boolean initLocationProvider(){
        locationMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        provider = locationMgr.getBestProvider(criteria,true);

        if (provider!=null){
            return true;

            /*
            *告訴系統想要取得什麼樣的提供器，
            * Criteria 是用來建立想要取得的提供器的標準，
            * 系統會去找出符合或條件之內的提供器。
            * */

        }
        else if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
            return  true;

            /*
            * 指名使用 GPS 衛星定位，
            * 所以必須判斷裝置是否有 GPS 設備，
            * 才能拿來使用。
            * */

        }
        else if(locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            provider = LocationManager.NETWORK_PROVIDER;
            return  true;

            /*
            * 網路定位
            * */

        }
        else{
            return  false;

        }

    }
    /**
     * 執行"我"在哪裡
     * 1.建立位置改變偵聽器
     * 2.預先顯示上次的已知位置
     */
    private  void whereAmI(){
        //取得上次已知的位置
        Location location = locationMgr.getLastKnownLocation(provider);
        updateWithNewLocation(location);

        //GPS Listener
        locationMgr.addGpsStatusListener(gpsListener);

        //Location Listener
        int minTime = 3000;//ms
        int minDist = 5;//meter
        locationMgr.requestLocationUpdates(provider, minTime, minDist, locationListener);

        /*
        * 先顯示上次的位罝。
        * 接著建立 GPS 偵聽器，
        * 用來知道 GPS 的狀態。
        * 最後建立位置偵聽器，
        * 我們設定的更新條件為「更新時間為 5 秒，且距離超過 5 公尺」必須同時符合這兩個條件，
        * 偵聽器才會接受更新。*/


    }

    /*
    * GPS 偵聽器
    * */
    GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(TAG, "GPS_EVENT_STARTED");
                    Toast.makeText(MapsActivity.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(TAG, "GPS_EVENT_STOPPED");
                    Toast.makeText(MapsActivity.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(TAG, "GPS_EVENT_FIRST_FIX");
                    Toast.makeText(MapsActivity.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d(TAG, "GPS_EVENT_SATELLITE_STATUS");
                    break;
            }
        }
    };
    /*
    * 位置偵聽器
    * */
    LocationListener locationListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.v(TAG, "Status Changed: Out of Service");
                    Toast.makeText(MapsActivity.this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.v(TAG, "Status Changed: Temporarily Unavailable");
                    Toast.makeText(MapsActivity.this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Log.v(TAG, "Status Changed: Available");
                    Toast.makeText(MapsActivity.this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    /*
    *  顯示本身位置
    *
    *@param lat
    *@param lng
    *
    * */

    private void showMarkerMe(double lat, double lng){
        if (markerMe != null) {
            markerMe.remove();
        }

        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(new LatLng(lat, lng));
        markerOpt.title("我在這裡");
        markerMe = mMap.addMarker(markerOpt);

        Toast.makeText(this, "lat:" + lat + ",lng:" + lng, Toast.LENGTH_SHORT).show();
    }

    private void cameraFocusOnMe(double lat, double lng){
        CameraPosition camPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(16)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }

    /*
    * 畫出移動軌跡
    * */
    private void trackToMe(double lat, double lng){
        if (traceOfMe == null) {
            traceOfMe = new ArrayList<LatLng>();
        }
        traceOfMe.add(new LatLng(lat, lng));

        PolylineOptions polylineOpt = new PolylineOptions();
        for (LatLng latlng : traceOfMe) {
            polylineOpt.add(latlng);
        }

        polylineOpt.color(Color.RED);

        Polyline line = mMap.addPolyline(polylineOpt);
        line.setWidth(10);
    }
    /**
     * 更新並顯示新位置
     * @param location
     */
    private void updateWithNewLocation(Location location) {
        String where = "";
        if (location != null) {
            //經度
            double lng = location.getLongitude();
            //緯度
            double lat = location.getLatitude();
            //速度
            float speed = location.getSpeed();
            //時間
            long time = location.getTime();
            String timeString = getTimeString(time);

            where = "經度: " + lng +
                    "\n緯度: " + lat +
                    "\n速度: " + speed +
                    "\n時間: " + timeString +
                    "\nProvider: " + provider;

            //"我"
            showMarkerMe(lat, lng);
            cameraFocusOnMe(lat, lng);
            trackToMe(lat, lng);

        }else{
            where = "No location found.";
        }

        //顯示資訊
        txtOutput.setText(where);
    }

    private String getTimeString(long timeInMilliseconds){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(timeInMilliseconds);
    }








    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
