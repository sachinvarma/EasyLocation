package com.sachinvarma.easylocationsample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.sachinvarma.easylocation.EasyLocationInit;
import com.sachinvarma.easylocation.event.Event;
import com.sachinvarma.easylocation.event.LocationEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.btGetLocation).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        // new EasyLocationInit(context, timeInterval , fastestTimeInterval, runAsBackgroundService);

        //timeInterval -> setInterval(long)(inMilliSeconds) means - set the interval in which you want to get locations.
        //fastestTimeInterval -> setFastestInterval(long)(inMilliSeconds) means - if a location is available sooner you can get it.
        //(i.e. another app is using the location services).
        //runAsBackgroundService = True (Service will run in Background and updates Frequently(according to the timeInterval and fastestTimeInterval))
        //runAsBackgroundService = False (Service will getDestroyed after a successful location update )
        new EasyLocationInit(MainActivity.this, 3000, 3000,false);
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @SuppressLint("SetTextI18n")
  @Subscribe
  public void getEvent(final Event event) {

    if (event instanceof LocationEvent) {
      if (((LocationEvent) event).location != null) {
        ((TextView) findViewById(R.id.tvLocation)).setText("The Latitude is "
          + ((LocationEvent) event).location.getLatitude()
          + " and the Longitude is "
          + ((LocationEvent) event).location.getLongitude());
      }
    }
  }
}
