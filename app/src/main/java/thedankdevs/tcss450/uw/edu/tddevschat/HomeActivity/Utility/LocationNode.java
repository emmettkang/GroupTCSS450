package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.Serializable;

/**
 * The Location Node; contains the primary functionality pertaining to retrieving Location Data
 * from the application database/server. Later passed to the Weather fragment.
 */
public class LocationNode implements Serializable {

    /**
     * The key to
     */
    public static final String LONGITUDE_KEY = "LONGITUDE";

    /*Location Services*/
    /**
     *
     */
    public static final String LATITUDE_KEY = "LATITUDE";
    /**
     *
     */
    public static final String LOCATION_KEY = "LOCATE";
    /**
     *
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /*Location Services*/
    /**
     *
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     *
     */
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private AppCompatActivity myNodeMaster;
    /**
     *
     */
    private LocationRequest mLocationRequest;
    /**
     *
     */
    private Location mCurrentLocation;
    /**
     *
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     *
     */
    private LocationCallback mLocationCallback;


    /**
     * @param MasterActivity
     */
    public LocationNode(AppCompatActivity MasterActivity) {
        setMyNodeMaster(MasterActivity);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MasterActivity);

        if (ActivityCompat.checkSelfPermission(MasterActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MasterActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MasterActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_LOCATIONS);
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("LOCATION", "Error retrieving location");
                }
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        Log.d("LOCATION", "LATITUDE: " + String.valueOf(location.getLatitude()));
                        Log.d("LOCATION", "LONG: " + String.valueOf(location.getLongitude()));

                    }
                }
            }
        };
        createLocationRequest();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getMyNodeMaster(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getMyNodeMaster(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * Create and configure a Location Request used when retrieving location updates
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    /**
     * @param location
     */
    private void setLocation(final Location location) {
        mCurrentLocation = location;
    }

    /**
     *
     */
    /*Method to request the location permissions from the user*/
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(getMyNodeMaster(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getMyNodeMaster(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getMyNodeMaster(), location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            setLocation(location);
                            Log.d("LOCATION", location.toString());
                        }
                    });
        }
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                    requestLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down...maybe ask for permission again?
                    myNodeMaster.finishAndRemoveTask();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * @return
     */
    private AppCompatActivity getMyNodeMaster() {
        return myNodeMaster;
    }

    /**
     * @param myNodeMaster
     */
    private void setMyNodeMaster(AppCompatActivity myNodeMaster) {
        this.myNodeMaster = myNodeMaster;
    }

    /**
     * @return
     */
    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    /**
     * @param mCurrentLocation
     */
    public void setmCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    /*^^^^^^^^^^^^^LOCATION~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/


}