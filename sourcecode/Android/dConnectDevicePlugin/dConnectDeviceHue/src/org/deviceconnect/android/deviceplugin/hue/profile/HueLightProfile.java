/*
HueLightProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue.profile;

import org.deviceconnect.android.deviceplugin.hue.HueDeviceService;
import org.deviceconnect.android.deviceplugin.hue.profile.attribute.HueGroupAttribute;
import org.deviceconnect.android.deviceplugin.hue.profile.attribute.HueLightAttribute;
import org.deviceconnect.android.deviceplugin.param.DcParam.DcParamException;
import org.deviceconnect.android.profile.LightProfile;

import android.content.Intent;


/**
 * 親クラスで振り分けられたメソッドに対して、Hueのlight attribute処理を呼び出す.
 * @author NTT DOCOMO, INC.
 */
public class HueLightProfile extends LightProfile {

    @Override
    protected boolean onGetLight(final Intent request, final Intent response) throws DcParamException {
        // Light Status API
        HueLightAttribute att = new HueLightAttribute((HueDeviceService) getContext());
        return att.getState(request, response);
    }

    @Override
    protected boolean onPostLight(final Intent request, final Intent response) throws DcParamException {
        // Light On API
        HueLightAttribute att = new HueLightAttribute((HueDeviceService) getContext());
        return att.updateLightState(request, response);
    }

    @Override
    protected boolean onDeleteLight(final Intent request, final Intent response) throws DcParamException {
        // Light Off API
        HueLightAttribute att = new HueLightAttribute((HueDeviceService) getContext());
        return att.deleteLight(request, response);
    }

    @Override
    protected boolean onPutLight(final Intent request, final Intent response) throws DcParamException {
        // Light Status Change API
        HueLightAttribute att = new HueLightAttribute((HueDeviceService) getContext());
        return att.changeName(request, response);
    }

    @Override
    protected boolean onGetLightGroup(final Intent request, final Intent response) throws DcParamException {
        // Light Group Status API
        HueGroupAttribute att = new HueGroupAttribute((HueDeviceService) getContext());
        return att.getState(request, response);
    }

    @Override
    protected boolean onPostLightGroup(final Intent request, final Intent response) throws DcParamException {
        // Light Group On API
        HueGroupAttribute att = new HueGroupAttribute((HueDeviceService) getContext());
        return att.updateLightState(request, response);
    }

    @Override
    protected boolean onDeleteLightGroup(final Intent request, final Intent response) throws DcParamException {
        // Light Group Off API
        HueGroupAttribute att = new HueGroupAttribute((HueDeviceService) getContext());
        return att.deleteLight(request, response);
    }

    @Override
    protected boolean onPutLightGroup(final Intent request, final Intent response) throws DcParamException {

        HueGroupAttribute att = new HueGroupAttribute((HueDeviceService) getContext());
        return att.changeName(request, response);
    }

    @Override
    protected boolean onPostLightGroupCreate(final Intent request, final Intent response) throws DcParamException {
        // Light Group Create API
        HueGroupAttribute att = new HueGroupAttribute((HueDeviceService) getContext());
        return att.createGroup(request, response);

    }

    @Override
    protected boolean onDeleteLightGroupClear(final Intent request, final Intent response) throws DcParamException {
        // Light Group Clear API
        HueGroupAttribute att = new HueGroupAttribute((HueDeviceService) getContext());
        return att.deleteGroup(request, response);
    }

}
