/*
 * Copyright 2013 Sony Corporation
 */

package com.example.sony.cameraremote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.sony.cameraremote.utils.SimpleHttpClient;
import com.example.sony.cameraremote.utils.XmlElement;

/**
 * ServerDevice.
 */
public final class ServerDevice {


    /** Index Number. */
    private static final int URL_TO_HOST_INDEX = 3;

    /**
     * Camera Remote API service (category). For example, "camera", "guide" and
     * so on. "Action List URL" is API request target URL of each service.
     */
    public static class ApiService {
        /** 名前の定義. */
        private String mName;
        /** APIリスト. */
        private String mActionListUrl;

        /**
         * Constructor.
         * 
         * @param name category name
         * @param actionListUrl action list URL of the category
         */
        public ApiService(final String name, final String actionListUrl) {
            mName = name;
            mActionListUrl = actionListUrl;
        }

        /**
         * Returns the category name.
         * 
         * @return category name.
         */
        public String getName() {
            return mName;
        }

        /**
         * Sets a category name.
         * 
         * @param name category name
         */
        public void setName(final String name) {
            this.mName = name;
        }

        /**
         * Returns the action list URL of the category.
         * 
         * @return action list URL
         */
        public String getActionListUrl() {
            return mActionListUrl;
        }

        /**
         * Sets an action list URL of the category.
         * 
         * @param actionListUrl action list URL of the category
         */
        public void setActionListUrl(final String actionListUrl) {
            this.mActionListUrl = actionListUrl;
        }

        /**
         * Returns the endpoint URL of the category.
         * 
         * @return endpoint URL
         */
        public String getEndpointUrl() {
            String url = null;
            if (mActionListUrl == null || mName == null) {
                url = null;
            } else if (mActionListUrl.endsWith("/")) {
                url = mActionListUrl + mName;
            } else {
                url = mActionListUrl + "/" + mName;
            }
            return url;
        }
    }

    /** Device Description Xml. */
    private String mDDUrl;
    /** Device Description Friendlyname. */
    private String mFriendlyName;
    /** Device Description modelName. */
    private String mModelName;
    /** Device Description UDN. */
    private String mUDN;
    /** Icons Url. */
    private String mIconUrl;
    /** List of ApiServices. */
    private List<ApiService> mApiServices;

    /** get Api Servicees List. */
    private ServerDevice() {
        mApiServices = new ArrayList<ServerDevice.ApiService>();
    }

    /**
     * Returns URL of Device Description XML.
     * 
     * @return URL string
     */
    public String getDDUrl() {
        return mDDUrl;
    }

    /**
     * Returns a value of friendlyName in DD.
     * 
     * @return mFriendlyName
     */
    public String getFriendlyName() {
        return mFriendlyName;
    }

    /**
     * Returns a value of modelName in DD.
     * 
     * @return mModelName
     */
    public String getModelName() {
        return mModelName;
    }

    /**
     * Returns a value of UDN in DD.
     * 
     * @return mUDN
     */
    public String getUDN() {
        return mUDN;
    }

    /**
     * Returns URL of icon in DD.
     * 
     * @return mIconUrl
     */
    public String getIconUrl() {
        return mIconUrl;
    }

    /**
     * Returns IP address of the DD.
     * 
     * @return ip
     */
    public String getIpAddres() {
        String ip = null;
        if (mDDUrl != null) {
            return toHost(mDDUrl);
        }
        return ip;
    }

    /**
     * Returns a list of categories that the server supports.
     * 
     * @return a list of categories
     */
    public List<ApiService> getApiServices() {
        return Collections.unmodifiableList(mApiServices);
    }

    /**
     * Checks to see whether the server supports the category.
     * 
     * @param serviceName category name
     * @return true if it's supported.
     */
    public boolean hasApiService(final String serviceName) {
        if (serviceName == null) {
            return false;
        }
        for (ApiService apiService : mApiServices) {
            if (serviceName.equals(apiService.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a ApiService object.
     * 
     * @param serviceName category name
     * @return ApiService object
     */
    public ApiService getApiService(final String serviceName) {
        if (serviceName == null) {
            return null;
        }
        for (ApiService apiService : mApiServices) {
            if (serviceName.equals(apiService.getName())) {
                return apiService;
            }
        }
        return null;
    }

    /** 
     * Adds a ApiService object. 
     * @param name 名前
     * @param actionUrl Url
     */
    private void addApiService(final String name, final String actionUrl) {
        ApiService service = new ApiService(name, actionUrl);
        mApiServices.add(service);
    }

    /**
     * Fetches device description xml file from server and parses it.
     * 
     * @param ddUrl URL of device description xml.
     * @return ServerDevice instance
     */
    public static ServerDevice fetch(final String ddUrl) {
        if (ddUrl == null) {
            throw new NullPointerException("ddUrl is null.");
        }

        String ddXml = "";
        try {
            ddXml = SimpleHttpClient.httpGet(ddUrl);
        } catch (IOException e) {
            return null;
        }
        XmlElement rootElement = XmlElement.parse(ddXml);

        // "root"
        ServerDevice device = null;
        if ("root".equals(rootElement.getTagName())) {
            device = new ServerDevice();
            device.mDDUrl = ddUrl;

            // "device"
            XmlElement deviceElement = rootElement.findChild("device");
            device.mFriendlyName = deviceElement.findChild("friendlyName")
                    .getValue();
            device.mModelName = deviceElement.findChild("modelName").getValue();
            device.mUDN = deviceElement.findChild("UDN").getValue();

            // "iconList"
            XmlElement iconListElement = deviceElement.findChild("iconList");
            List<XmlElement> iconElements = iconListElement
                    .findChildren("icon");
            for (XmlElement iconElement : iconElements) {
                // Choose png icon to show Android UI.
                if ("image/png".equals(iconElement.findChild("mimetype")
                        .getValue())) {
                    String uri = iconElement.findChild("url").getValue();
                    String hostUrl = toSchemeAndHost(ddUrl);
                    device.mIconUrl = hostUrl + uri;
                }
            }

            // "av:X_ScalarWebAPI_DeviceInfo"
            XmlElement wApiElement = deviceElement
                    .findChild("X_ScalarWebAPI_DeviceInfo");
            XmlElement wApiServiceListElement = wApiElement
                    .findChild("X_ScalarWebAPI_ServiceList");
            List<XmlElement> wApiServiceElements = wApiServiceListElement
                    .findChildren("X_ScalarWebAPI_Service");
            for (XmlElement wApiServiceElement : wApiServiceElements) {
                String serviceName = wApiServiceElement.findChild(
                        "X_ScalarWebAPI_ServiceType").getValue();
                String actionUrl = wApiServiceElement.findChild(
                        "X_ScalarWebAPI_ActionList_URL").getValue();
                device.addApiService(serviceName, actionUrl);
            }
        }
        return device;
    }

    /**
     * .
     * @param url URL
     * @return hostUrl
     */
    private static String toSchemeAndHost(final String url) {
        int i = url.indexOf("://"); // http:// or https://
        if (i == -1) {
            return "";
        }
        int j = url.indexOf("/", i + URL_TO_HOST_INDEX);
        if (j == -1) {
            return "";
        }
        String hostUrl = url.substring(0, j);
        return hostUrl;
    }

    /**
     * .
     * @param url URL
     * @return host
     */
    private static String toHost(final String url) {
        int i = url.indexOf("://"); // http:// or https://
        if (i == -1) {
            return "";
        }
        int j = url.indexOf(":", i + URL_TO_HOST_INDEX);
        if (j == -1) {
            return "";
        }
        String host = url.substring(i + URL_TO_HOST_INDEX, j);
        return host;
    }
}
