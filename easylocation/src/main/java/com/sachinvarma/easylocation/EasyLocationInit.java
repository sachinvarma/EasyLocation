package com.sachinvarma.easylocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import static com.sachinvarma.easylocation.EasyLocationConstants.LOCATION_REQUEST_CODE;

public class EasyLocationInit extends Intent {

  public EasyLocationInit(
    @NonNull final Activity context,
    int timeInterval,
    int fastestTimeInterval,
    boolean runAsBackgroundService
  ) {

    Intent intent = new Intent();
    intent.setClass(context, EasyLocation.class);
    intent.putExtra(EasyLocationConstants.TIME_INTERVAL, timeInterval);
    intent.putExtra(EasyLocationConstants.FASTEST_TIME_INTERVAL, fastestTimeInterval);
    intent.putExtra(EasyLocationConstants.RUN_AS_BACKGROUND_SERVICE, runAsBackgroundService);
    context.startActivityForResult(intent, LOCATION_REQUEST_CODE);
  }
}
