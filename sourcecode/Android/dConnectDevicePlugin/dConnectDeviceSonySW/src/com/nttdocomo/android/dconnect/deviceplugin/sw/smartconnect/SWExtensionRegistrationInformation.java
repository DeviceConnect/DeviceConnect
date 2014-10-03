/*
Copyright (c) 2011 Sony Ericsson Mobile Communications AB
Copyright (C) 2012 Sony Mobile Communications AB

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB nor the names
  of its contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

 * Neither the name of the Sony Mobile Communications AB nor the names
  of its contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.nttdocomo.android.dconnect.deviceplugin.sw.smartconnect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.nttdocomo.android.dconnect.deviceplugin.sw.R;
import com.nttdocomo.android.dconnect.deviceplugin.sw.SWConstants;
import com.nttdocomo.android.dconnect.deviceplugin.sw.setting.SWSettingMainActivity;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.HostApplicationInfo;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensor;

/**
ExtensionRegistrationInformation
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * Provides information needed during extension registration.
 */
public class SWExtensionRegistrationInformation extends RegistrationInformation {
    /**
     * Context.
     */
    final Context mContext;
    /**
     * extensionKey.
     */
    private String extensionKey;

    /**
     * Creates a Sensor registration object.
     *
     * @param context The context
     */
    protected SWExtensionRegistrationInformation(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        mContext = context;
    }

    @Override
    public int getRequiredControlApiVersion() {
        return 1;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return 1;
    }

    @Override
    public int getRequiredNotificationApiVersion() {
        return 1;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        return API_NOT_REQUIRED;
    }

    /**
     * Returns the properties of this extension.
     *
     * @return The registration configuration.
     */
    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
        String iconHostapp = ExtensionUtils.getUriString(mContext, R.drawable.icon);
        String iconExtension = ExtensionUtils.getUriString(mContext, R.drawable.icon_extension);

        ContentValues values = new ContentValues();

        values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY, SWSettingMainActivity.class.getName());
        values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT, "SW Device Plugin");
        values.put(Registration.ExtensionColumns.NAME, "SW Device Plugin");
        values.put(Registration.ExtensionColumns.EXTENSION_KEY, getExtensionKey());
        values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI, iconExtension);
        values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
                getRequiredNotificationApiVersion());
        values.put(Registration.ExtensionColumns.PACKAGE_NAME, mContext.getPackageName());

        return values;
    }

    @Override
    public ContentValues[] getSourceRegistrationConfigurations() {
        // This sample only adds one source but it is possible to add more
        // sources if needed.
        List<ContentValues> values = new ArrayList<ContentValues>();
        values.add(getSourceRegistrationConfiguration(SWConstants.EXTENSION_SPECIFIC_ID));
        return values.toArray(new ContentValues[values.size()]);
    }

    /**
     * Returns the properties of a source.
     *
     * @param extensionSpecificId The id of the extension to associate the source
     * with.
     * @return The source configuration.
     */
    public ContentValues getSourceRegistrationConfiguration(final String extensionSpecificId) {
        ContentValues sourceValues = new ContentValues();
        sourceValues.put(Notification.SourceColumns.ENABLED, true);
        sourceValues.put(Notification.SourceColumns.UPDATE_TIME, System.currentTimeMillis());
        sourceValues.put(Notification.SourceColumns.NAME, "Notification source");
        sourceValues.put(Notification.SourceColumns.EXTENSION_SPECIFIC_ID, extensionSpecificId);
        sourceValues.put(Notification.SourceColumns.PACKAGE_NAME, mContext.getPackageName());
        return sourceValues;
    }

    @Override
    public boolean isDisplaySizeSupported(final int width, final int height) {
        return (SWControlExtension.isWidthSupported(mContext, width) && SWControlExtension
                .isHeightSupported(mContext, height));
    }

    @Override
    public boolean isSensorSupported(final AccessorySensor sensor) {
        return Registration.SensorTypeValue.ACCELEROMETER.equals(sensor.getType().getName());
    }

    @Override
    public boolean isSupportedSensorAvailable(final Context context, final HostApplicationInfo hostApplication) {
        // Both control and sensor needs to be supported to register as sensor.
        return super.isSupportedSensorAvailable(context, hostApplication)
                && super.isSupportedControlAvailable(context, hostApplication);
    }

    @Override
    public boolean isSupportedControlAvailable(final Context context, final HostApplicationInfo hostApplication) {
        // Both control and sensor needs to be supported to register as control.
        return super.isSupportedSensorAvailable(context, hostApplication)
                && super.isSupportedControlAvailable(context, hostApplication);
    }

    /**
     * A basic implementation of getExtensionKey
     * Returns and saves a random string based on UUID.randomUUID()
     *
     * Note that this implementation doesn't guarantee random numbers on Android 4.3 and older. 
     * See <a href="https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html">
     * https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html</a>
     *
     * @return A saved key if it exists, otherwise a randomly generated one.
     * @see com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation#getExtensionKey()
     */
    @Override
    public synchronized String getExtensionKey() {
        if (TextUtils.isEmpty(extensionKey)) {
            // Retrieve key from preferences
            SharedPreferences pref = mContext.getSharedPreferences(SWConstants.EXTENSION_KEY_PREF,
                    Context.MODE_PRIVATE);
            extensionKey = pref.getString(SWConstants.EXTENSION_KEY_PREF, null);
            if (TextUtils.isEmpty(extensionKey)) {
                // Generate a random key if not found
                extensionKey = UUID.randomUUID().toString();
                pref.edit().putString(SWConstants.EXTENSION_KEY_PREF, extensionKey).commit();
            }
        }
        return extensionKey;
    }
}
