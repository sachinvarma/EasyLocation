# EasyLocation
A simple Library which will help you to get Location in a single Button click.

![](https://github.com/sachinvarma/EasyLocation/blob/master/Art/demo.gif)

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

<dependency>
    <groupId>org.greenrobot</groupId>
    <artifactId>eventbus</artifactId>
    <version>3.1.1</version>
</dependency>

```


**How it works:**

1) Just call

 ```new EasyLocationInit(MainActivity.this, timeInterval, fastestTimeInterval, runAsBackgroundService);```
 
 
**timeInterval** -> setInterval(long)(inMilliSeconds) means - set the interval in which you want to get locations.
**fastestTimeInterval** -> setFastestInterval(long)(inMilliSeconds) means - if a location is available sooner you can get it.
(i.e. another app is using the location services).

**runAsBackgroundService** = True (Service will run in Background and updates Frequently(according to the timeInterval and fastestTimeInterval))

**runAsBackgroundService** = False (Service will getDestroyed after a successful location update )

2) Prepare EventBus subscribers: Declare and annotate your subscribing method, optionally specify a thread mode:

```
@Subscribe(threadMode = ThreadMode.MAIN)  
public void getEvent(Event event) {/* Do something */};
```

Register and unregister your subscriber. For example on Android, activities and fragments should usually register according to their life cycle:

```
 @Override
 public void onStart() {
     super.onStart();
     EventBus.getDefault().register(this);
 }

 @Override
 public void onStop() {
     super.onStop();
     EventBus.getDefault().unregister(this);
 }
 
 ```

3) Location will be received in

```
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
```

For more Details - > 

https://github.com/sachinvarma/EasyLocation/blob/master/app/src/main/java/com/sachinvarma/easylocationsample/MainActivity.java


Donations
=============

This project needs you! If you would like to support this project's further development, the creator of this project or the continuous maintenance of this project, feel free to donate. Your donation is highly appreciated (and I love food, coffee and beer). Thank you!

**PayPal**

* **[Donate $5](https://www.paypal.me/sachinvarmaraja/5USD)**: Thank's for creating this project, here's a tea (or some juice) for you!
* **[Donate $10](https://www.paypal.me/sachinvarmaraja/10USD)**: Wow, I am stunned. Let me take you to the movies!
* **[Donate $15](https://www.paypal.me/sachinvarmaraja/15USD)**: I really appreciate your work, let's grab some lunch!
* **[Donate $25](https://www.paypal.me/sachinvarmaraja/25USD)**: That's some awesome stuff you did right there, dinner is on me!
* **[Donate $50](https://www.paypal.me/sachinvarmaraja/50USD)**: I really really want to support this project, great job!
* **[Donate $100](https://www.paypal.me/sachinvarmaraja/100USD)**: You are the man! This project saved me hours (if not days) of struggle and hard work, simply awesome!
* **[Donate $2799](https://www.paypal.me/sachinvarmaraja/2799USD)**: Go buddy, buy Macbook Pro for yourself!

Of course, you can also choose what you want to donate, all donations are awesome!

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
 



