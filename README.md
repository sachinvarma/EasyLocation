# EasyLocation
A simple Library which will help you to get Location in a single Button click.

**How to Add :**

Via Gradle:

```
repositories {
        maven { url "https://jitpack.io" }
    }
```
```
implementation 'com.github.sachinvarma:EasyLocation:1.0.0'
implementation 'org.greenrobot:eventbus:3.1.1'
```

Via Maven:

```
<dependency>
 <groupId>com.github.sachinvarma</groupId>
 <artifactId>EasyLocation</artifactId>
 <version>1.0.0</version>
</dependency> 
```


**How it works:**

1) Just call

 ```new EasyLocationInit(MainActivity.this, 3000, 3000,false);```
 
 new EasyLocationInit(context, timeInterval , fastestTimeInterval, runAsBackgroundService);
timeInterval -> setInterval(long)(inMilliSeconds) means - set the interval in which you want to get locations.
fastestTimeInterval -> setFastestInterval(long)(inMilliSeconds) means - if a location is available sooner you can get it.
(i.e. another app is using the location services).
runAsBackgroundService = True (Service will run in Background and updates Frequently(according to the timeInterval and fastestTimeInterval))
runAsBackgroundService = False (Service will getDestroyed after a successful location update )

For Example:

```
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.btGetLocation).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        // new EasyLocationInit(context, timeInterval , fastestTimeInterval, runAsBackgroundService);\n

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

```

**LICENSE**
```
Copyright (C) 2018 Sachin Varma

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```
 



