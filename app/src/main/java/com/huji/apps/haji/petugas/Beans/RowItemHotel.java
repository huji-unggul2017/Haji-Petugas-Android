package com.huji.apps.haji.petugas.Beans;

/**
 * Created by Dell_Cleva on 19/03/2017.
 */

public class RowItemHotel {
    String sId;
    String sLatitude;
    String sLongitude;
    String sPlaceName;
    String sPlaceAddress;

    public RowItemHotel(String sid, String slatitude, String slongitude, String splacename, String splaceaddress) {
        this.sId = sid;
        this.sLatitude = slatitude;
        this.sLongitude = slongitude;
        this.sPlaceName = splacename;
        this.sPlaceAddress = splaceaddress;

    }

    public String getsPlaceAddress() {
        return sPlaceAddress;
    }

    public void setsPlaceAddress(String sPlaceAddress) {
        this.sPlaceAddress = sPlaceAddress;
    }

    public String getsLatitude() {
        return sLatitude;
    }

    public void setsLatitude(String sLatitude) {
        this.sLatitude = sLatitude;
    }

    public String getsLongitude() {
        return sLongitude;
    }

    public void setsLongitude(String sLongitude) {
        this.sLongitude = sLongitude;
    }

    public String getsPlaceName() {
        return sPlaceName;
    }

    public void setsPlaceName(String sPlaceName) {
        this.sPlaceName = sPlaceName;
    }
}
