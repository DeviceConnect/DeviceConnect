package com.example.androidappsample;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * d-Connect スマートデバイス.
 */
public class SmartDevice implements Parcelable {

    /**
     * Parcelableクリエイター.
     */
    public static final Parcelable.Creator<SmartDevice> CREATOR =
            new Parcelable.Creator<SmartDevice>() {
        public SmartDevice createFromParcel(final Parcel in) {
            return new SmartDevice(in);
        }
        public SmartDevice[] newArray(final int size) {
            return new SmartDevice[size];
        }
    };

    /**
     * デバイス名.
     */
    private String mName;

    /**
     * デバイス種別.
     */
    private String mType;

    /**
     * デバイスID.
     */
    private String mId;

    /**
     * サービスリスト.
     */
    private List<SmartService> mServiceList = new ArrayList<SmartService>();

    /**
     * コンストラクタ.
     * @param id デバイスID
     * @param name デバイス名
     */
    public SmartDevice(final String id, final String name) {
        setId(id);
        setName(name);
    }

    /**
     * Parcelableコンストラクタ.
     * @param in 入力
     */
    private SmartDevice(final Parcel in) {
        setName(in.readString());
        setType(in.readString());
        setId(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mName);
        dest.writeString(mType);
        dest.writeString(mId);
    }

    @Override
    public String toString() {
        return mName;
    }

    /**
     * デバイスIDを設定する.
     * @param id デバイスID
     */
    public void setId(final String id) {
        mId = id;
    }

    /**
     * デバイス名を設定する.
     * @param name デバイス名
     */
    public void setName(final String name) {
        mName = name;
    }

    /**
     * デバイス種別を設定する.
     * @param type デバイス種別
     */
    public void setType(final String type) {
        mType = type;
    }

    /**
     * デバイス名.
     * @return デバイス名
     */
    public String getName() {
        return mName;
    }

    /**
     * デバイス種別を取得する.
     * @return デバイス種別
     */
    public String getType() {
        return mType;
    }

    /**
     * デバイスIDを取得する.
     * @return デバイスID
     */
    public String getId() {
        return mId;
    }

    /**
     * サービスリストを取得する.
     * @return サービスリスト
     */
    public List<SmartService> getServiceList() {
        return mServiceList;
    }

    /**
     * サービスを追加する.
     * @param service サービス
     */
    public void addService(final SmartService service) {
        mServiceList.add(service);
    }

    /**
     * サービスを削除する.
     * @param service サービス
     */
    public void removeService(final SmartService service) {
        mServiceList.remove(service);
    }

}
