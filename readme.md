* 日本語説明はこちら
https://github.com/DeviceConnect/DeviceConnect/blob/master/readme.ja.md

# About DeviceConnect WebAPI
"DeviceConnect WebAPI" is WebAPI which operates as a virtual server on a smart phone. It can use easily various wearable devices and an IoT device by unific description from a web browser or an application. 

# Example for Android
* https://github.com/DeviceConnect/DeviceConnect/blob/master/Bin/demoWebSite.zip

1.Make this Folder to Android's internal Storage.
```
Android root
   └── mnt
       └── sdcard
           └── dConnect
                └── demoWebSite
```

2.Access the internal file URI with Chrome browser.
```
  e.g. "file:///storage/emulated/0/dConnect/demoWebSite/index.html"
```

3.Install the Manager APK and the Android Host APK from "Download APK".
  If you have other supported gadgets , install other APKs.

4.Go back to the top page.

5.Try the prototype GotAPI behavior from "Launch UI-App".

#List of the corresponding device
<table>
  <tr>
    <td>Manufacturer</td>
    <td>Product name</td>
    <td>Device type</td>
    <td>Plug-in</td>
    <td>Remarks</td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>SmartWatchMN2</td>
    <td>Watch</td>
    <td>MN2/SW2</td>
    <td>Need the app launch on SmartWatch</td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>SmartWatchSW2</td>
    <td>Watch</td>
    <td>MN2/SW2</td>
    <td>Need the app launch on SmartWatch</td>
  </tr>
  <tr>
    <td>Orbotix</td>
    <td>Sphero 2.0</td>
    <td>Toy</td>
    <td>Sphero</td>
    <td></td>
  </tr>
  <tr>
    <td>Game Technologies</td>
    <td>DICE+</td>
    <td>Toy</td>
    <td>DICE+</td>
    <td>Need the firmware for development</td>
  </tr>
  <tr>
    <td>Philips</td>
    <td>hue</td>
    <td>Light</td>
    <td>hue</td>
    <td></td>
  </tr>
  <tr>
    <td>Philips</td>
    <td>Bloom Lamp</td>
    <td>Light</td>
    <td>hue</td>
    <td></td>
  </tr>
  <tr>
    <td>Philips</td>
    <td>LightStrips</td>
    <td>Light</td>
    <td>hue</td>
    <td></td>
  </tr>
  <tr>
    <td>IRKit</td>
    <td>IRKit</td>
    <td>Infrared remote control</td>
    <td>IRKit</td>
    <td></td>
  </tr>
  <tr>
    <td>Epson</td>
    <td>Moverio BT-200</td>
    <td>Glasses</td>
    <td>Android Host</td>
    <td>The future works; Support the extension</td>
  </tr>
  <tr>
    <td>Vuzix</td>
    <td>M100 Smart Glass</td>
    <td>Glasses</td>
    <td>Android Host</td>
    <td>The future works; Support the extension</td>
  </tr>
  <tr>
    <td>WESTUNITIS</td>
    <td>Inforod</td>
    <td>Glasses</td>
    <td>Android Host</td>
    <td>The future works; Support the extension</td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>DSC-QX100</td>
    <td>Camera</td>
    <td>SonyCamera</td>
    <td></td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>DSC-QX10</td>
    <td>Camera</td>
    <td>SonyCamera</td>
    <td></td>
  </tr>
  <tr>
    <td>Pebble</td>
    <td>Pebble</td>
    <td>Watch</td>
    <td>Pebble</td>
    <td></td>
  </tr>
  <tr>
    <td>-</td>
    <td>Android Ver4.0</td>
    <td>Android</td>
    <td>Android Host</td>
    <td></td>
  </tr>
  <tr>
    <td>LG</td>
    <td>G Watch</td>
    <td>Android  Wear</td>
    <td>Wear</td>
    <td>Provisional support</td>
  </tr>
  <tr>
    <td>Samsung</td>
    <td>Gear Live</td>
    <td>Android  Wear</td>
    <td>Wear</td>
    <td>Provisional support</td>
  </tr>
  <tr>
    <td>Google</td>
    <td>ChromeCast</td>
    <td>ChromeCast</td>
    <td>Chromecast</td>
    <td>Need to register the Receiver Apps and device on Google Cast SDK Developer Console.</td>
  </tr>
  <tr>
    <td>AND</td>
    <td>UA-767PBT-C</td>
    <td>Sphygmomanometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>AND</td>
    <td>UA-851PBT-C</td>
    <td>Sphygmomanometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>AND</td>
    <td>TM-2656VPM</td>
    <td>Sphygmomanometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>AND</td>
    <td>UC-321PBT-C</td>
    <td>Weight scale</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON HEALTHCARE</td>
    <td>HEM-708-IT</td>
    <td>Sphygmomanometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON HEALTHCARE</td>
    <td>HBF-206IT</td>
    <td>Body composition monitors</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON HEALTHCARE</td>
    <td>HHX-IT1</td>
    <td>Activity meter</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>AND</td>
    <td>UA-772</td>
    <td>Sphygmomanometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>AND</td>
    <td>UW201</td>
    <td>Activity meter</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON HEALTHCARE</td>
    <td>HEM-7250IT</td>
    <td>Sphygmomanometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON HEALTHCARE</td>
    <td>HBF-208IT</td>
    <td>Body composition monitors</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON HEALTHCARE</td>
    <td>HBF-215IT</td>
    <td>Body composition monitors</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>ESTERA</td>
    <td>FS-500</td>
    <td>Pedometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>ESTERA</td>
    <td>FS-700</td>
    <td>Activity meter</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>YAMASA</td>
    <td>EX-950</td>
    <td>Pedometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>TERUMO</td>
    <td>MSFV01</td>
    <td>Blood glucose meter</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>TERUMO</td>
    <td>MT-KT02DZ</td>
    <td>Walking intensity meter</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>TERUMO</td>
    <td>C215</td>
    <td>Thermometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>TERUMO</td>
    <td>ES-H700D</td>
    <td>Sphygmomanometer</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>TERUMO</td>
    <td>ZS-NS05</td>
    <td>Pulse Oximeter</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>TERUMO</td>
    <td>WT-B100DZ</td>
    <td>Body composition monitors</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>Polar</td>
    <td>H7</td>
    <td>Heart rate meter</td>
    <td>mHealth</td>
    <td></td>
  </tr>
  <tr>
    <td>Mio Global</td>
    <td>Mio Alpha</td>
    <td>Heart rate meter</td>
    <td>mHealth</td>
    <td></td>
  </tr>
</table>
