package com.webwerks.quickbloxdemo.chat.location;

import android.Manifest;
import android.databinding.ViewDataBinding;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.model.ShareLocationModel;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;
import com.webwerks.quickbloxdemo.utils.PermissionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by webwerks on 3/5/17.
 */

public class SendLocationActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        LocationListener,OnNearByPlacesCallback{

    GoogleApiClient mGoogleApiClient;
    GoogleMap mGoogleMap;
    Location mLastLocation;
    Marker mCurrentLocMarker;

    @Override
    public int getContentLayout() {
        return R.layout.activity_send_location;
    }

    @Override
    public void initializeUiComponents(ViewDataBinding binding) {
        setToolbarTitle("Send Location");
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng defaultLoc = new LatLng(19.0760, 72.8777);
        mCurrentLocMarker=mGoogleMap.addMarker(new MarkerOptions().position(defaultLoc).title("Mumbai"));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc,17));

        if (PermissionManager.askForPermissions(1, this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                "Do you want to access Location ?")) {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }*/
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .addApi(com.google.android.gms.location.places.Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setNumUpdates(1);

        if (PermissionManager.askForPermissions(1, this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                "Do you want to access Location ?")) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            callCurrentAndNearByPlaces();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        //((TextView)findViewById(R.id.lbl_current_loc)).setText();
        if (mCurrentLocMarker != null) {
            mCurrentLocMarker.remove();
        }

        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(location.getProvider());

        mCurrentLocMarker=mGoogleMap.addMarker(markerOptions);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

//        NearByPlacesTask googlePlacesReadTask = new NearByPlacesTask(this);
//        Object[] toPass = new Object[2];
//        toPass[0] = mGoogleMap;
//        toPass[1] = location;
//        googlePlacesReadTask.execute(toPass);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    List<ShareLocationModel> shareLocationModels;

    public void callCurrentAndNearByPlaces(){
        shareLocationModels = new ArrayList<ShareLocationModel>(  );
        PendingResult<PlaceLikelihoodBuffer> result = com.google.android.gms.location.places.Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                final CharSequence thirdPartyAttributions = placeLikelihoods.getAttributions();
                Log.e("result",placeLikelihoods.toString());
                for (PlaceLikelihood placeLikelihood : placeLikelihoods) {

                    ShareLocationModel shareLocationModel = new ShareLocationModel();
                    LatLng lat_lng = placeLikelihood.getPlace().getLatLng();
                    shareLocationModel.setLatitude( lat_lng.latitude );
                    shareLocationModel.setLongitude( lat_lng.longitude );
                    shareLocationModel.setLocationName( placeLikelihood.getPlace().getName().toString() );
                    shareLocationModel.setLocationDesc( placeLikelihood.getPlace().getAddress().toString() );
                    shareLocationModel.setPlaceID( placeLikelihood.getPlace().getId() );
                    shareLocationModels.add( shareLocationModel );
                }
                if ( shareLocationModels != null && shareLocationModels.size()>0) {
                    ShareLocationModel shareLocation = shareLocationModels.get( 0 );
                    ListView lstPlaces= (ListView) findViewById(R.id.lst_near_by);
                    lstPlaces.setAdapter(new PlacesAdapter(SendLocationActivity.this,shareLocationModels));
                }else {
                    Toast.makeText(SendLocationActivity.this, " Please check your GPS Connection", Toast.LENGTH_LONG ).show();
                }

                placeLikelihoods.release();
            }
        });
    }

    @Override
    public void onNearByPlacesReceived(List<HashMap<String, String>> list) {
        ListView lstPlaces= (ListView) findViewById(R.id.lst_near_by);
        //lstPlaces.setAdapter(new PlacesAdapter(this,list));

    }
}
