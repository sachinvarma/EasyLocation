package com.sachinvarma.easylocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;

public class EasyLocation extends Activity
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
  LocationListener {

  private static final int REQUEST_CODE_LOCATION_PERMISSION = 100;
  private static final int REQUEST_CODE_LOCATION_SETTINGS = 1;
  private static final String TAG = "EASYLOCATION";

  private GoogleApiClient googleApiClient;
  private LocationRequest locationRequest;
  private int timeInterval = 0;
  private int fastestTimeInterval = 0;
  private boolean runAsBackgroundService = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getIntent() != null) {
      timeInterval = getIntent().getIntExtra(EasyLocationConstants.TIME_INTERVAL, 0);
      fastestTimeInterval = getIntent().getIntExtra(EasyLocationConstants.FASTEST_TIME_INTERVAL, 0);
      runAsBackgroundService =
        getIntent().getBooleanExtra(EasyLocationConstants.RUN_AS_BACKGROUND_SERVICE, false);
    }
    initLocation();
  }

  @Override
  public void onRequestPermissionsResult(
    int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED) {*/
        //}
        initializeFusionLocation();
      } else {
        //presenter.sendSetLocationRequest(currentLocation);
        Toast.makeText(EasyLocation.this, "Oops! , Permission denied can't access Location",
          Toast.LENGTH_SHORT).show();
        finish();
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
      initLocation();
    }
  }

  /**
   * Intialiasing Location
   */
  private void initLocation() {
    // Checking if we have have permission to access user location, if not, asking for permission
    if (isGooglePlayServicesAvailable()) {
      if (Build.VERSION.SDK_INT >= M) {
        int locationPermission =
          ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        /*int storagePermission =
          ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);*/
        List<String> permissions = new ArrayList<>();

        if (locationPermission == PackageManager.PERMISSION_DENIED) {
          permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
          permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        /*if (storagePermission == PackageManager.PERMISSION_DENIED) {
          permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/
        if (locationPermission == PackageManager.PERMISSION_DENIED
          /*|| storagePermission == PackageManager.PERMISSION_DENIED*/) {
          ActivityCompat.requestPermissions(this,
            permissions.toArray(new String[permissions.size()]), REQUEST_CODE_LOCATION_PERMISSION);
        } else {
          initializeFusionLocation();
        }
      } else {
        initializeFusionLocation();
      }
    } else {
      Toast.makeText(this, "Google Play Service is not available.", Toast.LENGTH_SHORT).show();
      finish();
    }
  }

  /**
   * Checking whether google play services enable or not.
   *
   * @return true or false
   */
  private boolean isGooglePlayServicesAvailable() {
    int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
    if (ConnectionResult.SUCCESS == status) {
      return true;
    } else {
      if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
        GoogleApiAvailability.getInstance().getErrorDialog(this, status, 1000).show();
      }
      return false;
    }
  }

  /**
   * Initialisation of Fused Location
   */
  private void initializeFusionLocation() {
    if (googleApiClient == null) {
      googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
      googleApiClient.connect();
    }
    locationChecker(googleApiClient);
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {

  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public void onLocationChanged(Location location) {

    Log.d("NewLocation", location.getLatitude() + " : " + location.getLongitude());
    finish();
  }

  /**
   * Checking Location Permissions and Gps enabled or not
   *
   * @param mGoogleApiClient apiCleint
   */
  public void locationChecker(GoogleApiClient mGoogleApiClient) {
    locationRequest = LocationRequest.create();
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval(timeInterval);
    locationRequest.setFastestInterval(fastestTimeInterval);
    LocationSettingsRequest.Builder builder =
      new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
    builder.setAlwaysShow(true);
    PendingResult<LocationSettingsResult> result =
      LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
      @SuppressLint("RestrictedApi")
      @Override
      public void onResult(@NonNull LocationSettingsResult result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
          case LocationSettingsStatusCodes.SUCCESS:
            // All location settings are satisfied. The client can initialize location
            // requests here.
            Intent intent = new Intent();
            intent.setClass(EasyLocation.this, EasyLocationService.class);
            intent.putExtra(EasyLocationConstants.TIME_INTERVAL, timeInterval);
            intent.putExtra(EasyLocationConstants.FASTEST_TIME_INTERVAL, fastestTimeInterval);
            intent.putExtra(EasyLocationConstants.RUN_AS_BACKGROUND_SERVICE,
              runAsBackgroundService);
            startService(intent);
            finish();

            break;
          case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            // Location settings are not satisfied. But could be fixed by showing the user
            // a dialog.
            try {
              // Show the dialog by calling startResolutionForResult(),
              // and check the result in onActivityResult().
              //status.startResolutionForResult(
              //  getActivity(), 1000);

              startIntentSenderForResult(status.getResolution().getIntentSender(),
                REQUEST_CODE_LOCATION_SETTINGS, null, 0, 0, 0, null);
            } catch (IntentSender.SendIntentException e) {
              // Ignore the error.
            }
            break;
          case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            // Location settings are not satisfied. However, we have no way to fix the
            // settings so we won't show the dialog.
            break;
          case LocationSettingsStatusCodes.CANCELED:
            break;
        }
      }
    });
  }

  /**
   * Location update and connection to ApiClient according to situations.
   */
  public void activityVisible() {
    if (googleApiClient == null) {
      initLocation();
    } else if (googleApiClient.isConnected()) {
      startLocationUpdate();
    } else if (!googleApiClient.isConnected()) {
      connectGoogleAPIClient();
    }
  }

  /**
   * Start location update
   */
  private void startLocationUpdate() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
      == PackageManager.PERMISSION_GRANTED) {
      if (isGPSActivated()) {
        Log.d(TAG, "Starting location update service");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
          this);
        connectGoogleAPIClient();
      } else {
        initializeFusionLocation();
      }
    }
  }

  /**
   * Connecting to the GoogleApiClient.
   */
  private void connectGoogleAPIClient() {
    if (isGPSActivated()) {
      googleApiClient.connect();
    }
  }

  /**
   * Checking whether is Gps is activated or not.
   *
   * @return true or false.
   */
  private boolean isGPSActivated() {
    return ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(
      LocationManager.GPS_PROVIDER);
  }

  /**
   * Stop location update.
   */
  private void stopLocationUpdate() {
    if (googleApiClient != null && googleApiClient.isConnected()) {
      Log.d(TAG, "Stopping location update service");
      LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onStop() {

    if (googleApiClient != null) {
      stopLocationUpdate();
      // removing memory leak due to Google API client
      googleApiClient.unregisterConnectionCallbacks(this);
      googleApiClient.unregisterConnectionFailedListener(this);
      if (googleApiClient.isConnected()) {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
      }
      googleApiClient.disconnect();
      googleApiClient = null;
    }
    super.onStop();
    Log.d(TAG, "On Stop");
  }

}
