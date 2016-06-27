* 日本語説明はこちら
https://github.com/DeviceConnect/DeviceConnect/blob/master/readme.ja.md

# Migration of DeviceConnect of repository
The repository of DeviceConnect I was moved to the following locations:


DeviceConnect-Docs: Documents & Binaries
https://github.com/DeviceConnect/DeviceConnect-Docs

DeviceConnect-Android: Android Platform Sources
https://github.com/DeviceConnect/DeviceConnect-Android

DeviceConnect-JS: JavaScript Platform Sources
https://github.com/DeviceConnect/DeviceConnect-JS

DeviceConnect-iOS: iOS Platform Sources
https://github.com/DeviceConnect/DeviceConnect-iOS

DeviceConnect-Common: Device App's Sources
https://github.com/DeviceConnect/DeviceConnect-Common



# About DeviceConnect WebAPI
"DeviceConnect WebAPI" is WebAPI which operates as a virtual server on a smart phone. It can use easily various wearable devices and an IoT device by unific description from a web browser or an application. 

# Example for Android
* https://github.com/DeviceConnect/DeviceConnect/blob/master/Bin/demoWebSite.zip

_Updated some package names of the sample for Android at 2014/10/15._
_Please reinstall by following procedure if you would check this sample again._

```
  1. Delete files that are related to the demoWebSite.zip on internal storage. 
  2. Uninstall a APK of old Manager and plugins. 
  3. Choose "CLEAR BROWSING DATA..." in "history" on Chrome Browser menu. 
  4. Choose "Clear" button with "Clear the cache" check box. 
  5. Refer to the procedure for "Example for Android" in "Readme.md".
```

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


##About access from the external devices

By adding the parameter of the IP address to the demoWebSite URL, can control the external device  by DeviceConnect WebAPI  at a local network. However, the following settings are required for terminal on the operation side.

_*Please do not use at network that can not be trusted because there is a security risk._

1. By the procedure as above , set up the DeviceConnect WebAPI to the terminal of the operation target. 

2. Start the DeviceConnectManager from the launcher of Android,and turn off the slide toggle for DeviceConnectManager  service. 
3. Enable "Allow External IP" checkbox, and turn on the slide toggle for DeviceConnectManager  service. 

4. Add the IP address of the operation target in URL of demoWebSite on the operation side terminal.
```
 e.g. "file:///C:/demoWebSite/demo/index.html?ip=192.168.13.3"
 ```



#Supported devices
<table>
 <tr>
    <td>Manufacturer</td>
    <td>Product name</td>
    <td>Device type</td>
    <td>Plug-in</td>
    <td>Android</td>
    <td>iOS</td>
    <td>Remarks</td>
  </tr>
   <tr>
    <td>SONY</td>
    <td>SmartWatchMN2</td>
    <td>Watch</td>
    <td>MN2/SW2 </td>
    <td>○</td>
    <td>×</td>
    <td>Need the app launch on SmartWatch</td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>SmartWatchSW2</td>
    <td>Watch</td>
    <td>MN2/SW2</td>
    <td>○</td>
    <td>×</td>
    <td>Need the app launch on SmartWatch</td>
  </tr>
  <tr>
    <td>Orbotix</td>
    <td>Sphero 2.0</td>
    <td>Toy</td>
    <td>Sphero</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>Game Technologies</td>
    <td>DICE+</td>
    <td>Toy</td>
    <td>DICE+</td>
    <td>○</td>
    <td>○</td>
    <td>Need the firmware for development</td>
  </tr>
  <tr>
    <td>Philips</td>
    <td>hue</td>
    <td>Light</td>
    <td>hue</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>Philips</td>
    <td>Bloom Lamp</td>
    <td>スマートライト</td>
    <td>hue</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>Philips</td>
    <td>LightStrips</td>
    <td>Light</td>
    <td>hue</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>IRKit</td>
    <td>IRKit</td>
    <td>Infrared remote control</td>
    <td>IRKit</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>Epson</td>
    <td>Moverio BT-200</td>
    <td>Glasses</td>
    <td>AndroidHost</td>
    <td>○</td>
    <td>×</td>
    <td>The future works; Support the extension</td>
  </tr>
  <tr>
    <td>Vuzix</td>
    <td>M100 Smart Glass</td>
    <td>Glasses</td>
    <td>AndroidHost</td>
    <td>○</td>
    <td>×</td>
    <td>The future works; Support the extension</td>
  </tr>
  <tr>
    <td>WESTUNITIS</td>
    <td>Inforod</td>
    <td>Glasses</td>
    <td>AndroidHost</td>
    <td>○</td>
    <td>×</td>
    <td>The future works; Support the extension</td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>DSC-QX100</td>
    <td>Camera</td>
    <td>SonyCamera</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>DSC-QX10</td>
    <td>Camera</td>
    <td>SonyCamera</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>SONY</td>
    <td>ActionCam</td>
    <td>Camera</td>
    <td>SonyCamera</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>Pebble</td>
    <td>Pebble</td>
    <td>Watch</td>
    <td>Pebble</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>-</td>
    <td>Android Ver4.2</td>
    <td>Android</td>
    <td>AndroidHost</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>LG</td>
    <td>G Watch</td>
    <td>Android  Wear</td>
    <td>Wear</td>
    <td>○</td>
    <td>×</td>
    <td>Provisional support</td>
  </tr>
  <tr>
    <td>Samsung</td>
    <td>Gear Live</td>
    <td>Android  Wear</td>
    <td>Wear</td>
    <td>○</td>
    <td>×</td>
    <td>Provisional support</td>
  </tr>
  <tr>
    <td>Google</td>
    <td>ChromeCast</td>
    <td>ChromeCast</td>
    <td>ChromeCast</td>
    <td>○</td>
    <td>○</td>
    <td>Need to register the Receiver Apps and device on Google Cast SDK Developer Console.</td>
  </tr>
  <tr>
    <td>Google</td>
    <td>NexusPlayer</td>
    <td>AndroidTV</td>
    <td>ChromeCast</td>
    <td>○</td>
    <td>○</td>
    <td>Need to register the Receiver Apps and device on Google Cast SDK Developer Console.</td>
  </tr>

  <tr>
    <td>Polar</td>
    <td>H7</td>
    <td>Heart rate meter</td>
    <td>BLE HeartRate</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>Mio Global</td>
    <td>Mio Alpha</td>
    <td>Heart rate meter</td>
    <td>BLE HeartRate</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>Mio Global</td>
    <td>Mio Fuse</td>
    <td>Heart rate meter</td>
    <td>BLE HeartRate</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
    <tr>
    <td>EPSON</td>
    <td>Pulsense PS-500</td>
    <td>Heart rate meter</td>
    <td>BLE HeartRate</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
    <tr>
    <td>EPSON</td>
    <td>Pulsense PS-100</td>
    <td>Heart rate meter</td>
    <td>BLE HeartRate</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>NTT DOCOMO</td>
    <td>Hitoe</td>
    <td>Heart rate meter</td>
    <td>Hitoe</td>
    <td>○</td>
    <td>○</td>
    <td>Now under development</td>
  </tr>
  <tr>
    <td>A&D</td>
    <td>UT-201BLE</td>
    <td>Thermometer</td>
    <td></td>
    <td>○</td>
    <td>x</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON</td>
    <td>HVC-C</td>
    <td>Human Vision Components</td>
    <td>HVC</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON</td>
    <td>HVC-C2W</td>
    <td>Human Vision Components</td>
    <td>HVC2W</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>OMRON</td>
    <td>HVC-P</td>
    <td>Human Vision Components</td>
    <td>HVCP</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>FUJITSU</td>
    <td>F-PLUG</td>
    <td>SmartMeter</td>
    <td>F-PLUG</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>RICOH</td>
    <td>THETA m15</td>
    <td>Omnidirectional Camera</td>
    <td>THETA</td>
    <td>○</td>
    <td>○</td>
    <td>Need to register developer and download SDK at RICHO THETA Developers.</td>
  </tr>
    <tr>
    <td>RICOH</td>
    <td>THETA S</td>
    <td>Omnidirectional Camera</td>
    <td>THETA</td>
    <td>○</td>
    <td>○</td>
    <td>Need to register developer and download SDK at RICHO THETA Developers.</td>
  </tr>

  <tr>
    <td>LIFX</td>
    <td>White 800</td>
    <td>Light</td>
    <td>AllJoyn</td>
    <td>○</td>
    <td>○</td>
    <td>No color change.</td>
  </tr>
  <tr>
    <td>LIFX</td>
    <td>Color 1000</td>
    <td>Light</td>
    <td>AllJoyn</td>
    <td>○</td>
    <td>○</td>
    <td></td>
  </tr>
  <tr>
    <td>EchonetLite</td>
    <td>Standard-compliant equipment in general</td>
    <td>Home control</td>
    <td>EchoneLite</td>
    <td>○</td>
    <td>×</td>
    <td>Standard-compliant equipment in general</td>
  </tr>
  <tr>
    <td>Linking</td>
    <td>Tomoru</td>
    <td>BLE</td>
    <td>Linking</td>
    <td>○</td>
    <td>○</td>
    <td>Now under development</td>
  </tr>
  <tr>
    <td>－</td>
    <td>UVC(USB Video Class)</td>
    <td>USBCamera</td>
    <td>UVC</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>－</td>
    <td>Mobile camera</td>
    <td>TV conference,Remote work support</td>
    <td>WebRTC</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>Infinitegra</td>
    <td>OWLIFT</td>
    <td>Thermal camera</td>
    <td></td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>
  <tr>
    <td>FaBo</td>
    <td>FaBo</td>
    <td>IoT HW Prototype kit</td>
    <td>FaBo</td>
    <td>○</td>
    <td>×</td>
    <td></td>
  </tr>

</table>