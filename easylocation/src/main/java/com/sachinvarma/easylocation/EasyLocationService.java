package com.sachinvarma.easylocation;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sachinvarma.easylocation.event.LocationEvent;
import org.greenrobot.eventbus.EventBus;

import static android.content.ContentValues.TAG;

/**
 * <p>Custom class extending Service. This class is used to track the user location after every
 * predefined
 * time interval or distance. To further save the user's battery, this Service will stop when the
 * user
 * isn't viewing our app which include cases like - the user has locked the device or user is now
 * viewing
 * some other app then ours.</p>
 * <p>This class will get the user location after some time/distance interval and update the
 * database
 * with that location. If the user changes the current city i.e. user move to a different city then
 * the stored responses for HomePage, DrawerMenu & LandingPage will be deleted</p>
 */
public class EasyLocationService extends Service
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
  LocationListener {

  private PowerManager.WakeLock cpuWakeLock;
  private NotificationManager mNotificationManager;

  private GoogleApiClient googleApiClient;
  private boolean locationFound = false;
  private Location location;
  private String user_activity = "";
  private int timeInterval = 0;
  private int fastestTimeInterval = 0;
  private boolean runAsBackgroundService = false;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "onStart called");
    if (intent != null) {
      timeInterval = intent.getIntExtra(EasyLocationConstants.TIME_INTERVAL, 0);
      fastestTimeInterval = intent.getIntExtra(EasyLocationConstants.FASTEST_TIME_INTERVAL, 0);
      runAsBackgroundService =
        intent.getBooleanExtra(EasyLocationConstants.RUN_AS_BACKGROUND_SERVICE, false);
    }
    getLocationUpdate();
    return Service.START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    //sendNotification("Location Service stopped", "Stopping location service");
    // Release in onDestroy of your service
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  /**
   * Function called when we want to get location from GPS. This function will take care of asking
   * permission,
   * swiching on GPS and getting location. To get location, just call this function.
   */
  private void getLocationUpdate() {
    if (googleApiClient == null) {
      initLocation();
    } else if (googleApiClient.isConnected()) {
      startLocationUpdate();
    } else if (!googleApiClient.isConnected()) {
      connectGoogleAPIClient();
    }
  }

  /**
   * Function to check for location permission, availability of Google play services and
   * initialization of GoogleApiClient for getting user location
   */
  private void initLocation() {
    // Checking if we have have permission to access user location, if not, asking for permission
    if (isGooglePlayServicesAvailable()) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
        initializeFusionLocation();
      } else {
        initializeFusionLocation();
      }
    } else {
      //show error dialog if GooglePlayServices not available
      // Dismissing alert or progress dialog if available
      Log.e(TAG, "Google play services not found");
    }
  }

  /**
   * Function to check if we have Google Play Service avaialable, without which fusion location
   * won't work
   *
   * @return True or False depending on whether we have Google Play installed or not
   */
  private boolean isGooglePlayServicesAvailable() {
    int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
    if (ConnectionResult.SUCCESS == status) {
      return true;
    } else {
      //GoogleApiAvailability.getInstance().getErrorDialog(this, status, 0).show();
      return false;
    }
  }

  /**
   * Function to initialize fusion location for getting user location
   */
  private void initializeFusionLocation() {
    if (googleApiClient == null) {
      googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
      googleApiClient.connect();
    }
    if (isGPSActivated()) {
      getLocationUpdate();
    }
  }

  /**
   * Function to connect to Google API client. Also, we create a CountDownTimer which detects if
   * we getCartProductDetail location in 10 seconds
   */
  private void connectGoogleAPIClient() {
    if (isGPSActivated()) {
      //showProgress(getString(R.string.please_wait));
      googleApiClient.connect();
      locationFound = false;
    }
  }

  private boolean isGPSActivated() {
    return ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(
      LocationManager.GPS_PROVIDER);
  }

  /**
   * Function to stop listening for location updates
   */
  private void stopLocationUpdate() {
    if (googleApiClient != null && googleApiClient.isConnected()) {
      Log.d(TAG, "Stopping location update service");
      LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }
  }

  /**
   * Function to start listening for location updates
   */
  private void startLocationUpdate() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
      == PackageManager.PERMISSION_GRANTED) {
      if (isGPSActivated()) {
        Log.d(TAG, "Starting location update service");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(timeInterval);
        locationRequest.setFastestInterval(fastestTimeInterval);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
         locationRequest, this);
        connectGoogleAPIClient();
        //showProgress(getString(R.string.please_wait));
      } else {
        initializeFusionLocation();
      }
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    Log.d(TAG, "Connected, sending location request");
    if (isGPSActivated()) {
      initializeFusionLocation();
    } else {
      connectGoogleAPIClient();
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
    locationFound = true;
    this.location = location;
    // Once we get location, we can disconnect Google API client

    // If for some reasons location returned was null, dismissing the dialog and returning
    if (location == null) {
      return;
    }

    LocationEvent event = new LocationEvent();
    event.location = location;
    EventBus.getDefault().post(event);

    if (!runAsBackgroundService) {
      stopSelf();
    }
  }
}
