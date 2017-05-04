package com.webwerks.quickbloxdemo.chat.location;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.webwerks.quickbloxdemo.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by webwerks on 4/5/17.
 */

public class NearByPlacesTask extends AsyncTask<Object,Integer,String> {

    //private static final String PLACES_SEARCH_URL =  "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String GOOGLE_SERVER_API_KEY="AIzaSyAwORYJjl7trw4FOXXDTdbbIryXbBZO9CE";
    private int PROXIMITY_RADIUS = 200;

    String googlePlacesData = null;
    GoogleMap googleMap;
    OnNearByPlacesCallback mListener;

    public NearByPlacesTask(OnNearByPlacesCallback listener){
        mListener=listener;
    }

    @Override
    protected String doInBackground(Object... objects) {

        googleMap = (GoogleMap) objects[0];
        Location location= (Location) objects[1];

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + location.getLatitude() + "," + location.getLongitude());
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + "point_of_interest");
        googlePlacesUrl.append("&key=" + GOOGLE_SERVER_API_KEY);
        googlePlacesUrl.append("&sensor=true");

        try {
            googlePlacesData=read(googlePlacesUrl.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = s;
        placesDisplayTask.execute(toPass);
    }

    public class PlacesDisplayTask extends AsyncTask<Object,Integer,List<HashMap<String,String>>>{

        JSONObject googlePlacesJson;

        @Override
        protected List<HashMap<String, String>> doInBackground(Object... objects) {
            List<HashMap<String, String>> googlePlacesList = null;
            Places placeJsonParser = new Places();

            try {
                googleMap = (GoogleMap) objects[0];
                googlePlacesJson = new JSONObject((String) objects[1]);
                googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return googlePlacesList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            googleMap.clear();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);

            mListener.onNearByPlacesReceived(list);
            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = list.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                markerOptions.icon(icon);
                googleMap.addMarker(markerOptions);
            }
        }
    }

    public String read(String httpUrl) throws IOException {
        String httpData = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(httpUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            httpData = stringBuffer.toString();
            bufferedReader.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            if(inputStream!=null)
                inputStream.close();
            httpURLConnection.disconnect();
        }
        return httpData;
    }

}
